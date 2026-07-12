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

See full setup and troubleshooting guide: `docs/00-dev-environment-setup.md`.

1. Start dependencies:

```powershell
cd D:\kamesh-codes\BrahmiBhojan
cd infra
docker compose up -d
```

2. Run backend tests once before app startup:

```powershell
cd D:\kamesh-codes\BrahmiBhojan\backend
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.11"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
..\tools\apache-maven-3.9.9\bin\mvn.cmd -B test
```

3. Run backend:

```powershell
cd D:\kamesh-codes\BrahmiBhojan\backend
mvn spring-boot:run
```

4. Useful endpoints:

- Health: `http://localhost:8080/actuator/health`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

## Environment

Copy `.env.example` values into your shell or IDE run configuration.

## IntelliJ Notes

- Open the `backend/` directory as IntelliJ project root.
- Set Project SDK and Maven importer JDK to Java 21.
- If dependencies look stale, run Maven **Reload All Projects**.
- If the IDE gets stuck with bad indexes, invalidate caches and reimport.

