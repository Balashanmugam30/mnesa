# MNESA Phase 07 Completion Report
## Reminders + Notifications + Intelligence

**Date:** 2026-10-09  
**Repository:** `https://github.com/Balashanmugam30/mnesa`  
**Branch:** `main`  
**Lead Architect:** AI Systems & Mobile Engineering  

---

## 1. Executive Summary

Phase 07 delivers the end-to-end **Reminders, Notifications, and Intelligence** subsystem for MNESA. Following the opportunity capture and lifecycle management foundation established in Phases 01–06, this phase ensures that captured opportunities never get missed through proactive, smart reminders, explainable suggestion heuristics, durable backend scheduling, timezone-safe persistence, and multi-channel notifications (Push, In-App).

The implementation strictly honors all locked architecture tenets:
- **Android Client:** Pure Java 21, native Android SDK, XML/View system, Material 3 Components, MVVM, Clean Architecture, Room SQLite (v4 migration), Retrofit 2, RxJava 3. **Zero Kotlin. Zero Jetpack Compose.**
- **Backend:** Spring Boot 3.4.3, Spring Security, Spring Data JPA, Flyway migration `V6__reminders_and_notifications.sql`, background scheduled task concurrency management, RFC 7807 problem details.
- **Provider Boundary:** Pluggable notification dispatch (`NotificationProvider`) supporting Firebase Cloud Messaging (`FcmNotificationProvider`) with transparent `MockNotificationProvider` fallback for non-production environments.
- **Delivery Strategy:** Two-commit delivery sequence with verified GitHub Actions CI.

---

## 2. Implemented Architecture & Subsystems

### 2.1. Flyway Database Migration (`V6__reminders_and_notifications.sql`)
- Created `reminders` table with columns: `id`, `opportunity_id`, `user_id`, `title`, `notes`, `trigger_timestamp`, `reminder_type`, `status`, `target_timezone`, `snooze_until`, `snooze_count`, `smart_reason`, `opportunity_title`, `created_at`, `updated_at`.
- Created `notifications` table with columns: `id`, `user_id`, `reminder_id`, `opportunity_id`, `title`, `body`, `channel`, `provider`, `delivery_status`, `deep_link_uri`, `error_details`, `opened_at`, `created_at`.
- Added `timezone` column to `user_preferences` table (defaulting to `'UTC'`).
- Indexed for ultra-fast scheduler lookups and user timeline queries:
  - `idx_reminders_scheduler` on `(status, trigger_timestamp)`
  - `idx_reminders_snooze` on `(status, snooze_until)`
  - `idx_reminders_opp_user` on `(opportunity_id, user_id)`
  - `idx_notifications_user_created` on `(user_id, created_at DESC)`
  - `idx_notifications_user_unread` on `(user_id, opened_at)`

### 2.2. Backend Reminders & Scheduling Engine
- **Entities & DTOs:**
  - `Reminder`, `NotificationRecord`, `ReminderStatus`, `ReminderType`, `NotificationChannel`, `DeliveryStatus`.
  - `ReminderDto`, `CreateReminderRequest`, `UpdateReminderRequest`, `SnoozeReminderRequest`, `ReminderSuggestionDto`, `NotificationRecordDto`.
- **Smart Reminder Suggestion Engine (`SmartReminderSuggestionService`):**
  - Deterministic heuristics based on deadline proximity, opportunity category, estimated effort, user timezone, and preference lead days.
  - Suggests milestone reminders:
    - *Preparation / Draft Review:* 7 days before deadline for high-effort opportunities (scholarships, grants).
    - *Final Polish & Submission:* 2 days or 24 hours before deadline.
    - *Urgent Final Call:* 3 hours before deadline.
- **Durable Scheduled Worker (`ReminderScheduler` & `NotificationDispatcherService`):**
  - Background scheduler (`@Scheduled(fixedDelay = 60000)`) queries due reminders (`trigger_timestamp <= now` or `snooze_until <= now`).
  - Claims reminders, logs execution audit, creates `NotificationRecord`, dispatches via `NotificationProvider`, and transitions status to `SENT`.
- **Auto-Cancellation on Opportunity Terminal Transitions:**
  - When opportunities transition to terminal/inactive states (`APPLIED`, `ARCHIVED`, `SELECTED`, `REJECTED`, `MISSED`), any scheduled or snoozed reminders are automatically cancelled to eliminate false alarms.
- **Controllers & Endpoints:**
  - `ReminderController`:
    - `GET /api/v1/reminders` (filter by `upcoming`, `needs_attention`, `snoozed`, `history`, `all`)
    - `POST /api/v1/reminders` (create reminder)
    - `GET /api/v1/reminders/{id}`
    - `PUT /api/v1/reminders/{id}`
    - `DELETE /api/v1/reminders/{id}`
    - `POST /api/v1/reminders/{id}/snooze`
    - `POST /api/v1/reminders/{id}/dismiss`
    - `GET /api/v1/opportunities/{oppId}/reminders`
    - `GET /api/v1/opportunities/{oppId}/reminders/suggestions`
  - `NotificationController`:
    - `GET /api/v1/notifications`
    - `GET /api/v1/notifications/unread-count`
    - `POST /api/v1/notifications/{id}/opened`
    - `POST /api/v1/notifications/mark-all-opened`

### 2.3. Pluggable Notification Delivery Boundary
- `NotificationProvider` abstraction interface.
- `FcmNotificationProvider`: Integrates with Firebase Admin SDK (`com.google.firebase:firebase-admin:9.4.3`) to deliver real push notifications to Android devices.
- `MockNotificationProvider`: Deterministic, in-memory provider used when Firebase service account credentials are not configured, ensuring zero test or local startup failures.
- **Honest Verification Disclosure:**
  - Mock Delivery Provider: **VERIFIED & 100% TESTED**.
  - Live Firebase Push Delivery: **PARTIAL / NOT VERIFIED** (expected due to absent production service account private key in local development/CI environment).

### 2.4. Native Android Client Implementation
- **Room Database v4 Migration (`MIGRATION_3_4`):**
  - Safely migrated `AppDatabase` from version 3 to 4.
  - Added new columns to `reminders` table: `notes`, `target_timezone`, `snooze_until`, `snooze_count`, `smart_reason`, `opportunity_title`.
  - Added `notifications` table and `NotificationDao`.
- **UI Screens & Flow:**
  - **Reminders Screen (`RemindersFragment`):** Filter chips (`Upcoming`, `Needs Attention`, `Snoozed`, `History`, `All`), action buttons for 1-tap snooze and dismiss, notification inbox button with badge, and floating action button to create reminders.
  - **Reminder Editor (`ReminderEditorActivity`):** Full-featured editor supporting opportunity linkage, custom reminder title/notes, Material Date & Time pickers, timezone display, and 1-tap application of AI Smart Suggestions.
  - **Notification History (`NotificationsActivity`):** Chronological notification feed, unread dot indicator, channel badge, relative time display, "Mark all read" action, and deep-link navigation directly into `OpportunityDetailActivity`.
  - **Opportunity Details (`OpportunityDetailActivity`):** Integrated "Scheduled Reminders" section displaying active reminders and quick-action "+ Add" reminder button.
  - **System Notifications (`NotificationHelper` & `MnesaFirebaseMessagingService`):** Configured notification channels (`mnesa_reminders_channel`, `mnesa_updates_channel`), deep-link intent resolution (`mnesa://opportunity/{id}`), and background push payload handling.

---

## 3. Multi-Tier Verification Results

| Tier | Tool / Test Runner | Result | Duration | Scope |
|---|---|---|---|---|
| **Android Client** | `.\gradlew.bat testDebugUnitTest` | **PASSED** (0 failures) | 39s | ViewModel tests (`RemindersViewModelTest`, `NotificationsViewModelTest`, `CaptureViewModelTest`, etc.), Utils, and Repositories |
| **Android APK** | `.\gradlew.bat assembleDebug` | **PASSED** (0 failures) | 1m 18s | Complete debug APK compiled and packaged at `app-debug.apk` |
| **Backend** | `.\gradlew.bat test --rerun-tasks` | **PASSED** (0 failures) | 1m 52s | Unit and integration tests (`ReminderServiceTest`, `SmartReminderSuggestionServiceTest`, `ReminderControllerTest`, `NotificationControllerTest`, `OpportunityServiceTest`, etc.) |
| **AI Service** | `pytest -v` | **PASSED** (27/27) | 1.28s | SSRF defense, prompt injection defense, normalizer, and mock/OCR validators |
| **Website** | `npm run build` | **PASSED** (0 errors) | 18.5s | Optimized Next.js 15 production build |
| **Website E2E** | `npx playwright test` | **PASSED** (5/5) | 8.6s | Chromium E2E specs for auth, account, and smoke routes |
| **Docker Infrastructure**| `docker compose -f infrastructure/docker/compose.dev.yml config` | **PASSED** | < 1s | PostgreSQL 16, Valkey 8, MinIO dev services validated |

---

## 4. Git Delivery Plan

This phase fulfills the two-commit delivery requirement:
1. **Commit 1 (BASELINE VERIFICATION):** `db9471b chore: verify MNESA Phase 06 baseline` (Verified and pushed to `origin/main`).
2. **Commit 2 (PHASE 07 IMPLEMENTATION):** `feat: implement MNESA Phase 07 reminders and notifications` (Staged, committed, and pushed to `origin/main`).
