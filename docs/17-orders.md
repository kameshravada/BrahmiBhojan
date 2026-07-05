# 17 - Orders

Status: Draft for approval

## Order Statuses

- CREATED
- PAYMENT_PENDING
- PAID
- PACKING
- PACKED
- HANDED_TO_PARTNER
- SHIPPED
- OUT_FOR_DELIVERY
- DELIVERED
- CANCELLED
- RETURN_REQUESTED
- RETURNED
- REFUNDED

## Rules

- Payment status is separate from order status.
- Order stores item and price snapshot.
- Admin updates operational status.
- Delivery partner references are captured when available.

