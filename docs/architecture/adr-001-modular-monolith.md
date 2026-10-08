# ADR 001: Modular Monolith Backend Architecture

## Status
Accepted

## Context
MNESA is a high-velocity, production-oriented startup project requiring fast mobile capture, reliable data consistency, proactive reminder scheduling, and AI extraction. A premature microservices architecture introduces excessive distributed systems complexity (distributed transactions, network latency, multiple deployment pipelines, service discovery overhead) before scale justifies it.

## Decision
We adopt a **Modular Monolith** architecture for the MNESA backend using Spring Boot 3.4 and Java 21 LTS:
1. All domain modules (`auth`, `user`, `opportunity`, `reminder`, `ai`) reside within a single codebase and compile into a single deployable artifact.
2. Domain boundaries are strictly enforced through Java package visibility and explicit Service interface contracts.
3. Modules interact through Java method calls and immutable DTOs, not through direct foreign JPA entity references.
4. The database remains a single unified PostgreSQL relational database managed by versioned Flyway migrations.

## Consequences
- **Positive:** Maximum refactoring agility, simple single-command local development via Docker Compose, zero network serialization latency between core modules, ACID transactional consistency for critical opportunity capture operations.
- **Negative:** Requires strong developer discipline to prevent inappropriate cross-module coupling. Enforced by package-private classes and architecture linting.
