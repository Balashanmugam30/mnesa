---
name: mnesa-performance
description: MNESA Performance engineering guidelines covering ultra-fast share intake, asynchronous processing, caching strategies, and mobile startup efficiency.
---

# MNESA Performance Engineering Guidelines

## 1. Ultra-Fast Share Intake
- **Target Latency:** Capture activity on Android must finish UI interaction in under 200ms.
- **Asynchronous Handoff:** Never block the user while waiting for web scraping or LLM extraction. Store locally in Room and queue a WorkManager task to dispatch to backend.

## 2. Backend Throughput & Caching
- **Valkey Caching:** Cache extracted opportunities and public metadata with appropriate TTLs.
- **Asynchronous Execution:** Use Spring `@Async` or message queues for heavy external operations (fetching pages, calling Gemini, generating image thumbnails).
- **Database Connection Pooling:** Configure HikariCP pool sizing appropriately for CPU core count.

## 3. Mobile Performance & Battery Hygiene
- **Startup Time:** Keep Application class initialization minimal. Lazy-load non-essential third-party SDKs.
- **WorkManager Constraints:** Require `NetworkType.CONNECTED` and battery-not-low constraints for heavy background syncs.
- **Image Loading:** Optimize image caching using Glide or Coil with downsampled thumbnails.
