from datetime import datetime
from enum import Enum
from typing import List, Optional
from pydantic import BaseModel, Field


class OpportunityType(str, Enum):
    INTERNSHIP = "INTERNSHIP"
    JOB = "JOB"
    HACKATHON = "HACKATHON"
    SCHOLARSHIP = "SCHOLARSHIP"
    COMPETITION = "COMPETITION"
    EVENT = "EVENT"
    CONFERENCE = "CONFERENCE"
    COURSE = "COURSE"
    OTHER = "OTHER"


class ExtractionPayload(BaseModel):
    raw_text: str = Field(..., min_length=5, max_length=50000, description="Raw text extracted from webpage or user share")
    source_url: Optional[str] = Field(None, description="Original source URL if available")
    user_notes: Optional[str] = Field(None, max_length=1000, description="Optional caption or note from user")


class ExtractedOpportunity(BaseModel):
    title: str = Field(..., min_length=1, max_length=255, description="Clear, concise opportunity title")
    organization: Optional[str] = Field(None, max_length=255, description="Company, university, or hosting entity")
    opportunity_type: OpportunityType = Field(default=OpportunityType.OTHER, description="Classified category")
    deadline_at: Optional[datetime] = Field(None, description="Submission, registration, or application deadline in ISO 8601")
    key_requirements: List[str] = Field(default_factory=list, description="Extracted eligibility, prerequisites, or required skills")
    action_url: Optional[str] = Field(None, description="Direct URL to apply, register, or read details")
    confidence_score: float = Field(..., ge=0.0, le=1.0, description="Composite confidence score between 0.0 and 1.0")
    evidence_snippets: List[str] = Field(default_factory=list, description="Verbatim snippets from source justifying extracted values")


class ExtractionResult(BaseModel):
    success: bool
    opportunity: Optional[ExtractedOpportunity] = None
    provider_used: str
    latency_ms: float
    sanitization_flags: List[str] = Field(default_factory=list)
    error_message: Optional[str] = None
