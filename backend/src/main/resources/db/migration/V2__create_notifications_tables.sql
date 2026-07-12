CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY,
    type VARCHAR(40) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    subject VARCHAR(180),
    body VARCHAR(2000) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notification_templates_type_channel_active
    ON notification_templates(type, channel, active);

CREATE TABLE IF NOT EXISTS notification_events (
    id UUID PRIMARY KEY,
    user_id UUID,
    type VARCHAR(40) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    recipient_mobile VARCHAR(16),
    recipient_email VARCHAR(120),
    subject VARCHAR(180),
    message VARCHAR(2000) NOT NULL,
    idempotency_key VARCHAR(180) NOT NULL UNIQUE,
    is_marketing BOOLEAN NOT NULL,
    provider_message_id VARCHAR(120),
    error_message VARCHAR(500),
    retry_count INTEGER NOT NULL,
    max_retries INTEGER NOT NULL,
    next_retry_at TIMESTAMP WITH TIME ZONE,
    last_attempt_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notification_events_user_created
    ON notification_events(user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_notification_events_retry
    ON notification_events(status, next_retry_at);

CREATE TABLE IF NOT EXISTS notification_preferences (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    channel VARCHAR(20) NOT NULL,
    marketing_enabled BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_notification_preferences_user_channel UNIQUE (user_id, channel)
);

CREATE INDEX IF NOT EXISTS idx_notification_preferences_user
    ON notification_preferences(user_id);

