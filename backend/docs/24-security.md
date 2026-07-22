# 24 - Security

Status: Draft for approval

## Requirements

- OTP rate limiting.
- JWT and refresh token security.
- Admin RBAC.
- Audit logs for sensitive actions.
- Secrets outside Git.
- HTTPS in production.
- Razorpay webhook signature verification.
- Input validation with Zod/frontend and backend validation.
- SQL injection protection through JPA/prepared queries.
- Secure CORS configuration.

## Sensitive Areas

Auth, payments, admin actions, customer PII, addresses, refunds, coupons, inventory adjustments.

