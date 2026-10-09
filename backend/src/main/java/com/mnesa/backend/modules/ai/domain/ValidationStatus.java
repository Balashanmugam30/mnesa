package com.mnesa.backend.modules.ai.domain;

public enum ValidationStatus {
    SUCCEEDED,
    SUCCEEDED_WITH_WARNINGS,
    LOW_CONFIDENCE,
    UNSUPPORTED_SOURCE,
    CONTENT_UNAVAILABLE,
    PROVIDER_UNAVAILABLE,
    FAILED
}
