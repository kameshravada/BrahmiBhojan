# 17 - Orders

Status: Refined draft for approval  
Project: BrahmiBhojan  
Last Updated: 2026-07-06

## 1. Purpose

This document defines order lifecycle, order data snapshots, fulfillment status, delivery partner handoff, cancellation, return links, and admin operations.

## 2. Order Creation

Orders are created from validated checkout. Each order must preserve purchase-time snapshots so later product/price/address edits do not change historical orders.

Snapshot requirements:

- Product name.
- SKU if available.
- Pack size.
- Unit price.
- Quantity.
- Discount.
- GST/cart tax summary.
- Delivery amount.
- Final payable amount.
- Delivery address.

## 3. Order Statuses

| Status | Meaning |
| --- | --- |
| CREATED | Order record created. |
| PAYMENT_PENDING | Awaiting payment confirmation. |
| PAID | Payment confirmed. |
| PACKING | Internal team is preparing items. |
| PACKED | Order is packed. |
| HANDED_TO_PARTNER | Given to delivery partner. |
| SHIPPED | Partner shipment is moving. |
| OUT_FOR_DELIVERY | Delivery attempt in progress. |
| DELIVERED | Customer received order. |
| CANCELLED | Order cancelled. |
| DELIVERY_FAILED | Delivery failed. |
| RETURN_REQUESTED | Customer/admin initiated return. |
| RETURN_APPROVED | Admin approved return. |
| RETURN_REJECTED | Admin rejected return. |
| RETURNED | Item/order returned. |
| REFUNDED | Refund completed where applicable. |

## 4. Payment Statuses

Payment status is separate:

- NOT_INITIATED
- CREATED
- PENDING
- SUCCESS
- FAILED
- REFUND_PENDING
- REFUNDED
- PARTIALLY_REFUNDED

## 5. Status Transition Rules

- Only paid orders can enter packing.
- Delivered orders cannot return to shipped.
- Cancelled orders cannot be fulfilled.
- Refund status does not automatically mean order was returned unless return flow says so.
- Admin status changes require audit log.

## 6. Delivery Partner Handoff

BrahmiBhojan packs internally and then hands to partner such as Delhivery.

Admin must capture:

- Partner name.
- Tracking number/reference where available.
- Handoff timestamp.
- Shipment status.
- Notes/exceptions.

## 7. Cancellation

Cancellation policy should depend on order status:

- Before packing: likely cancellable.
- After handoff: admin decision or partner-dependent.
- Delivered: cancellation not allowed; return flow applies if eligible.

## 8. Returns

Return request is allowed only when product/category is return eligible and within policy window.

Admin decides approval/rejection. Refund is linked through payment/refund module.

## 9. Acceptance Criteria

- Customer can view order history and details.
- Admin can update operational status.
- Status history is preserved.
- Payment and fulfillment states are independent.
- Delivery partner workflow is represented.

