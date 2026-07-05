# 12 - API Specifications

Status: Refined draft for approval  
Project: BrahmiBhojan  
Last Updated: 2026-07-06

## 1. Purpose

This document defines the initial REST API contract direction for BrahmiBhojan. Detailed OpenAPI schemas can be generated from these requirements during implementation.

## 2. API Style

| Area | Decision |
| --- | --- |
| Protocol | HTTPS |
| Style | REST |
| Payload | JSON |
| Auth | Bearer JWT for protected APIs |
| Versioning | `/api/v1` |
| Pagination | Page/size initially |
| Time format | ISO-8601 |
| Currency | INR |

## 3. API Response Envelope

Successful responses may use direct resource payloads. Errors must use a standard structure:

```json
{
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed.",
  "fieldErrors": [
    { "field": "mobileNumber", "message": "Mobile number is required." }
  ],
  "traceId": "..."
}
```

## 4. API Security Classes

| Class | Description | Examples |
| --- | --- | --- |
| Public | No login required. | Product listing, product detail, categories, reviews. |
| Customer | Verified customer JWT required. | Profile, addresses, checkout, orders. |
| Admin | Admin JWT and permission required. | Product admin, orders admin, payments admin. |
| Webhook | Provider signature required. | Razorpay webhook. |

## 5. Public Storefront APIs

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/v1/categories` | List active categories. |
| GET | `/api/v1/categories/{slug}` | Category detail. |
| GET | `/api/v1/products` | Product listing with filters. |
| GET | `/api/v1/products/{slug}` | Product detail. |
| GET | `/api/v1/search` | Product search. |
| GET | `/api/v1/products/{productId}/reviews` | Approved product reviews. |
| GET | `/api/v1/cms/pages/{slug}` | Public CMS page. |
| GET | `/api/v1/banners` | Active storefront banners. |

Product list filters should include category, price range, availability, sort, page, size, and future attributes.

## 6. Authentication APIs

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/v1/auth/otp/request` | Request OTP for mobile login. |
| POST | `/api/v1/auth/otp/verify` | Verify OTP and login/create customer. |
| POST | `/api/v1/auth/refresh` | Rotate access token using refresh token. |
| POST | `/api/v1/auth/logout` | Revoke refresh token. |

No password and no social login APIs are allowed.

## 7. Customer APIs

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/v1/me` | Current customer profile. |
| PATCH | `/api/v1/me` | Update name/email. |
| GET | `/api/v1/me/addresses` | List addresses. |
| POST | `/api/v1/me/addresses` | Create address. |
| PATCH | `/api/v1/me/addresses/{addressId}` | Update address. |
| DELETE | `/api/v1/me/addresses/{addressId}` | Deactivate address. |
| POST | `/api/v1/me/addresses/{addressId}/default` | Set default address. |

## 8. Cart APIs

Cart APIs must support guest and authenticated users.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/v1/cart` | Get current cart. |
| POST | `/api/v1/cart/items` | Add item. |
| PATCH | `/api/v1/cart/items/{itemId}` | Update quantity. |
| DELETE | `/api/v1/cart/items/{itemId}` | Remove item. |
| POST | `/api/v1/cart/merge` | Merge guest cart after login. |
| POST | `/api/v1/cart/coupon` | Apply coupon. |
| DELETE | `/api/v1/cart/coupon` | Remove coupon. |

Guest cart identity should use a secure anonymous cart token, not a plain database ID.

## 9. Checkout APIs

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/v1/checkout/validate` | Validate cart, address, stock, pricing, coupon, GST, and delivery eligibility. |
| POST | `/api/v1/checkout/payment-intent` | Create Razorpay payment order for validated checkout. |
| POST | `/api/v1/checkout/confirm` | Confirm payment and create/finalize order where needed. |

Checkout APIs require customer login. Client totals are never trusted.

## 10. Order APIs

| Method | Endpoint | Purpose |
| --- | --- | --- |
| GET | `/api/v1/orders` | Customer order history. |
| GET | `/api/v1/orders/{orderId}` | Customer order detail. |
| POST | `/api/v1/orders/{orderId}/cancel` | Request cancellation where allowed. |
| POST | `/api/v1/orders/{orderId}/items/{itemId}/return` | Request return where eligible. |

## 11. Payment APIs

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/v1/payments/razorpay/verify` | Verify Razorpay client callback. |
| POST | `/api/v1/webhooks/razorpay` | Razorpay webhook receiver. |

Webhook endpoint must verify signature and process idempotently.

## 12. Admin APIs

All admin APIs are under `/api/v1/admin/**`.

Required groups:

- `/products`
- `/categories`
- `/inventory`
- `/orders`
- `/customers`
- `/coupons`
- `/payments`
- `/refunds`
- `/delivery`
- `/returns`
- `/reviews`
- `/reports`
- `/analytics`
- `/marketing`
- `/notifications`
- `/cms`
- `/admin-users`
- `/roles`
- `/audit-logs`
- `/settings`
- `/system-health`

## 13. Idempotency

Require `Idempotency-Key` header for:

- Checkout payment intent creation.
- Order creation/finalization.
- Refund initiation.
- Delivery status callbacks if automated later.

Idempotency key scope should include customer/admin actor and endpoint.

## 14. Pagination and Sorting

List APIs use:

- `page`
- `size`
- `sort`
- `direction`

Admin list APIs must support filtering by status and date ranges.

## 15. Validation Rules

- Mobile number required for OTP.
- Address required for checkout.
- Pincode required for delivery checks.
- Quantity must be positive.
- Coupon code normalized and validated server-side.
- Admin mutation payloads require strict validation.

## 16. Acceptance Criteria

- API groups map to backend modules.
- Guest browsing APIs are public.
- Checkout and order APIs require customer login.
- Admin APIs are isolated under `/admin`.
- Razorpay webhook is separated from customer APIs.
- No social login or password endpoints exist.

