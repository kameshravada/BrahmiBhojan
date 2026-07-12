# Task Log

Use one entry per completed task.

## Entry Template

### [TASK-ID] Title
- Date:
- Module:
- Status: Done
- What was implemented:
- Files changed:
- Design/approach:
- Tests run:
- Notes:
- Follow-up TODOs:

---

## Initial Entries

### [NOTIF-001] Notification service foundation with transactional triggers
- Date: 2026-07-13
- Module: notifications, checkout, payments
- Status: Done
- What was implemented: Added full notifications foundation with channel/provider abstraction (SMS, WhatsApp, Email), template rendering and seed data, persistent event log, user marketing preference API, retry scheduler, and transactional triggers on order creation/payment outcomes.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/notifications/**`, `backend/src/main/java/com/brahmibhojan/modules/checkout/service/CheckoutService.java`, `backend/src/main/java/com/brahmibhojan/modules/payments/service/PaymentService.java`, `backend/src/main/resources/application*.properties`, `backend/src/test/java/com/brahmibhojan/modules/notifications/NotificationControllerIntegrationTest.java`, `backend/src/test/java/com/brahmibhojan/modules/checkout/CheckoutControllerIntegrationTest.java`, `backend/src/test/java/com/brahmibhojan/modules/payments/PaymentWorkflowIntegrationTest.java`, `docs/20-notifications.md`
- Design/approach: Keep gateway interfaces provider-agnostic, persist all outbound events for observability and retries, and attach notification calls to existing transactional domain flows with non-blocking failure handling.
- Tests run: `mvn -B test` (PASS: 19 tests, 0 failures, 0 errors).
- Notes: Default provider adapters currently log payloads for local validation; production provider wiring can replace these adapters without service-layer changes.
- Follow-up TODOs: Add delivery status notifications (`packed`, `shipped`, `delivered`) and campaign/abandonment flows.

### [DEVX-001] Developer onboarding guide and sanity script
- Date: 2026-07-13
- Module: devops, documentation
- Status: Done
- What was implemented: Added a full developer setup guide for Docker + Maven + IntelliJ and a PowerShell sanity script for local environment validation.
- Files changed: `docs/00-dev-environment-setup.md`, `scripts/dev-sanity-check.ps1`, `README.md`, `backend/README.md`, `project-context/work-progress/edge-cases-and-risks.md`
- Design/approach: Keep one canonical setup guide, add a script for fast readiness checks, and link all major READMEs to reduce onboarding drift.
- Tests run: `scripts/dev-sanity-check.ps1` (PASS: Java/Docker/Maven checks).
- Notes: Manual IntelliJ clean-machine import verification is still tracked as a final closure step.
- Follow-up TODOs: Run one clean IDE reopen/import pass and mark the blocker resolved.

### [CATALOG-003] Profile-based catalog seed data toggle
- Date: 2026-07-13
- Module: catalog, config
- Status: Done
- What was implemented: Added config-driven guard for catalog seed runner using `catalog.seed.enabled`; set development/test enabled and production disabled.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/catalog/service/CatalogSeedDataInitializer.java`, `backend/src/main/resources/application.properties`, `backend/src/main/resources/application-dev.properties`, `backend/src/main/resources/application-prod.properties`, `backend/src/test/resources/application-test.yml`
- Design/approach: Use `@ConditionalOnProperty` to keep seeding behavior explicit per profile while preserving backward compatibility with default enabled behavior.
- Tests run: `mvn -B test` (PASS).
- Notes: Production profile now avoids accidental sample data injection unless explicitly enabled via environment override.
- Follow-up TODOs: Add onboarding note documenting how to override `CATALOG_SEED_ENABLED` during local setup.

### [CATALOG-002] Catalog search ranking tuning
- Date: 2026-07-13
- Module: catalog
- Status: Done
- What was implemented: Added sort-aware catalog listing with `relevance`, `price_asc`, `price_desc`, and `newest`; tuned relevance ordering for exact/prefix/contains matching on product name and slug.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/catalog/controller/CatalogController.java`, `backend/src/main/java/com/brahmibhojan/modules/catalog/service/CatalogService.java`, `backend/src/main/java/com/brahmibhojan/modules/catalog/repository/ProductRepository.java`, `backend/src/test/java/com/brahmibhojan/modules/catalog/CatalogControllerIntegrationTest.java`
- Design/approach: Keep ranking implementation inside repository query ordering while validating incoming sort values in service to enforce stable fallback behavior.
- Tests run: `mvn -B test` (PASS: 18 tests).
- Notes: Price sorts use minimum available variant price per product; unknown sort values fallback to `relevance`.
- Follow-up TODOs: Add popularity/rating-backed sort modes once product engagement metrics and rating module are available.

### [PAYMENT-INV-001] Inventory + payment reconciliation hardening and CI test workflow
- Date: 2026-07-13
- Module: inventory, payments, checkout, devops
- Status: Done
- What was implemented: Added inventory reserve/consume/release flow integration with checkout and payment webhook lifecycle; added webhook signature verification and duplicate event handling; added CI workflow for backend tests on push/PR.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/inventory/service/InventoryService.java`, `backend/src/main/java/com/brahmibhojan/modules/payments/service/PaymentService.java`, `backend/src/test/java/com/brahmibhojan/modules/payments/PaymentWorkflowIntegrationTest.java`, `backend/src/test/java/com/brahmibhojan/modules/inventory/InventoryServiceIntegrationTest.java`, `.github/workflows/backend-tests.yml`
- Design/approach: Keep payment webhook idempotent and deterministic while inventory transitions from ACTIVE reservation to CONSUMED/RELEASED states based on final payment status.
- Tests run: `mvn test` (PASS: 8 tests before this task); targeted follow-up suite run planned after new test additions.
- Notes: Local Docker services are running for PostgreSQL/Redis; Maven CLI validate succeeds via workspace Maven.
- Follow-up TODOs: Finalize clean commit slicing by module buckets and confirm IntelliJ Maven import stability on a clean reopen.

### [CHECKOUT-001] Checkout validation and transactional order creation
- Date: 2026-07-12
- Module: checkout, orders
- Status: Done
- What was implemented: Added checkout validation endpoint and transactional order creation endpoint; introduced `Order` and `OrderItem` entities with status and payment status; cart is moved to `CHECKED_OUT` after successful order creation.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/checkout/service/CheckoutService.java`, `backend/src/main/java/com/brahmibhojan/modules/checkout/controller/CheckoutController.java`, `backend/src/main/java/com/brahmibhojan/modules/orders/model/Order.java`, `backend/src/main/java/com/brahmibhojan/modules/orders/model/OrderItem.java`
- Design/approach: Validate cart + address + variant availability before order placement and snapshot final prices into order items for audit consistency.
- Tests run: Full suite via local Maven + JDK 21: `mvn test` (PASS: 7 tests).
- Notes: Local Maven binary is now available at `tools/apache-maven-3.9.9/bin/mvn.cmd` for stable CLI runs.
- Follow-up TODOs: Integrate inventory reservation and payment gateway order creation during checkout.

### [HARDENING-001] IP/device OTP controls + cart and catalog foundations
- Date: 2026-07-12
- Module: auth, catalog, cart
- Status: Done
- What was implemented: Added IP/device-aware OTP rate limiting in Redis; introduced refresh-token lifecycle; expanded catalog with product variants, seed data, and richer filters; implemented cart foundation with guest cart and post-login merge endpoint.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/auth/service/OtpService.java`, `backend/src/main/java/com/brahmibhojan/modules/catalog/service/CatalogService.java`, `backend/src/main/java/com/brahmibhojan/modules/cart/service/CartService.java`, `backend/src/main/java/com/brahmibhojan/modules/cart/controller/CartController.java`
- Design/approach: Keep modules practical and API-first with minimal entities, Redis guardrails, and explicit endpoints.
- Tests run: `mvn test "-Dtest=AuthControllerIntegrationTest,CatalogControllerIntegrationTest,CartControllerIntegrationTest"` (blocked - Maven CLI not installed in shell environment).
- Notes: Request/response logging now masks sensitive fields (OTP/tokens/password-like fields).
- Follow-up TODOs: Add inventory validation in cart add/update path and add seeded data control flag by profile.

### [AUTH-002] Refresh token lifecycle and API standards update
- Date: 2026-07-12
- Module: auth, common, customer
- Status: Done
- What was implemented: Added refresh-token rotate flow and logout revoke endpoint; switched entity timestamps to `Instant`; updated controllers to explicit full paths per mapping; applied `@RequiredArgsConstructor`; added request/response payload logging filter with sensitive field masking.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/auth/service/RefreshTokenService.java`, `backend/src/main/java/com/brahmibhojan/modules/auth/model/RefreshToken.java`, `backend/src/main/java/com/brahmibhojan/modules/auth/controller/AuthController.java`, `backend/src/main/java/com/brahmibhojan/common/logging/RequestResponseLoggingFilter.java`, `backend/src/main/java/com/brahmibhojan/modules/users/model/User.java`, `backend/src/main/java/com/brahmibhojan/modules/customer/model/CustomerAddress.java`
- Design/approach: Keep access token short-lived JWT and refresh token opaque/hash-based in DB with rotation; avoid overengineering while preserving revoke control.
- Tests run: `mvn test "-Dtest=AuthControllerIntegrationTest,CustomerControllerIntegrationTest,CatalogControllerIntegrationTest"` (blocked - Maven CLI not installed in shell environment).
- Notes: Auto-created users continue to default to name `BB user`; email remains optional profile data.
- Follow-up TODOs: Add IP/device-based OTP rate limiting and optionally move refresh token endpoint to secure cookie strategy.

### [CATALOG-001] Catalog entities and read API foundation
- Date: 2026-07-12
- Module: catalog
- Status: Done
- What was implemented: Added `Category` and `Product` JPA entities, repositories, service mapping, and public read APIs for categories/products/product-detail.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/catalog/model/Category.java`, `backend/src/main/java/com/brahmibhojan/modules/catalog/model/Product.java`, `backend/src/main/java/com/brahmibhojan/modules/catalog/controller/CatalogController.java`, `backend/src/main/java/com/brahmibhojan/modules/catalog/service/CatalogService.java`
- Design/approach: Read-first public catalog API with pagination/filtering and no admin write surface yet.
- Tests run: `mvn test -Dtest=CatalogControllerIntegrationTest` (blocked - Maven CLI not installed in shell environment).
- Notes: Security now permits `/api/v1/catalog/**` for guest discovery.
- Follow-up TODOs: Add seed data strategy and inventory-aware availability updates.

### [CUSTOMER-001] Customer profile and address foundation
- Date: 2026-07-11
- Module: customer
- Status: Done
- What was implemented: Added customer self-profile APIs (`/api/v1/me`) and address CRUD APIs; made `User` entity Lombok-based; added optional email field; default account name is now `BB user` for OTP auto-created users.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/customer/controller/CustomerController.java`, `backend/src/main/java/com/brahmibhojan/modules/customer/service/CustomerService.java`, `backend/src/main/java/com/brahmibhojan/modules/customer/model/CustomerAddress.java`, `backend/src/main/java/com/brahmibhojan/modules/users/model/User.java`, `backend/src/main/java/com/brahmibhojan/modules/auth/service/AuthService.java`
- Design/approach: Keep auth identity mobile-first, keep profile fields optional, and expose minimal customer APIs without overengineering.
- Tests run: `mvn test -Dtest=CustomerControllerIntegrationTest` (blocked - Maven CLI not installed in shell environment).
- Notes: Customer module now supports gradual profile completion after OTP login.
- Follow-up TODOs: Add pagination for addresses and module-level authorization checks for admin-managed customer views.

### [AUTH-001] OTP-first authentication baseline
- Date: 2026-07-11
- Module: auth
- Status: Done
- What was implemented: Replaced email/password auth endpoints with mobile OTP request/verify flow; added Redis OTP challenge handling with cooldown and attempt limits; switched JWT subject and user repository lookups to mobile.
- Files changed: `backend/src/main/java/com/brahmibhojan/modules/auth/controller/AuthController.java`, `backend/src/main/java/com/brahmibhojan/modules/auth/service/AuthService.java`, `backend/src/main/java/com/brahmibhojan/modules/auth/service/OtpService.java`, `backend/src/main/java/com/brahmibhojan/modules/users/model/User.java`, `backend/src/main/java/com/brahmibhojan/modules/users/repository/UserRepository.java`, `backend/src/test/java/com/brahmibhojan/modules/auth/AuthControllerIntegrationTest.java`
- Design/approach: Stateless JWT auth with mobile as primary identity; OTP values hashed with SHA-256 and never logged; simple adapter interface for OTP delivery provider.
- Tests run: `mvn test -Dtest=AuthControllerIntegrationTest` (blocked - Maven CLI not installed in shell environment).
- Notes: Test profile exposes `otpPreview` only for automated testing.
- Follow-up TODOs: Add refresh-token rotation, admin auth strategy, and stronger IP/device rate limiting.

### [INIT-001] Project planning and documentation baseline
- Date: 2026-07-11
- Module: project-context
- Status: Done
- What was implemented: Documentation set and working decisions were consolidated in `docs/` and `project-context/`.
- Files changed: `project-context/progress.md`, `project-context/decisions.md`, `docs/*`
- Design/approach: Documentation-first, backend-first after design approval.
- Tests run: N/A
- Notes: Foundation context is ready for implementation tracking.
- Follow-up TODOs: Start module-wise implementation entries after each coding task.

