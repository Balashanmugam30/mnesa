# MNESA Security Policy & Architecture

## 1. Security Philosophy & Invariants

MNESA operates under a strict **Zero-Trust Input** posture. Because MNESA is designed to capture, parse, and analyze arbitrary content shared from third-party applications and public web pages, all external data is considered inherently untrusted and hostile until validated.

---

## 2. Core Security Controls

### 2.1 Prompt-Injection Defense
- **Delimiter Sanitization:** Any incoming raw text or web scrape payload is stripped of system instructions and delimiter tricks.
- **XML Tag Encapsulation:** Untrusted external content is strictly wrapped in `<untrusted_content>` tags when supplied to LLM context windows. System prompts explicitly instruct models that instructions within those tags must be treated as inert data and ignored.
- **Strict Structured Outputs:** All extraction requests use native JSON Schema validation via Pydantic v2. Free-form text outputs from AI providers are strictly rejected.

### 2.2 Server-Side Request Forgery (SSRF) Prevention
- External URL scraping must only occur from isolated worker environments with egress rules restricting access to internal subnet ranges (`10.0.0.0/8`, `172.16.0.0/12`, `192.168.0.0/16`, `127.0.0.1/8`, `169.254.169.254`).
- DNS resolution checks must verify that target domains do not resolve to private or loopback addresses before making HTTP requests.

### 2.3 Secrets Hygiene
- **Zero Secrets in Client Bundles:** No private API keys, database connection strings, or cloud tokens may be bundled inside the Android APK or Next.js browser bundles.
- **No Committed Secrets:** Repository commits are scanned via pre-commit hooks and CI secret scanning.

### 2.4 Cryptography & Authentication
- **Password Hashing:** Passwords are stored exclusively using Argon2id (`m=65536, t=3, p=4`).
- **Stateless Tokens:** API sessions utilize short-lived cryptographically signed JWT tokens with asymmetric or high-entropy HMAC verification.
- **Transport Security:** All communication between mobile/web clients and backend services requires TLS 1.3 in production.

---

## 3. Reporting a Vulnerability

If you discover a security vulnerability within MNESA, please report it immediately:
- **Email:** `security@mnesa.ai`
- **Response SLA:** Acknowledgment within 24 hours; initial assessment within 72 hours.
- **Responsible Disclosure:** We ask that you do not disclose issues publicly until we have had an opportunity to address them.
