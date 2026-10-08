-- =====================================================================
-- MNESA Database Schema Baseline Migration (V1)
-- =====================================================================

-- Ensure pgcrypto is available for UUID generation
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ---------------------------------------------------------------------
-- 1. Table: app_users
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    password_hash VARCHAR(255),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_app_users_email UNIQUE (email),
    CONSTRAINT chk_app_users_role CHECK (role IN ('USER', 'ADMIN', 'SYSTEM')),
    CONSTRAINT chk_app_users_status CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED'))
);

-- Index on email
CREATE INDEX IF NOT EXISTS idx_app_users_email ON app_users(email);

-- ---------------------------------------------------------------------
-- 2. Table: opportunities
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS opportunities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    organization VARCHAR(255),
    opportunity_type VARCHAR(50) NOT NULL DEFAULT 'OTHER',
    source_url TEXT,
    raw_content TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'CAPTURED',
    deadline_at TIMESTAMPTZ,
    confidence_score NUMERIC(3, 2) DEFAULT 0.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_opportunities_type CHECK (
        opportunity_type IN ('INTERNSHIP', 'JOB', 'HACKATHON', 'SCHOLARSHIP', 'COMPETITION', 'EVENT', 'CONFERENCE', 'COURSE', 'OTHER')
    ),
    CONSTRAINT chk_opportunities_status CHECK (
        status IN ('CAPTURED', 'PROCESSING', 'UNDERSTOOD', 'SAVED', 'ACTED', 'COMPLETED', 'ARCHIVED')
    ),
    CONSTRAINT chk_opportunities_confidence CHECK (
        confidence_score >= 0.00 AND confidence_score <= 1.00
    )
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_opportunities_user_id ON opportunities(user_id);
CREATE INDEX IF NOT EXISTS idx_opportunities_user_status ON opportunities(user_id, status);
CREATE INDEX IF NOT EXISTS idx_opportunities_deadline ON opportunities(user_id, deadline_at);

-- ---------------------------------------------------------------------
-- 3. Table: reminders
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reminders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    opportunity_id UUID NOT NULL REFERENCES opportunities(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    scheduled_at TIMESTAMPTZ NOT NULL,
    cadence_type VARCHAR(50) NOT NULL DEFAULT 'STANDARD',
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_reminders_status CHECK (
        status IN ('PENDING', 'SENT', 'DISMISSED', 'FAILED')
    ),
    CONSTRAINT chk_reminders_cadence CHECK (
        cadence_type IN ('IMMEDIATE', 'ONE_WEEK_BEFORE', 'THREE_DAYS_BEFORE', 'ONE_DAY_BEFORE', 'CUSTOM')
    )
);

-- Indexes for reminder dispatch
CREATE INDEX IF NOT EXISTS idx_reminders_scheduled_status ON reminders(status, scheduled_at);
CREATE INDEX IF NOT EXISTS idx_reminders_opportunity_id ON reminders(opportunity_id);
CREATE INDEX IF NOT EXISTS idx_reminders_user_id ON reminders(user_id);
