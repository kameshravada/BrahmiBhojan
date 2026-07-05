# 10 - Database Design

Status: Draft for approval

## Database

PostgreSQL is the source of truth. Flyway manages schema migrations.

## Core Data Areas

- brands
- customers
- customer_addresses
- otp_challenges
- refresh_tokens
- products
- categories
- product_categories
- product_images
- inventory
- carts
- cart_items
- coupons
- orders
- order_items
- payments
- refunds
- delivery_shipments
- returns
- reviews
- notifications
- campaigns
- analytics_events
- admin_users
- roles
- permissions
- audit_logs

## Design Rules

- Orders store immutable purchase snapshots.
- Payment status and order status are separate.
- Return eligibility is configurable at product/category level.
- Address supports pincode/locality and rural/urban formats.
- Audit logs capture critical admin mutations.
- Brand awareness is included early where relevant.

