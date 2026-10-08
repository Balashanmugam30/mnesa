# MNESA Project Rules & Engineering Tenets

This document specifies the locked architecture, invariant technical decisions, and operational constraints for all human and AI engineers developing MNESA.

---

## 1. Locked Technical Architecture

1. **Production-Oriented Mindset:** MNESA is an enterprise-grade, production-oriented project. No temporary hacks, fragile shortcuts, or throwaway scripts in place of production code.
2. **Explicit Authorization for Stack Changes:** Do not switch, substitute, or alter any component of the locked stack without explicit human user authorization.
3. **Android Client:** Native Android using pure Java, Android SDK, XML/View system, Material Components, AndroidX, MVVM, Clean Architecture, Hilt, Room, Retrofit, OkHttp, RxJava 3 (where appropriate), WorkManager, Firebase Cloud Messaging, Firebase Crashlytics, and native `ACTION_SEND` intent integration. No Kotlin unless explicitly approved.
4. **Backend Client:** Java with Spring Boot, Spring Security, Spring Data JPA, Hibernate, Bean Validation, Flyway, Actuator, and Gradle Wrapper.
5. **AI Service:** Python with FastAPI, AI provider abstractions (Gemini provider, Local Ollama provider, OCR provider), strict structured output schemas, deterministic validation, and confidence scoring.
6. **Website Platform:** Next.js (App Router), React, TypeScript, Tailwind CSS, and Framer Motion (GSAP only when genuinely required).
7. **Primary Database:** PostgreSQL is the single source of truth and primary relational database for MNESA.
8. **No Supabase Dependency:** Supabase is not the primary backend or database for MNESA.
9. **No Firestore Substitution:** Firebase Firestore must not be introduced as a replacement for PostgreSQL. Firebase is strictly utilized for FCM push notifications and Crashlytics.
10. **Zero Secrets in Client Bundles:** Never put private API keys, service credentials, database strings, or signing keys into Android client APKs or Next.js web client bundles.
11. **Zero Secret Commits:** Never commit secrets, credentials, API keys, or private certificates to version control.
12. **Zero-Trust Web Input:** Treat all webpage content, shared URLs, raw text, and external data as untrusted. Enforce strict prompt-injection defenses and deterministic sanitization before AI processing.
13. **Mandatory Testing:** Every new feature, endpoint, data flow, or screen requires comprehensive unit and integration tests (JUnit 5, Mockito, Spring Boot Test, Testcontainers, Espresso, Playwright).
14. **No Ignored Failures:** Never silently ignore, suppress, comment out, or bypass failing tests.
15. **Monolith First & No Premature Microservices:** Maintain a modular monolith backend architecture first. Do not create premature or unnecessary distributed microservices.
16. **Backward Compatibility:** Preserve backward compatibility, existing contract guarantees, and verified working behavior across all modules.
17. **Strict Database Migrations:** All schema changes must be versioned and applied exclusively through Flyway migrations. No manual database alterations or destructive migration shortcuts.
18. **Production-Grade Error Handling:** Implement structured exception handling, domain-level error codes, RFC 7807 problem details, and deterministic user feedback.
19. **Ultra-Fast Capture Experience:** The core opportunity capture flow (`SEE → SHARE → UNDERSTAND → SAVE → REMIND → ACT → COMPLETE`) must be instantaneous on Android, requiring minimal to zero user friction.
20. **Accessible & Premium UI:** Maintain an accessible, responsive, high-contrast, WCAG-compliant, and premium design system across both Android and Web.
21. **No API Fabrication:** Never fabricate, hallucinate, or guess API endpoints, library functions, or SDK capabilities.
22. **Source-Grounding & Official Docs:** Ground all implementation decisions in official documentation when versions, frameworks, or APIs evolve.
