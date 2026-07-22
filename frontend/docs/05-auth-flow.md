# 05 - Auth Flow

## Current Backend Contract

- `POST /api/v1/auth/otp/request`
- `POST /api/v1/auth/otp/verify`
- `POST /api/v1/auth/refresh-token`
- `POST /api/v1/auth/logout`

## UI Flow

1. User enters mobile number.
2. UI requests OTP challenge.
3. User enters OTP.
4. UI verifies OTP and stores access/refresh tokens.
5. UI refreshes token silently as needed.
6. Logout revokes refresh token and clears local session.

## Dev Note

OTP is currently console-logged in backend for manual test entry.

