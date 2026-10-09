package com.mnesa.android.domain.model;

/**
 * State machine stages for the Android Share Intake and AI Extraction pipeline.
 */
public enum CaptureState {
    RECEIVED,
    VALIDATING,
    READY_TO_SUBMIT,
    QUEUED_OFFLINE,
    SUBMITTING,
    ACKNOWLEDGED,
    ANALYZING,
    EXTRACTION_SUCCESS,
    CONFIRMED,
    DUPLICATE,
    UNSUPPORTED,
    RETRYABLE_FAILURE,
    PERMANENT_FAILURE
}
