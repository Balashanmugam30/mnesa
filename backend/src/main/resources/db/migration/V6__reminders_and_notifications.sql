-- =====================================================================
-- MNESA Database Schema Migration (V6) - Reminders, Notifications & Intelligence
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Evolve table: reminders
-- ---------------------------------------------------------------------

-- Drop existing check constraints from V1 to allow rich statuses and cadences
ALTER TABLE reminders DROP CONSTRAINT IF EXISTS chk_reminders_status;
ALTER TABLE reminders DROP CONSTRAINT IF EXISTS chk_reminders_cadence;

-- Add new columns for Phase 07 smart reminders, timezones and snoozing
ALTER TABLE reminders ADD COLUMN IF NOT EXISTS title VARCHAR(255) NOT NULL DEFAULT 'Opportunity Reminder';
ALTER TABLE reminders ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE reminders ADD COLUMN IF NOT EXISTS reminder_type VARCHAR(50) NOT NULL DEFAULT 'CUSTOM';
ALTER TABLE reminders ADD COLUMN IF NOT EXISTS target_timezone VARCHAR(64) NOT NULL DEFAULT 'UTC';
ALTER TABLE reminders ADD COLUMN IF NOT EXISTS snooze_until TIMESTAMPTZ;
ALTER TABLE reminders ADD COLUMN IF NOT EXISTS snooze_count INT NOT NULL DEFAULT 0;
ALTER TABLE reminders ADD COLUMN IF NOT EXISTS smart_reason VARCHAR(255);

-- Apply updated constraints
ALTER TABLE reminders ADD CONSTRAINT chk_reminders_status CHECK (
    status IN ('SCHEDULED', 'PENDING', 'CLAIMED', 'TRIGGERED', 'SENT', 'SNOOZED', 'CANCELLED', 'DISMISSED', 'FAILED')
);

ALTER TABLE reminders ADD CONSTRAINT chk_reminders_type CHECK (
    reminder_type IN ('PREPARATION', 'APPROACHING_DEADLINE', 'FINAL_HOURS', 'CUSTOM', 'STANDARD', 'ONE_WEEK_BEFORE', 'THREE_DAYS_BEFORE', 'ONE_DAY_BEFORE', 'IMMEDIATE')
);

-- Optimize indexes for querying and poller claiming
CREATE INDEX IF NOT EXISTS idx_reminders_user_status ON reminders(user_id, status);
CREATE INDEX IF NOT EXISTS idx_reminders_user_scheduled ON reminders(user_id, scheduled_at ASC);
CREATE INDEX IF NOT EXISTS idx_reminders_poller ON reminders(status, scheduled_at, snooze_until);

-- ---------------------------------------------------------------------
-- 2. Table: notification_records (Audit & Delivery History)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notification_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    reminder_id UUID REFERENCES reminders(id) ON DELETE SET NULL,
    opportunity_id UUID REFERENCES opportunities(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    body TEXT NOT NULL,
    channel VARCHAR(50) NOT NULL DEFAULT 'PUSH',
    provider VARCHAR(50) NOT NULL DEFAULT 'MOCK_DEVELOPMENT',
    delivery_status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    attempt_count INT NOT NULL DEFAULT 0,
    last_attempt_at TIMESTAMPTZ,
    last_error TEXT,
    deep_link_uri VARCHAR(512),
    metadata_json TEXT,
    opened_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_notifications_channel CHECK (
        channel IN ('PUSH', 'IN_APP', 'EMAIL')
    ),
    CONSTRAINT chk_notifications_status CHECK (
        delivery_status IN ('PENDING', 'DELIVERED', 'FAILED', 'OPENED')
    )
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_created ON notification_records(user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_delivery_status ON notification_records(delivery_status, created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_reminder_id ON notification_records(reminder_id);

-- ---------------------------------------------------------------------
-- 3. Update table: user_preferences (Timezone)
-- ---------------------------------------------------------------------
ALTER TABLE user_preferences ADD COLUMN IF NOT EXISTS timezone VARCHAR(64) NOT NULL DEFAULT 'UTC';
