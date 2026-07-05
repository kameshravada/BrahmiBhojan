# 16 - Checkout

Status: Draft for approval

## Flow

Cart review -> OTP login -> address -> coupon -> tax/GST calculation -> Razorpay payment -> order confirmation.

## Rules

- Login required.
- Complete address with pincode/locality required.
- No COD at launch.
- Server recalculates all totals.
- Checkout must be idempotent.
- Payment pending/failure/success states must be handled.

## Acceptance Criteria

- Customer cannot checkout as guest.
- Final amount is visible before payment.
- Duplicate clicks do not create duplicate orders.

