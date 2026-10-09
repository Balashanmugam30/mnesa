package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class OpportunitySummaryDto {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("organization")
    private String organization;

    @SerializedName("category")
    private String category;

    @SerializedName("status")
    private String status;

    @SerializedName("priority")
    private String priority;

    @SerializedName("deadlineTimestamp")
    private Long deadlineTimestamp;

    @SerializedName("confidenceScore")
    private Float confidenceScore;

    @SerializedName("location")
    private String location;

    @SerializedName("workMode")
    private String workMode;

    @SerializedName("createdAt")
    private String createdAt;

    public OpportunitySummaryDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Long getDeadlineTimestamp() { return deadlineTimestamp; }
    public void setDeadlineTimestamp(Long deadlineTimestamp) { this.deadlineTimestamp = deadlineTimestamp; }

    public Float getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Float confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
