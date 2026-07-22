# 03 - Software Requirements Specification

Status: Draft for approval  
Project: BrahmiBhojan  
Last Updated: 2026-07-05

## Purpose

This SRS converts the approved PRD into software-level requirements for implementation planning. It defines what the system must do, how it must behave under normal and exceptional conditions, and the quality attributes expected from the production platform.

## System Scope

BrahmiBhojan consists of a customer storefront, admin portal, backend API, relational database, cache, payment integration, media storage, notification integrations, analytics layer, deployment pipeline, and operational monitoring.

## Functional Requirements

| ID | Area | Requirement | Priority |
| --- | --- | --- | --- |
| SRS-AUTH-001 | Auth | System shall authenticate customers using mobile OTP only. | Must |
| SRS-AUTH-002 | Auth | System shall automatically create customer account after successful OTP verification if mobile number does not exist. | Must |
| SRS-AUTH-003 | Auth | System shall issue short-lived JWT access tokens and refresh tokens after login. | Must |
| SRS-AUTH-004 | Auth | System shall rate-limit OTP generation and verification attempts. | Must |
| SRS-CAT-001 | Catalog | System shall allow guests to browse categories, products, pricing, offers, reviews, and product details. | Must |
| SRS-CAT-002 | Catalog | System shall support product/category active and inactive states. | Must |
| SRS-CAT-003 | Catalog | System shall support SEO-friendly product and category pages. | Must |
| SRS-SEARCH-001 | Search | System shall allow product search by name, category, keywords, and attributes. | Must |
| SRS-CART-001 | Cart | System shall support guest cart before login. | Must |
| SRS-CART-002 | Cart | System shall merge guest cart into user cart after OTP login. | Must |
| SRS-CART-003 | Cart | System shall recalculate price, offer, tax, stock, and coupon before checkout. | Must |
| SRS-CHECKOUT-001 | Checkout | System shall require verified login for checkout. | Must |
| SRS-CHECKOUT-002 | Checkout | System shall require complete delivery address with pincode/locality. | Must |
| SRS-CHECKOUT-003 | Checkout | System shall apply GST at cart level. | Must |
| SRS-PAY-001 | Payments | System shall support Razorpay online payment and UPI. | Must |
| SRS-PAY-002 | Payments | System shall not support cash on delivery at launch. | Must |
| SRS-PAY-003 | Payments | System shall process Razorpay webhooks idempotently. | Must |
| SRS-ORDER-001 | Orders | System shall create orders with item, price, discount, tax, and address snapshot. | Must |
| SRS-ORDER-002 | Orders | System shall track payment status separately from order fulfillment status. | Must |
| SRS-DEL-001 | Delivery | System shall support packing orders internally and handing them to delivery partners. | Must |
| SRS-DEL-002 | Delivery | System shall allow admin to update delivery status over time. | Must |
| SRS-RET-001 | Returns | System shall allow admin-configurable return eligibility by product/category. | Must |
| SRS-ADMIN-001 | Admin | System shall provide secure admin access. | Must |
| SRS-ADMIN-002 | Admin | System shall support product, category, inventory, order, customer, coupon, payment, report, marketing, notification, CMS, setting, and audit management. | Must |
| SRS-AN-001 | Analytics | System shall capture ecommerce, customer, product, search, cart, checkout, payment, geography, delivery, return, cancellation, and campaign metrics. | Must |
| SRS-NOTIF-001 | Notifications | System shall support replaceable SMS, WhatsApp, and email providers. | Must |

## Non-Functional Requirements

| ID | Requirement |
| --- | --- |
| NFR-PERF-001 | Product listing and product detail pages should respond quickly under normal traffic and use caching where safe. |
| NFR-SEC-001 | Customer and admin APIs must be protected against unauthorized access. |
| NFR-SEC-002 | Secrets must never be committed to Git. |
| NFR-OBS-001 | Production services must expose logs and health checks. |
| NFR-REL-001 | Payment and order workflows must be idempotent. |
| NFR-ACC-001 | Customer-facing UI must follow accessibility basics for keyboard navigation, contrast, labels, and responsive layout. |
| NFR-SEO-001 | Public product/category pages must support metadata, canonical URLs, and crawlable content. |
| NFR-MAINT-001 | Backend modules must be separated by business capability. |

## Error Handling

- Invalid OTP: show clear retry message.
- Expired OTP: require new OTP.
- Product unavailable: block checkout for that item.
- Payment pending: keep order/payment in pending state until verified.
- Webhook duplicate: ignore safely after idempotency check.
- Admin unauthorized: return forbidden and log critical access attempts where appropriate.

## Traceability

This SRS maps to PRD goals for guest commerce, OTP login, checkout, Razorpay payments, delivery partner model, configurable returns, GST cart calculation, admin operations, analytics, notifications, and multi-brand readiness.

## Acceptance Criteria

- Every PRD capability has a software-level requirement.
- Requirements are testable.
- No password-based login is included.
- COD is excluded from launch.
- Delivery partner workflow is included.
- Returns are configurable.

