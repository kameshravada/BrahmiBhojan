# 15 - Cart System

Status: Refined draft for approval  
Project: BrahmiBhojan  
Last Updated: 2026-07-06

## 1. Purpose

This document defines guest cart, customer cart, cart merge, cart calculations, and cart validation behavior.

## 2. Cart Types

| Cart Type | Owner | Use |
| --- | --- | --- |
| Guest cart | Anonymous browser/device token | Browsing and adding items before login. |
| Customer cart | Authenticated customer | Persistent cart after login. |

## 3. Guest Cart Identity

Guest carts should be identified by a secure random cart token. Store only a hash of the guest token in the database if persisted server-side.

Guest cart must not expose database IDs as identity.

## 4. Cart Item Rules

- Quantity must be greater than zero.
- Product must be active.
- Product must be visible to customers.
- Product must have available stock to proceed to checkout.
- Cart may contain temporarily unavailable items, but checkout must block them.
- Cart should show current price, not only added-time price.

## 5. Cart Calculations

Backend calculates:

- Item subtotal.
- Product discount.
- Coupon discount.
- Cart-level GST.
- Delivery charges if applicable.
- Payable total.

Client totals are display-only and never trusted.

## 6. Cart Merge Rules

When customer logs in:

1. Locate active guest cart by guest token.
2. Locate or create active customer cart.
3. For same product/variant, combine quantities.
4. Cap quantity by stock and business limits.
5. Preserve valid coupon only if still eligible.
6. Mark guest cart as `MERGED`.
7. Return merged cart with warnings for changed/unavailable items.

## 7. Cart Statuses

- ACTIVE
- MERGED
- ORDERED
- EXPIRED

## 8. Edge Cases

| Case | Expected Behavior |
| --- | --- |
| Product inactive | Keep item visible with unavailable warning; block checkout. |
| Price changed | Show updated price before checkout. |
| Stock reduced | Cap or block quantity during validation. |
| Coupon invalid after merge | Remove coupon and show warning. |
| Guest token missing | Create new guest cart. |

## 9. Acceptance Criteria

- Guest can add items before login.
- Customer cart survives login sessions.
- Merge does not lose items silently.
- Checkout always validates current price/stock/coupon/GST.

