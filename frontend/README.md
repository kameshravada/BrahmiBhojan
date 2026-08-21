# BrahmiBhojan Frontend

Next.js App Router storefront foundation with TypeScript, Tailwind CSS v4, React Query,
Zustand, React Hook Form, Zod, and Axios.

## Tech Stack

- Next.js 16 (App Router)
- React 19 + TypeScript
- Tailwind CSS v4
- TanStack Query (server state)
- Zustand (client UI/session state)
- React Hook Form + Zod (forms and validation)
- Axios (API client)

## Scripts

```bash
npm run dev
npm run lint
npm run typecheck
npm run build
npm run start
```

## Local Development

1. Install dependencies:

```bash
npm install
```

2. Start the app:

```bash
npm run dev
```

3. Open `http://localhost:3000`.

## Planned Architecture

```text
frontend/
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

## Notes

- Frontend setup is intentionally backend-contract aligned.
- API integration and feature modules will be added incrementally from Phase A onward.
