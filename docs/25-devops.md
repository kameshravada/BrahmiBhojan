# 25 - DevOps

Status: Draft for approval

## Stack

Docker, GitHub Actions, Nginx, Ubuntu VPS.

## Environments

- Local
- Staging
- Production

## CI/CD Requirements

- Build backend.
- Run backend tests.
- Build frontend.
- Run frontend lint/type checks.
- Build Docker images.
- Deploy to VPS after approval or configured branch rules.

## Production Needs

Nginx reverse proxy, SSL, environment variables, logs, database backups, Redis, health checks, and rollback plan.

