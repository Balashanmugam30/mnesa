-- =====================================================================
-- MNESA Database Schema Migration (V2) - Authentication & User Identity
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Table: auth_identities (Multi-provider authentication)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS auth_identities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    identity_type VARCHAR(50) NOT NULL,
    identifier VARCHAR(255) NOT NULL,
    credential_hash VARCHAR(255),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_auth_identities_type_identifier UNIQUE (identity_type, identifier),
    CONSTRAINT chk_auth_identities_type CHECK (
        identity_type IN ('EMAIL_PASSWORD', 'GOOGLE_OIDC', 'APPLE_OIDC')
    )
);

CREATE INDEX IF NOT EXISTS idx_auth_identities_user_id ON auth_identities(user_id);
CREATE INDEX IF NOT EXISTS idx_auth_identities_identifier ON auth_identities(identifier);

-- ---------------------------------------------------------------------
-- 2. Table: user_preferences (Interests, notification settings)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    interests TEXT[] NOT NULL DEFAULT '{}',
    reminder_timing VARCHAR(50) NOT NULL DEFAULT 'STANDARD',
    email_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    push_notifications_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_user_preferences_user_id UNIQUE (user_id),
    CONSTRAINT chk_user_preferences_timing CHECK (
        reminder_timing IN ('IMMEDIATE', 'STANDARD', 'AGGRESSIVE', 'CUSTOM')
    )
);

CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id);

-- ---------------------------------------------------------------------
-- 3. Table: device_installations (Device tracking & Push Tokens)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS device_installations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    installation_id VARCHAR(255) NOT NULL,
    platform VARCHAR(50) NOT NULL DEFAULT 'ANDROID',
    push_token VARCHAR(512),
    app_version VARCHAR(50),
    device_model VARCHAR(100),
    os_version VARCHAR(50),
    last_active_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_device_installations_install_id UNIQUE (installation_id),
    CONSTRAINT chk_device_installations_platform CHECK (
        platform IN ('ANDROID', 'WEB', 'IOS')
    )
);

CREATE INDEX IF NOT EXISTS idx_device_installations_user_id ON device_installations(user_id);
CREATE INDEX IF NOT EXISTS idx_device_installations_last_active ON device_installations(last_active_at);

-- ---------------------------------------------------------------------
-- 4. Table: refresh_tokens (Session rotation & revocation)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    device_id VARCHAR(255),
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_refresh_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_lookup ON refresh_tokens(token_hash, revoked, expires_at);

-- ---------------------------------------------------------------------
-- 5. Table: password_reset_tokens
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_password_reset_tokens_hash UNIQUE (token_hash)
);

CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_tokens_lookup ON password_reset_tokens(token_hash, used, expires_at);
