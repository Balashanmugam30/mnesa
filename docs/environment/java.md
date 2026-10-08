# Java Development Baseline & JDK Selection

**Timestamp:** 2026-10-08T19:03:00+05:30  
**Project:** MNESA

---

## 1. Selected JDK Distribution

- **Distribution:** Microsoft Build of OpenJDK with Hotspot 21 (LTS)
- **Version:** `21.0.12.1+1-LTS` (Release: August 2026)
- **Architecture:** `x64` (64-bit Windows)
- **Installation Path:** `C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot`
- **Environment Variable (`JAVA_HOME`):** `C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot`
- **Primary Binary (`java`):** `C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot\bin\java.exe`
- **Compiler Binary (`javac`):** `C:\Program Files\Microsoft\jdk-21.0.12.101-hotspot\bin\javac.exe`

---

## 2. Selection Rationale

1. **Spring Boot 3.x / 4.x Requirements:**
   MNESA's backend is built on modern Spring Boot. Spring Boot 3 requires Java 17 as a strict minimum and natively targets Java 21 for modern Virtual Threads (Project Loom), structured concurrency, and long-term enterprise support (LTS).

2. **Android Gradle Plugin (AGP) Compatibility:**
   Modern Android Gradle Plugin (AGP 8.x / 9.x) requires JDK 17+ at minimum and runs cleanly on JDK 21. Utilizing JDK 21 across both backend and Android builds harmonizes the developer toolchain and prevents classpath fragmentation.

3. **Performance & Language Features:**
   JDK 21 provides generational ZGC, pattern matching for switch, record patterns, and virtual threads, which will benefit backend high-concurrency event handling and processing pipelines.

4. **IDE & JetBrains Runtime Preservation:**
   If Android Studio's bundled JetBrains Runtime (`jbr`) is utilized by the Android Studio IDE UI process, it remains completely untouched in its own private directory. `JAVA_HOME` explicitly points to the Microsoft OpenJDK 21 distribution for command-line builds, Gradle wrappers, and backend development.

---

## 3. Verification Evidence

```text
openjdk version "21.0.12.1" 2026-08-18 LTS
OpenJDK Runtime Environment Microsoft-14941484 (build 21.0.12.1+1-LTS)
OpenJDK 64-Bit Server VM Microsoft-14941484 (build 21.0.12.1+1-LTS, mixed mode, sharing)
javac 21.0.12.1
```
