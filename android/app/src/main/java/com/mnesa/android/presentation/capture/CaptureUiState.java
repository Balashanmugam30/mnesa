package com.mnesa.android.presentation.capture;

import com.mnesa.android.domain.model.CaptureState;

/**
 * Immutable UI model representing the complete visual state of the Capture sheet.
 */
public class CaptureUiState {

    private final CaptureState state;
    private final String title;
    private final String subtitle;
    private final String sourceBadge;
    private final String statusMessage;
    private final boolean progressVisible;
    private final boolean success;
    private final boolean duplicate;
    private final boolean offline;
    private final boolean error;
    private final boolean canRetry;

    public CaptureUiState(CaptureState state,
                          String title,
                          String subtitle,
                          String sourceBadge,
                          String statusMessage,
                          boolean progressVisible,
                          boolean success,
                          boolean duplicate,
                          boolean offline,
                          boolean error,
                          boolean canRetry) {
        this.state = state;
        this.title = title;
        this.subtitle = subtitle;
        this.sourceBadge = sourceBadge;
        this.statusMessage = statusMessage;
        this.progressVisible = progressVisible;
        this.success = success;
        this.duplicate = duplicate;
        this.offline = offline;
        this.error = error;
        this.canRetry = canRetry;
    }

    public static CaptureUiState validating() {
        return new CaptureUiState(
                CaptureState.VALIDATING,
                "Validating...",
                "Reading shared opportunity content",
                "DETECTING",
                "Analyzing payload structure...",
                true, false, false, false, false, false
        );
    }

    public static CaptureUiState submitting(String title, String badge) {
        return new CaptureUiState(
                CaptureState.SUBMITTING,
                title,
                "Saving to MNESA...",
                badge,
                "Dispatching to intelligent capture pipeline...",
                true, false, false, false, false, false
        );
    }

    public static CaptureUiState acknowledged(String title, String badge, String message, boolean duplicate) {
        return new CaptureUiState(
                duplicate ? CaptureState.DUPLICATE : CaptureState.ACKNOWLEDGED,
                title,
                duplicate ? "Previously Captured" : "Opportunity Captured!",
                badge,
                message != null ? message : (duplicate ? "You already saved this opportunity" : "Captured and scheduled for analysis"),
                false, true, duplicate, false, false, false
        );
    }

    public static CaptureUiState queuedOffline(String title, String badge) {
        return new CaptureUiState(
                CaptureState.QUEUED_OFFLINE,
                title,
                "Saved Offline",
                badge,
                "Device is offline. Opportunity saved locally and will sync automatically.",
                false, true, false, true, false, false
        );
    }

    public static CaptureUiState retryableFailure(String title, String badge, String errorMsg) {
        return new CaptureUiState(
                CaptureState.RETRYABLE_FAILURE,
                title,
                "Queued for Background Sync",
                badge,
                errorMsg != null ? errorMsg : "Connection interrupted. Queued in local sync queue.",
                false, true, false, true, true, true
        );
    }

    public static CaptureUiState unsupported(String reason) {
        return new CaptureUiState(
                CaptureState.UNSUPPORTED,
                "Unsupported Content",
                "Cannot Capture",
                "INVALID",
                reason != null ? reason : "Shared content format cannot be captured.",
                false, false, false, false, true, false
        );
    }

    public static CaptureUiState permanentFailure(String reason) {
        return new CaptureUiState(
                CaptureState.PERMANENT_FAILURE,
                "Capture Failed",
                "Error",
                "ERROR",
                reason != null ? reason : "An error occurred while saving the opportunity.",
                false, false, false, false, true, false
        );
    }

    public CaptureState getState() { return state; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getSourceBadge() { return sourceBadge; }
    public String getStatusMessage() { return statusMessage; }
    public boolean isProgressVisible() { return progressVisible; }
    public boolean isSuccess() { return success; }
    public boolean isDuplicate() { return duplicate; }
    public boolean isOffline() { return offline; }
    public boolean isError() { return error; }
    public boolean canRetry() { return canRetry; }
}
