package com.mnesa.backend.modules.intake.domain;

/**
 * Status lifecycle of a captured intake record.
 */
public enum CaptureStatus {
    RECEIVED,
    QUEUED,
    PROCESSING,
    COMPLETED,
    FAILED
}
