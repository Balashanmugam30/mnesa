package com.mnesa.android.domain.model;

/**
 * State machine stages for the Android Share Intake pipeline.
 */
public enum CaptureState {
    RECEIVED,
    VALIDATING,
    READY_TO_SUBMIT,
    QUEUED_OFFLINE,
    SUBMITTING,
    ACKNOWLEDGED,
    DUPLICATE,
    UNSUPPORTED,
    RETRYABLE_FAILURE,
    PERMANENT_FAILURE
}
