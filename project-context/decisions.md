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

