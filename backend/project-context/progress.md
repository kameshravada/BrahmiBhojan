# BrahmiBhojan Progress

## Current Phase

Backend implementation hardening and integration stabilization.

## Completed

- Created `docs/01-vision-and-business-strategy.md` as the first formal architecture document.
- Proceeded from Document 01 to Document 02 after user approval.
- Created `docs/02-product-requirements-document.md` as the second formal architecture document.
- Approved Document 02 after resolving PRD open questions.
- Created draft documents 03 through 29 in a batch at user request to reduce approval lag.
- Confirmed planned stack and modular monolith architecture direction.
- Confirmed backend-first initialization strategy after documentation approval.
- Added repository-level project context tracking.
- Added structured work progress tracking under `project-context/work-progress/`.
- Created GitHub repository remote and pushed `main`.
- Replaced initial email/password auth scaffold with mobile OTP request/verify auth baseline.
- Added customer profile and address APIs under `/api/v1/me` with optional email profile updates.
- Added refresh token rotation and logout revoke flow with persisted hashed refresh tokens.
- Started catalog module with public read APIs for categories and products.
- Added IP/device-aware OTP rate limiting and request-response logging with sensitive payload masking.
- Added catalog product variants, richer filters, and startup seed data.
- Added cart module foundation with guest cart and merge-after-login support.
- Added checkout validation and order creation transaction with cart-to-order snapshotting.
- Fixed Maven/JDK local test setup and verified full backend test suite successfully.
- Added inventory reservation model and service flow (reserve on order creation, consume on payment success, release on payment failure).
- Added payment order creation and webhook reconciliation with signature verification and duplicate-event handling.
- Added integration coverage for payment success/idempotency, invalid webhook signature, payment failure release, and inventory over-reserve/concurrency checks.
- Added GitHub Actions workflow to run backend tests on push/PR for backend changes.

## Current Decisions

- Use a modular monolith backend with Java 21 and Spring Boot 3.
- Use Next.js App Router for the frontend.
- Use OTP-only authentication; no passwords.
- Allow guest browsing and guest cart.
- Require login only at checkout.
- Use PostgreSQL as the source of truth and Redis for temporary/performance-sensitive flows.
- Use Cloudinary for media and Razorpay for payments.
- Start deployment strategy with Docker, GitHub Actions, Nginx, and Ubuntu VPS.

## Pending

- Review and refine draft documents 03 through 29.
- Split current backend changes into clean logical commits and raise PR.
- Validate local IntelliJ Maven import flow on a clean project re-open.
