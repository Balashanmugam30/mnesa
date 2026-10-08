---
name: mnesa-release
description: MNESA Release engineering guidelines covering CI/CD, versioning, release builds, Play Store preparation, and rollback strategies.
---

# MNESA Release Engineering & Delivery

## 1. Release Architecture & Versioning
- **Semantic Versioning:** Follow `MAJOR.MINOR.PATCH` for backend, AI service, and web platforms.
- **Android Versioning:** Increment `versionCode` monotonically with every build; format `versionName` as `MAJOR.MINOR.PATCH`.
- **Git Branching:** Trunk-based development with short-lived feature branches (`feat/*`, `fix/*`).

## 2. CI/CD Quality Gates
- **GitHub Actions Workflows:**
  - Automated linting, static analysis (CodeQL, SpotBugs).
  - Test suites execution with 100% pass requirement.
  - Container image builds with multi-stage security scanning (Trivy).
  - Android APK / AAB signing via encrypted GitHub Secrets.

## 3. Production Readiness & Play Store Checklist
- Minification and obfuscation enabled via R8 with audited keep rules.
- App Bundle (`.aab`) generation targeting Google Play 64-bit architecture.
- Rollback strategies: Automated database backward-compatible migrations and canary container deployments.
