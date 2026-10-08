# MNESA Android Local-First Architecture & Core UI

**Phase:** Phase 03 — Android Core UI + Local Data + Offline-First Foundation  
**Status:** IMPLEMENTED & VERIFIED  

---

## 1. Overview & Core Philosophy

In MNESA, the core interaction loop is:
```text
SEE → SHARE → UNDERSTAND → SAVE → REMIND → ACT → COMPLETE
```

A core product invariant is that **the fastest path to capture and inspect opportunities must be offline-capable and immediate**. The mobile client never blocks UI presentation on remote network latency. 

When the user opens MNESA:
```text
UI opens
   ↓
ViewModel requests data
   ↓
Repository reads Room SQLite (Single Source of Truth)
   ↓
UI renders immediately (< 100ms)
   ↓
Network synchronization occurs independently in the background
   ↓
Room updates reactively via RxJava 3 Flowables
   ↓
UI reacts instantaneously to local database changes
```

---

## 2. Navigation Architecture

MNESA's Android client shell is hosted in [`MainActivity`](file:///c:/Users/balashanmugam/OneDrive/Desktop/Projects/Mnesa/android/app/src/main/java/com/mnesa/android/presentation/main/MainActivity.java) utilizing Material 3 `BottomNavigationView` coordinated across four primary destinations:

1. **Home (`HomeFragment`):**
   - Dynamic time-of-day greeting ("Good morning.", "Good afternoon.", "Good evening.").
   - Progress Summary Card displaying counts for Tracked Opportunities, Urgent Deadlines (< 7 days), and Scheduled Reminders.
   - Categorized urgency sections: "Needs Attention" (urgent items), "Upcoming Deadlines", and "Recently Saved".
   - Deterministic sample data loading controls for development and testing.
2. **Opportunities (`OpportunitiesFragment`):**
   - Full opportunity browsing with category filtering chips: *All*, *Internships*, *Jobs*, *Hackathons*, *Scholarships*, *Competitions*.
   - Multi-state rendering: Loading skeletons, Empty state with capture CTA, Error state with retry action, and loaded card lists.
3. **Reminders (`RemindersFragment`):**
   - Scheduled reminder cadences (Standard, Aggressive, Immediate) linked to deadlines.
   - Timing badges and trigger countdowns.
4. **Profile (`ProfileFragment`):**
   - Authenticated user identity (display name, email, initials avatar).
   - Configured focus area chips.
   - Storage architecture telemetry (Room SQLite cached status, WorkManager sync queue status).
   - Navigation to Settings (`SettingsActivity`) and secure Sign Out.

---

## 3. Local Data & Room Database Schema

The local storage foundation is implemented via AndroidX Room in [`AppDatabase`](file:///c:/Users/balashanmugam/OneDrive/Desktop/Projects/Mnesa/android/app/src/main/java/com/mnesa/android/data/local/AppDatabase.java) (Version 2) with explicit, non-destructive migration (`MIGRATION_1_2`):

### 3.1 `opportunities` Table
- `id` (TEXT, PK): Unique UUIDv7 opportunity identifier.
- `user_id` (TEXT, Indexed): Authenticated user ID for strict multi-user data isolation.
- `title` (TEXT NOT NULL): Opportunity title.
- `organization` (TEXT): Sponsoring company or institution.
- `category` (TEXT NOT NULL): Opportunity category (INTERNSHIP, JOB, HACKATHON, etc.).
- `description` (TEXT): High-level description.
- `source_url` (TEXT): Original captured source URL.
- `registration_url` (TEXT): Official application or registration URL.
- `deadline_timestamp` (INTEGER): Epoch millisecond timestamp of application deadline.
- `deadline_timezone` (TEXT): Local timezone string.
- `eligibility` (TEXT): Criteria and qualifications.
- `location` (TEXT): Geographic or virtual location.
- `estimated_effort` (TEXT): Effort estimate for application.
- `priority` (TEXT NOT NULL): Priority level (HIGH, MEDIUM, LOW).
- `status` (TEXT NOT NULL): Lifecycle state (CAPTURED, UNDERSTOOD, SAVED, etc.).
- `confidence_score` (REAL): AI extraction confidence score.
- `created_at` (INTEGER): Creation timestamp.
- `updated_at` (INTEGER): Last local modification timestamp.
- `sync_state` (TEXT NOT NULL): SYNCED, PENDING_SYNC, or LOCAL_ONLY.

### 3.2 `reminders` Table
- `id` (TEXT, PK): Unique reminder ID.
- `opportunity_id` (TEXT NOT NULL): Associated opportunity ID.
- `user_id` (TEXT NOT NULL): Associated user ID.
- `title` (TEXT NOT NULL): Reminder title / prompt.
- `trigger_timestamp` (INTEGER NOT NULL): Epoch millisecond scheduled trigger time.
- `reminder_type` (TEXT NOT NULL): STANDARD, AGGRESSIVE, IMMEDIATE.
- `status` (TEXT NOT NULL): SCHEDULED, FIRED, DISMISSED.
- `created_at` (INTEGER NOT NULL): Creation timestamp.

### 3.3 `sync_queue` Table
- `operation_id` (TEXT, PK): Stable UUID for operation idempotency.
- `user_id` (TEXT NOT NULL): User ID initiating mutation.
- `entity_type` (TEXT NOT NULL): OPPORTUNITY, REMINDER, PREFERENCE.
- `entity_id` (TEXT NOT NULL): Target entity identifier.
- `operation_type` (TEXT NOT NULL): CREATE, UPDATE, DELETE.
- `payload_json` (TEXT): Serialized mutation payload.
- `created_at` (INTEGER NOT NULL): Operation creation timestamp.
- `attempt_count` (INTEGER NOT NULL): Retry counter (capped at 5 before permanent failure).
- `last_attempt_at` (INTEGER): Last retry timestamp.
- `next_retry_at` (INTEGER): Next scheduled exponential backoff time.
- `state` (TEXT NOT NULL): PENDING, RUNNING, SUCCEEDED, FAILED, FAILED_PERMANENT.
- `error_category` (TEXT): Last observed error code or diagnostic.

---

## 4. Connectivity Monitoring & Offline Banner

A robust network observer [`ConnectivityMonitor`](file:///c:/Users/balashanmugam/OneDrive/Desktop/Projects/Mnesa/android/app/src/main/java/com/mnesa/android/core/network/ConnectivityMonitor.java) monitors system connectivity using `ConnectivityManager.NetworkCallback`.

States emitted via RxJava 3:
- `ONLINE`: Network active with verified internet capability.
- `OFFLINE`: Network disconnected or unavailable. Non-blocking banner appears at the top of the app: *"You're offline. Your saved opportunities are still available."*
- `RECONNECTING`: Transitional state upon connection restoration. Banner shows: *"Connection restored. Syncing changes..."* and triggers `SyncScheduler`.
- `UNKNOWN`: Uninitialized state during startup.

---

## 5. WorkManager Durable Retry Queue

Background synchronization is decoupled from the UI thread and managed by AndroidX WorkManager in [`SyncWorker`](file:///c:/Users/balashanmugam/OneDrive/Desktop/Projects/Mnesa/android/app/src/main/java/com/mnesa/android/core/sync/SyncWorker.java) and [`SyncScheduler`](file:///c:/Users/balashanmugam/OneDrive/Desktop/Projects/Mnesa/android/app/src/main/java/com/mnesa/android/core/sync/SyncScheduler.java):
- **Constraint:** `NetworkType.CONNECTED` ensures the worker runs only when an active network connection is established.
- **Backoff Policy:** `BackoffPolicy.EXPONENTIAL` with initial 15-second delay to prevent backend slamming.
- **Idempotency:** Stable `operation_id` guarantees duplicate network executions do not create duplicate records on the server.

---

## 6. Multi-User Isolation & Scoping

To prevent cross-account data leakage on shared devices:
- All Room entities include `user_id`.
- DAOs query strictly filtered on `user_id`.
- [`SecureTokenManager`](file:///c:/Users/balashanmugam/OneDrive/Desktop/Projects/Mnesa/android/app/src/main/java/com/mnesa/android/core/security/SecureTokenManager.java) stores active credentials using hardware-backed AES256-GCM `EncryptedSharedPreferences`.
- Upon sign out, cached user tokens are cleared, and data access is re-scoped to avoid displaying previous user records.

---

## 7. Testing Strategy

Phase 03 implementation is validated with comprehensive test suites:
- **Unit Tests:** `HomeViewModelTest`, `OpportunitiesViewModelTest`, `RemindersViewModelTest`, `SyncRepositoryTest`, `SampleDataProviderTest`, `DateTimeUtilsTest`, `OpportunityTest`.
- **Gradle Command:** `.\gradlew.bat testDebugUnitTest` (100% green).
- **Build Verification:** `.\gradlew.bat assembleDebug` produces `app-debug.apk` without errors.
