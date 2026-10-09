# MNESA Phase 09 — Legal & Launch Verification Checklist

**Date:** 2026-10-09  
**Phase:** 09 (Premium Website + Brand + Security & Privacy + Legal + Production States)  
**Status:** COMPLETE & VERIFIED  
**Lead Architect:** AI Systems & Web Platform Engineering  

---

## 1. Legal & Regulatory Compliance

- [x] **Privacy Policy (`/privacy`):**
  - Accurately details data collected: Account details, shared URLs, normalized webpage text, screenshot uploads, extracted metadata, and Firebase Cloud Messaging (FCM) device tokens.
  - Transparently explains third-party AI processing (Google Gemini API under enterprise data terms where inputs/outputs are not used to train public foundation models).
  - Explicitly states no data selling or ad-network brokering.
  - Documents user rights to export data and immediately delete accounts.
  - Lists official privacy contact: `privacy@mnesa.ai`.

- [x] **Terms of Service (`/terms`):**
  - Establishes early-access preview license and permitted use.
  - **Critical AI Extraction Disclaimer:** Explicitly requires users to independently verify extracted application deadlines and criteria with the originating host organization.
  - Enforces Acceptable Use Policy (no malicious scraping, prompt injection attacks, or unauthorized penetration testing).
  - Outlines intellectual property ownership (user retains full content ownership).
  - Contains limitation of liability and indemnification clauses.

- [x] **Account & Data Deletion (`/data-deletion`):**
  - Multi-channel deletion paths: In-app Android settings, Web dashboard (`/account`), and direct email (`privacy@mnesa.ai`).
  - Explains cascading deletion across PostgreSQL relational tables, MinIO S3 object buckets, and device-local Room SQLite.
  - Complies with Google Play Data Safety requirements and modern privacy standards.

---

## 2. Security Architecture & Threat Model Transparency

- [x] **Zero Secrets in Client Bundles:** Verified that neither the Android APK nor the Next.js static chunks contain database passwords, private API keys, or provider secrets.
- [x] **SSRF Protection:** Content ingestion gateway filters loopback (`127.0.0.1`), private subnets (`10.0.0.0/8`, `192.168.0.0/16`), and cloud instance metadata (`169.254.169.254`).
- [x] **Prompt Injection Hardening:** Delimiter collision escaping and strict Pydantic JSON schema deserialization.
- [x] **Data Isolation:** Enforced multi-tenant isolation by `user_id` at Spring Security JPA layer and PostgreSQL.
- [x] **HTTP Security Headers (`next.config.ts`):**
  - `X-Content-Type-Options: nosniff`
  - `X-Frame-Options: DENY`
  - `Referrer-Policy: strict-origin-when-cross-origin`
  - `Permissions-Policy: camera=(), microphone=(), geolocation=()`
  - `Content-Security-Policy: default-src 'self'; frame-ancestors 'none'; ...`
- [x] **Vulnerability Disclosure:** Program guidelines published on `/security#disclosure` with contact `security@mnesa.ai`.

---

## 3. Web Platform, Brand & Design System

- [x] **Navigation & Shell:** Responsive `Navbar.tsx` with desktop links, mobile drawer, and accessible focus trap.
- [x] **Footer:** 4-column structured footer with product, security, legal, and company resources.
- [x] **Accessibility (WCAG 2.1 AA):**
  - Skip to main content link (`#main-content`) with high-contrast visible focus.
  - Semantic HTML landmarks (`header[role='banner']`, `main[role='main']`, `footer[role='contentinfo']`).
  - High-contrast typography adhering to WCAG contrast minimums in light and dark modes.
  - Minimum 48px touch targets for mobile interactive elements.
- [x] **Production & System States:**
  - `/_not-found` (404 Page)
  - `/error` (Client Error Boundary with retry)
  - `/loading` (Skeleton Loader)
  - `/maintenance` (Scheduled Maintenance with offline reassurance)
  - `/offline` (Offline Architecture Guide)
  - `/unavailable` (503 Service Unavailable Guide)
- [x] **Interactive Simulator (`/demo`):**
  - Realistic presets for Fellowship, Hackathon, and Scholarship.
  - Animated pipeline simulation showing sanitization, structuring, and evidence extraction.
  - Verbatim citation display and calibrated confidence score meter.
- [x] **SEO & Metadata:**
  - Dynamic `sitemap.xml` covering 17 public routes.
  - Crawlable `robots.txt` disallowing private account/api routes.
  - OpenGraph and Twitter card metadata in root layout.

---

## 4. Multi-Tier Test Suite Verification

- [x] **Website Playwright E2E Suite:** 26/26 tests passing in 24.1s (`auth.spec.ts`, `pages.spec.ts`, `demo.spec.ts`, `smoke.spec.ts`).
- [x] **AI Service Pytest Suite:** 34/34 tests passing in 1.35s.
- [x] **Backend Spring Boot Suite:** 5 actionable Gradle tasks UP-TO-DATE, all tests passing.
- [x] **Android Client Suite:** 25 actionable Gradle tasks UP-TO-DATE, all unit tests passing.
- [x] **Android Debug APK:** Compiles cleanly with zero resource or layout errors.
