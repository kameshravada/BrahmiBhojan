# 06 - Frontend Testing

## Planned Test Layers

- Unit tests for utility and validation logic
- Component tests for key UI widgets
- Integration tests for feature flows with mocked API
- E2E smoke tests for auth -> catalog -> cart -> checkout

## Planned Tooling

- Unit and component: Vitest + React Testing Library
- E2E: Playwright

## Quality Gates

- Lint must pass
- Type-check must pass
- Core flow tests should be green before merge
