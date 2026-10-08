---
name: mnesa-springboot
description: MNESA Spring Boot backend engineering rules covering modular monolith architecture, Spring Security, JPA/Hibernate, Flyway, validation, and structured logging.
---

# MNESA Spring Boot Backend Architecture

## 1. Architectural Philosophy
- **Modular Monolith First:** Build clear domain boundaries within a single deployable artifact before considering microservices.
- **Packages by Feature/Domain:**
  - `opportunity`: Capture, extraction review, lifecycle state machine.
  - `reminder`: Notification scheduling, calendar sync, cadence engine.
  - `user` / `auth`: Authentication, JWT issuance, profile, device tokens.
  - `integration`: External AI service client, web fetcher, OCR client.

## 2. API Design & Validation
- **DTO Isolation:** Never expose JPA `@Entity` classes directly over REST APIs. Always transform through dedicated request/response DTOs.
- **Bean Validation:** Strict `@NotNull`, `@NotBlank`, `@Size`, `@URL`, and custom validators on all incoming payloads.
- **Error Handling:** Centralized `@RestControllerAdvice` implementing RFC 7807 Problem Details with machine-readable error codes.
- **OpenAPI:** Document all endpoints with SpringDoc OpenAPI / Swagger annotations.

## 3. Data Access & Transactions
- **Spring Data JPA & Hibernate:** Explicit fetch strategies to prevent `N+1` queries (`JOIN FETCH`, `@EntityGraph`).
- **Transaction Boundaries:** Place `@Transactional` at the service/use-case layer, with `readOnly = true` on query methods.
- **Flyway Migrations:** All schema modifications must use versioned SQL scripts in `src/main/resources/db/migration/`.

## 4. Security & Observability
- **Spring Security:** Stateless session management (`SessionCreationPolicy.STATELESS`), JWT verification filter, Argon2id password hashing.
- **Actuator & OpenTelemetry:** Expose health, metrics, and trace endpoints for Prometheus and Grafana dashboards.
- **Structured Logging:** JSON-formatted structured logging with correlation IDs (`MDC`) propagated across asynchronous task executors.
