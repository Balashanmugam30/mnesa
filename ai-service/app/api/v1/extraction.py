import time
from fastapi import APIRouter, HTTPException
from app.core.config import settings
from app.core.fetcher import (
    ContentTooLargeError,
    ContentUnavailableError,
    FetchError,
    FetchTimeoutError,
    SafeWebFetcher,
    SsrfViolationError,
    UnsupportedContentTypeError,
)
from app.core.logging import logger
from app.core.normalizer import normalize_html_content
from app.core.ocr import OcrProcessor
from app.core.security import sanitize_untrusted_input
from app.providers.gemini import GeminiAIProvider
from app.providers.mock import MockAIProvider
from app.providers.ollama import OllamaAIProvider
from app.schemas.extraction import (
    AssistantQueryPayload,
    AssistantQueryResponse,
    AssistantRecord,
    ExtractedOpportunity,
    ExtractionPayload,
    ExtractionResult,
    FetchAndExtractPayload,
    FieldResult,
    ImageExtractionPayload,
    MultiCandidateExtractionResult,
    OpportunityCategory,
    ValidationStatus,
)

router = APIRouter(prefix="/api/v1", tags=["Extraction"])

ocr_processor = OcrProcessor()
web_fetcher = SafeWebFetcher()


def get_active_provider():
    provider_name = (settings.AI_PROVIDER or "").lower()
    if provider_name == "ollama":
        return OllamaAIProvider()
    elif provider_name == "mock":
        return MockAIProvider()
    elif provider_name == "gemini":
        return GeminiAIProvider()
    # Default to Gemini if key is present, otherwise Mock
    if settings.GEMINI_API_KEY:
        return GeminiAIProvider()
    return MockAIProvider()


@router.post("/extract", response_model=ExtractionResult)
async def extract_opportunity_endpoint(payload: ExtractionPayload):
    """
    Extracts structured opportunity details from direct raw text, user notes, or OCR images.
    """
    text_content = payload.raw_text

    # Run OCR if base64 image attached
    if payload.image_base64:
        ocr_text, ocr_warning = ocr_processor.extract_text_from_base64(
            payload.image_base64, payload.image_mime_type
        )
        if ocr_text:
            text_content = f"{text_content}\n\n[OCR Image Text]:\n{ocr_text}"
        elif ocr_warning:
            logger.info(f"OCR note: {ocr_warning}")

    active_payload = ExtractionPayload(
        raw_text=text_content,
        source_url=payload.source_url,
        user_notes=payload.user_notes,
    )

    provider = get_active_provider()
    result = await provider.extract_opportunity(active_payload)
    return result


@router.post("/fetch-and-extract", response_model=ExtractionResult)
async def fetch_and_extract_endpoint(payload: FetchAndExtractPayload):
    """
    Safely acquires public webpage content, normalizes metadata and text,
    and runs intelligent opportunity extraction with post-validation and confidence scoring.
    """
    start_time = time.time()
    extracted_text = payload.raw_text or ""
    source_url = payload.url
    warnings = []

    # 1. Fetch public URL if provided
    if source_url and source_url.strip():
        try:
            html_content, content_type, final_url = await web_fetcher.fetch(source_url)
            normalized = normalize_html_content(html_content, final_url)

            # Combine normalized metadata and body text
            parts = []
            if normalized.best_candidate_title:
                parts.append(f"Title: {normalized.best_candidate_title}")
            if normalized.og_site_name:
                parts.append(f"Organization / Site: {normalized.og_site_name}")
            if normalized.meta_description:
                parts.append(f"Summary: {normalized.meta_description}")
            if normalized.clean_text:
                parts.append(normalized.clean_text)

            extracted_text = "\n\n".join(parts)
            if payload.raw_text:
                extracted_text = f"{payload.raw_text}\n\n{extracted_text}"

        except SsrfViolationError as e:
            logger.warning(f"SSRF violation blocked for URL '{source_url}': {str(e)}")
            if not extracted_text:
                return ExtractionResult(
                    success=False,
                    provider_used="security-filter",
                    latency_ms=round((time.time() - start_time) * 1000.0, 2),
                    validation_status=ValidationStatus.UNSUPPORTED_SOURCE,
                    error_message=f"Access to requested destination was prohibited for security: {str(e)}",
                )
            warnings.append(f"URL fetch blocked: {str(e)}. Proceeding with provided text.")

        except (ContentTooLargeError, UnsupportedContentTypeError) as e:
            logger.warning(f"Content format restriction for URL '{source_url}': {str(e)}")
            if not extracted_text:
                return ExtractionResult(
                    success=False,
                    provider_used="content-filter",
                    latency_ms=round((time.time() - start_time) * 1000.0, 2),
                    validation_status=ValidationStatus.UNSUPPORTED_SOURCE,
                    error_message=str(e),
                )
            warnings.append(f"URL format warning: {str(e)}. Proceeding with provided text.")

        except (FetchTimeoutError, ContentUnavailableError) as e:
            logger.warning(f"Content acquisition failed for URL '{source_url}': {str(e)}")
            if not extracted_text:
                return ExtractionResult(
                    success=False,
                    provider_used="web-fetcher",
                    latency_ms=round((time.time() - start_time) * 1000.0, 2),
                    validation_status=ValidationStatus.CONTENT_UNAVAILABLE,
                    error_message=f"Webpage content could not be retrieved: {str(e)}",
                )
            warnings.append(f"Webpage unavailable: {str(e)}. Proceeding with provided text.")

    if not extracted_text.strip():
        return ExtractionResult(
            success=False,
            provider_used="input-validator",
            latency_ms=round((time.time() - start_time) * 1000.0, 2),
            validation_status=ValidationStatus.CONTENT_UNAVAILABLE,
            error_message="No accessible content or text found to extract",
        )

    # 2. Extract structured opportunity via active provider
    extraction_payload = ExtractionPayload(
        raw_text=extracted_text,
        source_url=source_url,
        user_notes=payload.user_notes,
    )

    provider = get_active_provider()
    result = await provider.extract_opportunity(extraction_payload)

    # Attach any fetch warnings to extracted opportunity
    if result.opportunity and warnings:
        result.opportunity.warning_messages.extend(warnings)
        if result.opportunity.validation_status == ValidationStatus.SUCCEEDED:
            result.opportunity.validation_status = ValidationStatus.SUCCEEDED_WITH_WARNINGS

    return result


@router.post("/extract/image", response_model=MultiCandidateExtractionResult)
async def extract_image_endpoint(payload: ImageExtractionPayload):
    """
    Performs OCR on shared screenshots/images, extracts structured opportunities,
    and returns multi-candidate proposals with confidence and evidence grounding.
    """
    start_time = time.time()
    sanitized_notes, note_flags = sanitize_untrusted_input(payload.user_notes or "")

    ocr_text, ocr_warning = ocr_processor.extract_text_from_base64(
        payload.image_base64, payload.image_mime_type
    )

    if not ocr_text or not ocr_text.strip():
        warning_msg = ocr_warning or "No readable text detected in provided image"
        return MultiCandidateExtractionResult(
            success=False,
            ocr_text=None,
            candidates=[],
            primary_opportunity=None,
            provider_used="ocr-scanner",
            latency_ms=round((time.time() - start_time) * 1000.0, 2),
            sanitization_flags=note_flags,
            validation_status=ValidationStatus.CONTENT_UNAVAILABLE,
            error_message=warning_msg,
        )

    cleaned_ocr_text = ocr_text.strip()
    full_text = f"{cleaned_ocr_text}\n\nUser Notes: {sanitized_notes}" if sanitized_notes else cleaned_ocr_text

    extraction_payload = ExtractionPayload(
        raw_text=full_text,
        source_url=payload.source_url,
        user_notes=sanitized_notes,
    )

    provider = get_active_provider()
    candidates = await provider.extract_candidates(extraction_payload)
    primary = candidates[0] if candidates else None

    return MultiCandidateExtractionResult(
        success=len(candidates) > 0,
        ocr_text=cleaned_ocr_text,
        candidates=candidates,
        primary_opportunity=primary,
        provider_used=provider.provider_name,
        latency_ms=round((time.time() - start_time) * 1000.0, 2),
        sanitization_flags=note_flags,
        validation_status=primary.validation_status if primary else ValidationStatus.CONTENT_UNAVAILABLE,
    )


@router.post("/assistant/generate", response_model=AssistantQueryResponse)
async def assistant_generate_endpoint(payload: AssistantQueryPayload):
    """
    Grounded Personal AI Assistant answering user questions scoped strictly
    to authorized opportunity context records without SQL generation or hallucinations.
    """
    start_time = time.time()
    sanitized_query, flags = sanitize_untrusted_input(payload.query)

    query_lower = sanitized_query.lower()

    # Determine user intent
    intent = "GENERAL_QUERY"
    if any(k in query_lower for k in ["deadline", "due", "expir", "soon", "close", "date"]):
        intent = "UPCOMING_DEADLINES"
    elif any(k in query_lower for k in ["priority", "urgent", "critical", "important"]):
        intent = "PRIORITY_ACTIONS"
    elif any(k in query_lower for k in ["week", "this week", "next week", "schedule"]):
        intent = "WEEKLY_SUMMARY"
    elif any(k in query_lower for k in ["from", "at", "google", "meta", "amazon", "microsoft", "apple"]):
        intent = "ORGANIZATION_SEARCH"
    elif any(k in query_lower for k in ["intern", "job", "hackathon", "grant", "scholarship"]):
        intent = "CATEGORY_FILTER"
    elif any(k in query_lower for k in ["applied", "status", "progress", "submitted"]):
        intent = "APPLICATION_STATUS"

    records = payload.context_records
    matching_records = []
    cited_ids = []

    # Match and rank relevant records
    for r in records:
        r_text = f"{r.title} {r.organization or ''} {r.category or ''} {r.status or ''}".lower()
        if intent == "ORGANIZATION_SEARCH":
            words = [w for w in query_lower.split() if len(w) > 3 and w not in ["what", "show", "from", "saved", "have", "with", "which"]]
            if any(w in r_text for w in words):
                matching_records.append(r)
        elif intent == "CATEGORY_FILTER":
            category_keywords = ["intern", "job", "hackathon", "grant", "scholarship", "conference"]
            matched = [k for k in category_keywords if k in query_lower]
            if any(k in r_text for k in matched):
                matching_records.append(r)
        elif intent == "UPCOMING_DEADLINES":
            if r.deadline_at:
                matching_records.append(r)
        elif intent == "APPLICATION_STATUS":
            if r.status in ["APPLIED", "SELECTED", "REJECTED"]:
                matching_records.append(r)
        elif intent == "PRIORITY_ACTIONS":
            if r.priority in ["HIGH", "URGENT"]:
                matching_records.append(r)
        else:
            words = [w for w in query_lower.split() if len(w) > 3]
            if any(w in r_text for w in words):
                matching_records.append(r)

    target_records = matching_records if matching_records else records[:5]
    for r in target_records[:5]:
        cited_ids.append(r.id)

    # Produce grounded answer
    if not records:
        answer = "You don't have any saved opportunities yet. You can capture opportunities by sharing links or screenshots from any app!"
        action_suggestions = ["Capture an opportunity via Android Share", "Explore opportunities"]
    elif not target_records:
        answer = f"I couldn't find any opportunities matching '{sanitized_query}'. You currently have {len(records)} saved opportunities."
        action_suggestions = ["View all opportunities", "Check upcoming deadlines"]
    else:
        if intent == "UPCOMING_DEADLINES":
            lines = ["Here are your upcoming deadlines based on your saved opportunities:"]
            for r in target_records[:4]:
                org_str = f" at {r.organization}" if r.organization else ""
                dl_str = f" (Deadline: {r.deadline_at})" if r.deadline_at else " (No fixed deadline)"
                lines.append(f"• **{r.title}**{org_str}{dl_str}")
            answer = "\n".join(lines)
            action_suggestions = ["Set preparation reminders", "Review application requirements"]
        elif intent == "PRIORITY_ACTIONS":
            lines = ["Here are your highest priority items:"]
            for r in target_records[:4]:
                lines.append(f"• **{r.title}** ({r.priority or 'NORMAL'} priority) - Status: {r.status or 'SAVED'}")
            answer = "\n".join(lines)
            action_suggestions = ["Work on priority applications", "Mark completed submissions"]
        elif intent == "WEEKLY_SUMMARY":
            lines = ["Here is your weekly opportunity action plan:"]
            for r in target_records[:4]:
                lines.append(f"• **{r.title}** - Due: {r.deadline_at or 'Flexible'}")
            answer = "\n".join(lines)
            action_suggestions = ["Schedule focus blocks", "Submit applications before deadline"]
        else:
            lines = [f"Found {len(target_records)} relevant opportunities:"]
            for r in target_records[:4]:
                org_str = f" ({r.organization})" if r.organization else ""
                lines.append(f"• **{r.title}**{org_str} - Status: {r.status or 'SAVED'}")
            answer = "\n".join(lines)
            action_suggestions = ["View opportunity details", "Track application progress"]

    return AssistantQueryResponse(
        answer=answer,
        intent=intent,
        cited_opportunity_ids=cited_ids,
        action_suggestions=action_suggestions,
        latency_ms=round((time.time() - start_time) * 1000.0, 2),
    )

