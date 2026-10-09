-- =====================================================================
-- MNESA Database Schema Migration (V5) - Opportunity Management, Search, Tags & History
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Update check constraint on status with full Phase 06 lifecycle states
-- ---------------------------------------------------------------------
ALTER TABLE opportunities DROP CONSTRAINT IF EXISTS chk_opportunities_status;

-- Map legacy active statuses to clean lifecycle equivalents
UPDATE opportunities SET status = 'APPLIED' WHERE status = 'ACTED';
UPDATE opportunities SET status = 'SELECTED' WHERE status = 'COMPLETED';

ALTER TABLE opportunities ADD CONSTRAINT chk_opportunities_status CHECK (
    status IN ('CAPTURED', 'PROCESSING', 'UNDERSTOOD', 'SAVED', 'REVIEWING', 'APPLYING', 'APPLIED', 'WAITING', 'SELECTED', 'REJECTED', 'MISSED', 'ARCHIVED')
);

-- ---------------------------------------------------------------------
-- 2. Add opportunity management columns
-- ---------------------------------------------------------------------
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS registration_url VARCHAR(2048);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS source_domain VARCHAR(255);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS deadline_timezone VARCHAR(50);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS eligibility TEXT;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS location VARCHAR(255);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS work_mode VARCHAR(50) DEFAULT 'UNSPECIFIED';
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS estimated_effort VARCHAR(50);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS priority VARCHAR(50) NOT NULL DEFAULT 'NORMAL';
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS priority_reason VARCHAR(255);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS last_status_change_at TIMESTAMPTZ DEFAULT NOW();
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS previous_status VARCHAR(50);
ALTER TABLE opportunities ADD COLUMN IF NOT EXISTS extraction_id UUID REFERENCES ai_extractions(id) ON DELETE SET NULL;

-- ---------------------------------------------------------------------
-- 3. Table: tags (User-owned opportunity tags)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_tags_user_name UNIQUE (user_id, name)
);

-- ---------------------------------------------------------------------
-- 4. Table: opportunity_tags (Many-to-many junction)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS opportunity_tags (
    opportunity_id UUID NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (opportunity_id, tag_id)
);

-- ---------------------------------------------------------------------
-- 5. Table: opportunity_activities (Auditable chronological history)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS opportunity_activities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    opportunity_id UUID NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    action_type VARCHAR(50) NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    description VARCHAR(500),
    metadata TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ---------------------------------------------------------------------
-- 6. Performance indexes & Full-Text Search
-- ---------------------------------------------------------------------
CREATE INDEX IF NOT EXISTS idx_opportunities_user_type ON opportunities(user_id, opportunity_type);
CREATE INDEX IF NOT EXISTS idx_opportunities_user_priority ON opportunities(user_id, priority);
CREATE INDEX IF NOT EXISTS idx_opportunities_user_created ON opportunities(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_opportunities_archived ON opportunities(user_id, archived_at) WHERE archived_at IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_tags_user ON tags(user_id, name);
CREATE INDEX IF NOT EXISTS idx_opportunity_tags_tag ON opportunity_tags(tag_id);
CREATE INDEX IF NOT EXISTS idx_opportunity_activities_opp ON opportunity_activities(opportunity_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_opportunity_activities_user ON opportunity_activities(user_id, created_at DESC);

-- PostgreSQL Full-Text Search GIN index
CREATE INDEX IF NOT EXISTS idx_opportunities_fts ON opportunities
USING gin(to_tsvector('english', coalesce(title, '') || ' ' || coalesce(organization, '') || ' ' || coalesce(description, '')));
