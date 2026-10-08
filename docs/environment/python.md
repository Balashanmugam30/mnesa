# Python Runtime & AI Service Virtual Environment Strategy

**Timestamp:** 2026-10-08T19:03:00+05:30  
**Project:** MNESA (AI Service / FastAPI)

---

## 1. Verified Python Runtime

- **Version:** Python `3.13.6` (64-bit)
- **Pip Version:** `pip 26.1.2`
- **Installation Path:** `C:\Users\balashanmugam\AppData\Local\Programs\Python\Python313\python.exe`
- **Module Support:** `venv` module natively verified and operational.

---

## 2. Virtual Environment Strategy

### Rule 1: Zero Global Dependencies
- Global Python packages must remain clean. No AI service dependencies (`fastapi`, `uvicorn`, `pydantic`, `google-genai`, etc.) shall be installed globally in the base Python interpreter.

### Rule 2: Dedicated Project Virtual Environment
- The AI service environment will reside in a project-local virtual environment:
  `ai-service/.venv`
- Creation command:
  ```powershell
  python -m venv ai-service/.venv
  ```
- Activation command:
  ```powershell
  .\ai-service\.venv\Scripts\Activate.ps1
  ```

### Rule 3: Dependency Specification
- Dependencies must be managed with deterministic lockfiles or pinned `pyproject.toml` / `requirements.txt`.
- Native extensions and compiled packages must target Python 3.13 (or use verified wheels).
- All AI dependencies will be introduced cleanly during Phase 1 AI service development, not prematurely during environment bootstrap.
