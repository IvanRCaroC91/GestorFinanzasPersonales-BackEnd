# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Multi-module Maven project implementing a personal finance manager backend using a microservices architecture. Java 21, Spring Boot 3.2.5, Spring Cloud 2023.0.3.

## Modules

| Module | Port | Purpose |
|---|---|---|
| `ms-service-registry` | 8761 | Eureka service discovery |
| `ms-api-gateway` | 8080 | API Gateway (Spring Cloud Gateway / WebFlux) — routing, CORS, JWT filter |
| `ms-auth-service` | 8081 | Authentication — JWT generation, BCrypt password hashing |
| `ms-finance-service` | 8083 | Finance domain — movements, budgets, categories, merchants |

## Common Commands

```bash
# Build all modules (skip tests)
mvn clean package -DskipTests

# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=AuthServiceTest -pl ms-auth-service

# Run a single module
mvn spring-boot:run -pl ms-service-registry
mvn spring-boot:run -pl ms-api-gateway
mvn spring-boot:run -pl ms-auth-service
mvn spring-boot:run -pl ms-finance-service
```

## Local Development with Docker

```bash
# Start all services
docker-compose up -d

# Start only infrastructure (postgres + eureka)
docker-compose up -d postgres eureka

# View logs
docker-compose logs -f auth-service

# Rebuild a single service image
docker-compose build auth-service
```

Startup order matters: postgres → eureka → gateway → auth-service / finance-service.

## Architecture

```
Client → API Gateway (8080) → [Auth Service | Finance Service]
                ↕ (service discovery)
          Service Registry (8761 / Eureka)
                ↕
           PostgreSQL (5432)
```

- The API Gateway uses Spring Cloud Gateway (reactive / WebFlux). **Do not use Spring MVC patterns here.**
- Route predicates: `/api/v1/auth/**` → auth-service, `/api/v1/finance/**` → finance-service.
- The Gateway validates JWTs and injects `X-User-Id` header for downstream services. Downstream services trust this header and do not re-validate the JWT.
- Services register with Eureka on startup; the gateway resolves service addresses via load-balanced URIs (`lb://ms-auth-service`).

## JWT & Security

- JWT library: `io.jsonwebtoken:jjwt` v0.12.3.
- Tokens are generated in `ms-auth-service` and validated in `ms-api-gateway` (not in individual services).
- Downstream services receive the authenticated user's ID via the `X-User-Id` HTTP header set by the gateway filter.

## Database

- PostgreSQL 15, single shared database (`finanzas_db`).
- Schema initialized from `database/init.sql`; test data in `database/test-data.sql`.
- Primary keys are UUIDs. Key tables: `usuarios`, `categorias`, `presupuestos`, `movimientos`, `comercios`, `reglas_clasificacion`.
- Each service connects directly to PostgreSQL via JPA/Hibernate.

## Configuration Profiles

- `application.yml` — local development (uses `localhost`).
- `application-docker.yml` — Docker/Railway deployment (uses container/service names as hostnames).
- `railway.env` — environment variables for Railway cloud deployment.

## Dockerfiles

All services use multi-stage builds:
1. Build stage: `maven:3.9.6-eclipse-temurin-21`
2. Runtime stage: `eclipse-temurin:21-jre-alpine`

## CORS

CORS is configured centrally in the API Gateway (`application-docker.yml`). Do not add `@CrossOrigin` or additional `CorsConfig` beans in individual services — this caused duplicate header issues in the past.
