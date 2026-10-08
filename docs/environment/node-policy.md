# Node.js Version Policy & Package Management

**Timestamp:** 2026-10-08T19:03:00+05:30  
**Project:** MNESA (Web Platform & Frontend Tooling)

---

## 1. Node.js Specifications

- **Required Minimum Node Version:** Node.js `>= 20.9.0` (strict Next.js requirement).
- **Selected Development Version:** Node.js `v22.20.0` (Active LTS).
- **Package Manager:** `npm 10.9.3`.
- **Node Binary Location:** `C:\Program Files\nodejs\node.exe`.

---

## 2. Policy Guidelines

### Rule 1: Node.js Runtime Updates
- The team develops exclusively on Active LTS releases (currently v22 LTS or v24 LTS when broadly stabilized across all build toolchains).
- Odd-numbered releases (e.g., v23, v25) are strictly prohibited in production and development as they are non-LTS interim releases.
- Runtime upgrades must be verified against Next.js build scripts and Playwright browser drivers before updating team developer images.

### Rule 2: Dependency Pinning & Lockfile Enforcement
- All web project dependencies must be committed with `package-lock.json`.
- In CI/CD and clean development builds, dependencies must be installed using `npm ci` rather than `npm install` to enforce exact version fidelity.
- Direct dependencies must use explicit semver constraints (avoiding unpinned `*` or floating ranges that allow unexpected major breaking changes).

### Rule 3: Client Bundle Security & Zero-Secret Policy
- Never expose environment variables without the explicit `NEXT_PUBLIC_` prefix when intended for the browser.
- Never place backend database credentials, private API keys (Gemini API keys, MinIO secret keys), or internal service secrets in any web package or `.env.local` files exposed to client builds.
