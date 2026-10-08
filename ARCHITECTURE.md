# MNESA System Architecture Specification

## 1. Architectural Vision & Core Topology

MNESA is designed as a high-reliability, low-latency opportunity capture and follow-through platform. The architecture prioritizes ultra-fast mobile intake, offline resilience, deterministic data contracts, and a modular monolith backend.

```mermaid
graph TD
    subgraph Client Tier
        Android[Android Mobile Client<br/>Pure Java / Clean Arch / Room / WorkManager]
        Web[Next.js Web Client<br/>React 19 / TypeScript / Tailwind CSS]
    end

    subgraph Ingress & Gateway
        Nginx[Nginx Reverse Proxy / TLS Termination]
    end

    subgraph Backend Core [Modular Monolith]
        SpringApp[Spring Boot 3.4 API Core]
        ModAuth[Module: Auth & Users]
        ModOpp[Module: Opportunity Core]
        ModRem[Module: Reminders & Cadence]
        ModInt[Module: AI & Integrations]
        SpringApp --- ModAuth
        SpringApp --- ModOpp
        SpringApp --- ModRem
        SpringApp --- ModInt
    end

    subgraph Intelligence Tier
        AIService[FastAPI AI Service<br/>Python 3.13 / Pydantic v2]
        Gemini[Google Gemini Flash API]
        Ollama[Local Ollama Fallback]
        AIService --> Gemini
        AIService --> Ollama
    end

    subgraph Data & Persistence Tier
        Postgres[(PostgreSQL 16<br/>Primary Relational Store)]
        Valkey[(Valkey 8.0<br/>Cache & Rate Limiting)]
        MinIO[(MinIO Object Storage<br/>Raw Media & Screenshots)]
    end

    Android -->|REST / HTTPS| Nginx
    Web -->|REST / HTTPS| Nginx
    Nginx --> SpringApp
    ModInt -->|Internal REST / JSON| AIService
    SpringApp --> Postgres
    SpringApp --> Valkey
    SpringApp --> MinIO
```

---

## 2. The Core Capture Pipeline

The defining user experience of MNESA is instantaneous, frictionless intake from third-party Android applications via `android.intent.action.SEND`.

```mermaid
sequenceDiagram
    autonumber
    actor User
    participant 3rdParty as External App (LinkedIn/Browser)
    participant CaptureAct as Android Share Target (CaptureActivity)
    participant LocalRoom as Android Local Room DB
    participant WorkMgr as Android WorkManager
    participant Backend as Spring Boot API (/api/v1)
    participant AISvc as FastAPI AI Extraction Service
    participant Postgres as PostgreSQL DB

    User->>3rdParty: Sees Opportunity & taps "Share"
    3rdParty->>CaptureAct: Fires ACTION_SEND with text/URL/image
    CaptureAct->>LocalRoom: Saves capture immediately (Status: PENDING_SYNC)
    CaptureAct-->>User: Instant visual acknowledgement (< 200ms) & closes
    CaptureAct->>WorkMgr: Enqueues background sync job
    WorkMgr->>Backend: POST /api/v1/opportunities/capture (Payload, Idempotency-Key)
    Backend->>Postgres: Persists raw opportunity (Status: CAPTURED)
    Backend->>AISvc: POST /api/v1/extract (Sanitized payload)
    AISvc->>AISvc: Prompt-injection defense & schema extraction
    AISvc-->>Backend: Strict ExtractionResult (Deadlines, Requirements, Confidence)
    Backend->>Postgres: Updates opportunity (Status: UNDERSTOOD) & creates default reminders
    Backend-->>WorkMgr: Sync success (201 Created)
    WorkMgr->>LocalRoom: Updates local state to SYNCED
```

---

## 3. Module Boundaries & Backend Architecture

The backend adopts a **Modular Monolith** pattern. Features are encapsulated into clean packages communicating via strict domain interfaces and DTOs:

```text
com.mnesa.backend
├── common/               # Shared cross-cutting infrastructure
│   ├── dto/              # Standardized ApiResponse<T>, PageResponse<T>
│   ├── exception/        # Global RFC 7807 problem details handler
│   ├── filter/           # Correlation ID (MDC), Security headers
│   └── util/             # Date utilities, String sanitizers
├── config/               # Security, Database, Valkey, OpenAPI, Async configs
├── health/               # Health check and Actuator readiness probes
└── modules/              # Domain-bounded contexts
    ├── auth/             # Authentication, JWT issuance, Password hashing
    ├── user/             # User management, Profiles, Preferences
    ├── opportunity/      # Capture, Validation, Lifecycle state machine
    ├── reminder/         # Proactive scheduling, Notification engine
    └── ai/               # HTTP client integration with FastAPI AI Service
```

### Module Coupling Rules
1. Modules **must not** directly depend on JPA entities from another module. Communication between modules occurs strictly via Service interfaces and immutable DTOs.
2. The `ai` module serves as an anti-corruption layer for external AI services.
3. Database transactions are scoped at the service boundary with explicit `@Transactional` annotations.

---

## 4. Android Client Architecture

The mobile application is built with **Pure Java 21** using Android SDK 35, MVVM, and Clean Architecture:

```text
com.mnesa.android
├── core/                 # Cross-cutting mobile foundations
│   ├── base/             # BaseActivity, BaseFragment, BaseViewModel
│   ├── di/               # Hilt Dependency Injection modules
│   ├── network/          # Retrofit, OkHttp interceptors, NetworkCallback
│   └── utils/            # Resource wrappers, DateTime formatters
├── data/                 # Data Layer
│   ├── local/            # Room Database, DAOs, Local Entities
│   ├── remote/           # Retrofit API interfaces, Remote DTOs
│   └── repository/       # Repository implementations (SSOT pattern)
├── domain/               # Domain Layer (Pure Java, zero Android framework dependencies)
│   ├── model/            # Business models (Opportunity, Reminder, User)
│   ├── repository/       # Repository interfaces
│   └── usecase/          # Interactors / UseCases
└── presentation/         # Presentation Layer (XML + ViewBinding + MVVM)
    ├── main/             # Bottom navigation shell, dashboard
    ├── capture/          # Ultra-fast Share Intent ingestion dialog
    ├── opportunities/    # Opportunity list, detail, and filters
    └── viewmodel/        # AndroidX ViewModels emitting State/LiveData
```

### Mobile Invariants
- **Zero Kotlin:** Pure Java ensures maximum stability, deterministic bytecode compilation, and zero Kotlin-runtime overhead.
- **XML / Material 3:** Native XML View system with ViewBinding. No Jetpack Compose.
- **Offline First:** Room is the Single Source of Truth for all displayed UI states. Network responses mutate Room; UI observes Room.

---

## 5. AI Service Architecture

The AI extraction service is built with **FastAPI (Python 3.13)** and adheres to zero-trust processing principles:

```text
ai-service/
├── app/
│   ├── api/v1/           # API routes (/extract, /health)
│   ├── core/             # Configuration, logging, security sanitizers
│   ├── providers/        # BaseAIProvider, GeminiProvider, OllamaProvider
│   └── schemas/          # Pydantic v2 extraction request/response schemas
└── tests/                # Unit and integration tests
```

### Zero-Trust & Prompt-Injection Safeguards
1. **Sanitization:** All input content is stripped of control characters, null bytes, and delimiter collision patterns before inclusion in prompts.
2. **XML Tag Wrapping:** External content is strictly wrapped in `<untrusted_content>` tags with explicit system directives that nested text must never override extraction rules.
3. **Structured Outputs:** Models are strictly bound to Pydantic v2 schemas using JSON Schema constraints. Free-form text is rejected.
4. **Deterministic Validation:** Extracted deadlines must parse into valid ISO 8601 timestamps, and action URLs must pass RFC 3986 URL parsing with approved protocols (`http`, `https`).

---

## 6. Persistence & Caching Strategy

### Relational Database (PostgreSQL 16)
- **Primary Key Strategy:** Time-sortable UUIDs (UUIDv7) for all primary keys to guarantee distributed generation without sequential index fragmentation.
- **Audit Columns:** Every table includes `created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()` and `updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()`.
- **Flyway Migrations:** All schema alterations are recorded in versioned scripts (`V1__...sql`, `V2__...sql`). Manual DDL on production databases is forbidden.

### In-Memory Cache (Valkey 8.0)
- Used for distributed session tokens, rate limiting counters, and short-term caching of frequently accessed opportunities.

---

## 7. Security & Privacy Architecture

- **Zero Secrets in Client Bundles:** No private API keys or database credentials inside Android APKs or Next.js browser assets.
- **Correlation Tracking:** Every request receives a unique `X-Correlation-ID` header injected at ingress and propagated across Spring Boot MDC and AI Service logs.
- **RFC 7807 Error Standards:** Error responses adhere to Problem Details specification with domain error codes, preventing internal stack trace leaks.
