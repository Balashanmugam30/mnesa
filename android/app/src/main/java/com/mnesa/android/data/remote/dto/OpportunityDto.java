package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OpportunityDto {

    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("title")
    private String title;

    @SerializedName("organization")
    private String organization;

    @SerializedName("category")
    private String category;

    @SerializedName("status")
    private String status;

    @SerializedName("description")
    private String description;

    @SerializedName("sourceUrl")
    private String sourceUrl;

    @SerializedName("registrationUrl")
    private String registrationUrl;

    @SerializedName("deadlineTimestamp")
    private Long deadlineTimestamp;

    @SerializedName("deadlineTimezone")
    private String deadlineTimezone;

    @SerializedName("eligibility")
    private String eligibility;

    @SerializedName("location")
    private String location;

    @SerializedName("workMode")
    private String workMode;

    @SerializedName("estimatedEffort")
    private String estimatedEffort;

    @SerializedName("priority")
    private String priority;

    @SerializedName("priorityReason")
    private String priorityReason;

    @SerializedName("notes")
    private String notes;

    @SerializedName("confidenceScore")
    private Float confidenceScore;

    @SerializedName("tags")
    private List<TagDto> tags;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("updatedAt")
    private String updatedAt;

    public OpportunityDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getRegistrationUrl() { return registrationUrl; }
    public void setRegistrationUrl(String registrationUrl) { this.registrationUrl = registrationUrl; }

    public Long getDeadlineTimestamp() { return deadlineTimestamp; }
    public void setDeadlineTimestamp(Long deadlineTimestamp) { this.deadlineTimestamp = deadlineTimestamp; }

    public String getDeadlineTimezone() { return deadlineTimezone; }
    public void setDeadlineTimezone(String deadlineTimezone) { this.deadlineTimezone = deadlineTimezone; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getEstimatedEffort() { return estimatedEffort; }
    public void setEstimatedEffort(String estimatedEffort) { this.estimatedEffort = estimatedEffort; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getPriorityReason() { return priorityReason; }
    public void setPriorityReason(String priorityReason) { this.priorityReason = priorityReason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Float getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Float confidenceScore) { this.confidenceScore = confidenceScore; }

    public List<TagDto> getTags() { return tags; }
    public void setTags(List<TagDto> tags) { this.tags = tags; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
