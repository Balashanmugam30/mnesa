import re
from datetime import datetime, timezone
from typing import List, Optional, Tuple
from urllib.parse import urlparse

from app.core.fetcher import is_ip_blocked
from app.schemas.extraction import (
    ExtractedOpportunity,
    OpportunityCategory,
    PriorityLevel,
    ValidationStatus,
)


DATE_PATTERNS = [
    r"\b(?:jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)[a-z]*\s+\d{1,2}(?:st|nd|rd|th)?,?\s+\d{4}\b",
    r"\b\d{1,2}/\d{1,2}/\d{2,4}\b",
    r"\b\d{4}-\d{2}-\d{2}\b",
]
COMPILED_DATE_PATTERNS = [re.compile(p, re.IGNORECASE) for p in DATE_PATTERNS]

ROLLING_PATTERNS = [
    r"rolling basis",
    r"rolling applications",
    r"until filled",
    r"open until filled",
    r"first-come",
]


def check_evidence_in_text(evidence: Optional[str], source_text: str) -> bool:
    """Verifies that the evidence quote is grounded in the source text."""
    if not evidence or not source_text:
        return False
    clean_ev = " ".join(evidence.lower().split())
    clean_src = " ".join(source_text.lower().split())
    # Allow exact or significant substring match (at least 20 chars)
    if clean_ev in clean_src:
        return True
    if len(clean_ev) > 30 and clean_ev[:30] in clean_src:
        return True
    return False


def validate_url(url_str: Optional[str]) -> Tuple[Optional[str], Optional[str]]:
    """Validates that a URL is a valid public HTTP/HTTPS URL."""
    if not url_str or not url_str.strip():
        return None, None
    try:
        parsed = urlparse(url_str.strip())
        if parsed.scheme.lower() not in ("http", "https"):
            return None, f"Invalid URL scheme: '{parsed.scheme}'"
        if not parsed.netloc:
            return None, "Missing URL domain"
        host = parsed.netloc.split(":")[0].strip()
        # Basic check for localhost or loopback
        if host in ("localhost", "127.0.0.1", "0.0.0.0"):
            return None, "Prohibited local URL destination"
        return url_str.strip(), None
    except Exception as e:
        return None, f"Malformed URL: {str(e)}"


def post_validate_opportunity(opportunity: ExtractedOpportunity, source_text: str) -> ExtractedOpportunity:
    """
    Applies deterministic validation rules and computes calibrated confidence and explainable priority.
    """
    warnings: List[str] = list(opportunity.warning_messages)
    now_utc = datetime.now(timezone.utc)

    # 1. Title validation
    if not opportunity.title.value or len(opportunity.title.value.strip()) < 3:
        warnings.append("Opportunity title is missing or suspiciously short")
        opportunity.title.confidence = min(opportunity.title.confidence, 0.4)
    elif opportunity.title.evidence and not check_evidence_in_text(opportunity.title.evidence, source_text):
        warnings.append("Title evidence could not be corroborated in source text")
        opportunity.title.confidence = max(0.2, opportunity.title.confidence - 0.2)

    # 2. Registration URL validation
    if opportunity.registration_url:
        valid_url, err = validate_url(opportunity.registration_url)
        if err:
            warnings.append(f"Registration URL validation warning: {err}")
            opportunity.registration_url = None
        else:
            opportunity.registration_url = valid_url

    # 3. Deadline validation
    deadline_field = opportunity.deadline
    if deadline_field.value:
        dt = deadline_field.value
        # Ensure timezone-aware comparison
        if dt.tzinfo is None:
            dt = dt.replace(tzinfo=timezone.utc)

        if dt < now_utc:
            warnings.append(f"Detected deadline ({dt.strftime('%Y-%m-%d')}) is in the past")
            deadline_field.confidence = max(0.3, deadline_field.confidence - 0.25)
            opportunity.priority = PriorityLevel.LOW
            opportunity.priority_reason = "Deadline has already passed"
        else:
            days_left = (dt - now_utc).days
            if days_left <= 3:
                opportunity.priority = PriorityLevel.URGENT
                opportunity.priority_reason = f"Urgent deadline approaching in {days_left} day(s)"
            elif days_left <= 7:
                opportunity.priority = PriorityLevel.HIGH
                opportunity.priority_reason = f"High priority: Deadline in {days_left} days"
            else:
                opportunity.priority = PriorityLevel.NORMAL
                opportunity.priority_reason = f"Application closes on {dt.strftime('%B %d, %Y')}"
    else:
        # Check if text contains rolling or until filled
        is_rolling = any(re.search(pat, source_text, re.IGNORECASE) for pat in ROLLING_PATTERNS)
        if is_rolling:
            deadline_field.raw_text = "Rolling / Until Filled"
            deadline_field.confidence = 0.75
            opportunity.priority = PriorityLevel.NORMAL
            opportunity.priority_reason = "Rolling applications (reviewed on rolling basis)"
        else:
            opportunity.priority = PriorityLevel.LOW
            opportunity.priority_reason = "Priority is conservative because no deadline was found"
            warnings.append("No explicit application deadline detected in content")

    # Check for multiple ambiguous dates in source text
    found_dates = []
    for pattern in COMPILED_DATE_PATTERNS:
        matches = pattern.findall(source_text)
        found_dates.extend(matches)
    if len(set(found_dates)) > 2 and not deadline_field.evidence:
        deadline_field.is_ambiguous = True
        warnings.append("Multiple candidate dates found in source; deadline requires user confirmation")
        deadline_field.confidence = max(0.3, deadline_field.confidence - 0.2)

    # 4. Evidence Grounding & Verification
    if deadline_field.evidence and not check_evidence_in_text(deadline_field.evidence, source_text):
        warnings.append("Deadline evidence quote was not verbatim in source text")
        deadline_field.confidence = max(0.2, deadline_field.confidence - 0.25)

    # 5. Composite Overall Confidence Computation
    # Weights: Title (25%), Category (20%), Deadline (30%), Org (15%), Evidence (10%)
    title_score = opportunity.title.confidence
    cat_score = opportunity.category.confidence
    deadline_score = deadline_field.confidence
    org_score = opportunity.organization.confidence if opportunity.organization.value else 0.5
    evidence_score = 0.8 if (opportunity.title.evidence and deadline_field.evidence) else 0.5

    raw_overall = (
        title_score * 0.25 +
        cat_score * 0.20 +
        deadline_score * 0.30 +
        org_score * 0.15 +
        evidence_score * 0.10
    )

    # Deductions for ambiguity or warnings
    if deadline_field.is_ambiguous:
        raw_overall -= 0.10
    if len(warnings) > 2:
        raw_overall -= 0.10

    overall_confidence = max(0.1, min(1.0, round(raw_overall, 2)))
    opportunity.overall_confidence = overall_confidence

    # 6. Assign Validation Status
    if overall_confidence >= 0.80 and not deadline_field.is_ambiguous:
        opportunity.validation_status = ValidationStatus.SUCCEEDED
    elif overall_confidence >= 0.50 or deadline_field.is_ambiguous:
        opportunity.validation_status = ValidationStatus.SUCCEEDED_WITH_WARNINGS
    else:
        opportunity.validation_status = ValidationStatus.LOW_CONFIDENCE

    opportunity.warning_messages = warnings

    # Collect all evidence snippets
    snippets = []
    if opportunity.title.evidence:
        snippets.append(opportunity.title.evidence)
    if deadline_field.evidence:
        snippets.append(deadline_field.evidence)
    if opportunity.eligibility.evidence:
        snippets.append(opportunity.eligibility.evidence)
    opportunity.evidence_snippets = snippets

    return opportunity
