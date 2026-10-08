package com.mnesa.android.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room persistence entity for cached opportunities with user isolation.
 */
@Entity(tableName = "opportunities")
public class OpportunityEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "user_id", defaultValue = "")
    private String userId;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "organization")
    private String organization;

    @NonNull
    @ColumnInfo(name = "category", defaultValue = "OTHER")
    private String category;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "source_url")
    private String sourceUrl;

    @ColumnInfo(name = "registration_url")
    private String registrationUrl;

    @ColumnInfo(name = "deadline_timestamp")
    private Long deadlineTimestamp;

    @ColumnInfo(name = "deadline_timezone")
    private String deadlineTimezone;

    @ColumnInfo(name = "eligibility")
    private String eligibility;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "estimated_effort")
    private String estimatedEffort;

    @NonNull
    @ColumnInfo(name = "priority", defaultValue = "MEDIUM")
    private String priority;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "confidence_score")
    private float confidenceScore;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at", defaultValue = "0")
    private long updatedAt;

    @NonNull
    @ColumnInfo(name = "sync_state", defaultValue = "SYNCED")
    private String syncState;

    public OpportunityEntity(@NonNull String id,
                             @NonNull String userId,
                             @NonNull String title,
                             String organization,
                             @NonNull String category,
                             String description,
                             String sourceUrl,
                             String registrationUrl,
                             Long deadlineTimestamp,
                             String deadlineTimezone,
                             String eligibility,
                             String location,
                             String estimatedEffort,
                             @NonNull String priority,
                             @NonNull String status,
                             float confidenceScore,
                             long createdAt,
                             long updatedAt,
                             @NonNull String syncState) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.organization = organization;
        this.category = category;
        this.description = description;
        this.sourceUrl = sourceUrl;
        this.registrationUrl = registrationUrl;
        this.deadlineTimestamp = deadlineTimestamp;
        this.deadlineTimezone = deadlineTimezone;
        this.eligibility = eligibility;
        this.location = location;
        this.estimatedEffort = estimatedEffort;
        this.priority = priority;
        this.status = status;
        this.confidenceScore = confidenceScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.syncState = syncState;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }

    public Long getDeadlineTimestamp() {
        return deadlineTimestamp;
    }

    public void setDeadlineTimestamp(Long deadlineTimestamp) {
        this.deadlineTimestamp = deadlineTimestamp;
    }

    public String getDeadlineTimezone() {
        return deadlineTimezone;
    }

    public void setDeadlineTimezone(String deadlineTimezone) {
        this.deadlineTimezone = deadlineTimezone;
    }

    public String getEligibility() {
        return eligibility;
    }

    public void setEligibility(String eligibility) {
        this.eligibility = eligibility;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEstimatedEffort() {
        return estimatedEffort;
    }

    public void setEstimatedEffort(String estimatedEffort) {
        this.estimatedEffort = estimatedEffort;
    }

    @NonNull
    public String getPriority() {
        return priority;
    }

    public void setPriority(@NonNull String priority) {
        this.priority = priority;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public float getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(float confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getSyncState() {
        return syncState;
    }

    public void setSyncState(@NonNull String syncState) {
        this.syncState = syncState;
    }
}
