# Working Decisions

This file records project-level working decisions before they are formalized into Architecture Decision Records.

## D001 - Documentation First

Decision: Complete and approve architecture/product documentation before feature implementation.

Reason: BrahmiBhojan is intended to be an enterprise-grade ecommerce platform, so shared understanding must come before code.

## D002 - Backend First

Decision: Start implementation with backend foundation after documentation approval.

Reason: Ecommerce correctness depends on backend-owned rules for authentication, cart, checkout, orders, inventory, payments, and admin permissions.

## D003 - Modular Monolith

Decision: Use a modular monolith instead of microservices for the initial platform.

Reason: This gives clear domain boundaries with simpler deployment, stronger transactional consistency, and lower operational complexity.

## D004 - Multi-Brand Ready

Decision: BrahmiBhojan is the first brand, but the commerce engine must be brand-aware from the beginning.

Reason: Retrofitting multi-brand support later would affect catalog, pricing, orders, analytics, media, admin permissions, and marketing.

## D005 - Launch Delivery Model

Decision: BrahmiBhojan will pack orders internally and hand shipments to delivery partners such as Delhivery or similar providers.

Reason: This lets the business control product packing and quality while avoiding the operational complexity of building an in-house delivery fleet at launch.

## D006 - Launch Payment Modes

Decision: Launch without cash on delivery. Support online payments and UPI through Razorpay.

Reason: Online prepaid payment reduces cash handling, failed doorstep collections, reconciliation complexity, and delivery partner dependency.

## D007 - Configurable Returns

Decision: Returns will be allowed only for selected products/categories, configurable by admin.

Reason: Grocery and food categories have different safety, perishability, and quality constraints, so return eligibility cannot be universal.

## D008 - Cost-Conscious Notification Providers

Decision: Select affordable SMS, WhatsApp, and email providers while keeping provider integrations replaceable.

Reason: Notification costs can grow with OTP, transactional, and marketing volume, so the platform should avoid hard dependency on a costly provider.
