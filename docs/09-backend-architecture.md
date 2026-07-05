# 09 - Backend Architecture

Status: Draft for approval

## Architecture Style

Use a modular monolith with Spring Boot 3 and Java 21.

## Modules

- Auth
- Customer
- Catalog
- Pricing
- Cart
- Checkout
- Orders
- Inventory
- Payments
- Delivery
- Returns
- Notifications
- Marketing
- Analytics
- Admin
- Audit
- CMS
- Platform/Common

## Principles

- Business logic belongs in services, not controllers.
- Each module owns its domain rules.
- Cross-module calls should be explicit.
- Payment/order operations must be idempotent.
- Admin actions must be audited.
- Future service extraction should be possible for high-scale modules.

## Recommended Package Direction

```text
com.brahmibhojan
  auth
  customer
  catalog
  cart
  checkout
  order
  inventory
  payment
  notification
  admin
  shared
```

