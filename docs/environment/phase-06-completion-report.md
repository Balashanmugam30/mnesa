# MNESA — Phase 06 Completion Report
## Opportunity Management, Home Dashboard, Full-Text Search, Multi-Filter, and Lifecycle Tracking

**Project:** MNESA — AI-Powered Opportunity Capture & Follow-Through Platform  
**Phase:** 06 — Opportunity Management + Home + Search + Filters  
**Repository:** `https://github.com/Balashanmugam30/mnesa`  
**Branch:** `main`  
**Execution Date:** 2026-10-09  

---

## 1. Executive Summary

Phase 06 establishes the complete full-stack Opportunity Lifecycle management system for MNESA. Building directly on the verified Phase 05 foundation (which confirmed multi-modal extraction, confidence scoring, and evidence grounding), Phase 06 equips MNESA with:

1. **Deterministic Opportunity Lifecycle State Machine:** Strict transitions across the nine production states (`SAVED`, `REVIEWING`, `APPLYING`, `APPLIED`, `WAITING`, `SELECTED`, `REJECTED`, `MISSED`, `ARCHIVED`), enforced by domain validation and tracked via an append-only audit trail (`opportunity_activities`).
2. **PostgreSQL Search & Filter Engine:** Indexed full-text search (PostgreSQL `tsvector` with GIN indexing via `V5__opportunity_management_and_search.sql`) and dynamic multi-attribute filtering (category, status, priority, deadlines, organization, location, tags) with Spring Data JPA `JpaSpecificationExecutor`.
3. **Home Dashboard Aggregator (`GET /api/v1/home`):** Real-time aggregation of active counts, urgency buckets (<7 days), upcoming deadlines, recently saved items, and personalized AI-suggested next actions.
4. **Native Android Client (Pure Java 21, Material 3):**
   - **All Opportunities Screen (`OpportunitiesFragment`):** Real-time search query filtering, status chip toggles, category chip filtering, swipe-to-refresh, skeleton loaders, and empty/error states.
   - **Opportunity Details Screen (`OpportunityDetailActivity`):** Rich metadata inspection, status and priority pills, countdown urgency timer, clickable safe intent launchers for source and registration URLs, and chronological activity audit timeline.
   - **Edit & Add Screens (`EditOpportunityActivity`, `AddOpportunityActivity`):** Complete metadata manipulation forms with validated inputs, exposed dropdowns for category/priority/work-mode, and asynchronous persistence.
   - **Home Screen (`HomeFragment`):** Real metrics dashboard, dynamic time-of-day greeting, interactive suggested action cards, urgency sections, and click-through navigation to detail views.
   - **Room Offline-First Architecture:** Local SQLite caching via `OpportunityDao` and `OpportunityRepositoryImpl` ensuring seamless offline operation with transparent remote synchronization via Retrofit `OpportunityApiService`.

---

## 2. Technical Architecture & Database Schema

### 2.1 Database Migration (`V5__opportunity_management_and_search.sql`)
- **Status Constraint:** Updated `chk_opportunities_status` to strictly enforce the production lifecycle: `'SAVED', 'REVIEWING', 'APPLYING', 'APPLIED', 'WAITING', 'SELECTED', 'REJECTED', 'MISSED', 'ARCHIVED', 'CAPTURED', 'UNDERSTOOD', 'PROCESSING', 'ACTED', 'COMPLETED'`.
- **Enriched Opportunity Columns:**
  - `description` (`TEXT`)
  - `registration_url` (`VARCHAR(2048)`)
  - `source_domain` (`VARCHAR(255)`)
  - `deadline_timezone` (`VARCHAR(64)`)
  - `eligibility` (`TEXT`)
  - `location` (`VARCHAR(255)`)
  - `work_mode` (`VARCHAR(32)`)
  - `estimated_effort` (`VARCHAR(64)`)
  - `priority` (`VARCHAR(32) NOT NULL DEFAULT 'MEDIUM'`)
  - `priority_reason` (`VARCHAR(255)`)
  - `notes` (`TEXT`)
  - `last_status_change_at` (`TIMESTAMPTZ`)
  - `archived_at` (`TIMESTAMPTZ`)
  - `previous_status` (`VARCHAR(32)`)
  - `extraction_id` (`UUID REFERENCES extractions(id)`)
- **Normalized Tag System:**
  - `tags` table (`id`, `user_id`, `name`, `color_hex`, `created_at`) with unique constraint on `(user_id, lower(name))`.
  - `opportunity_tags` join table (`opportunity_id`, `tag_id`, `created_at`).
- **Activity & History Audit Trail:**
  - `opportunity_activities` table (`id`, `opportunity_id`, `user_id`, `activity_type`, `from_status`, `to_status`, `description`, `comment`, `created_at`).
- **PostgreSQL GIN Full-Text Index:**
  - `idx_opportunities_fts` created on `to_tsvector('english', coalesce(title, '') || ' ' || coalesce(organization, '') || ' ' || coalesce(description, '') || ' ' || coalesce(location, ''))`.
  - Performance B-Tree indexes on `(user_id, status)`, `(user_id, category)`, `(user_id, deadline_timestamp)`, and `(user_id, created_at)`.

---

## 3. Backend Services & REST APIs

### 3.1 REST Endpoints
- `GET /api/v1/opportunities`: Dynamic search, filter, sort, and pagination.
- `GET /api/v1/opportunities/{id}`: Opportunity details with user ownership enforcement.
- `POST /api/v1/opportunities`: Manual creation of opportunities with tag association and `CREATED` activity logging.
- `PUT /api/v1/opportunities/{id}`: Metadata modification with `EDITED` activity logging.
- `PATCH /api/v1/opportunities/{id}/status`: Lifecycle transition validation with `STATUS_CHANGE` activity logging.
- `POST /api/v1/opportunities/{id}/archive`: Soft archiving preserving `previous_status`.
- `POST /api/v1/opportunities/{id}/restore`: Restoring archived opportunities to their prior active status.
- `DELETE /api/v1/opportunities/{id}`: Complete deletion with activity purge.
- `GET /api/v1/opportunities/{id}/history`: Chronological audit trail retrieval.
- `GET /api/v1/home`: Dashboard summary with counts, urgency buckets, and suggested actions.
- `GET /api/v1/categories`: Category breakdown summary with opportunity counts.
- `GET /api/v1/tags`: User tag list.

### 3.2 State Machine Transition Rules
Enforced in `OpportunityTransitionService`:
- `SAVED` → `REVIEWING`, `APPLYING`, `ARCHIVED`
- `REVIEWING` → `APPLYING`, `SAVED`, `MISSED`, `ARCHIVED`
- `APPLYING` → `APPLIED`, `REVIEWING`, `MISSED`, `ARCHIVED`
- `APPLIED` → `WAITING`, `SELECTED`, `REJECTED`, `ARCHIVED`
- `WAITING` → `SELECTED`, `REJECTED`, `ARCHIVED`
- `SELECTED` → `ARCHIVED`
- `REJECTED` → `ARCHIVED`
- `MISSED` → `ARCHIVED`
- `ARCHIVED` → `previous_status` (or `SAVED`) upon restoration.

---

## 4. Native Android Client Implementation

### 4.1 Strict Architectural Compliance
- **Language:** Pure Java 21 exclusively. Zero Kotlin code (`compileDebugKotlin NO-SOURCE`).
- **UI Framework:** Android ViewBinding, XML layouts, and Google Material Design 3 components. Zero Jetpack Compose.
- **Offline-First:** Room SQLite database acting as single source of local truth. UI observes LiveData/RxJava Flowables backed by local DAO; mutations execute locally and synchronize asynchronously over Retrofit.

### 4.2 Screens & Components Delivered
- **`OpportunityDetailActivity` & `activity_opportunity_detail.xml`:** Full metadata display, status/category/priority pills, deadline countdown timer, web URL launcher intents, status transition dialog, options menu for archive/restore/delete, and audit timeline view.
- **`EditOpportunityActivity` & `AddOpportunityActivity`:** Two-way editable form handling all metadata attributes, exposed dropdowns, and form validation.
- **`OpportunitiesFragment` & `fragment_opportunities.xml`:** Real-time search query filtering via `TextWatcher`, horizontal status filter chips, category chips, FAB for new opportunities, and click navigation.
- **`HomeFragment` & `fragment_home.xml`:** Real metrics cards, suggested next action card, urgent deadline bucket lists, and recent activity cards.
- **`OpportunityAdapter` & `item_opportunity_card.xml`:** Reusable `ListAdapter` with `DiffUtil`, status badges with semantic color coding, and relative deadline formatting.

---

## 5. Verification & Test Results Matrix

| Tier | Component | Tests Executed | Result | Duration |
|------|-----------|----------------|--------|----------|
| **Android** | Unit Tests (`testDebugUnitTest`) | 52 tests | **PASSED** (0 failures) | 24s |
| **Android** | Build & Packaging (`assembleDebug`) | Debug APK generated | **PASSED** | Included |
| **Backend** | Unit & Integration Tests (`test`) | 62 tests | **PASSED** (0 failures) | 52s |
| **AI Service**| Pytest Suite (`pytest -v`) | 27 tests | **PASSED** (0 failures) | 0.82s |
| **Website** | Next.js Production Build (`npm run build`) | Static & SSR routes | **PASSED** | 3.2s |
| **Website** | Playwright E2E Tests (`playwright test`) | 5 browser tests | **PASSED** (0 failures) | 6.8s |

---

## 6. Git Delivery & Commit Structure

1. **Commit 1 (Baseline Verification):**
   - SHA: `98bf740`
   - Message: `chore: verify MNESA Phase 05 baseline`
   - Description: Full verification of Phase 05 across all 4 tiers before starting Phase 06.
2. **Commit 2 (Implementation):**
   - Message: `feat: implement MNESA Phase 06 opportunity management and search`
   - Description: Full-stack implementation of Opportunity Lifecycle state machine, PostgreSQL full-text search, multi-field filters, audit logging, Home dashboard, and complete native Android UI.
