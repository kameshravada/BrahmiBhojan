# 00 - Frontend Setup

## Goal

Prepare and run a standalone frontend workspace under `frontend/` for VS Code-based UI development.

## Current Implementation Status

- Next.js App Router project initialized.
- TypeScript, Tailwind CSS v4, and ESLint configured.
- Base scripts added for `dev`, `build`, `start`, `lint`, and `typecheck`.

## Minimum Setup Checklist

- Install Node.js LTS.
- Install a package manager (`npm` is sufficient).
- Open only `frontend/` in VS Code for daily UI work.
- Keep backend running separately for API calls.

## Run Commands

```bash
cd frontend
npm install
npm run dev
```

## Environment Variables (planned)

- `NEXT_PUBLIC_API_BASE_URL` (example: `http://localhost:8080`)

## Notes

This document now reflects the created frontend scaffold and will evolve as feature modules are implemented.
