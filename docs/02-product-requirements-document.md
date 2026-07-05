# BrahmiBhojan Product Requirements Document

## Document Control

| Field | Value |
| --- | --- |
| Project | BrahmiBhojan |
| Document | 02 - Product Requirements Document |
| Status | Approved |
| Owner | Product & Architecture |
| Audience | Founders, product, engineering, QA, design, operations, marketing, support |
| Last Updated | 2026-07-05 |
| Previous Document | 01 - Vision & Business Strategy |

## 1. Purpose

This Product Requirements Document defines what BrahmiBhojan must deliver as a product. It translates the business vision into product capabilities, user-facing behavior, admin workflows, analytics requirements, business rules, release scope, edge cases, and acceptance criteria.

This document does not define database tables, backend package structure, API payloads, infrastructure scripts, or UI implementation details. Those will be specified in later documents.

## 2. Product Summary

BrahmiBhojan is an ecommerce platform for healthy groceries, natural foods, traditional foods, and future product categories. Customers must be able to discover products without friction, maintain a guest cart, authenticate using OTP, complete checkout, pay online, receive notifications, track orders, and return for repeat purchases.

Administrators must be able to manage the business through a secure admin portal covering catalog, categories, inventory, orders, customers, coupons, payments, reports, analytics, marketing, notifications, CMS, roles, permissions, audit logs, settings, and system health.

The product must be built as the first brand on a reusable ecommerce engine that can later support multiple brands.

## 3. Goals

### 3.1 Customer Goals

| Goal | Product Requirement |
| --- | --- |
| Browse without friction | Customers can browse products, categories, prices, offers, reviews, and product details without login. |
| Find products quickly | Customers can search, filter, sort, and navigate categories. |
| Buy with minimal effort | Customers can add products to cart as guests and log in only at checkout. |
| Avoid password friction | Authentication uses mobile OTP only. |
| Trust product quality | Product pages must show clear details, pricing, images, availability, reviews, and relevant attributes. |
| Receive order confidence | Customers receive order, payment, packing, shipping, delivery, and review request notifications. |

### 3.2 Business Goals

| Goal | Product Requirement |
| --- | --- |
| Sell healthy and traditional foods online | Product catalog supports grocery and wellness-oriented categories. |
| Increase repeat purchases | Account, order history, review requests, win-back campaigns, and future reorder flows are supported. |
| Operate reliably | Admin portal supports order, inventory, payment, notification, and customer operations. |
| Measure growth | Analytics cover revenue, orders, conversion, customers, products, campaigns, geography, delivery, cancellations, and returns. |
| Expand over time | Product architecture remains brand-aware and category-extensible. |

### 3.3 Platform Goals

| Goal | Product Requirement |
| --- | --- |
| Support multi-brand future | Core business concepts must allow brand ownership/configuration where relevant. |
| Support SEO growth | Product and category pages must be suitable for search engine indexing. |
| Support secure operations | Admin access requires role-based permissions and auditability. |
| Support scalable delivery | Product, cart, checkout, order, payment, notification, and analytics flows must be clearly separated by responsibility. |

## 4. Non-Goals for Initial Release

The following are not required for the first release unless explicitly reprioritized:

- Native mobile apps.
- Marketplace seller onboarding.
- Subscription ordering.
- Loyalty wallet.
- AI recommendations.
- Multi-warehouse routing.
- Franchise management.
- International commerce.
- Full ERP integration.
- Microservices deployment.

## 5. User Types

| User Type | Description |
| --- | --- |
| Guest visitor | Unauthenticated user browsing products and using guest cart. |
| Customer | Authenticated user with verified mobile number. |
| Admin user | Internal user managing business operations. |
| Catalog manager | Admin user managing products, categories, pricing, media, and SEO content. |
| Inventory manager | Admin user managing stock availability, adjustments, and low-stock monitoring. |
| Order manager | Admin user managing order lifecycle, cancellations, returns, and customer issues. |
| Marketing manager | Admin user managing coupons, campaigns, segments, notifications, and automation. |
| Finance user | Admin user reviewing payments, refunds, reconciliation, revenue, and reports. |
| Support user | Admin user assisting customers with orders, payments, delivery, and account issues. |
| Super admin | Highest-privilege user managing roles, permissions, settings, and critical operations. |

## 6. Customer Product Requirements

### 6.1 Home Page

The home page should help customers quickly discover categories, featured products, offers, and trusted product groups.

Requirements:

- Show brand identity and primary navigation.
- Show major product categories.
- Show featured products, best sellers, new arrivals, and offers where configured.
- Support banners or campaign slots controlled from admin/CMS.
- Provide search access.
- Support responsive mobile-first layout.
- Avoid requiring login for viewing content.

Acceptance criteria:

- A guest can open the home page and navigate to products without logging in.
- Category and product sections can be controlled by admin-configured data in future implementation.
- Home page can support SEO metadata.

### 6.2 Category Browsing

Requirements:

- Customers can browse categories and subcategories.
- Categories support active/inactive visibility.
- Categories support display order.
- Categories support images and SEO content.
- Product listing must show price, offer price if applicable, availability, pack size, and primary image.

Acceptance criteria:

- A guest can browse all visible categories.
- Inactive categories are not visible to customers.
- Category pages can be indexed by search engines.

### 6.3 Product Search

Requirements:

- Customers can search by product name, category, keywords, and relevant product attributes.
- Search must work for guests and authenticated users.
- Search queries must be tracked for analytics.
- Search results should support sorting and filtering.
- Empty search results should show helpful alternatives.

Acceptance criteria:

- A guest can search without login.
- Search terms are captured for reporting.
- Empty results do not break the browsing experience.

### 6.4 Filters and Sorting

Requirements:

- Filter by category, price range, availability, brand, product type, dietary attributes where relevant, and future custom attributes.
- Sort by relevance, price, newest, popularity, and rating where data exists.
- Filters must be shareable through URLs where practical.

Acceptance criteria:

- Customers can refine product lists without logging in.
- Filters do not show unavailable options when data does not support them.
- Filter state remains understandable on mobile.

### 6.5 Product Details

Requirements:

- Product page displays name, images, price, offer price, pack size, stock status, description, ingredients or product details where applicable, usage/storage instructions, reviews, related products, and return/cancellation information.
- Product page must show whether the item can be added to cart.
- Product page must support SEO metadata.
- Product page must preserve product trust by showing clear and accurate information.

Acceptance criteria:

- A guest can view product details without login.
- Unavailable products cannot be added to cart.
- Product page contains enough information for a customer to make a purchase decision.

### 6.6 Reviews and Ratings

Requirements:

- Guests can read approved reviews.
- Authenticated customers can submit reviews for products they purchased.
- Reviews require moderation rules to prevent abuse.
- Reviews should support rating, title, text, and optional media in future.

Acceptance criteria:

- Reviews are visible on product detail pages after approval.
- Customers cannot review products they never purchased unless business policy changes.
- Admin can moderate reviews.

### 6.7 Guest Cart

Requirements:

- Guests can add products to cart without login.
- Guest cart persists for a reasonable duration on the same browser/device.
- Guest cart supports quantity changes and item removal.
- Guest cart must validate price and stock again before checkout.
- Guest cart must merge into user cart after OTP login.

Acceptance criteria:

- Guest can add, update, and remove items.
- Checkout redirects unauthenticated users to OTP login.
- After login, guest cart contents are not lost.

### 6.8 Authentication

Requirements:

- Login uses mobile number and OTP only.
- No password signup or password login is allowed.
- If the mobile number exists, successful OTP verification logs in the customer.
- If the mobile number does not exist, successful OTP verification creates a customer account automatically.
- Mobile number is mandatory.
- Email is optional.
- Name and address are collected progressively.

Acceptance criteria:

- Customer can authenticate without password.
- New account creation is automatic after OTP verification.
- Invalid, expired, or excessive OTP attempts are handled safely.

### 6.9 Profile and Address

Requirements:

- Customer profile starts with verified mobile number.
- Customer can add or update name.
- Customer can add optional email.
- Customer can manage delivery addresses.
- Address must support country, state, district, town/city/village, locality, pincode, address lines, landmark, and recipient details.
- Address data must support geographic analytics.

Acceptance criteria:

- Checkout requires a valid delivery address.
- Email is never mandatory for basic purchase.
- Address fields support both urban and rural delivery formats.

### 6.10 Checkout

Requirements:

- Checkout requires authenticated customer.
- Checkout includes cart review, address selection, pricing summary, coupon application, payment method, and order confirmation.
- System must revalidate products, stock, price, offer, coupon, and delivery eligibility before order placement.
- Checkout must prevent duplicate order creation on repeated clicks or retries.
- Checkout must handle payment success, failure, pending status, and webhook updates.

Acceptance criteria:

- Unauthenticated users cannot place orders.
- Customer sees final payable amount before payment.
- Order is created only when business rules pass.
- Payment and order status are tracked separately.

### 6.11 Orders

Requirements:

- Customer can view order history.
- Customer can view order detail.
- Order detail shows items, amounts, address, payment status, order status, and timeline.
- Customers receive status notifications.
- Cancellation and return rules must be policy-driven.

Acceptance criteria:

- Customer can see current and past orders.
- Order status updates are visible and understandable.
- Payment failure does not appear as a successful order.

## 7. Admin Product Requirements

### 7.1 Admin Dashboard

Requirements:

- Show revenue, orders, customers, AOV, conversion indicators, low-stock alerts, recent orders, payment issues, campaign performance, and system health indicators.
- Dashboard widgets must respect permissions.

Acceptance criteria:

- Admin can quickly understand business and operational health.
- Restricted users do not see unauthorized metrics.

### 7.2 Product Management

Requirements:

- Create, edit, view, publish, unpublish, and archive products.
- Manage product name, slug, description, images, categories, attributes, pack sizes, pricing, tax fields if applicable, inventory linkage, SEO fields, and visibility.
- Support active/inactive status.
- Support future multi-brand product ownership.

Acceptance criteria:

- Admin can create a sellable product with required commerce data.
- Incomplete products cannot be published.
- Product changes are auditable.

### 7.3 Category Management

Requirements:

- Manage hierarchical categories.
- Manage category images, SEO content, display order, and active/inactive status.
- Prevent deletion when products depend on the category unless safe archival is used.

Acceptance criteria:

- Admin can manage category tree without breaking storefront navigation.
- Inactive categories are hidden from customers.

### 7.4 Inventory Management

Requirements:

- Track available stock.
- Support stock adjustments with reason.
- Show low-stock and out-of-stock products.
- Support stock reservation during checkout in later detailed design.
- Maintain audit trail for manual stock changes.

Acceptance criteria:

- Admin can identify low-stock products.
- Customers cannot buy unavailable products.
- Inventory adjustments are auditable.

### 7.5 Order Management

Requirements:

- Admin can view, filter, and search orders.
- Admin can update operational statuses according to allowed transitions.
- Admin can handle cancellation, return, refund initiation, and support notes.
- Admin can view payment status separately from fulfillment status.

Acceptance criteria:

- Invalid order status transitions are blocked.
- Admin can resolve operational exceptions.
- Order changes are auditable.

### 7.6 Customer Management

Requirements:

- Admin can search customers by mobile, name, email, and order history.
- Admin can view customer profile, addresses, orders, reviews, coupons, and communication history where permitted.
- Sensitive customer actions require permissions.

Acceptance criteria:

- Support can assist customers without direct database access.
- Customer data is visible only to authorized roles.

### 7.7 Coupon Management

Requirements:

- Create fixed amount, percentage, free delivery, and campaign coupons where supported by business rules.
- Support validity period, usage limits, minimum order value, applicable products/categories/customers, and active/inactive status.
- Track coupon usage.

Acceptance criteria:

- Invalid coupons cannot be applied.
- Coupon discount is reflected in checkout and analytics.

### 7.8 Payment Management

Requirements:

- View payment attempts, success, failure, pending, refund, and reconciliation status.
- Store payment provider references.
- Support Razorpay webhook-driven updates.
- Finance users can view settlement-relevant reports.

Acceptance criteria:

- Payment status is traceable for every order.
- Refund and failure cases can be investigated.

### 7.9 Marketing and Notifications

Requirements:

- Manage notification templates for email, SMS, and WhatsApp.
- Define automation workflows for welcome, cart abandonment, order confirmation, packed, shipped, delivered, review request, win-back, coupon, festival, birthday, and inactive customers.
- Track campaign performance.
- Respect customer communication consent where applicable.

Acceptance criteria:

- Admin can define campaigns without engineering changes once implemented.
- Transactional notifications are separated from marketing notifications.

### 7.10 Reports and Analytics

Requirements:

- Report revenue, orders, AOV, CLV, repeat customers, cart abandonment, coupon usage, payment modes, top products, low-selling products, most viewed products, search keywords, conversion funnel, traffic sources, geographic revenue, delivery performance, cancellation reasons, return reasons, and campaign performance.
- Reports should support filters by date, geography, category, product, customer segment, and campaign where applicable.

Acceptance criteria:

- Leadership can understand business performance.
- Operations can detect fulfillment and inventory issues.
- Marketing can measure campaign effectiveness.

### 7.11 Roles, Permissions, and Audit Logs

Requirements:

- Admin portal must support role-based access.
- Sensitive operations require explicit permissions.
- Critical actions must be logged with actor, time, target, action, and changed values where practical.

Acceptance criteria:

- A non-authorized admin cannot perform restricted actions.
- Business-critical changes can be audited.

## 8. Analytics Requirements

The product must capture events and business records needed for the following metrics:

| Area | Required Metrics |
| --- | --- |
| Revenue | Gross revenue, net revenue, discounts, refunds, revenue by geography. |
| Orders | Total orders, completed orders, cancelled orders, returned orders, order status distribution. |
| Customers | New customers, repeat customers, customer lifetime value, inactive customers. |
| Cart | Cart additions, cart abandonment, cart value, checkout starts. |
| Coupons | Coupon usage, coupon conversion, discount amount, campaign association. |
| Payments | Payment modes, success rate, failure rate, pending payments, refunds. |
| Products | Top products, low-selling products, most viewed products, out-of-stock products. |
| Search | Search keywords, zero-result searches, search-to-cart conversion. |
| Funnel | Visit, product view, cart add, checkout start, payment start, order success. |
| Traffic | Traffic sources, campaigns, landing pages. |
| Geography | Country, state, district, town, village, locality, pincode. |
| Delivery | Delivery performance, delays, failed delivery reasons. |
| Returns/Cancellations | Reasons, frequency, product/category patterns. |
| Marketing | Campaign sends, opens/clicks where available, conversions, revenue attribution. |

## 9. Marketing Automation Requirements

| Workflow | Trigger | Delay | Conditions | Channels | Exit Conditions |
| --- | --- | --- | --- | --- | --- |
| Welcome | New customer created | Immediate | OTP verified | WhatsApp/SMS/Email | Sent successfully or customer opts out of marketing where applicable. |
| Cart Abandonment | Cart has items and checkout not completed | Configurable | Customer reachable and cart still valid | WhatsApp/SMS/Email | Order placed, cart emptied, product unavailable, campaign expired. |
| Order Confirmation | Order created/payment confirmed as applicable | Immediate | Valid order | WhatsApp/SMS/Email | Message sent. |
| Order Packed | Order status becomes packed | Immediate | Order active | WhatsApp/SMS | Message sent or order cancelled. |
| Order Shipped | Order status becomes shipped | Immediate | Order active | WhatsApp/SMS | Message sent or order cancelled. |
| Delivered | Order status becomes delivered | Immediate | Order active | WhatsApp/SMS/Email | Message sent. |
| Review Request | Order delivered | Configurable | Review not submitted | WhatsApp/Email | Review submitted or request window expired. |
| Win Back | Customer inactive | Configurable | No recent order | WhatsApp/SMS/Email | Customer orders or campaign expires. |
| Coupon Campaign | Campaign starts | Scheduled | Eligible segment | WhatsApp/SMS/Email | Coupon used, expired, or customer excluded. |
| Festival Campaign | Festival campaign schedule | Scheduled | Eligible segment/geography | WhatsApp/SMS/Email | Campaign ends. |
| Birthday Campaign | Customer birthday if collected | Scheduled | Consent and valid date | WhatsApp/SMS/Email | Campaign sent or date passed. |
| Inactive Customers | Inactivity threshold reached | Scheduled | No order in configured period | WhatsApp/SMS/Email | Customer orders or unsubscribe/opt-out applies. |

## 10. Business Rules

1. Login must use mobile OTP only.
2. Password authentication must not be implemented.
3. Mobile number is required for customer identity.
4. Email is optional.
5. Guest browsing must be allowed.
6. Guest cart must be allowed.
7. Checkout requires verified customer login.
8. Guest cart must merge with user cart after successful login.
9. Product price, offer, stock, and coupon must be recalculated at checkout.
10. Orders must store a purchase-time snapshot of item names, prices, discounts, and quantities.
11. Payment status and order fulfillment status must be separate.
12. Admin operations must be permission-controlled.
13. Critical admin changes must be auditable.
14. Analytics must support geographic reporting down to pincode/locality where data exists.
15. Multi-brand readiness must be preserved in product and admin design.

## 11. Edge Cases

| Area | Edge Case | Expected Behavior |
| --- | --- | --- |
| OTP | OTP expired | Customer must request a new OTP. |
| OTP | Too many attempts | Temporarily block or rate-limit attempts. |
| Signup | Mobile number does not exist | Create account after OTP verification. |
| Cart | Product becomes unavailable | Show item as unavailable and block checkout for that item. |
| Cart | Price changes after cart add | Show updated price during cart/checkout recalculation. |
| Cart merge | Same product exists in guest and user cart | Merge according to defined quantity rules in cart design document. |
| Checkout | Coupon expires during checkout | Remove coupon and show updated total. |
| Checkout | Stock runs out during payment | Order/payment recovery flow must be handled in checkout/order/payment designs. |
| Payment | Payment success webhook arrives late | Payment status must update idempotently. |
| Payment | Customer closes payment page | Order remains pending/failed according to payment verification result. |
| Order | Duplicate payment callback | Must not create duplicate orders. |
| Admin | Unauthorized user opens restricted page | Block access and log if necessary. |
| Notification | WhatsApp/SMS fails | Retry or fallback according to notification design. |

## 12. Release Strategy

### 12.1 Foundation Release

- Project structure.
- Documentation set.
- Backend skeleton.
- Frontend skeleton.
- Local Docker services.
- CI baseline.

### 12.2 MVP Commerce Release

- Customer storefront.
- Product browsing.
- Product detail.
- Search/filter basics.
- Guest cart.
- OTP login.
- Address management.
- Checkout.
- Razorpay payment.
- Order creation.
- Basic admin for products, categories, inventory, orders.

### 12.3 Operations Release

- Expanded admin dashboard.
- Coupons.
- Payment reconciliation.
- Notifications.
- Reviews.
- Reports.
- Audit logs.

### 12.4 Growth Release

- Marketing automation.
- Advanced analytics.
- Customer segmentation.
- Campaign reporting.
- CMS improvements.
- Future multi-brand enablement.

## 13. Product Acceptance Criteria

The PRD is approved when:

1. Customer-facing capabilities are clear enough for design and engineering planning.
2. Admin capabilities are clear enough for operational planning.
3. OTP-only authentication is confirmed.
4. Guest browsing and guest cart behavior are confirmed.
5. Checkout login requirement is confirmed.
6. Analytics and marketing automation scope are captured.
7. Business rules and edge cases are documented.
8. Initial release boundaries are understood.
9. The document does not conflict with Document 01.

## 14. Product Decisions Confirmed During Approval

The following decisions were confirmed during PRD approval and must guide later SRS, architecture, database, API, checkout, order, payment, notification, and admin designs.

| Question | Decision |
| --- | --- |
| Initial delivery geography | Customers must provide complete delivery address with pincode/locality, similar to quick-commerce platforms. Delivery eligibility and reporting must be pincode/locality-aware. |
| Launch delivery model | BrahmiBhojan will collect order details, pack products internally, hand shipments to delivery partners such as Delhivery or similar providers, and update order status over time. |
| Payment modes at launch | Cash on delivery is not supported initially. Online payment and UPI are required. Razorpay remains the planned payment gateway. |
| Returns policy | Returns are allowed only for selected products/categories. Admin must be able to configure return eligibility at product and/or category level. |
| Tax/GST | GST will be applied at cart level. Detailed GST calculation rules must be designed in checkout, order, and database documents. |
| SMS, WhatsApp, and email providers | Provider choice should prioritize low cost while keeping the notification architecture provider-replaceable. |
| Admin and storefront frontend boundary | Follow common industry practice: keep customer storefront and admin portal separated by application/route boundary with different access control, navigation, and UX patterns. Final structure will be decided in frontend architecture. |
| Initial admin roles | Initial release starts with admins only. Fine-grained roles and permissions will still be designed for enterprise readiness and future operational growth. |

## 15. Approval Gate

After stakeholder review, one of the following decisions is required:

| Decision | Meaning |
| --- | --- |
| Approved | Proceed to Document 03 - Software Requirements Specification. |
| Approved with changes | Apply requested changes, then proceed after confirmation. |
| Rework required | Revise this document and resubmit for approval. |
