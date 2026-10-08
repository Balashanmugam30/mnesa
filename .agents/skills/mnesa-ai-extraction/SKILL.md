---
name: mnesa-ai-extraction
description: MNESA AI Extraction service guidelines covering prompt injection defense, strict structured output, confidence scoring, evidence verification, and multi-provider abstraction.
---

# MNESA AI Opportunity Extraction Engine

## 1. Zero-Trust Content Processing
- **Untrusted Input Invariant:** All web scrape text, shared URLs, image OCR text, and user-provided captions are inherently hostile and untrusted.
- **Prompt Injection Defenses:**
  - Strip control characters and sanitize input delimiters before feeding to LLM context.
  - Wrap external content in strict XML or markdown escape tags (e.g., `<untrusted_content>...</untrusted_content>`).
  - Instruct the model that instructions inside the untrusted content tags must never override extraction rules.

## 2. Strict Structured Output
- **Pydantic Validation:** All model outputs must conform to strict Pydantic schemas. Freeform text responses are strictly rejected.
- **Extraction Schema Contract:**
  - `title`: String (1 to 200 chars).
  - `organization`: String (1 to 150 chars).
  - `opportunity_type`: Enum (`INTERNSHIP`, `JOB`, `HACKATHON`, `SCHOLARSHIP`, `COMPETITION`, `EVENT`, `CONFERENCE`, `COURSE`, `OTHER`).
  - `deadline_at`: ISO 8601 Datetime string or null.
  - `key_requirements`: List of strings.
  - `action_url`: Validated HTTP/HTTPS URL.
  - `confidence_score`: Float between `0.0` and `1.0`.
  - `evidence_snippets`: List of verbatim quote snippets from the source text justifying the extracted deadline and requirements.

## 3. Provider Abstraction
- **Abstract Interface:** Define `BaseExtractionProvider` with async method `extract_opportunity(content: ExtractionPayload) -> ExtractionResult`.
- **Implementations:**
  - `GeminiExtractionProvider`: Primary cloud model utilizing Google Gemini 2.5/3.x Flash with native JSON schema constraints.
  - `OllamaExtractionProvider`: Local fallback model running local open models (e.g., Llama 3 / Qwen) for air-gapped or low-latency local runs.
  - `TesseractOcrProvider`: Pre-processing provider for shared screenshots and flyers before text extraction.

## 4. Deterministic Post-Validation
- Verify that extracted dates parse into valid future or past calendar dates.
- Check that `action_url` matches safe domain protocols.
- Compute composite confidence score incorporating text length, date certainty, and provider scoring.
