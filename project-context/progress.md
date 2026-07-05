# BrahmiBhojan Progress

## Current Phase

Documentation-first project setup and architecture planning.

## Completed

- Created `docs/01-vision-and-business-strategy.md` as the first formal architecture document.
- Proceeded from Document 01 to Document 02 after user approval.
- Created `docs/02-product-requirements-document.md` as the second formal architecture document.
- Approved Document 02 after resolving PRD open questions.
- Created draft documents 03 through 29 in a batch at user request to reduce approval lag.
- Confirmed planned stack and modular monolith architecture direction.
- Confirmed backend-first initialization strategy after documentation approval.
- Added repository-level project context tracking.
- Created GitHub repository remote and pushed `main`.

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
- Initialize backend skeleton after the required design gate.
