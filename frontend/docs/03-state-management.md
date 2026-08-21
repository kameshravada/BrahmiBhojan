# 03 - State Management

## State Buckets

- Server state: products, categories, cart snapshot, order history
- Session state: auth tokens, user profile basics
- UI state: modals, toasts, loading states

## Rules

- Prefer server state cache for API-driven data.
- Keep global client state minimal.
- Avoid duplicating derived state across modules.

## Chosen Libraries

- Server state: TanStack Query
- Client/session/UI state: Zustand
- Forms and validation: React Hook Form + Zod

## Ownership Guideline

- Use TanStack Query for products, categories, cart, addresses, orders, payment status.
- Use Zustand for drawer/modal state, filter panel state, auth UI flow flags.
- Use React Hook Form + Zod for OTP forms, address forms, checkout inputs.
