package com.mnesa.android.domain.model;

/**
 * Lifecycle states of an opportunity in the MNESA pipeline.
 * SEE → SHARE → UNDERSTAND → SAVE → REMIND → ACT → COMPLETE
 */
public enum OpportunityStatus {
    CAPTURED,
    PROCESSING,
    UNDERSTOOD,
    SAVED,
    ACTED,
    COMPLETED,
    ARCHIVED
}
