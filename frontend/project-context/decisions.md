# Frontend Decisions

Track key frontend decisions and rationale here.

## D001 - Next.js App Router Baseline

- Date: 2026-07-24
- Decision: Initialize frontend with Next.js App Router + TypeScript + Tailwind CSS.
- Context: Need SEO-ready rendering patterns and scalable route/layout boundaries from day one.
- Alternatives considered: Vite SPA.
- Outcome: Next.js scaffold created under `frontend/`.
- Follow-up actions: Add route groups for storefront and protected user flows.

## D002 - State Ownership Split

- Date: 2026-07-24
- Decision: TanStack Query for server state, Zustand for client UI/session state.
- Context: Ecommerce flows are backend-driven and cache-sensitive.
- Alternatives considered: Redux Toolkit for all state.
- Outcome: Stack selected and dependencies added.
- Follow-up actions: Create query hooks and minimal global stores by feature.

## D003 - Form and Validation Strategy

- Date: 2026-07-24
- Decision: React Hook Form + Zod for form handling and validation.
- Context: OTP, address, and checkout flows need strict validation with low rerender cost.
- Alternatives considered: Formik + Yup.
- Outcome: Dependencies added and architecture documented.
- Follow-up actions: Add reusable form field wrappers in shared UI.
