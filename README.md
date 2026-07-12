# BrahmiBhojan

BrahmiBhojan is a production-grade ecommerce platform for healthy groceries, traditional foods, and future multi-brand commerce.

The project is documentation-first. Architecture, product requirements, APIs, database design, security, operations, and delivery workflows must be documented and approved before feature implementation.

## Planned Stack

- Frontend: React, Next.js App Router, TypeScript, Tailwind CSS, React Query, Zustand, React Hook Form, Zod, Axios
- Backend: Java 21, Spring Boot 3, Spring Security, JWT, Refresh Tokens, Spring Data JPA, Maven
- Data: PostgreSQL, Flyway, Redis
- Media: Cloudinary
- Payments: Razorpay
- Notifications: Email, SMS, WhatsApp
- DevOps: Docker, GitHub Actions, Nginx, Ubuntu VPS

## Architecture Direction

The backend will follow a modular monolith architecture. BrahmiBhojan will launch as the first brand, while the platform will be designed as a reusable ecommerce engine for future brands.

## Repository Structure

```text
BrahmiBhojan/
  docs/
  project-context/
  backend/
  frontend/
  infra/
  docker-compose.yml
```

Some folders will be created when their approved phase begins.

## Current Bootstrap Status

- `backend/` scaffold created with Spring Boot 3 (Java 21, Maven)
- Initial auth module added (`register`, `login`) with JWT
- PostgreSQL migration baseline added with Flyway (`users` table)
- Local infra compose added for PostgreSQL and Redis (`docker-compose.yml` and `infra/docker-compose.yml`)

## Developer Onboarding

- Full local setup and IntelliJ sanity guide: `docs/00-dev-environment-setup.md`
- Backend-focused quick start: `backend/README.md`

## Local Run (Backend)

```powershell
cd D:\kamesh-codes\BrahmiBhojan
docker compose up -d
cd backend
..\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
```

Open:

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/swagger-ui/index.html`

