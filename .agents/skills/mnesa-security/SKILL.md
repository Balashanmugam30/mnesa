---
name: mnesa-security
description: MNESA Security & Hardening engineering standards covering SSRF prevention, IDOR, authentication, rate limiting, and secure file handling.
---

# MNESA Security Engineering Standards

## 1. Server-Side Request Forgery (SSRF) Defenses
- Inbound shared URLs must never be fetched blindly.
- Resolve hostnames to IPs before opening sockets; block loopback, link-local, private RFC 1918 addresses, and container service names (`postgres`, `valkey`, `minio`).
- Reject non-standard ports (allow only 80 and 443).
- Disable automatic redirect following across IP boundaries.

## 2. Insecure Direct Object References (IDOR) & Authorization
- Every database query accessing opportunities, reminders, or user preferences must scope to the authenticated user ID (`WHERE user_id = :authenticatedUserId`).
- Enforce method-level security in Spring Boot with `@PreAuthorize`.

## 3. Authentication & Password Hashing
- Use Argon2id for password hashing with tuned memory and iteration parameters.
- JWT access tokens must have short lifespans (e.g., 15 minutes) with refresh token rotation stored securely in Valkey or PostgreSQL.

## 4. Input Sanitization & File Upload Safety
- Shared image uploads (flyers, screenshots) must be inspected for MIME type headers and verified via magic bytes inspection, never trusting client-supplied file extensions.
- Process images inside isolated sandboxes or MinIO buckets with randomized UUID file names.
- Rate limit incoming share capture and extraction endpoints by IP and user ID via Valkey sliding window rate limiters.
