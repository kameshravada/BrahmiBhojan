# 00 - Dev Environment Setup

## Goal

Get a new developer to a stable local backend run with repeatable checks for Docker, Maven, and IntelliJ.

## Prerequisites

- Windows 10/11
- Java 21 JDK installed
- Docker Desktop running
- IntelliJ IDEA (Community or Ultimate)

## Project Paths

- Workspace root: `D:\kamesh-codes\BrahmiBhojan`
- Backend module: `D:\kamesh-codes\BrahmiBhojan\backend`
- Local Maven binary (repo-managed): `D:\kamesh-codes\BrahmiBhojan\tools\apache-maven-3.9.9\bin\mvn.cmd`

## One-Time IntelliJ Import

1. Open IntelliJ and choose **Open** for `D:\kamesh-codes\BrahmiBhojan\backend`.
2. Ensure **Project SDK** is Java 21.
3. Open Maven tool window and click **Reload All Maven Projects**.
4. Confirm active profile is `dev` for local app runs.

## Quick Local Bring-Up

```powershell
Set-Location "D:\kamesh-codes\BrahmiBhojan"
docker compose up -d

Set-Location "D:\kamesh-codes\BrahmiBhojan\backend"
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.11"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
..\tools\apache-maven-3.9.9\bin\mvn.cmd -B test
```

Run app:

```powershell
Set-Location "D:\kamesh-codes\BrahmiBhojan\backend"
..\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
```

## Environment Toggles

- `catalog.seed.enabled` controls seed loading.
  - `application-dev.properties`: enabled
  - `application-prod.properties`: disabled
- Override at runtime when needed:

```powershell
$env:CATALOG_SEED_ENABLED="false"
```

## Sanity Script

Use the repo script to validate basic local readiness:

```powershell
Set-Location "D:\kamesh-codes\BrahmiBhojan"
.\scripts\dev-sanity-check.ps1
```

It checks:
- Java and `JAVA_HOME`
- Docker daemon availability
- PostgreSQL/Redis compose services
- Maven availability and version

## Common IntelliJ Issues

- Maven dependencies not resolved:
  - Reimport Maven project from tool window.
  - Verify IntelliJ Maven JDK is Java 21.
- App fails with wrong JDK internals:
  - Ensure Run Configuration uses Java 21 (not newer EA/preview JDK).
- Stale indexes or strange compile errors:
  - `File -> Invalidate Caches -> Invalidate and Restart`, then reimport Maven.

## Final Manual Checklist

- `docker compose ps` shows Postgres and Redis healthy/running.
- Maven test run exits with code 0.
- IntelliJ can run `BrahmiBhojanApplication` with profile `dev`.
- `http://localhost:8080/actuator/health` returns `UP`.

