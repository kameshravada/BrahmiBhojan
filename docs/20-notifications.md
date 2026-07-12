# 20 - Notifications

Status: Foundation implemented

## Channels

- SMS
- WhatsApp
- Email

## Provider Strategy

Choose low-cost providers but keep integration replaceable.

Current backend abstraction:

- `SmsGateway`
- `WhatsAppGateway`
- `EmailGateway`

Default provider adapters log payloads for local/dev testing and can be replaced by real providers.

## Notification Types

- OTP
- Order confirmation
- Packed
- Shipped
- Delivered
- Review request
- Cart abandonment
- Win-back
- Festival/coupon campaigns

## Rules

- OTP is transactional and high priority.
- Marketing messages must respect consent/opt-out where applicable.
- Failed notifications should be retryable.

## Implemented Foundation (Backend)

- Notification events persisted in `notification_events`.
- Marketing channel consent persisted in `notification_preferences`.
- Templates persisted in `notification_templates` with seed initializer.
- Retry scheduler processes pending/failed notifications using exponential backoff.

## Current Triggered Events

- Order confirmation after order creation.
- Payment success after webhook reconciliation.
- Payment failed after webhook reconciliation.

## Customer APIs

- `GET /api/v1/me/notifications`
- `PATCH /api/v1/me/notifications/preferences`

