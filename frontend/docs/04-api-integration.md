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

## Implementation Direction

- API client: Axios instance in `src/lib/api/client.ts`.
- Base URL: `NEXT_PUBLIC_API_BASE_URL`.
- Error normalization: map backend error envelope to UI-safe message shape.
- Auth integration: request interceptor for access token and refresh retry policy.
