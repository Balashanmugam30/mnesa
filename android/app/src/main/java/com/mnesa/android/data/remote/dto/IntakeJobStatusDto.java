package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class IntakeJobStatusDto {

    @SerializedName("job_id")
    private String jobId;

    @SerializedName("capture_id")
    private String captureId;

    @SerializedName("status")
    private String status;

    @SerializedName("attempt_count")
    private int attemptCount;

    @SerializedName("error_message")
    private String errorMessage;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("extraction")
    private AiExtractionDto extraction;

    public IntakeJobStatusDto() {}

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getCaptureId() { return captureId; }
    public void setCaptureId(String captureId) { this.captureId = captureId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public AiExtractionDto getExtraction() { return extraction; }
    public void setExtraction(AiExtractionDto extraction) { this.extraction = extraction; }
}
