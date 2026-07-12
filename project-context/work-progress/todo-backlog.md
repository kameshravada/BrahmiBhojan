# TODO Backlog

## Open - High Priority

- [ ] Finalize clean commit slicing: `auth+customer`, `catalog+cart+checkout`, `inventory+payments`, `docs/status`.
- [ ] Confirm backend runs locally from IntelliJ with Maven import after clean IDE re-open.
- [ ] Add payment webhook test case for missing signature header.
- [ ] Add address integration tests for create/update/delete and default-address switching.
- [ ] Add inventory check before cart item quantity updates.

## Open - Medium Priority

- [ ] Add periodic reconciliation job for payment-provider eventual consistency.
- [ ] Add profile-based toggle for seed data runner.
- [x] Add catalog product search ranking tuning.
- [ ] Add onboarding note for Docker + Maven + IntelliJ local setup.

## Open - Low Priority

- [ ] Add dashboard-ready analytics aggregates.
- [ ] Add advanced notification provider abstraction.

## Done

- [x] Architecture and domain planning docs created in `docs/`.
- [x] Working decisions captured in `project-context/decisions.md`.
- [x] Progress baseline captured in `project-context/progress.md`.
- [x] Created structured work-progress tracker in `project-context/work-progress/`.
- [x] Replaced email/password auth with mobile OTP request/verify baseline.
- [x] Added customer profile and address APIs with mobile-authenticated `/api/v1/me` endpoints.
- [x] Implemented refresh token rotation and logout revoke flow.
- [x] Started catalog with public category/product read APIs.
- [x] Added IP/device-aware OTP rate limits backed by Redis counters.
- [x] Added catalog variants and richer filters (`category`, `query`, `unit`, `minPrice`, `maxPrice`).
- [x] Added cart module foundation with guest cart and merge-after-login endpoint.
- [x] Implemented checkout validation and transactional order creation with order item snapshots.
- [x] Added inventory reservation/release/consume flow across checkout and payment lifecycle.
- [x] Added payment order creation endpoint and webhook reconciliation with signature verification and idempotency.
- [x] Added backend CI workflow for test runs on push/PR under `.github/workflows/backend-tests.yml`.
- [x] Added integration tests for invalid webhook signature, payment-failure inventory release, and reservation over-reserve/concurrency.
- [x] Validated local Docker services status for PostgreSQL and Redis.
- [x] Added catalog search ranking options (`relevance`, `price_asc`, `price_desc`, `newest`) with integration coverage.

