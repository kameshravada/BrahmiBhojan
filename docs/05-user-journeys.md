# 05 - User Journeys

Status: Draft for approval

## Guest Browse to Purchase

1. Guest lands on home/category/product page.
2. Guest searches or filters products.
3. Guest views product detail.
4. Guest adds items to cart.
5. Guest opens checkout.
6. System asks for mobile OTP login.
7. Guest verifies OTP.
8. System creates/logs in account and merges guest cart.
9. Customer adds complete delivery address with pincode/locality.
10. Customer pays online/UPI via Razorpay.
11. System creates order and sends confirmation.

## Repeat Customer Journey

1. Customer visits site.
2. Customer is recognized if session valid, otherwise logs in by OTP.
3. Customer uses search/order history/favorites in future.
4. Customer checks out with saved address.
5. Customer receives order updates.

## Admin Order Fulfillment Journey

1. Admin receives paid order.
2. Admin verifies inventory and packs products.
3. Admin hands shipment to delivery partner.
4. Admin records partner/tracking details where available.
5. Admin updates status: packed, shipped, out for delivery, delivered, failed, cancelled, returned as applicable.

## Return Journey

1. Customer requests return for eligible item/category.
2. System validates return eligibility.
3. Admin reviews request.
4. Admin approves/rejects.
5. Refund is processed if applicable.

## Failure Journeys

- OTP fails: customer retries within allowed limits.
- Payment fails: customer can retry payment.
- Product out of stock: item blocked from checkout.
- Delivery delayed: admin updates status and customer receives notification.

