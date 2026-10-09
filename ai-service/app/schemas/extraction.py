from datetime import datetime
from enum import Enum
from typing import Generic, List, Optional, TypeVar
from pydantic import BaseModel, Field, computed_field

T = TypeVar("T")


class OpportunityCategory(str, Enum):
    INTERNSHIP = "INTERNSHIP"
    JOB = "JOB"
    HACKATHON = "HACKATHON"
    SCHOLARSHIP = "SCHOLARSHIP"
    COMPETITION = "COMPETITION"
    EVENT = "EVENT"
    CONFERENCE = "CONFERENCE"
    COURSE = "COURSE"
    GRANT = "GRANT"
    OTHER = "OTHER"


class PriorityLevel(str, Enum):
    LOW = "LOW"
    NORMAL = "NORMAL"
    HIGH = "HIGH"
    URGENT = "URGENT"


class WorkMode(str, Enum):
    REMOTE = "REMOTE"
    HYBRID = "HYBRID"
    ONSITE = "ONSITE"
    UNKNOWN = "UNKNOWN"


class ValidationStatus(str, Enum):
    SUCCEEDED = "SUCCEEDED"
    SUCCEEDED_WITH_WARNINGS = "SUCCEEDED_WITH_WARNINGS"
    LOW_CONFIDENCE = "LOW_CONFIDENCE"
    UNSUPPORTED_SOURCE = "UNSUPPORTED_SOURCE"
    CONTENT_UNAVAILABLE = "CONTENT_UNAVAILABLE"
    PROVIDER_UNAVAILABLE = "PROVIDER_UNAVAILABLE"
    FAILED = "FAILED"


class FieldResult(BaseModel, Generic[T]):
    value: Optional[T] = Field(None, description="Extracted value for field")
    confidence: float = Field(0.0, ge=0.0, le=1.0, description="Field-level confidence score")
    evidence: Optional[str] = Field(None, description="Concise verbatim supporting quote from source text")


class DeadlineField(BaseModel):
    value: Optional[datetime] = Field(None, description="Validated ISO 8601 deadline timestamp")
    raw_text: Optional[str] = Field(None, description="Original unparsed date string from source")
    timezone: Optional[str] = Field(None, description="Detected or specified timezone (e.g., UTC, EST, IST)")
    is_ambiguous: bool = Field(False, description="True if multiple dates exist or deadline is uncertain")
    confidence: float = Field(0.0, ge=0.0, le=1.0, description="Deadline extraction confidence score")
    evidence: Optional[str] = Field(None, description="Source quote supporting deadline determination")


class ExtractedOpportunity(BaseModel):
    title: FieldResult[str] = Field(..., description="Opportunity title with confidence and evidence")
    organization: FieldResult[str] = Field(default_factory=FieldResult, description="Host company, university, or organizer")
    category: FieldResult[OpportunityCategory] = Field(
        default_factory=lambda: FieldResult(value=OpportunityCategory.OTHER, confidence=0.5),
        description="Classified opportunity type"
    )
    summary: Optional[str] = Field(None, max_length=1000, description="Concise factual summary grounded in source")
    deadline: DeadlineField = Field(default_factory=DeadlineField, description="Deadline analysis and validation")
    eligibility: FieldResult[str] = Field(default_factory=FieldResult, description="Target audience, qualifications, requirements")
    location: FieldResult[str] = Field(default_factory=FieldResult, description="Geographic location or city/country")
    work_mode: WorkMode = Field(default=WorkMode.UNKNOWN, description="Remote, hybrid, onsite, or unknown")
    registration_url: Optional[str] = Field(None, description="Direct URL to apply, register, or submit")
    source_url: Optional[str] = Field(None, description="Original source webpage or post URL")
    estimated_effort_minutes: Optional[int] = Field(None, ge=1, le=10000, description="Estimated application effort in minutes")
    tags: List[str] = Field(default_factory=list, description="Relevant domain keywords or topics")
    priority: PriorityLevel = Field(default=PriorityLevel.NORMAL, description="Calculated urgency and importance")
    priority_reason: Optional[str] = Field(None, description="Explainable reason for assigned priority")
    overall_confidence: float = Field(0.0, ge=0.0, le=1.0, description="Composite weighted confidence score")
    validation_status: ValidationStatus = Field(default=ValidationStatus.SUCCEEDED, description="Deterministic validation status")
    warning_messages: List[str] = Field(default_factory=list, description="Quality, ambiguity, or security warnings")
    evidence_snippets: List[str] = Field(default_factory=list, description="Aggregated evidence snippets from source")

    # Backward compatibility properties for Phase 01-04 clients & tests
    @computed_field
    @property
    def opportunity_type(self) -> OpportunityCategory:
        return self.category.value if self.category and self.category.value else OpportunityCategory.OTHER

    @computed_field
    @property
    def confidence_score(self) -> float:
        return self.overall_confidence

    @computed_field
    @property
    def deadline_at(self) -> Optional[datetime]:
        return self.deadline.value if self.deadline else None

    @computed_field
    @property
    def action_url(self) -> Optional[str]:
        return self.registration_url or self.source_url


# Compatibility alias for Phase 01-04 schemas
OpportunityType = OpportunityCategory


class ExtractionPayload(BaseModel):
    raw_text: str = Field(..., min_length=5, max_length=50000, description="Raw text extracted from webpage or user share")
    source_url: Optional[str] = Field(None, description="Original source URL if available")
    user_notes: Optional[str] = Field(None, max_length=1000, description="Optional caption or note from user")
    image_base64: Optional[str] = Field(None, description="Optional base64-encoded image for OCR")
    image_mime_type: Optional[str] = Field(None, description="MIME type of provided image")


class FetchAndExtractPayload(BaseModel):
    url: Optional[str] = Field(None, description="Public webpage URL to fetch and extract")
    raw_text: Optional[str] = Field(None, description="Fallback or supplemental raw text")
    user_notes: Optional[str] = Field(None, max_length=1000, description="Optional user caption or note")


class ExtractionResult(BaseModel):
    success: bool
    opportunity: Optional[ExtractedOpportunity] = None
    provider_used: str
    latency_ms: float
    sanitization_flags: List[str] = Field(default_factory=list)
    validation_status: ValidationStatus = Field(default=ValidationStatus.SUCCEEDED)
    error_message: Optional[str] = None
