package com.mnesa.android.domain.model;

/**
 * Full lifecycle states of an opportunity in the MNESA pipeline.
 * Intake states: CAPTURED, PROCESSING, UNDERSTOOD
 * Active lifecycle: SAVED, REVIEWING, APPLYING, APPLIED, WAITING, SELECTED, REJECTED, MISSED, ARCHIVED
 * Legacy backward-compatibility: ACTED, COMPLETED
 */
public enum OpportunityStatus {
    CAPTURED,
    PROCESSING,
    UNDERSTOOD,
    SAVED,
    REVIEWING,
    APPLYING,
    APPLIED,
    WAITING,
    SELECTED,
    REJECTED,
    MISSED,
    ARCHIVED,
    ACTED,
    COMPLETED
}
