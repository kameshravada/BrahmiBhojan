# 04 - API Integration

## Backend Base

Backend APIs are served from `backend/` service (default local: `http://localhost:8080`).

## Priority Flows

- Auth OTP request/verify/refresh/logout
- Catalog listing and filters
- Cart add/update/remove/fetch
- Checkout and order creation

## API Guidelines

- Centralize HTTP client config.
- Add auth token injection via interceptor/middleware.
- Normalize error responses for consistent UI handling.

