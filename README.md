# MNESA

<div align="center">

**Capture it. We'll remember.**  
*Never miss what matters.*

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](#)
[![License](https://img.shields.io/badge/license-Proprietary-blue)](#)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange)](#)
[![Android](https://img.shields.io/badge/Android-SDK%2035%20(Pure%20Java)-green)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen)](#)
[![Python](https://img.shields.io/badge/Python-3.13-yellow)](#)
[![Next.js](https://img.shields.io/badge/Next.js-15%20(App%20Router)-black)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](#)

</div>

---

## 1. Executive Summary

**MNESA** is an AI-powered opportunity capture and follow-through platform designed for ambitious individuals who discover critical opportunities while busy and risk forgetting them.

Whether it is an internship, job posting, hackathon, scholarship, competition, research grant, workshop, conference, or deadline-sensitive application, MNESA transforms passive discovery into proactive execution:

```
SEE → SHARE → UNDERSTAND → SAVE → REMIND → ACT → COMPLETE
```

### The Core Problem Solved
People encounter high-value career and educational opportunities in fragmented environments (LinkedIn, Twitter/X, Instagram, WhatsApp, Telegram, email, campus portals). Traditional capture methods fail:
- Bookmarks are forgotten graves.
- Taking screenshots accumulates unsearchable image clutter.
- Messaging oneself loses context and deadline urgency.
- Manually creating reminders incurs high cognitive friction.

### The MNESA Solution
The fastest path to capture something important is the default path. With native Android sharing (`ACTION_SEND`), sharing any URL, text snippet, or screenshot to MNESA captures it in less than 200ms without interrupting the user's flow. MNESA's backend and AI service extract actionable metadata, structured deadlines, and key requirements, and automatically orchestrate a proactive reminder cadence.

---

## 2. Locked Architecture & Technology Stack

MNESA is built on an enterprise-grade, production-oriented architecture with strict technological invariants:

| Tier | Technology | Purpose | Invariants |
| :--- | :--- | :--- | :--- |
| **Android Client** | Pure Java 21, Android SDK 35 | Native mobile client, system share target | XML/View system, Material 3, MVVM, Clean Architecture, Hilt, Room, Retrofit, OkHttp, RxJava 3, WorkManager. **No Kotlin, No Compose.** |
| **Backend API** | Java 21, Spring Boot 3.4 | Core business logic, data persistence, scheduling | Modular monolith first, Spring Security, Spring Data JPA, Hibernate, Bean Validation, Flyway migrations, Gradle Wrapper. |
| **AI Service** | Python 3.13, FastAPI | Intelligence extraction, prompt-injection defense | Provider abstraction (Gemini + Local Ollama), strict Pydantic v2 schemas, deterministic confidence scoring. |
| **Web Platform** | Next.js 15, React 19, TypeScript | Dashboard, marketing, opportunity review | App Router, Tailwind CSS, Framer Motion, strict type safety, WCAG 2.1 AA accessibility. |
| **Primary Database**| PostgreSQL 16 | Single source of truth relational store | Flyway-managed versioned migrations, UUIDv7, strict foreign keys, check constraints. |
| **In-Memory Cache** | Valkey 8.0 | Cache, session store, rate limiting | High-throughput distributed cache. |
| **Object Storage**  | MinIO | S3-compatible asset & document storage | Local development and on-premises asset storage. |
| **Streaming**       | Apache Kafka (KRaft) | Event stream processing | Introduced when scale and decoupled event streaming justify it. |

---

## 3. Repository Structure

```text
mnesa/
├── .agents/                 # Antigravity agent configuration, MCP servers, and skills
├── .github/                 # GitHub Actions CI/CD workflows and automation
├── android/                 # Native Android application (Pure Java, Material 3, Clean Architecture)
│   ├── app/                 # Application module
│   ├── build.gradle         # Root Android build configuration
│   └── settings.gradle      # Android project settings
├── backend/                 # Spring Boot modular monolith (Java 21, Gradle)
│   ├── src/main/java/       # Domain modules (auth, user, opportunity, reminder, ai)
│   ├── src/main/resources/  # Application config, Flyway migrations (db/migration)
│   └── src/test/java/       # Comprehensive JUnit 5 & Mockito test suites
├── ai-service/              # FastAPI AI extraction service (Python 3.13)
│   ├── app/                 # FastAPI routes, schemas, providers, security sanitizers
│   └── tests/               # pytest test suite
├── website/                 # Next.js 15 web platform (TypeScript, Tailwind CSS, Playwright)
│   ├── src/app/             # App Router pages and API routes
│   ├── src/components/      # Reusable design system primitives
│   └── tests/               # Playwright automated E2E and visual tests
├── infrastructure/          # Local Docker development services & deployment configs
│   └── docker/              # Docker Compose manifests (PostgreSQL, Valkey, MinIO, Kafka)
├── docs/                    # Architecture, API specifications, and runbooks
│   ├── architecture/        # System design, data flow diagrams, ADRs
│   ├── api/                 # REST contracts, OpenAPI schemas, error specifications
│   ├── database/            # Schema diagrams, Flyway migration guidelines
│   ├── environment/         # Environment setup inventories and diagnostics
│   └── security/            # Threat models, prompt-injection defenses, compliance
├── scripts/                 # Developer automation scripts (PowerShell, Bash)
├── AGENTS.md                # System invariants & engineering tenets
├── ARCHITECTURE.md          # Architectural specification and module boundaries
├── DESIGN.md                # Centralized Design System tokens & guidelines
├── CONTRIBUTING.md          # Engineering standards, code style, git workflows
└── SECURITY.md              # Security policies, disclosure, and threat models
```

---

## 4. Local Development Quickstart

### Prerequisites
Verify that the following runtimes are installed (validated in `docs/environment/environment-inventory.md`):
- **JDK 21 LTS** (`java -version`, `JAVA_HOME` set)
- **Node.js 22 LTS & npm 10+** (`node -v`, `npm -v`)
- **Python 3.13+** (`python --version`)
- **Android SDK (API 35)** (`adb --version`)
- **Docker Desktop / Docker Engine** (`docker compose version`)

### 1. Start Infrastructure Services
Start the local PostgreSQL, Valkey, and MinIO development services via Docker Compose:
```powershell
docker compose -f infrastructure/docker/compose.dev.yml up -d
```
Verify all containers report healthy:
```powershell
docker compose -f infrastructure/docker/compose.dev.yml ps
```

### 2. Start Backend Service
```powershell
cd backend
./gradlew bootRun
```
The API is available at `http://localhost:8080/api/v1` and health check at `http://localhost:8080/actuator/health`.

### 3. Start AI Service
```powershell
cd ai-service
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 8000 --reload
```
Health endpoint: `http://localhost:8000/health`.

### 4. Start Web Platform
```powershell
cd website
npm install
npm run dev
```
Website is accessible at `http://localhost:3000`.

### 5. Build & Run Android Application
```powershell
cd android
./gradlew assembleDebug
```
To install on a connected emulator or device:
```powershell
./gradlew installDebug
```

---

## 5. Design System

MNESA features a bespoke, accessible, cross-platform design system grounded in:
- **Primary:** Energetic Deep Indigo (`#2563EB`)
- **Urgency/Deadline:** Vibrant Amber (`#F59E0B`) and Alert Coral (`#EF4444`)
- **Success:** Crisp Emerald (`#10B981`)
- **Accessibility:** Strict WCAG 2.1 AA compliance with minimum 4.5:1 contrast ratio and 48x48dp touch targets.

See [`DESIGN.md`](./DESIGN.md) for complete design tokens, component guidelines, and implementation rules.

---

## 6. Engineering Tenets

1. **Zero-Trust Web Input:** All external text, URLs, and screenshots are untrusted. Sanitization and prompt-injection defenses are mandatory.
2. **Modular Monolith First:** Domain boundaries reside in a single deployable backend artifact. No premature microservices.
3. **Pure Native Java on Android:** No Kotlin, no Jetpack Compose. Rock-solid XML/View system and Material Components.
4. **Strict Flyway Migrations:** All database changes must be versioned SQL scripts. No manual database alterations.
5. **Mandatory Testing:** Every endpoint, screen, and module requires unit and integration test coverage.

---

## 7. License

Copyright © 2026 MNESA. All rights reserved.
