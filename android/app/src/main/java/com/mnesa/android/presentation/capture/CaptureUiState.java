package com.mnesa.android.presentation.capture;

import com.mnesa.android.domain.model.CaptureState;

/**
 * Immutable UI model representing the complete visual state of the Capture & AI Extraction sheet.
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

    // AI Extraction specific fields
    private final String organization;
    private final String category;
    private final String summary;
    private final String deadlineFormatted;
    private final boolean deadlineAmbiguous;
    private final String confidencePill;
    private final float confidenceScore;
    private final String evidenceSnippet;
    private final String jobId;
    private final boolean extractionReady;
    private final boolean canConfirm;
    private final java.util.List<com.mnesa.android.data.remote.dto.AiCandidateDto> candidates;
    private final com.mnesa.android.data.remote.dto.AiCandidateDto selectedCandidate;

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
                          boolean canRetry,
                          String organization,
                          String category,
                          String summary,
                          String deadlineFormatted,
                          boolean deadlineAmbiguous,
                          String confidencePill,
                          float confidenceScore,
                          String evidenceSnippet,
                          String jobId,
                          boolean extractionReady,
                          boolean canConfirm) {
        this(state, title, subtitle, sourceBadge, statusMessage, progressVisible, success, duplicate, offline,
                error, canRetry, organization, category, summary, deadlineFormatted, deadlineAmbiguous,
                confidencePill, confidenceScore, evidenceSnippet, jobId, extractionReady, canConfirm,
                java.util.Collections.emptyList(), null);
    }

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
                          boolean canRetry,
                          String organization,
                          String category,
                          String summary,
                          String deadlineFormatted,
                          boolean deadlineAmbiguous,
                          String confidencePill,
                          float confidenceScore,
                          String evidenceSnippet,
                          String jobId,
                          boolean extractionReady,
                          boolean canConfirm,
                          java.util.List<com.mnesa.android.data.remote.dto.AiCandidateDto> candidates,
                          com.mnesa.android.data.remote.dto.AiCandidateDto selectedCandidate) {
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
        this.organization = organization;
        this.category = category;
        this.summary = summary;
        this.deadlineFormatted = deadlineFormatted;
        this.deadlineAmbiguous = deadlineAmbiguous;
        this.confidencePill = confidencePill;
        this.confidenceScore = confidenceScore;
        this.evidenceSnippet = evidenceSnippet;
        this.jobId = jobId;
        this.extractionReady = extractionReady;
        this.canConfirm = canConfirm;
        this.candidates = candidates != null ? candidates : java.util.Collections.emptyList();
        this.selectedCandidate = selectedCandidate;
    }

    public static CaptureUiState validating() {
        return new CaptureUiState(
                CaptureState.VALIDATING,
                "Validating...",
                "Reading shared opportunity content",
                "DETECTING",
                "Analyzing payload structure...",
                true, false, false, false, false, false,
                null, null, null, null, false, null, 0f, null, null, false, false
        );
    }

    public static CaptureUiState submitting(String title, String badge) {
        return new CaptureUiState(
                CaptureState.SUBMITTING,
                title,
                "Saving to MNESA...",
                badge,
                "Dispatching to intelligent capture pipeline...",
                true, false, false, false, false, false,
                null, null, null, null, false, null, 0f, null, null, false, false
        );
    }

    public static CaptureUiState analyzing(String title, String badge, String jobId) {
        return new CaptureUiState(
                CaptureState.ANALYZING,
                title,
                "Analyzing with MNESA AI...",
                badge,
                "Safely extracting key facts, requirements, and deadlines...",
                true, false, false, false, false, false,
                null, null, null, null, false, "Analyzing...", 0f, null, jobId, false, false
        );
    }

    public static CaptureUiState extractionSuccess(String title,
                                                   String organization,
                                                   String category,
                                                   String summary,
                                                   String deadlineFormatted,
                                                   boolean deadlineAmbiguous,
                                                   String confidencePill,
                                                   float confidenceScore,
                                                   String evidenceSnippet,
                                                   String jobId) {
        return extractionSuccess(title, organization, category, summary, deadlineFormatted,
                deadlineAmbiguous, confidencePill, confidenceScore, evidenceSnippet, jobId,
                java.util.Collections.emptyList(), null);
    }

    public static CaptureUiState extractionSuccess(String title,
                                                   String organization,
                                                   String category,
                                                   String summary,
                                                   String deadlineFormatted,
                                                   boolean deadlineAmbiguous,
                                                   String confidencePill,
                                                   float confidenceScore,
                                                   String evidenceSnippet,
                                                   String jobId,
                                                   java.util.List<com.mnesa.android.data.remote.dto.AiCandidateDto> candidates,
                                                   com.mnesa.android.data.remote.dto.AiCandidateDto selectedCandidate) {
        return new CaptureUiState(
                CaptureState.EXTRACTION_SUCCESS,
                title,
                organization != null ? organization : "Opportunity Extracted",
                category != null ? category : "OPPORTUNITY",
                "Opportunity details extracted and verified.",
                false, true, false, false, false, false,
                organization, category, summary, deadlineFormatted, deadlineAmbiguous,
                confidencePill, confidenceScore, evidenceSnippet, jobId, true, true,
                candidates, selectedCandidate
        );
    }

    public static CaptureUiState confirmed(String title, String category) {
        return new CaptureUiState(
                CaptureState.CONFIRMED,
                title,
                "Opportunity Saved!",
                category != null ? category : "SAVED",
                "Successfully saved to your active opportunities.",
                false, true, false, false, false, false,
                null, category, null, null, false, "Saved", 1.0f, null, null, false, false
        );
    }

    public static CaptureUiState acknowledged(String title, String badge, String message, boolean duplicate) {
        return new CaptureUiState(
                duplicate ? CaptureState.DUPLICATE : CaptureState.ACKNOWLEDGED,
                title,
                duplicate ? "Previously Captured" : "Opportunity Captured!",
                badge,
                message != null ? message : (duplicate ? "You already saved this opportunity" : "Captured and scheduled for analysis"),
                false, true, duplicate, false, false, false,
                null, null, null, null, false, null, 0f, null, null, false, false
        );
    }

    public static CaptureUiState queuedOffline(String title, String badge) {
        return new CaptureUiState(
                CaptureState.QUEUED_OFFLINE,
                title,
                "Saved Offline",
                badge,
                "Device is offline. Opportunity saved locally and will sync automatically.",
                false, true, false, true, false, false,
                null, null, null, null, false, null, 0f, null, null, false, false
        );
    }

    public static CaptureUiState retryableFailure(String title, String badge, String errorMsg) {
        return new CaptureUiState(
                CaptureState.RETRYABLE_FAILURE,
                title,
                "Queued for Background Sync",
                badge,
                errorMsg != null ? errorMsg : "Connection interrupted. Queued in local sync queue.",
                false, true, false, true, true, true,
                null, null, null, null, false, null, 0f, null, null, false, false
        );
    }

    public static CaptureUiState unsupported(String reason) {
        return new CaptureUiState(
                CaptureState.UNSUPPORTED,
                "Unsupported Content",
                "Cannot Capture",
                "INVALID",
                reason != null ? reason : "Shared content format cannot be captured.",
                false, false, false, false, true, false,
                null, null, null, null, false, null, 0f, null, null, false, false
        );
    }

    public static CaptureUiState permanentFailure(String reason) {
        return new CaptureUiState(
                CaptureState.PERMANENT_FAILURE,
                "Capture Failed",
                "Error",
                "ERROR",
                reason != null ? reason : "An error occurred while saving the opportunity.",
                false, false, false, false, true, false,
                null, null, null, null, false, null, 0f, null, null, false, false
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
    public String getOrganization() { return organization; }
    public String getCategory() { return category; }
    public String getSummary() { return summary; }
    public String getDeadlineFormatted() { return deadlineFormatted; }
    public boolean isDeadlineAmbiguous() { return deadlineAmbiguous; }
    public String getConfidencePill() { return confidencePill; }
    public float getConfidenceScore() { return confidenceScore; }
    public String getEvidenceSnippet() { return evidenceSnippet; }
    public String getJobId() { return jobId; }
    public boolean isExtractionReady() { return extractionReady; }
    public boolean canConfirm() { return canConfirm; }
    public java.util.List<com.mnesa.android.data.remote.dto.AiCandidateDto> getCandidates() { return candidates; }
    public com.mnesa.android.data.remote.dto.AiCandidateDto getSelectedCandidate() { return selectedCandidate; }
}
