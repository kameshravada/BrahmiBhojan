# 28 - Architecture Decision Records

Status: Draft for approval

## ADR-001 - Use Modular Monolith

Decision: Use a modular monolith for backend launch.

Reason: Faster delivery, lower operational complexity, strong transactional consistency, and future service extraction path.

## ADR-002 - OTP-Only Authentication

Decision: No passwords; mobile OTP only.

Reason: Matches quick-commerce behavior and reduces friction.

## ADR-003 - Guest-First Commerce

Decision: Guests can browse and use cart; checkout requires login.

Reason: Improves conversion while protecting order placement.

## ADR-004 - Razorpay for Payments

Decision: Use Razorpay for online payment and UPI at launch.

Reason: Strong India payment support and webhook-based confirmation.

## ADR-005 - No COD at Launch

Decision: Exclude cash on delivery initially.

Reason: Reduces reconciliation and delivery failure complexity.

## ADR-006 - Delivery Partner Model

Decision: Pack internally and use partners such as Delhivery.

Reason: Keeps quality control internal while avoiding delivery fleet complexity.

