# Backend Setup

Spring Boot backend for BrahmiBhojan with modular-monolith package structure.

## Stack

- Java 21
- Spring Boot 3
- Maven
- PostgreSQL + Flyway
- Redis
- JWT auth
- JUnit 5 + MockMvc

## Quick Start

1. Start dependencies:

```powershell
cd D:\kamesh-codes\BrahmiBhojan
cd infra
docker compose up -d
```

2. Run backend:

```powershell
cd D:\kamesh-codes\BrahmiBhojan\backend
mvn spring-boot:run
```

3. Useful endpoints:

- Health: `http://localhost:8080/actuator/health`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

## Environment

Copy `.env.example` values into your shell or IDE run configuration.

