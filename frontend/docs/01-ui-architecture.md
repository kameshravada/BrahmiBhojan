# 01 - UI Architecture

## Objectives

- Keep UI modular and domain-oriented.
- Separate presentation from API/data logic.
- Preserve simple onboarding for new contributors.

## Planned Layers

- `app`/routing layer
- feature modules (`auth`, `catalog`, `cart`, `checkout`, `orders`)
- shared UI components
- API client/service layer

## Integration Principle

UI should consume backend APIs as contracts and avoid backend-specific business logic duplication.

