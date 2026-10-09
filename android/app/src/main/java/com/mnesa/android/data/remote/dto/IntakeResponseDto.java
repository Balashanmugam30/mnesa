package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class IntakeResponseDto {

    @SerializedName("captureId")
    private String captureId;

    @SerializedName("jobId")
    private String jobId;

    @SerializedName("status")
    private String status;

    @SerializedName("sourceType")
    private String sourceType;

    @SerializedName("canonicalUrl")
    private String canonicalUrl;

    @SerializedName("message")
    private String message;

    @SerializedName("duplicate")
    private boolean duplicate;

    @SerializedName("createdAt")
    private String createdAt;

    public IntakeResponseDto() {}

    public String getCaptureId() { return captureId; }
    public void setCaptureId(String captureId) { this.captureId = captureId; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isDuplicate() { return duplicate; }
    public void setDuplicate(boolean duplicate) { this.duplicate = duplicate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
