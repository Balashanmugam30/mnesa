# MNESA Development Security Baseline & Hygiene Policies

**Timestamp:** 2026-10-08T19:55:00+05:30  
**Project:** MNESA

---

## 1. Secrets Policy & Credential Isolation

1. **Zero Secret Commits:** No API keys, database passwords, private keys, JWT signing secrets, or cloud credentials may ever be committed to the Git repository.
2. **Local Environment Templates:** Secrets are defined exclusively via `.env.example` templates with sanitized dummy placeholders. Real local development values must reside in gitignored `.env.dev` or `.env.local` files.
3. **No Client Bundling:** Under no circumstances should backend secrets (e.g., PostgreSQL credentials, Valkey passwords, Gemini API keys, MinIO root credentials) be referenced or compiled into the Android client APK or Next.js public client bundles.

---

## 2. Environment Variable Policy

- Backend Spring Boot configuration must read environment variables hierarchically:
  `application.yml` -> environment variable overrides (`SPRING_DATASOURCE_PASSWORD`, etc.).
- Website Next.js frontend variables must strictly separate client-exposed values (`NEXT_PUBLIC_*`) from server-only values. Server-only keys must never leak into client components.
- AI service FastAPI secrets (e.g. `GEMINI_API_KEY`) must be loaded strictly via Pydantic `BaseSettings` reading from protected environment variables.

---

## 3. Git Hygiene & Secret Scanning

- `.gitignore` must strictly exclude:
  - `.env`, `.env.*.local`, `.env.dev`
  - Keystores (`*.jks`, `*.keystore`)
  - Google Services credentials (`google-services.json`, `GoogleService-Info.plist`)
  - IDE caches (`.idea/`, `.vscode/`, `*.iml`)
  - Build outputs (`build/`, `dist/`, `.next/`, `bin/`)
  - Virtual environments (`.venv/`, `venv/`, `__pycache__/`)
- Automated secret scanning (via GitHub Secret Scanning and pre-commit checks) must run prior to merging pull requests.

---

## 4. Dependency Security & Vulnerability Audits

- **Java / Backend:** Gradle dependency verification metadata and OWASP dependency-check must run during CI.
- **Node.js / Frontend:** `npm audit` must pass without high or critical vulnerabilities before deployment.
- **Python / AI Service:** Virtual environments must be isolated. Package hashes must be verified using `pip-audit` or pinned requirement hashes.

---

## 5. URL Validation & SSRF Prevention

Since MNESA's core user action involves capturing URLs shared from external Android apps:
1. **Zero Trust for Inbound URLs:** Every incoming shared URL must be validated deterministically by the backend before fetching or dispatching to the AI extraction pipeline.
2. **Private Network Blacklisting:** The backend URL fetcher must strictly block requests targeting:
   - Loopback addresses (`127.0.0.0/8`, `::1`)
   - RFC 1918 private subnets (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`)
   - Link-local and cloud metadata endpoints (`169.254.169.254`)
   - Internal Docker network hostnames (`postgres`, `valkey`, `minio`, `kafka`)
3. **DNS Rebinding Protection:** Resolve hostnames to IPs before requesting and ensure subsequent HTTP connections connect strictly to the validated public IP.
4. **Protocols:** Only `http` and `https` schemes are permitted; file, gopher, ftp, and custom schemes are strictly rejected.

---

## 6. Test Data Rules & Synthetic Data Policy

- No production user data, real emails, or confidential opportunity documents may ever be used in automated tests or committed test fixtures.
- All test suites must use synthetic mock datasets or Testcontainers instances populated on-the-fly.

---

## 7. Model Context Protocol (MCP) Principle of Least Privilege

- Workspace MCP servers must be bounded to local loopback connections or read-only roles where possible.
- The PostgreSQL MCP server is scoped exclusively to local development (`127.0.0.1:5432`) and must never be pointed at staging or production clusters.
- All MCP tools that perform mutations or file modifications require explicit developer awareness and scoped paths.
