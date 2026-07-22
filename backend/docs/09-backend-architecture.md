# 09 - Backend Architecture

Status: Refined draft for approval  
Project: BrahmiBhojan  
Last Updated: 2026-07-06

## 1. Purpose

This document defines the backend architecture for BrahmiBhojan before Spring Boot implementation begins. It establishes the architecture style, module boundaries, package direction, dependency rules, transaction strategy, integration points, and operational expectations.

## 2. Architecture Style

BrahmiBhojan will use a modular monolith built with Java 21 and Spring Boot 3.

This means the backend is deployed as one application, but internally organized as independent business modules. The goal is to get production-grade ecommerce correctness without the deployment and data-consistency complexity of microservices.

## 3. Why Modular Monolith

| Option | Benefits | Problems | Decision |
| --- | --- | --- | --- |
| Simple monolith | Fast to build. | Becomes tightly coupled and hard to maintain. | Rejected. |
| Modular monolith | Clear domain boundaries, simpler deployment, strong transactions. | Requires discipline in dependencies. | Chosen. |
| Microservices | Independent scaling and deployments. | Too much operational, data, network, and DevOps complexity for launch. | Future option only. |

## 4. Backend Runtime Stack

| Technology | Responsibility |
| --- | --- |
| Java 21 | Backend language. |
| Spring Boot 3 | Application framework and dependency wiring. |
| Spring Web | REST APIs. |
| Spring Security | Customer/admin security, JWT, authorization. |
| Spring Data JPA | Database persistence. |
| PostgreSQL | Source-of-truth relational database. |
| Flyway | Versioned schema migrations. |
| Redis | OTP, rate limits, cache, temporary state, locks where needed. |
| Maven | Dependency management and build. |
| Docker | Local and production packaging. |

## 5. Module Map

| Module | Responsibilities | Owns Data |
| --- | --- | --- |
| Auth | OTP challenges, JWT, refresh tokens, login/logout, token rotation. | `otp_challenges`, `refresh_tokens` |
| Customer | Customer profile, mobile identity, addresses. | `customers`, `customer_addresses` |
| Catalog | Products, categories, product media, product attributes, visibility. | `products`, `categories`, `product_images`, `product_categories` |
| Pricing | Base price, sale price, offer rules, cart-level tax inputs. | Product price fields, future offer tables |
| Cart | Guest cart, user cart, cart items, cart merge. | `carts`, `cart_items` |
| Checkout | Checkout validation, final totals, address validation, payment initiation. | Does not own long-term entities; orchestrates cart/order/payment |
| Orders | Order creation, order snapshots, status lifecycle. | `orders`, `order_items`, `order_status_history` |
| Inventory | Stock, adjustments, availability, low-stock signals. | `inventory`, `inventory_adjustments` |
| Payments | Razorpay order references, payment attempts, webhooks, refunds. | `payments`, `payment_events`, `refunds` |
| Delivery | Partner handoff, tracking reference, shipment status. | `delivery_shipments`, `delivery_status_history` |
| Returns | Return eligibility, return requests, return decisions. | `return_requests`, product/category eligibility flags |
| Notifications | SMS, WhatsApp, email templates, delivery attempts. | `notification_templates`, `notification_events` |
| Marketing | Coupons, campaigns, automation triggers. | `coupons`, `coupon_redemptions`, `campaigns` |
| Analytics | Event ingestion and reporting facts. | `analytics_events`, derived reporting tables later |
| Admin | Admin users, permissions, operational APIs. | `admin_users`, `roles`, `permissions` |
| Audit | Audit log for sensitive changes. | `audit_logs` |
| CMS | Banners, pages, storefront content blocks. | `cms_pages`, `banners` |
| Shared | Shared exceptions, response models, security utilities, base types. | None |

## 6. Package Direction

Recommended initial package structure:

```text
com.brahmibhojan
  BrahmiBhojanApplication.java
  shared
    config
    error
    web
    security
    validation
    money
    audit
  auth
    api
    application
    domain
    infrastructure
  customer
  catalog
  pricing
  cart
  checkout
  order
  inventory
  payment
  delivery
  returns
  notification
  marketing
  analytics
  admin
  cms
```

Each module should follow this internal shape where useful:

```text
module
  api              REST controllers, request/response DTOs
  application      use cases/services
  domain           entities, value objects, domain rules
  infrastructure   repositories, provider clients, persistence adapters
```

## 7. Dependency Rules

1. Controllers call application services.
2. Controllers must not contain business rules.
3. Application services enforce use cases and transactions.
4. Domain objects enforce local invariants.
5. Infrastructure code talks to databases, Redis, Razorpay, Cloudinary, and notification providers.
6. Modules can depend on `shared`.
7. Cross-module calls must go through application-level services or explicit interfaces.
8. No module should directly mutate another module's owned tables except through approved services.

## 8. Transaction Strategy

| Flow | Transaction Boundary |
| --- | --- |
| OTP verify and account creation | Single transaction for customer creation and refresh token creation after OTP validation. |
| Cart updates | Transaction per cart mutation. |
| Guest cart merge | Single transaction for merge and conflict handling. |
| Checkout validation | Read-only transaction plus inventory/price checks. |
| Order creation | Single transaction to create order snapshot and payment record after validation. |
| Payment webhook | Transaction with idempotency check before state update. |
| Inventory adjustment | Transaction with audit log. |
| Admin updates | Transaction with audit log for critical mutations. |

## 9. Idempotency Requirements

Idempotency is mandatory for:

- OTP request throttling.
- Cart item update retries.
- Checkout payment initiation.
- Order creation.
- Razorpay webhook processing.
- Refund initiation.
- Delivery status update callbacks or manual retries.

Use idempotency keys for client-initiated checkout/order/payment operations. Use provider event IDs for webhook idempotency.

## 10. Security Architecture

| Area | Decision |
| --- | --- |
| Customer login | Mobile OTP only. No password and no social login. |
| Customer API auth | JWT access token plus refresh token. |
| Admin API auth | Separate admin authentication path and role/permission enforcement. |
| Public APIs | Catalog, search, product details, reviews, and public CMS content. |
| Sensitive APIs | Checkout, orders, profile, addresses, admin operations, payments. |
| Secrets | Environment variables or secret manager; never Git. |
| Webhooks | Signature verification and idempotent processing. |

## 11. Integration Boundaries

| Integration | Owning Module | Strategy |
| --- | --- | --- |
| Razorpay | Payments | Provider adapter behind interface. |
| SMS/WhatsApp/Email | Notifications/Auth | Low-cost provider adapters behind interfaces. |
| Cloudinary | Catalog/CMS | Media adapter behind interface. |
| Delivery partners | Delivery | Start manual/admin-driven; later add partner API adapter. |
| Redis | Auth/Cart/Shared | OTP, rate limits, cache, locks where necessary. |

## 12. Error Handling

Use a standard API error response:

```json
{
  "code": "CART_ITEM_UNAVAILABLE",
  "message": "One or more items are unavailable.",
  "fieldErrors": [],
  "traceId": "..."
}
```

Error categories:

- Validation errors.
- Authentication errors.
- Authorization errors.
- Business rule errors.
- Conflict/idempotency errors.
- External provider errors.
- Internal server errors.

## 13. Observability

Backend must support:

- Structured logs.
- Request correlation ID.
- Health endpoint.
- Readiness checks for PostgreSQL and Redis.
- Payment webhook processing logs.
- Admin audit logs.
- Error logging without leaking OTPs, tokens, or PII.

## 14. Future Service Extraction Candidates

The following modules may become separate services only when scale or team boundaries justify it:

- Search.
- Payments.
- Notifications.
- Analytics.
- Delivery integration.
- Catalog media/search indexing.

No extraction should happen before the modular monolith boundaries are proven in production.

## 15. Implementation Acceptance Criteria

- Spring Boot app can be generated with the package structure above.
- Modules are clearly separated.
- Public, customer, and admin APIs have separate security rules.
- OTP-only and no-social-login decisions are enforceable.
- Order/payment flows are designed for idempotency.
- Database ownership is clear enough for Flyway migration planning.

