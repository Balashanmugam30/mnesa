package com.mnesa.backend.modules.intake.domain;

/**
 * Execution status of an intake job.
 */
public enum IntakeJobStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
