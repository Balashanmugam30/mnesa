-- =====================================================================
-- MNESA Database Schema Migration (V4) - AI Extraction Records
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Table: ai_extractions (Persisted structured proposals from AI extraction)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_extractions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    capture_id UUID NOT NULL REFERENCES captures(id) ON DELETE CASCADE,
    intake_job_id UUID NOT NULL REFERENCES intake_jobs(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    provider VARCHAR(50) NOT NULL,
    model_name VARCHAR(100),
    schema_version VARCHAR(20) NOT NULL DEFAULT 'v1',
    validation_status VARCHAR(50) NOT NULL,
    overall_confidence NUMERIC(4, 3) NOT NULL,
    title VARCHAR(255) NOT NULL,
    organization VARCHAR(255),
    category VARCHAR(50) NOT NULL,
    summary TEXT,
    deadline_at TIMESTAMPTZ,
    deadline_raw VARCHAR(255),
    deadline_timezone VARCHAR(50),
    deadline_ambiguous BOOLEAN NOT NULL DEFAULT FALSE,
    registration_url VARCHAR(2048),
    location VARCHAR(255),
    work_mode VARCHAR(50),
    eligibility TEXT,
    estimated_effort_minutes INT,
    priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL',
    priority_reason VARCHAR(255),
    extracted_fields JSONB,
    evidence_snippets JSONB,
    warning_messages JSONB,
    error_category VARCHAR(50),
    error_message TEXT,
    processing_duration_ms BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_ai_extractions_job UNIQUE (intake_job_id),
    CONSTRAINT chk_ai_extractions_status CHECK (validation_status IN (
        'SUCCEEDED', 'SUCCEEDED_WITH_WARNINGS', 'LOW_CONFIDENCE',
        'UNSUPPORTED_SOURCE', 'CONTENT_UNAVAILABLE', 'PROVIDER_UNAVAILABLE', 'FAILED'
    ))
);

CREATE INDEX IF NOT EXISTS idx_ai_extractions_user_id ON ai_extractions(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ai_extractions_capture_id ON ai_extractions(capture_id);
CREATE INDEX IF NOT EXISTS idx_ai_extractions_status ON ai_extractions(validation_status);
