# MNESA Initial Environment Inventory

**Timestamp:** 2026-10-08T18:56:00+05:30  
**Host Machine:** Windows 11 Home Single Language (Build 10.0.26200, 64-bit)  
**Workspace:** `C:\Users\balashanmugam\OneDrive\Desktop\Projects\Mnesa`

---

## 1. Hardware & System Specifications

| Attribute | Detected Value | Status / Assessment |
| :--- | :--- | :--- |
| **Operating System** | Microsoft Windows 11 Home Single Language (Version 10.0.26200) | Compatible |
| **Architecture** | 64-bit (x86_64 / AMD64) | Compatible |
| **CPU Architecture** | 13th Gen Intel(R) Core(TM) i5-13450HX (10 Cores, 16 Logical Processors) | High-performance |
| **Total RAM** | 24,866,680 KB (~23.7 GB) | Adequate for Android Emulator + Docker + Dev Services |
| **Free RAM** | ~2.7 GB free physical memory at inspection | Needs resource budgeting during emulator launch |
| **Disk Space** | Drive C: 297 GB Used / 212.7 GB Free | Ample space for SDKs, emulators, and Docker images |
| **Virtualization Support** | `HypervisorPresent: True` (Intel VT-x enabled) | Ready for hardware-accelerated emulation & Docker |
| **WSL Availability** | WSL 2 Installed (`Ubuntu` v2, `docker-desktop` v2) | WSL 2 ready |

---

## 2. Runtimes & Languages

| Component | Detected State / Version | Baseline Path | Target Requirement | Action Needed |
| :--- | :--- | :--- | :--- | :--- |
| **Java (Runtime)** | Oracle JRE 1.8.0_481 | `C:\Program Files (x86)\Common Files\Oracle\Java\java8path\java.exe` | JDK 21 LTS | **Upgrade required:** Install JDK 21 (Microsoft OpenJDK 21) & set `JAVA_HOME` |
| **Java Compiler (`javac`)** | Missing (only JRE 8 present) | N/A | JDK 21 | Install JDK 21 |
| **Node.js** | v22.20.0 | `C:\Program Files\nodejs\node.exe` | >= 20.9 (Next.js baseline) | **Preserve:** Meets stable LTS requirements |
| **npm** | 10.9.3 | `C:\Program Files\nodejs\npm.ps1` | Compatible with Node 22 | **Preserve:** Healthy |
| **Python** | Python 3.13.6 | `C:\Users\balashanmugam\AppData\Local\Programs\Python\Python313\python.exe` | Python 3.13.x | **Preserve:** Matches preferred AI service runtime |
| **pip** | 26.1.2 | `C:\Users\balashanmugam\AppData\Local\Programs\Python\Python313\Lib\site-packages\pip` | Current | **Preserve:** Healthy |

---

## 3. Version Control & Collaboration

| Component | Detected Version | Path | Status |
| :--- | :--- | :--- | :--- |
| **Git** | git version 2.50.1.windows.1 | `C:\Program Files\Git\cmd\git.exe` | Healthy |
| **Git LFS** | git-lfs/3.7.0 | `C:\Program Files\Git\cmd\git-lfs.exe` | Available |
| **GitHub CLI (`gh`)** | gh version 2.86.0 | `C:\Program Files\GitHub CLI\gh.exe` | Installed |

---

## 4. Containerization & Infrastructure

| Component | Detected Version | Path / State | Status / Action |
| :--- | :--- | :--- | :--- |
| **Docker CLI** | Docker version 29.8.2 | `C:\Program Files\Docker\Docker\resources\bin\docker.exe` | Installed |
| **Docker Compose** | Docker Compose version v5.5.1 | Built-in CLI plugin | Installed |
| **Docker Engine / Daemon** | Stopped (Docker Desktop not active) | WSL 2 / Windows Named Pipe | Start Docker Desktop service when dev stack needed |

---

## 5. Android Tooling Baseline

| Component | Detected State | Status / Required Action |
| :--- | :--- | :--- |
| **Android CLI (`android`)** | Not detected | Install via `winget install --id Google.AndroidCLI` |
| **Android Studio** | Not detected in standard paths | Install latest stable Android Studio (`Google.AndroidStudio`) |
| **Android SDK** | Not detected (`%LOCALAPPDATA%\Android\Sdk` absent) | Provision via Android Studio / CLI with latest stable platform & tools |
| **Android Debug Bridge (`adb`)** | Not detected | Will be provisioned with Android SDK Platform-Tools |
| **Android Emulator** | Not detected | Will be provisioned with Android SDK Emulator package |
| **AVD Manager (`avdmanager`)** | Not detected | Will be provisioned with cmdline-tools |
| **SDK Manager (`sdkmanager`)** | Not detected | Will be provisioned with cmdline-tools |

---

## 6. Build Systems

| Component | Detected State | Status / Required Action |
| :--- | :--- | :--- |
| **Maven (`mvn`)** | Not installed globally | Not strictly needed (Spring Boot project uses Gradle Wrapper) |
| **Gradle (`gradle`)** | Not installed globally | Will use Gradle Wrapper (`gradlew`) in backend & Android projects |

---

## 7. Antigravity IDE Plugins & Capabilities

| Capability / Plugin | Detected State | Version | Details |
| :--- | :--- | :--- | :--- |
| `agent-skills` | Installed & Enabled | Builtin | Core agent skill definitions |
| `chrome-devtools-plugin` | Installed & Enabled | 1.0.0 | DevTools MCP & browser debugging |
| `firebase` | Installed & Enabled | 1.0.3 | Firebase CLI, rules, crashlytics, etc. |
| `gemini-api` | Installed & Enabled | 2.2.0 | Gemini API & SDK integrations |
| `modern-web-guidance-plugin`| Installed & Enabled | 1.0.6 | Modern web APIs, CSS, frameworks |
| `android` / Android CLI bundle | Not yet installed | - | Required to be installed in Part 2/3 |

---

## 8. Antigravity MCP Servers

Configured globally in `C:\Users\balashanmugam\.gemini\config\mcp_config.json`:
- `21st`: UI component assistance
- `MCP_DOCKER`: Docker MCP gateway
- `StitchMCP`: Google UI design system integration
- `chrome-devtools`: DevTools MCP server
- `context7`: Library documentation query
- `graphify`: Knowledge graph MCP
- `inspo`: Screen inspiration
- `lightswind`: Tailwind/shadcn component references
- `magicuidesign-mcp`: MagicUI registry
- `mobbin`: Mobile UI patterns
- `motion`: Animation references
- `playwright`: Browser automation MCP
- `ponytail`: Tooling helper
- `shadcn`: UI registry
- `vercel`: Vercel deployments & docs
- `github`: Official GitHub MCP server (`@modelcontextprotocol/server-github`)
- `supabase`: (Existing global config, will NOT be used for MNESA)

---

## 9. IDE Extensions (VS Code / Antigravity)

Detected installed extensions:
- `ms-python.python`
- `ms-python.vscode-pylance`
- `ms-azuretools.vscode-containers`
- `anthropic.claude-code`
- Various python & utility extensions

Missing extensions to install:
- `vscjava.vscode-java-pack` (Extension Pack for Java)
- `vmware.vscode-spring-boot` (Spring Boot Extension Pack)
- `esbenp.prettier-vscode` (Prettier)

---

## 10. Summary of Initial Deficits & Action Plan

1. **Java:** JRE 8 is active. Must install Microsoft OpenJDK 21, verify `java` / `javac`, set `JAVA_HOME`.
2. **Android CLI:** Install `Google.AndroidCLI` via winget, update, initialize, add skills.
3. **Android Studio & SDK:** Install `Google.AndroidStudio`, SDK platform, build-tools, platform-tools, emulator, system-image, create one AVD.
4. **IDE Extensions:** Install Java extension pack, Spring Boot tools, Prettier.
5. **Docker:** Verify Docker Desktop launch and WSL 2 backend.
6. **Infrastructure Skeleton:** Prepare `compose.dev.yml` (PostgreSQL, Valkey, MinIO) and `.env.example`.
7. **Workspace Configuration:** Create `.agents/mcp_config.json`, `.agents/skills/*`, `AGENTS.md`, and docs.
