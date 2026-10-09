# MNESA Phase 05 Completion Report: Web Content Extraction, AI Extraction, and Confidence Pipeline

**Product:** MNESA — AI-powered opportunity capture and follow-through platform  
**Phase:** 05 — Web Content Extraction + AI Extraction + Confidence & Evidence  
**Status:** COMPLETE & FULLY VERIFIED  
**Date:** 2026-10-09  
**Repository:** `https://github.com/Balashanmugam30/mnesa`  
**Branch:** `main`  

---

## 1. Executive Summary & Architecture Overview

Phase 05 bridges the high-speed Share Intake Pipeline (established in Phase 04) with the intelligent comprehension and opportunity extraction subsystem:
$$\text{SEE} \longrightarrow \text{SHARE} \longrightarrow \mathbf{\text{UNDERSTAND}} \longrightarrow \mathbf{\text{SAVE}} \longrightarrow \text{REMIND} \longrightarrow \text{ACT} \longrightarrow \text{COMPLETE}$$

When an opportunity URL, text, or image is shared to MNESA, the system now:
1. Safely fetches external web content with strict SSRF defenses, private IP blocking, redirect hop restrictions, and 2MB payload streaming limits.
2. Normalizes HTML content into semantic, structured Markdown with boilerplate stripping and metadata extraction (Open Graph, JSON-LD, microdata).
3. Executes multi-provider AI extraction (Gemini 2.5 Flash, Local Ollama, Mock fallback) with strict Pydantic v2 schemas and prompt injection delimiters.
4. Performs deterministic post-extraction validation: date consistency, deadline urgency, URL format verification, grounded evidence quote checks, and composite confidence score calculation.
5. In the Spring Boot backend, manages asynchronous intake background processing via `IntakeJobProcessor` and scheduled polling via `IntakeJobScheduler`, storing immutable extraction records in PostgreSQL (`ai_extractions` table via Flyway migration `V4`).
6. Enriches the Android native intake bottom sheet with real-time job status polling, confidence badges, deadline alerts, grounded evidence display, and an instantaneous single-tap "Confirm & Save" opportunity action.

---

## 2. Multi-Tier Implementation Details

### Tier 1: AI Service (Python 3.13, FastAPI, Pydantic v2)
- **SSRF Defense & Web Fetcher (`ai-service/app/core/fetcher.py`):**
  - Resolves domain hostnames and verifies target IPs against dangerous subnets before connecting.
  - Blocks IPv4 loopback (`127.0.0.0/8`), RFC 1918 private subnets (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`), link-local (`169.254.0.0/16`), cloud metadata endpoints (`169.254.169.254`, `metadata.google.internal`), IPv6 loopback (`::1`), ULA (`fc00::/7`), multicast, and internal Docker service DNS names (`postgres`, `valkey`, `minio`, `kafka`, `ai-service`, `backend`).
  - Enforces HTTPS/HTTP scheme whitelist (rejects `file://`, `gopher://`, `ftp://`).
  - Implements manual redirect handling with a maximum of 3 hops and prohibits HTTPS-to-HTTP downgrade redirects.
  - Enforces a strict 2MB download limit and 10-second timeout.
- **Content Normalizer (`ai-service/app/core/normalizer.py`):**
  - Custom standard-library `HTMLParser` implementation stripping `<script>`, `<style>`, `<nav>`, `<footer>`, `<aside>`, `<noscript>`, `<svg>`, and `<header>` boilerplate.
  - Extracts title, Open Graph description, canonical URL, and JSON-LD schema metadata.
  - Preserves hierarchical headings (`h1`–`h4`), list structures, paragraphs, and explicit deadline terms.
  - Caps normalized clean text at 15,000 characters to prevent LLM context saturation and resource exhaustion.
- **OCR Processor (`ai-service/app/core/ocr.py`):**
  - Magic-byte verification for image uploads (`PNG`, `JPEG`, `WEBP`), 10MB file limit, and graceful fallback when Tesseract is absent.
- **Deterministic Validator (`ai-service/app/core/validator.py`):**
  - Grounded quote verification: ensures extracted quotes exist verbatim in the source content, penalizing confidence by 25% if ungrounded.
  - Temporal validation: flags past dates, detects urgent deadlines (<72 hours), and identifies ambiguous deadline formats.
  - Composite confidence calculation with explainable scoring across Title, Organization, Deadline, Requirements, and Grounding.
- **AI Providers (`ai-service/app/providers/`):**
  - `BaseAIProvider` abstraction with `GeminiProvider`, `OllamaProvider`, and `MockAIProvider`.
  - Structured prompt construction with prompt-injection defense boundaries (`### UNTRUSTED USER CONTENT ###`).
  - Backward compatibility via `@computed_field` properties (`opportunity_type`, `confidence_score`, `deadline_at`, `action_url`) on `ExtractedOpportunity`.
- **API Endpoints (`ai-service/app/api/v1/extraction.py`):**
  - `POST /api/v1/extract`: Extracts structured opportunity from raw text/images.
  - `POST /api/v1/fetch-and-extract`: Safely fetches URL and extracts opportunity.

### Tier 2: Backend (Java 21, Spring Boot 3.4.3, PostgreSQL, Flyway)
- **Database Migration (`V4__create_ai_extraction_records.sql`):**
  - Created table `ai_extractions` with foreign keys to `captures`, `intake_jobs`, and `app_users`.
  - Unique constraint `uk_ai_extractions_job` on `intake_job_id`.
  - Indexed on `(user_id, created_at)` and `validation_status`.
- **Domain Entities & Repositories (`backend/src/main/java/com/mnesa/backend/modules/`):**
  - Entities: `AiExtraction`, `Opportunity`, `OpportunityStatus` (`CAPTURED`, `UNDERSTOOD`, `SAVED`, `ACTED`, `COMPLETED`), `OpportunityType` (`INTERNSHIP`, `JOB`, `HACKATHON`, `SCHOLARSHIP`, `COMPETITION`, `EVENT`, `GRANT`, `OTHER`), `ValidationStatus`, `PriorityLevel`, `WorkMode`.
  - Repositories: `AiExtractionRepository`, `OpportunityRepository`.
- **AI Client (`AiServiceClient.java`):**
  - Spring `RestClient` with configured timeouts, DTO serialization, and resilient fallback.
- **Background Worker & Scheduler (`IntakeJobProcessor.java`, `IntakeJobScheduler.java`):**
  - Polling worker automatically claims `PENDING` intake jobs, invokes `AiServiceClient`, persists `AiExtraction` entity, transitions `IntakeJob` and `Capture` to `COMPLETED`, and generates an `Opportunity` in `UNDERSTOOD` status.
  - Recovery mechanism cleans up abandoned or stalled processing jobs older than 5 minutes.
  - Scheduled via Spring `@EnableScheduling` with fixed 3-second polling interval.
- **API Endpoints (`IntakeController.java`):**
  - `GET /api/v1/intake/jobs/{jobId}`: Returns enriched job status containing structured `AiExtractionDto` proposal.
  - `POST /api/v1/intake/jobs/{jobId}/confirm`: Allows user to confirm or override extracted fields, transitioning `Opportunity` to `SAVED`.

### Tier 3: Android Client (Pure Java 21, Material 3, XML, ViewBinding)
- **Locked Stack Adherence:** Pure Java 21, zero Kotlin, zero Jetpack Compose, native XML layouts, Material Components 3, ViewBinding, MVVM architecture.
- **Remote DTOs & Retrofit (`com.mnesa.android.data.remote/`):**
  - `AiExtractionDto`, `IntakeJobStatusDto`, `ConfirmOpportunityRequestDto`.
  - Added `getJobStatus(jobId)` and `confirmJob(jobId, request)` to `IntakeApiService`.
- **Repository Layer (`IntakeRepositoryImpl.java`):**
  - Added `pollJobStatus(jobId)` and `confirmOpportunity(jobId, request)` with RxJava 3 `Single` return types.
- **State Machine & ViewModel (`CaptureState.java`, `CaptureUiState.java`, `CaptureViewModel.java`):**
  - Added states: `ANALYZING`, `EXTRACTION_SUCCESS`, `CONFIRMED`.
  - Automatic RxJava 3 timer polling (1.5-second interval, max 10 ticks / 15 seconds) after successful intake submission.
  - `confirmOpportunity(title, category, deadline)` dispatches single-tap confirmation to backend.
- **UI & Layout (`activity_capture.xml`, `CaptureActivity.java`):**
  - Added `cardExtraction` CardView displaying extracted organization, confidence badge, formatted deadline, summary, and grounded evidence snippet.
  - Added `btnConfirmOpportunity` Material button enabling instantaneous 1-tap confirmation.
  - Dynamic color-coding on confidence pills: `>=85%` green (`mnesa_status_success`), `>=50%` amber (`mnesa_urgency_warning`), `<50%` red (`mnesa_error`).

---

## 3. Comprehensive Verification & Test Results

### 1. Android Native Client
- **Unit Tests:** `BUILD SUCCESSFUL in 31s`
  - All test suites passing in `com.mnesa.android`:
    - `CaptureViewModelTest` (tested intake acknowledging, async job analyzing transition, polling completion, confirm opportunity, duplicate handling, offline fallback, invalid payload)
    - `IntakeRepositoryTest` (tested offline persistence, online submission, job status polling, opportunity confirmation)
    - `DateTimeUtilsTest`, `AuthRepositoryTest`, `SyncRepositoryTest`, `SampleDataProviderTest`, `IntakePayloadParserTest`, `OpportunityTest`, `AuthViewModelTest`, `HomeViewModelTest`, `OpportunitiesViewModelTest`, `RemindersViewModelTest`.
- **Debug APK Build:** `BUILD SUCCESSFUL in 34s`
  - Output: `android/app/build/outputs/apk/debug/app-debug.apk` assembled cleanly without warnings or errors.

### 2. Backend (Spring Boot 3.4.3 & Java 21)
- **Test Suite Execution:** `BUILD SUCCESSFUL in 1m 3s` (`.\gradlew.bat test --rerun`)
  - **38 tests passing (100% green)** across all test classes:
    - `IntakeControllerTest` (intake submission, idempotency, duplicate handling, job status polling, opportunity confirmation)
    - `IntakeJobProcessorTest` (end-to-end background job execution, AI client integration, entity persistence)
    - `AuthControllerTest`, `AccountControllerTest`, `JwtTokenProviderTest`, `UserRepositoryTest`, `PasswordResetTokenRepositoryTest`, `AuthRateLimitServiceTest`.

### 3. AI Service (Python 3.13 & FastAPI)
- **Pytest Suite Execution:** `27 passed in 0.96s` (`.\.venv\Scripts\pytest -v`)
  - `test_fetcher_and_ssrf.py`: 6 tests passing (loopback blocking, private subnet blocking, cloud metadata blocking, scheme validation, internal Docker hostname blocking, public domain allowance).
  - `test_normalizer.py`: 4 tests passing (metadata extraction, script/style removal, boilerplate stripping, heading/deadline preservation).
  - `test_validator_and_providers.py`: 9 tests passing (mock provider extraction, past deadline detection, urgent deadline detection, ungrounded evidence penalty, ambiguous date handling, prompt injection defense, endpoint validation, OCR error handling).
  - `test_extraction.py`: 2 tests passing (extraction endpoint success, validation failure).
  - `test_security.py`: 3 tests passing (sanitization, injection detection, delimiter escaping).
  - `test_health.py`: 3 tests passing (health endpoints).

### 4. Website Platform (Next.js 15 & Playwright)
- **Production Build:** `Compiled successfully in 12.9s`
  - Static pages generated: 8/8 (`/`, `/_not-found`, `/account`, `/api/health`, `/register`, `/signin`).
- **Playwright E2E Tests:** `5 passed (6.3s)`
  - All 5 Playwright E2E tests passing in Chromium.

---

## 4. Security & Compliance Audit
- **Zero Hardcoded Secrets:** Full scan confirms zero private keys, API credentials, or passwords in committed code or git index.
- **SSRF Prevention:** Enforced at DNS, IP, and scheme levels with private address range rejection.
- **Prompt Injection Defense:** Strict boundaries, delimiter sanitization, and structured Pydantic v2 schemas.
- **Database Integrity:** Versioned Flyway migration `V4__create_ai_extraction_records.sql` with referential foreign key integrity and user isolation.
