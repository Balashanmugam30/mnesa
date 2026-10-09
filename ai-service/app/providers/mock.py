import re
import time
from datetime import datetime, timedelta, timezone
from typing import Optional

from app.core.security import sanitize_untrusted_input
from app.core.validator import post_validate_opportunity
from app.providers.base import BaseAIProvider
from app.schemas.extraction import (
    DeadlineField,
    ExtractedOpportunity,
    ExtractionPayload,
    ExtractionResult,
    FieldResult,
    OpportunityCategory,
    PriorityLevel,
    ValidationStatus,
    WorkMode,
)


class MockAIProvider(BaseAIProvider):
    """
    Deterministic provider for unit testing and local development without external API keys.
    Extracts realistic, testable fields and grounded evidence directly from input text.
    """

    @property
    def provider_name(self) -> str:
        return "mock"

    async def extract_opportunity(self, payload: ExtractionPayload) -> ExtractionResult:
        start_time = time.time()
        sanitized_content, flags = sanitize_untrusted_input(payload.raw_text)
        text = payload.raw_text
        text_lower = text.lower()

        # 1. Category detection
        if "intern" in text_lower:
            category = OpportunityCategory.INTERNSHIP
        elif "hackathon" in text_lower:
            category = OpportunityCategory.HACKATHON
        elif "scholarship" in text_lower:
            category = OpportunityCategory.SCHOLARSHIP
        elif "competition" in text_lower:
            category = OpportunityCategory.COMPETITION
        elif "conference" in text_lower:
            category = OpportunityCategory.CONFERENCE
        elif "job" in text_lower or "hiring" in text_lower:
            category = OpportunityCategory.JOB
        elif "grant" in text_lower:
            category = OpportunityCategory.GRANT
        elif "course" in text_lower or "workshop" in text_lower:
            category = OpportunityCategory.COURSE
        else:
            category = OpportunityCategory.OTHER

        # 2. Title extraction
        title_val = "Captured Opportunity"
        title_ev = None
        for line in text.splitlines():
            line_str = line.strip()
            if 5 < len(line_str) < 120 and not line_str.startswith("http"):
                title_val = line_str
                title_ev = line_str
                break

        # 3. Organization detection
        org_val = "MNESA Network"
        org_ev = None
        org_match = re.search(r"(?:hosted by|at|by|company:?)\s+([A-Z][A-Za-z0-9\s&]{2,30})", text, re.IGNORECASE)
        if org_match:
            org_val = org_match.group(1).strip()
            org_ev = org_match.group(0)

        # 4. Deadline detection
        deadline_dt: Optional[datetime] = None
        deadline_raw = None
        deadline_ev = None
        deadline_ambiguous = False

        date_match = re.search(r"(?:deadline|due|by|closes? on):?\s+([A-Za-z]+ \d{1,2},? \d{4}|\d{4}-\d{2}-\d{2})", text, re.IGNORECASE)
        if date_match:
            deadline_raw = date_match.group(1)
            deadline_ev = date_match.group(0)
            try:
                # Try parsing standard formats
                if "-" in deadline_raw:
                    deadline_dt = datetime.strptime(deadline_raw, "%Y-%m-%d").replace(tzinfo=timezone.utc)
                else:
                    clean_date = deadline_raw.replace(",", "")
                    deadline_dt = datetime.strptime(clean_date, "%B %d %Y").replace(tzinfo=timezone.utc)
            except Exception:
                deadline_dt = datetime.now(timezone.utc) + timedelta(days=14)
        elif "rolling" in text_lower or "until filled" in text_lower:
            deadline_raw = "Rolling / Until Filled"
            deadline_ev = "rolling basis" if "rolling" in text_lower else "until filled"
        else:
            # Check for multiple dates
            dates = re.findall(r"\b\d{4}-\d{2}-\d{2}\b", text)
            if len(dates) > 1:
                deadline_ambiguous = True
                deadline_raw = dates[0]
                deadline_ev = f"Date candidate: {dates[0]}"

        # 5. Work mode
        work_mode = WorkMode.UNKNOWN
        if "remote" in text_lower:
            work_mode = WorkMode.REMOTE
        elif "hybrid" in text_lower:
            work_mode = WorkMode.HYBRID
        elif "onsite" in text_lower or "on-site" in text_lower:
            work_mode = WorkMode.ONSITE

        # 6. Build opportunity model
        opp = ExtractedOpportunity(
            title=FieldResult(value=title_val, confidence=0.92, evidence=title_ev),
            organization=FieldResult(value=org_val, confidence=0.88, evidence=org_ev),
            category=FieldResult(value=category, confidence=0.95, evidence=f"Category matched: {category.value}"),
            summary=f"Automated extraction proposal for {title_val} ({category.value}).",
            deadline=DeadlineField(
                value=deadline_dt,
                raw_text=deadline_raw,
                timezone="UTC" if deadline_dt else None,
                is_ambiguous=deadline_ambiguous,
                confidence=0.85 if deadline_dt else (0.75 if deadline_raw else 0.4),
                evidence=deadline_ev,
            ),
            eligibility=FieldResult(value="Open to eligible applicants", confidence=0.75, evidence="Open to eligible applicants" if "eligible" in text_lower else None),
            location=FieldResult(value="Global / Online" if work_mode == WorkMode.REMOTE else "See description", confidence=0.7),
            work_mode=work_mode,
            registration_url=payload.source_url or "https://mnesa.ai",
            source_url=payload.source_url,
            estimated_effort_minutes=30,
            tags=[category.value.lower(), "opportunity"],
            priority=PriorityLevel.NORMAL,
            overall_confidence=0.85,
            validation_status=ValidationStatus.SUCCEEDED,
        )

        # Apply deterministic post-validation
        validated_opp = post_validate_opportunity(opp, text)
        latency = (time.time() - start_time) * 1000.0

        return ExtractionResult(
            success=True,
            opportunity=validated_opp,
            provider_used=self.provider_name,
            latency_ms=round(latency, 2),
            sanitization_flags=flags,
            validation_status=validated_opp.validation_status,
        )
