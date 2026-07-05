# BrahmiBhojan Progress

## Current Phase

Documentation-first project setup and architecture planning.

## Completed

- Created `docs/01-vision-and-business-strategy.md` as the first formal architecture document.
- Confirmed planned stack and modular monolith architecture direction.
- Confirmed backend-first initialization strategy after documentation approval.
- Added repository-level project context tracking.

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

- Approve Document 01: Vision & Business Strategy.
- Create Document 02: Product Requirements Document.
- Initialize backend skeleton after the required design gate.
- Create GitHub repository and add it as remote.

