# Contributing to MNESA

Thank you for contributing to MNESA. This repository houses an enterprise-grade platform combining a native Android client, Spring Boot modular monolith, FastAPI AI intelligence service, and Next.js web application.

---

## 1. Locked Invariants & Ground Rules

Before opening a pull request or submitting code, ensure you respect MNESA's core engineering invariants:
1. **No Language Substitutions:**
   - **Android:** Strictly pure Native Java 21, XML View layout system, and Material 3. **No Kotlin. No Jetpack Compose.**
   - **Backend:** Java 21 with Spring Boot 3.4.
   - **AI Service:** Python 3.13 with FastAPI and Pydantic v2.
   - **Website:** Next.js 15 (App Router) with React 19, TypeScript strict, and Tailwind CSS.
2. **Zero Client Secrets:** Never commit, bundle, or expose API keys, database credentials, or private certificates in client APKs or browser bundles.
3. **Strict Database Migrations:** All schema alterations must be applied via incremental Flyway migration scripts (`V<number>__<description>.sql`). Never modify applied migrations.
4. **Mandatory Tests:** Every new endpoint, domain service, or user screen must include automated tests (JUnit 5, Mockito, Spring Boot Test, pytest, Playwright). Never skip or bypass failing tests.
5. **Zero-Trust Web Content:** All incoming URLs, scrape data, or shared strings must be sanitized against prompt injection and SSRF prior to AI processing.

---

## 2. Commit Message Standards

MNESA enforces the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```text
<type>(<scope>): <short description>

[optional body]

[optional footer(s)]
```

### Allowed Types
- `feat`: A new user-facing or platform capability.
- `fix`: A bug fix.
- `refactor`: Code restructuring without modifying behavior.
- `test`: Adding or correcting tests without production code changes.
- `docs`: Documentation updates only.
- `style`: Formatting, missing semicolons, whitespace (no functional change).
- `perf`: Performance optimizations.
- `chore`: Build tooling, dependency bumps, or repository configuration.

### Common Scopes
- `android`: Native mobile app code.
- `backend`: Spring Boot modular monolith.
- `ai`: Python FastAPI service.
- `web`: Next.js web platform.
- `infra`: Docker Compose, Nginx, or GitHub Actions.
- `design`: Design tokens, XML themes, or Tailwind tokens.

---

## 3. Pull Request Checklist

Before submitting a PR, verify the following:
- [ ] Code compiles without warnings (`./gradlew compileJava`, `npm run build`, `pytest`).
- [ ] All unit, integration, and contract tests pass.
- [ ] No hardcoded secrets, local IP addresses, or unverified endpoints exist in the diff.
- [ ] Public API methods and REST endpoints are documented with OpenAPI or Javadoc.
- [ ] Any schema alterations include an accompanying Flyway migration file.
- [ ] Mobile UI elements meet the 48x48dp minimum touch target and WCAG 2.1 AA contrast ratio.
