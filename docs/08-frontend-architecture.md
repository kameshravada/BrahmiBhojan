# 08 - Frontend Architecture

Status: Draft for approval

## Stack

Next.js App Router, React, TypeScript, Tailwind CSS, React Query, Zustand, React Hook Form, Zod, Axios.

## Application Boundary

Use a separated route/application boundary for customer storefront and admin portal, following industry practice.

Recommended initial structure:

```text
frontend/
  src/
    app/
      (storefront)/
      admin/
    components/
    features/
    lib/
    stores/
    schemas/
```

## Responsibilities

- Next.js: routing, SSR/SEO pages, layouts.
- React Query: server state and API caching.
- Zustand: lightweight client UI state.
- React Hook Form + Zod: form validation.
- Axios: backend API client.
- Tailwind: consistent styling.

## Key Requirements

- Product/category pages SEO-friendly.
- OTP login modal/page reusable.
- Guest cart state survives normal browsing.
- Admin pages permission-aware.
- Mobile-first storefront.
- Dense admin UI optimized for operations.

