-- =====================================================================
-- MNESA Database Schema Migration (V7) - Screenshot/OCR, AI Assistant & Insights
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Table: attachments (Stored screenshots & intake media)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    capture_id UUID REFERENCES captures(id) ON DELETE SET NULL,
    opportunity_id UUID REFERENCES opportunities(id) ON DELETE SET NULL,
    file_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(1024) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_attachments_user_id ON attachments(user_id);
CREATE INDEX IF NOT EXISTS idx_attachments_capture_id ON attachments(capture_id);
CREATE INDEX IF NOT EXISTS idx_attachments_opportunity_id ON attachments(opportunity_id);

-- ---------------------------------------------------------------------
-- 2. Table: ai_candidates (Multi-candidate opportunity proposals from OCR)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ai_candidates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    extraction_id UUID NOT NULL REFERENCES ai_extractions(id) ON DELETE CASCADE,
    candidate_index INT NOT NULL DEFAULT 0,
    title VARCHAR(255) NOT NULL,
    organization VARCHAR(255),
    category VARCHAR(50) NOT NULL DEFAULT 'OTHER',
    summary TEXT,
    deadline_at TIMESTAMPTZ,
    deadline_raw VARCHAR(255),
    confidence_score NUMERIC(4, 3) NOT NULL DEFAULT 0.8,
    evidence_snippets TEXT,
    is_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_ai_candidates_extraction ON ai_candidates(extraction_id);

-- ---------------------------------------------------------------------
-- 3. Enhance captures with media storage path
-- ---------------------------------------------------------------------
ALTER TABLE captures ADD COLUMN IF NOT EXISTS media_storage_path VARCHAR(1024);

-- ---------------------------------------------------------------------
-- 4. Optimize indexes for user-scoped insights aggregations
-- ---------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_opp_user_category ON opportunities(user_id, opportunity_type);
CREATE INDEX IF NOT EXISTS idx_opp_user_created ON opportunities(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_opp_activity_user_date ON opportunity_activities(user_id, created_at DESC);
