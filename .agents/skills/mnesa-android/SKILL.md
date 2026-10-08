---
name: mnesa-android
description: MNESA Android engineering guidelines covering native Java, XML/View system, Material Components, AndroidX, MVVM, Clean Architecture, Hilt, Room, Retrofit, OkHttp, RxJava 3, WorkManager, and ACTION_SEND sharing.
---

# MNESA Android Engineering Guidelines

## 1. Core Principles
- **Language:** Pure Native Java (Java 21 source/target compatibility). Absolutely no Kotlin unless explicitly authorized.
- **UI Toolkit:** Native Android XML/View layout system and Material Components (`com.google.android.material`).
- **Architecture:** Strict Clean Architecture + MVVM.
  - `presentation`: Activities, Fragments, ViewModels, ViewBinding, Adapters.
  - `domain`: UseCases / Interactors, Domain Models, Repository Interfaces.
  - `data`: Repository Implementations, Room Entities/DAOs, Retrofit API Clients, Data Mappers.

## 2. Share Capture Pipeline (`ACTION_SEND`)
- **Intent Filter:** Handle `android.intent.action.SEND` and `ACTION_SEND_MULTIPLE` in a dedicated `CaptureActivity` with transparent/dialog theme.
- **Speed Requirement:** Immediate visual acknowledgement (< 200ms). The user must see instant capture confirmation.
- **Intake Flow:**
  1. Receive `Intent.EXTRA_TEXT`, `Intent.EXTRA_STREAM`, or clip data.
  2. Normalize and persist payload locally in Room with status `PENDING_ANALYSIS`.
  3. Schedule background analysis via WorkManager with network constraints.
  4. Dismiss capture dialog immediately; do not block the user.

## 3. Storage & Concurrency
- **Room Database:** Offline-first architecture. All user data resides in local Room DB with reactive observation (`Flowable` / `Observable` via RxJava 3 or `LiveData`).
- **RxJava 3:** Use RxJava 3 for asynchronous pipelines, debounce operators on search, and chaining background operations. Always dispose subscriptions via `CompositeDisposable` in `onCleared()` / `onDestroyView()`.
- **WorkManager:** Background sync, retries with exponential backoff, and periodic reminder verifications must use `OneTimeWorkRequest` or `PeriodicWorkRequest`.

## 4. Networking & Push
- **Retrofit + OkHttp:** Pin TLS certificates where applicable, configure connection pooling, timeouts (10s connect, 30s read), and auth interceptors for Bearer tokens.
- **Firebase Cloud Messaging (FCM):** Background token refreshes and opportunity deadline reminder notifications.
- **Crashlytics:** Non-fatal logging for unexpected API or parsing exceptions.
