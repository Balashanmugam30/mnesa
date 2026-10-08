# ADR 002: Offline-First Local Data Foundation with Room and Durable WorkManager Sync Queue

## Status
Accepted

## Context
MNESA's primary value proposition is instantaneous capture and follow-through on opportunities:
```text
SEE → SHARE → UNDERSTAND → SAVE → REMIND → ACT → COMPLETE
```
Users often browse job boards, conferences, or academic portals on mobile devices under fluctuating, intermittent, or completely absent cellular/Wi-Fi connectivity. 

If the application required network round-trips to render lists, display reminders, or inspect opportunity details:
1. Cold start and navigation latency would introduce noticeable friction.
2. Users in poor network environments (subways, flights, low-signal campuses) would experience blank screens, loading spinners, or crash states.
3. Rapid user actions would risk data loss if connections dropped mid-flight.

## Decision
1. **Room as Single Source of Truth:**
   The Android client treats the local Room SQLite database as the single source of truth for the UI layer. All ViewModels consume reactive RxJava 3 `Flowable` streams emitted by Room DAOs.
2. **Immediate Local Persistence:**
   Local mutations (saves, deletes, preference updates) are committed to Room first and immediately reflected in the UI.
3. **Durable Sync Queue:**
   Mutations requiring backend synchronization are recorded into a persistent `sync_queue` table with unique, stable operation IDs.
4. **AndroidX WorkManager for Retries:**
   The background retry mechanism is delegated to `WorkManager`, constrained to `NetworkType.CONNECTED` with exponential backoff.
5. **Strict Multi-User Isolation:**
   All Room tables (`opportunities`, `reminders`, `sync_queue`) mandate a `user_id` column to prevent cross-account data leakage when different users log into the same physical device.
6. **Intentional Boundary:**
   Live Share Intent ingestion and server-side intake pipeline remain strictly deferred to Phase 04.

## Consequences
### Positive
- Instant UI rendering (< 100ms) from local SQLite cache.
- Complete offline capability: all saved opportunities and reminders remain fully browsable without network.
- Zero data loss during network disconnections due to durable queueing.
- Multi-user privacy guaranteed on shared mobile hardware.

### Negative
- Requires maintaining explicit Room migrations (`MIGRATION_1_2`) as data schemas evolve.
- Requires conflict resolution policy (deterministic timestamp and authoritative server merge) when server synchronization is introduced in future phases.
