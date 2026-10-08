# MNESA API Standards & Conventions

## 1. REST API Base Path & Versioning
- All public backend endpoints are prefixed with `/api/v1/`.
- Health, metrics, and observability endpoints are exposed under `/actuator/` and `/api/v1/health`.

## 2. Request Correlation
- Every incoming HTTP request must include or will be assigned an `X-Correlation-ID` header (UUID format).
- The correlation ID is placed in Spring's `MDC` (Mapped Diagnostic Context) and returned in the HTTP response headers for end-to-end tracing across client, backend, and AI service logs.

## 3. Standard Response Envelope
All successful responses return a structured JSON envelope:
```json
{
  "success": true,
  "data": { ... },
  "message": "Operation completed successfully",
  "timestamp": "2026-10-08T20:50:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

## 4. RFC 7807 Problem Details for Error Responses
All error responses return HTTP status code matching the RFC standard, with content type `application/problem+json`:
```json
{
  "type": "https://api.mnesa.ai/errors/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Opportunity with ID 18bf0a01-5231-7b90-84a1-0242ac120002 was not found",
  "instance": "/api/v1/opportunities/18bf0a01-5231-7b90-84a1-0242ac120002",
  "code": "OPPORTUNITY_NOT_FOUND",
  "timestamp": "2026-10-08T20:50:00Z",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "invalidParams": []
}
```
