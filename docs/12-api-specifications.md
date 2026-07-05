# 12 - API Specifications

Status: Draft for approval

## API Style

REST APIs using JSON over HTTPS.

## Public APIs

- `GET /api/categories`
- `GET /api/products`
- `GET /api/products/{slug}`
- `GET /api/search`
- `GET /api/reviews/products/{productId}`

## Auth APIs

- `POST /api/auth/otp/request`
- `POST /api/auth/otp/verify`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

## Cart APIs

- `GET /api/cart`
- `POST /api/cart/items`
- `PATCH /api/cart/items/{itemId}`
- `DELETE /api/cart/items/{itemId}`
- `POST /api/cart/merge`

## Checkout/Order APIs

- `POST /api/checkout/validate`
- `POST /api/checkout/payment`
- `POST /api/orders`
- `GET /api/orders`
- `GET /api/orders/{orderId}`

## Admin APIs

Admin APIs must be under `/api/admin/**` and require admin authentication.

## API Rules

- Use validation errors with clear field messages.
- Use idempotency keys for payment/order-sensitive operations.
- Never trust client totals; recalculate server-side.
- Use pagination for lists.

