# Edge Cases and Risks

## Auth

- Case: OTP expires before verification.
  - Impact: User cannot complete login.
  - Mitigation: Clear expiry message and resend flow.
  - Track status: Open

- Case: Multiple OTP requests in short time.
  - Impact: Abuse and SMS cost increase.
  - Mitigation: Redis-based rate limiting and cooldown windows.
  - Track status: Open

- Case: OTP challenge replay with reused challengeId.
  - Impact: Potential unauthorized login attempt reuse.
  - Mitigation: Challenge is deleted immediately after successful verification.
  - Track status: Mitigated

- Case: Brute force attempts from many IPs for one number.
  - Impact: Increased attack window.
  - Mitigation: Add IP/device rate limiting (currently mobile-based cooldown only).
  - Track status: Mitigated (basic)

- Case: Reuse of old refresh token after rotation.
  - Impact: Session hijack persistence risk.
  - Mitigation: Old refresh token is revoked during rotation and rejected on reuse.
  - Track status: Mitigated

- Case: Refresh token stolen from insecure client storage.
  - Impact: Unauthorized long-lived session creation.
  - Mitigation: Move refresh token to secure httpOnly cookie strategy in frontend phase.
  - Track status: Open

## Cart and Checkout

- Case: Item goes out of stock between cart and checkout.
  - Impact: Order failure or mismatch.
  - Mitigation: Re-validate stock at checkout and return precise errors.
  - Track status: Mitigated (basic)

- Case: Price changes after item added to cart.
  - Impact: User trust issue at payment time.
  - Mitigation: Final price lock rules and explicit delta message in checkout.
  - Track status: Open

- Case: Guest cart merge conflicts with existing user cart variant quantities.
  - Impact: Incorrect merged quantity if duplicate variants are not combined correctly.
  - Mitigation: Merge by `productVariantId` and recompute line totals transactionally.
  - Track status: Mitigated

## Catalog

- Case: Product has no active variants.
  - Impact: Product listing/detail can fail at runtime.
  - Mitigation: Service throws explicit server error; add admin validation and seed integrity checks.
  - Track status: Open

- Case: Query filters produce duplicate products due to variant joins.
  - Impact: Incorrect pagination/user experience.
  - Mitigation: Use `distinct` in search query.
  - Track status: Mitigated

## Customer Profile and Addresses

- Case: Two concurrent requests set different addresses as default.
  - Impact: Multiple default addresses for same user.
  - Mitigation: Transactional update path with explicit default reset.
  - Track status: Mitigated (basic)

- Case: Email updated to an already used email.
  - Impact: Identity collision and notification routing issues.
  - Mitigation: Unique constraint and service-level conflict validation.
  - Track status: Mitigated

## Payments

- Case: Payment webhook retry causes duplicate updates.
  - Impact: Double processing risk.
  - Mitigation: Idempotency key and processed-event table.
  - Track status: Mitigated

- Case: Payment success but order update fails.
  - Impact: Reconciliation inconsistency.
  - Mitigation: Retry job + reconciliation task + alerting.
  - Track status: Open

## Platform

- Case: Environment mismatch between local and production.
  - Impact: deployment defects.
  - Mitigation: profile-based config, Docker parity, CI validation.
  - Track status: Open

- Case: Logging sensitive fields by mistake.
  - Impact: security leakage in logs.
  - Mitigation: request/response logger masks OTP, access token, refresh token and authorization fields.
  - Track status: Mitigated

