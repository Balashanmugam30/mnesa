---
name: mnesa-testing
description: MNESA Testing strategy covering unit, integration, UI, E2E, Testcontainers, Espresso, and Playwright.
---

# MNESA Testing Strategy & Quality Bar

## 1. Test Pyramid & Tooling
- **Unit Testing:** JUnit 5 and Mockito for backend Java; JUnit 4/5 and Mockito for Android Java; pytest for Python AI service; Vitest/Jest for Next.js.
- **Integration Testing:** Spring Boot `@SpringBootTest` with Testcontainers (real PostgreSQL and Valkey containers).
- **Mobile UI Testing:** Espresso and AndroidX Test runner for native Android UI workflows.
- **Web E2E Testing:** Playwright for Next.js web application journeys.

## 2. Invariants
- No feature is complete without accompanying unit and integration tests.
- Never mock out the database in integration tests where Testcontainers can provide real PostgreSQL semantics.
- All test runs must be reproducible and isolated from external network dependencies (use WireMock for external APIs).
