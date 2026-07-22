# 03 - State Management

## State Buckets

- Server state: products, categories, cart snapshot, order history
- Session state: auth tokens, user profile basics
- UI state: modals, toasts, loading states

## Rules

- Prefer server state cache for API-driven data.
- Keep global client state minimal.
- Avoid duplicating derived state across modules.

