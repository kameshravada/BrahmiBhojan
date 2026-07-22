# 19 - Payments

Status: Refined draft for approval  
Project: BrahmiBhojan  
Last Updated: 2026-07-06

## 1. Purpose

This document defines payment behavior for BrahmiBhojan launch using Razorpay online payments and UPI. Cash on delivery is excluded.

## 2. Launch Payment Modes

| Mode | Status |
| --- | --- |
| UPI | Supported via Razorpay |
| Cards | Supported via Razorpay if enabled |
| Net banking | Supported via Razorpay if enabled |
| Wallets | Supported via Razorpay if enabled |
| Cash on delivery | Not supported |

## 3. Payment Flow

```mermaid
sequenceDiagram
    participant C as Customer
    participant FE as Frontend
    participant API as Backend
    participant RP as Razorpay
    participant DB as PostgreSQL

    C->>FE: Confirm checkout
    FE->>API: Create payment intent
    API->>DB: Create pending order/payment
    API->>RP: Create Razorpay order
    RP->>API: Return provider order id
    API->>FE: Return payment options
    C->>RP: Pay online/UPI
    RP->>FE: Callback result
    FE->>API: Verify payment
    API->>RP: Verify signature/payment
    API->>DB: Mark payment success/failure
    RP->>API: Webhook event
    API->>DB: Idempotent webhook update
```

## 4. Payment Statuses

- CREATED
- PENDING
- SUCCESS
- FAILED
- REFUND_PENDING
- REFUNDED
- PARTIALLY_REFUNDED

## 5. Razorpay Requirements

- Create Razorpay order server-side.
- Never create payment amounts from client totals.
- Verify Razorpay signature.
- Process webhooks idempotently.
- Store provider order ID.
- Store provider payment ID when available.
- Store failure reason where available.
- Support refunds for approved cancellation/return cases.

## 6. Idempotency

Payment operations must protect against:

- Duplicate payment intent requests.
- Duplicate Razorpay callbacks.
- Duplicate webhooks.
- Retry after network failure.

Use provider event ID for webhook idempotency and internal idempotency key for checkout payment intent.

## 7. Refund Rules

Refunds can be initiated only for:

- Approved cancellation with successful payment.
- Approved return where refund applies.
- Admin-approved exception.

Refund actions must be audited.

## 8. Reconciliation

Admin/finance should be able to compare:

- Internal order amount.
- Internal payment status.
- Razorpay payment ID.
- Razorpay settlement/refund state.
- Failure/refund reasons.

## 9. Acceptance Criteria

- COD is not exposed.
- UPI/online payment is supported through Razorpay.
- Webhooks cannot create duplicate payment updates.
- Payment status is separate from order status.
- Refunds are linked to approved business workflows.

