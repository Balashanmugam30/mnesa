-- =====================================================================
-- MNESA Database Schema Migration (V3) - Share Intake & Capture Pipeline
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Table: captures (Raw intake records from Android Share and Web)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS captures (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    source_type VARCHAR(32) NOT NULL,
    original_text TEXT,
    original_url VARCHAR(2048),
    canonical_url VARCHAR(2048),
    source_domain VARCHAR(255),
    client_capture_id VARCHAR(64),
    idempotency_key VARCHAR(128) NOT NULL,
    media_mime_type VARCHAR(100),
    media_size_bytes BIGINT,
    metadata JSONB,
    status VARCHAR(32) NOT NULL DEFAULT 'RECEIVED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_captures_user_idempotency UNIQUE (user_id, idempotency_key),
    CONSTRAINT chk_captures_source_type CHECK (source_type IN ('URL', 'TEXT', 'IMAGE', 'HYBRID')),
    CONSTRAINT chk_captures_status CHECK (status IN ('RECEIVED', 'QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_captures_user_id ON captures(user_id);
CREATE INDEX IF NOT EXISTS idx_captures_canonical_url ON captures(user_id, canonical_url);
CREATE INDEX IF NOT EXISTS idx_captures_status ON captures(status);
CREATE INDEX IF NOT EXISTS idx_captures_created_at ON captures(created_at DESC);

-- ---------------------------------------------------------------------
-- 2. Table: intake_jobs (Async processing jobs for captured opportunities)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS intake_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    capture_id UUID NOT NULL REFERENCES captures(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    attempt_count INT NOT NULL DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_intake_jobs_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_intake_jobs_user_id ON intake_jobs(user_id);
CREATE INDEX IF NOT EXISTS idx_intake_jobs_capture_id ON intake_jobs(capture_id);
CREATE INDEX IF NOT EXISTS idx_intake_jobs_status ON intake_jobs(status);
