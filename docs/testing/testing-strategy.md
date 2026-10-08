# MNESA Testing Strategy

## 1. Testing Pyramid Across Tiers

```text
       /\
      /  \      E2E (Playwright, Espresso)
     /----\     
    /      \    Integration (Spring Boot Test, Testcontainers, Pytest)
   /--------\   
  /          \  Unit Tests (JUnit 5, Mockito, Pytest, React Testing)
 /------------\
```

## 2. Backend (Spring Boot)
- **Unit Tests:** JUnit 5 + Mockito for fast unit tests of domain services, validation logic, and utility functions.
- **Controller Tests:** `@WebMvcTest` with MockMvc for validating request serialization, RFC 7807 problem details handling, and validation annotations.
- **Integration Tests:** `@SpringBootTest` with Testcontainers for PostgreSQL database integration tests.

## 3. Android Client (Native Java)
- **Unit Tests:** JUnit 4/5 + Mockito for Domain UseCases, Repository logic, and ViewModel state transformations.
- **Instrumented Tests:** AndroidX Test Runner + Espresso for testing user interactions and UI components.

## 4. AI Service (Python FastAPI)
- **Unit Tests:** `pytest` + `pytest-asyncio` testing Pydantic schema validation, input sanitization, and fallback provider behavior.
- **Mocking:** Tests run completely offline with mock responses when no live Gemini or Ollama credentials are configured.

## 5. Web Platform (Next.js)
- **Component Tests:** React component testing verifying design token application and accessibility markup.
- **E2E & Smoke Tests:** Playwright verifying server rendering, page navigation, accessibility landmarks, and visual integrity.
