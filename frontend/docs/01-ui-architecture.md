# 01 - UI Architecture

## Objectives

- Keep UI modular and domain-oriented.
- Separate presentation from API/data logic.
- Preserve simple onboarding for new contributors.

## Planned Layers

- `src/app`: route and layout layer (App Router)
- `src/features`: domain modules (`auth`, `catalog`, `cart`, `checkout`, `orders`)
- `src/components`: shared UI primitives and composed sections
- `src/lib`: API client, query client, utility helpers
- `src/stores`: global client state via Zustand
- `src/schemas`: Zod validation schemas

## Initial Folder Blueprint

```text
src/
	app/
	components/
		providers/
	features/
	lib/
		api/
	schemas/
	stores/
```

## Integration Principle

UI should consume backend APIs as contracts and avoid backend-specific business logic duplication.
