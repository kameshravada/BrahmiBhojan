# 10 - Database Design

Status: Refined draft for approval  
Project: BrahmiBhojan  
Last Updated: 2026-07-06

## 1. Purpose

This document defines the initial PostgreSQL database design direction for BrahmiBhojan. It identifies core entities, ownership, key fields, indexes, constraints, and data rules required before backend implementation.

## 2. Database Technology

| Technology | Decision |
| --- | --- |
| Primary database | PostgreSQL |
| Migration tool | Flyway |
| Cache/temporary store | Redis |
| ORM | Spring Data JPA / Hibernate |

PostgreSQL is the source of truth. Redis must not become the only storage for business-critical records such as orders, payments, inventory, or customer addresses.

## 3. Naming Rules

- Table names use plural snake_case: `customers`, `order_items`.
- Primary key column name: `id`.
- Foreign keys use `<entity>_id`.
- Timestamps: `created_at`, `updated_at`.
- Soft delete where needed: `deleted_at` or status field.
- Money values use integer minor units or `numeric(12,2)`. Final choice must be consistent before implementation.

Recommendation: use `numeric(12,2)` for readability during early development, then revisit if high-volume financial precision demands integer minor units.

## 4. Core Tables

### 4.1 Brand

`brands`

| Column | Notes |
| --- | --- |
| id | Primary key |
| code | Unique business code |
| name | Brand name |
| status | ACTIVE/INACTIVE |
| created_at, updated_at | Audit timestamps |

BrahmiBhojan is the first brand. Brand awareness is included early to avoid future redesign.

### 4.2 Customer

`customers`

| Column | Notes |
| --- | --- |
| id | Primary key |
| mobile_number | Unique, required |
| mobile_verified_at | Required after OTP verification |
| name | Optional |
| email | Optional |
| status | ACTIVE/BLOCKED/DELETED |
| created_at, updated_at | Audit timestamps |

Indexes:

- Unique index on `mobile_number`.
- Optional index on `email`.

### 4.3 Address

`customer_addresses`

| Column | Notes |
| --- | --- |
| id | Primary key |
| customer_id | FK to customers |
| recipient_name | Required |
| recipient_mobile | Required |
| address_line1 | Required |
| address_line2 | Optional |
| landmark | Optional |
| locality | Required where available |
| village_or_town | Required |
| district | Required |
| state | Required |
| country | Default India initially |
| pincode | Required |
| is_default | Boolean |
| status | ACTIVE/INACTIVE |

Address design must support quick-commerce style pincode/locality delivery checks and rural/urban address formats.

### 4.4 Authentication

`otp_challenges`

| Column | Notes |
| --- | --- |
| id | Primary key |
| mobile_number | Required |
| otp_hash | Store hash, never raw OTP |
| purpose | LOGIN |
| status | PENDING/VERIFIED/EXPIRED/BLOCKED |
| expires_at | Required |
| attempts_count | Required |
| created_at, verified_at | Timestamps |

`refresh_tokens`

| Column | Notes |
| --- | --- |
| id | Primary key |
| customer_id | FK nullable if admin tokens separated later |
| token_hash | Required |
| expires_at | Required |
| revoked_at | Nullable |
| device_fingerprint | Optional |
| created_at | Required |

### 4.5 Catalog

`categories`

| Column | Notes |
| --- | --- |
| id | Primary key |
| brand_id | FK to brands |
| parent_id | Self FK for hierarchy |
| name | Required |
| slug | Unique per brand |
| description | Optional |
| image_url | Optional |
| sort_order | Required |
| is_active | Boolean |
| is_returnable_default | Boolean |
| seo_title, seo_description | Optional |

`products`

| Column | Notes |
| --- | --- |
| id | Primary key |
| brand_id | FK to brands |
| name | Required |
| slug | Unique per brand |
| short_description | Optional |
| description | Optional |
| sku | Unique where used |
| pack_size | Required |
| base_price | Required |
| sale_price | Optional |
| is_active | Boolean |
| is_returnable | Nullable override |
| return_window_days | Nullable |
| seo_title, seo_description | Optional |
| created_at, updated_at | Timestamps |

`product_categories`, `product_images`, and optional `product_attributes` support mapping, media, and filters.

### 4.6 Inventory

`inventory`

| Column | Notes |
| --- | --- |
| id | Primary key |
| product_id | FK to products |
| available_quantity | Required |
| reserved_quantity | Default 0 |
| low_stock_threshold | Optional |
| status | IN_STOCK/LOW_STOCK/OUT_OF_STOCK |
| updated_at | Timestamp |

`inventory_adjustments`

Tracks manual changes with reason and admin actor.

### 4.7 Cart

`carts`

| Column | Notes |
| --- | --- |
| id | Primary key |
| customer_id | Nullable for guest cart |
| guest_token_hash | Nullable for guest cart |
| status | ACTIVE/MERGED/ORDERED/EXPIRED |
| created_at, updated_at | Timestamps |

`cart_items`

| Column | Notes |
| --- | --- |
| id | Primary key |
| cart_id | FK to carts |
| product_id | FK to products |
| quantity | Required |
| added_price_snapshot | Optional display/reference only |
| created_at, updated_at | Timestamps |

Server always recalculates current cart price during cart view and checkout.

### 4.8 Coupons

`coupons`

Tracks coupon code, type, discount value, validity, usage limits, minimum order value, active status, and applicable constraints.

`coupon_redemptions`

Tracks customer/order coupon usage.

### 4.9 Orders

`orders`

| Column | Notes |
| --- | --- |
| id | Primary key |
| order_number | Unique customer-facing number |
| customer_id | FK |
| brand_id | FK |
| status | Fulfillment status |
| payment_status | Separate payment status |
| subtotal_amount | Required |
| discount_amount | Required |
| gst_amount | Cart-level GST amount |
| delivery_amount | Required, can be zero |
| total_amount | Required |
| address_snapshot_json | Immutable delivery address snapshot |
| placed_at | Timestamp |
| created_at, updated_at | Timestamps |

`order_items`

Stores product snapshot: product ID, name, SKU, pack size, quantity, unit price, discount, tax allocation if needed, and line total.

`order_status_history`

Tracks status changes, actor, notes, and timestamps.

### 4.10 Payments

`payments`

| Column | Notes |
| --- | --- |
| id | Primary key |
| order_id | FK |
| provider | RAZORPAY |
| provider_order_id | Razorpay order ID |
| provider_payment_id | Razorpay payment ID |
| amount | Required |
| currency | INR |
| method | UPI/CARD/NETBANKING/WALLET/UNKNOWN |
| status | CREATED/PENDING/SUCCESS/FAILED/REFUNDED |
| failure_code, failure_reason | Optional |
| created_at, updated_at | Timestamps |

`payment_events`

Stores webhook event IDs and payload references for idempotency/audit.

`refunds`

Tracks refund amount, status, reason, provider reference, and timestamps.

### 4.11 Delivery

`delivery_shipments`

| Column | Notes |
| --- | --- |
| id | Primary key |
| order_id | FK |
| partner_name | Example: Delhivery |
| tracking_number | Optional |
| status | CREATED/HANDED_TO_PARTNER/SHIPPED/OUT_FOR_DELIVERY/DELIVERED/FAILED |
| handed_to_partner_at | Optional |
| delivered_at | Optional |

`delivery_status_history`

Tracks manual or future integration-driven status updates.

### 4.12 Returns

`return_requests`

Tracks order item return requests, status, reason, admin decision, refund linkage, and timestamps.

Return eligibility is determined by product override first, then category default.

### 4.13 Admin and Audit

`admin_users`, `roles`, `permissions`, `admin_user_roles`, `role_permissions`.

Initial launch may use only admin users, but table design should support future roles.

`audit_logs`

Tracks actor, action, entity type, entity ID, old value, new value, IP/user agent where available, and timestamp.

## 5. Indexing Strategy

Required initial indexes:

- `customers.mobile_number` unique.
- `products(brand_id, slug)` unique.
- `categories(brand_id, slug)` unique.
- `products(is_active)`.
- `inventory(product_id)` unique.
- `carts(customer_id, status)`.
- `carts(guest_token_hash, status)`.
- `orders(customer_id, created_at)`.
- `orders(order_number)` unique.
- `payments(order_id)`.
- `payments(provider_order_id)`.
- `payment_events(provider_event_id)` unique.
- `audit_logs(actor_id, created_at)`.

## 6. Data Integrity Rules

1. Mobile number is unique for customers.
2. Product/category slugs are unique per brand.
3. Order totals are immutable after order placement except status/refund fields.
4. Payment records cannot be deleted after creation.
5. Webhook events are stored idempotently.
6. Inventory cannot go below zero unless an explicit adjustment policy allows it.
7. Admin critical changes require audit logs.
8. No raw OTP or raw refresh token is stored.

## 7. Open Design Point

Money representation must be finalized before migrations:

- `numeric(12,2)` is easier for humans during early development.
- integer minor units are stricter for financial calculations.

Recommendation: use `numeric(12,2)` for launch with careful BigDecimal usage in Java.

## 8. Acceptance Criteria

- Tables cover auth, customer, catalog, cart, checkout/order, payment, delivery, returns, admin, audit, and analytics needs.
- Guest cart and user cart are representable.
- No-COD and Razorpay-first payment design is supported.
- Pincode/locality address design is supported.
- Multi-brand readiness is represented.
- Flyway migrations can be created from this design.

