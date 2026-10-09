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
from app.providers.gemini import GeminiAIProvider
from app.providers.mock import MockAIProvider
from app.providers.ollama import OllamaAIProvider
from app.schemas.extraction import (
    ExtractedOpportunity,
    ExtractionPayload,
    ExtractionResult,
    FetchAndExtractPayload,
    FieldResult,
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
