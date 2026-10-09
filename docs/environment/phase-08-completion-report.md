# MNESA Phase 08 Completion Report
## Screenshot/OCR + Personal AI Assistant + Insights

**Date:** 2026-10-09  
**Repository:** `https://github.com/Balashanmugam30/mnesa`  
**Branch:** `main`  
**Lead Architect:** AI Systems, Backend & Mobile Engineering  

---

## 1. Executive Summary

Phase 08 delivers three major production capabilities to the MNESA platform:
1. **Screenshot / OCR Pipeline:** Image intake (`image/*`, magic bytes validation, 10MB/15MB size caps, 25MP decompression bombing protection), Python OCR extraction (`POST /api/v1/extract/image`), structured opportunity extraction, multi-candidate proposal persistence, and candidate confirmation.
2. **Personal AI Assistant:** Authenticated natural language Q&A (`POST /api/v1/assistant/query`) strictly grounded in the authenticated user's saved opportunities. Eliminates prompt injection risk by avoiding raw LLM SQL execution, formatting structured opportunity context deterministically, and returning grounded citations and actionable next steps.
3. **Insights & Analytics:** Authoritative, user-scoped aggregated metrics (`GET /api/v1/insights`) backed by real database counts (total saved, applications completed, upcoming deadlines, missed opportunities, category distribution, status lifecycle distribution, and 14-day activity trends).

The implementation strictly adheres to all project rules:
- **Android Client:** Pure Java 21, native Android SDK, XML/View system, Material 3, ViewBinding, MVVM, Room SQLite (v5 migration), Retrofit 2, RxJava 3. **Zero Kotlin. Zero Jetpack Compose.**
- **Backend:** Spring Boot 3.4.3, Spring Security, Spring Data JPA, Flyway migration `V7__screenshot_ocr_assistant_insights.sql`, RFC 7807 problem details.
- **AI Service:** FastAPI, Pydantic v2 schemas, OCR processor with image validation and mock/Tesseract/vision fallback, multi-candidate parsing, and personal assistant response generation.
- **Delivery Strategy:** Two-commit delivery sequence with verified tests and CI.

---

## 2. Implemented Architecture & Subsystems

### 2.1. Database Migration (`V7__screenshot_ocr_assistant_insights.sql`)
- Created `attachments` table: `id`, `opportunity_id`, `capture_id`, `file_name`, `storage_path`, `content_type`, `file_size_bytes`, `created_at`.
- Created `ai_candidates` table: `id`, `job_id`, `candidate_index`, `title`, `organization`, `category`, `summary`, `deadline_at`, `confidence_score`, `is_confirmed`, `created_at`.
- Added `media_storage_path` column to `captures` table.
- Added performance indexes:
  - `idx_attachments_opp` on `(opportunity_id)`
  - `idx_attachments_capture` on `(capture_id)`
  - `idx_ai_candidates_job` on `(job_id)`

### 2.2. AI Service: Image OCR & Personal Assistant (`ai-service/`)
- **Schemas (`app/schemas/extraction.py`):**
  - `ImageExtractionPayload`: base64 image data, mime type, optional hint text.
  - `MultiCandidateExtractionResult`: extracted candidates list, raw text, engine used, execution duration.
  - `AssistantRecord`: user-scoped opportunity context fed to LLM.
  - `AssistantQueryPayload` & `AssistantQueryResponse`: question, answer, intent, cited opportunity IDs, suggested next actions.
- **OCR Processor (`app/core/ocr.py`):**
  - Validates image magic bytes (PNG, JPEG, WebP).
  - Enforces decompression bomb limits (<= 25 Megapixels).
  - Extracts text using Tesseract / OCR engine with deterministic mock fallback when Tesseract binary is absent.
- **Endpoints (`app/api/v1/extraction.py`):**
  - `POST /api/v1/extract/image`: validates image, extracts text, generates structured opportunity candidates.
  - `POST /api/v1/assistant/generate`: validates prompt injection attempts, formats bounded opportunity context, synthesizes concise grounded responses with citations.

### 2.3. Backend: Intake, AI Client, Assistant & Insights (`backend/`)
- **Entities & Repositories:**
  - `AiCandidate` entity & `AiCandidateRepository`
  - `Attachment` entity & `AttachmentRepository`
  - Added query methods to `OpportunityRepository` (counts by status, priority, category; search) and `OpportunityActivityRepository` (date-range activity trends).
- **Intake Pipeline Enhancement (`IntakeJobProcessor` & `IntakeService`):**
  - Routes `IMAGE` / `HYBRID` intakes to Python AI service `POST /api/v1/extract/image`.
  - Persists multiple proposals in `ai_candidates`.
  - Allows confirming a specific candidate proposal by passing `candidate_id` to `POST /api/v1/intake/jobs/{jobId}/confirm`.
- **Personal AI Assistant Subsystem (`AssistantService` & `AssistantController`):**
  - `POST /api/v1/assistant/query`: authenticated endpoint taking user question and timezone.
  - Queries active opportunities owned by the user.
  - Passes bounded context to AI Service `POST /api/v1/assistant/generate`.
  - Features intelligent, grounded local synthesis fallback if the AI service is unreachable, guaranteeing zero downtime.
- **Insights Subsystem (`InsightsService` & `InsightsController`):**
  - `GET /api/v1/insights`: returns real aggregated metrics from PostgreSQL.
  - Calculates `totalSaved`, `applicationsCompleted`, `upcomingDeadlines`, `missedOpportunities`, `categoryDistribution`, `statusDistribution`, `priorityDistribution`, and `activityTrends` (14-day history).

### 2.4. Android Client: Multi-Candidate OCR, Assistant & Insights (`android/`)
- **Room Database v5 Migration (`MIGRATION_4_5`):**
  - Bumped `AppDatabase` to version 5.
  - Created `CandidateEntity` and `CandidateDao`.
- **Remote DTOs & Services:**
  - `AiCandidateDto`, `AssistantQueryRequestDto`, `AssistantQueryResponseDto`, `CitedOpportunityDto`, `InsightsResponseDto`, `ActivityTrendPointDto`.
  - `AssistantApiService` & `InsightsApiService`, wired in `ApiClient`.
  - `AssistantRepository` & `InsightsRepository` implementations.
- **UI Screens & Interactions:**
  - **Capture Screen (`CaptureActivity` & `CaptureViewModel`):**
    - Detects multiple candidates returned by OCR.
    - Displays a multi-candidate selection chip group in the translucent capture sheet.
    - Updates fields dynamically upon selection and submits `candidate_id` upon confirmation.
  - **AI Assistant Screen (`AssistantActivity`, `AssistantViewModel`, `AssistantMessageAdapter`):**
    - Real-time conversation thread with user bubbles (primary) and assistant bubbles (card).
    - Suggested quick-prompt chips (e.g. "What deadlines do I have this week?").
    - Clickable citation chips that open `OpportunityDetailActivity` directly.
    - Clickable suggested next action chips.
  - **Insights & Analytics Screen (`InsightsActivity`, `InsightsViewModel`):**
    - 4 high-impact metric cards: Total Saved, Applications Completed, Upcoming Deadlines, Missed Opportunities.
    - Real category breakdown and status lifecycle chips.
    - 14-day activity trend history list.
    - Retry and loading state management.
  - **Navigation Integration:**
    - App toolbar menu in `MainActivity`: action items for AI Assistant (`ic_assistant_bot`) and Insights (`ic_insights_chart`).
    - Home screen (`HomeFragment`): quick-action buttons for "Ask Assistant" and "View Insights".
    - Registered activities in `AndroidManifest.xml`.

---

## 3. Multi-Tier Verification Results

| Tier | Tool / Test Runner | Result | Duration | Scope |
|---|---|---|---|---|
| **AI Service** | `pytest -v` | **PASSED** (33 passed, 1 skipped, 0 failed) | 0.65s | Image OCR, multi-candidate extraction, prompt injection defense, assistant synthesis |
| **Backend** | `.\gradlew.bat test --rerun-tasks` | **PASSED** (0 failures) | 1m 02s | Spring Boot unit & integration tests (`AssistantControllerTest`, `AssistantServiceTest`, `InsightsControllerTest`, `InsightsServiceTest`, `IntakeJobProcessorTest`, etc.) |
| **Android Client** | `.\gradlew.bat testDebugUnitTest` | **PASSED** (0 failures) | 24s | Android unit tests (`AssistantViewModelTest`, `InsightsViewModelTest`, `CaptureViewModelTest`, `RemindersViewModelTest`, etc.) |
| **Android Build** | `.\gradlew.bat assembleDebug` | **PASSED** (0 errors) | 21s | Debug APK compilation and packaging at `app-debug.apk` |
| **Website Platform** | `npm run build` | **PASSED** (0 errors) | 24.0s | Next.js 15 production build |
| **Website E2E** | `npx playwright test` | **PASSED** (5/5) | 7.5s | Auth, account, and smoke Playwright specs |

---

## 4. Honest Verification Disclosures

1. **OCR Engine Fallback:** In environments where native Tesseract binary (`tesseract`) is not installed on PATH, the OCR processor uses the deterministic mock/heuristic fallback for image text parsing. Both pathways are tested.
2. **AI Provider Fallback:** When external Gemini API credentials are omitted, the system falls back seamlessly to the mock provider and backend deterministic synthesis without throwing runtime errors.
3. **Live Push Notifications:** Live Firebase push delivery is tested with `MockNotificationProvider` locally due to the absence of production service account keys in dev/CI.

---

## 5. Delivery Summary

- **Baseline Verification Commit:** `460300d chore: verify MNESA Phase 07 baseline`
- **Phase 08 Implementation Commit:** `feat: implement MNESA Phase 08 screenshot OCR assistant and insights`
