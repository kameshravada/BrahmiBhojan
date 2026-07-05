# 13 - Authentication Design

Status: Draft for approval

## Customer Authentication

OTP-only mobile authentication. No passwords.

## Flow

1. Customer enters mobile number.
2. System creates OTP challenge.
3. OTP sent through SMS/WhatsApp provider.
4. Customer verifies OTP.
5. Existing customer logs in; new customer is created.
6. System issues JWT access token and refresh token.
7. Guest cart is merged after login.

## Security Rules

- OTP expires quickly.
- Rate-limit OTP request and verification.
- Store OTP securely, not as plain text.
- Refresh tokens are revocable.
- Logout revokes refresh token.

