package com.mnesa.android.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room persistence entity for cached opportunities.
 */
@Entity(tableName = "opportunities")
public class OpportunityEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "organization")
    private String organization;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "source_url")
    private String sourceUrl;

    @ColumnInfo(name = "deadline_timestamp")
    private Long deadlineTimestamp;

    @ColumnInfo(name = "confidence_score")
    private float confidenceScore;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public OpportunityEntity(@NonNull String id,
                             @NonNull String title,
                             String organization,
                             @NonNull String type,
                             @NonNull String status,
                             String sourceUrl,
                             Long deadlineTimestamp,
                             float confidenceScore,
                             long createdAt) {
        this.id = id;
        this.title = title;
        this.organization = organization;
        this.type = type;
        this.status = status;
        this.sourceUrl = sourceUrl;
        this.deadlineTimestamp = deadlineTimestamp;
        this.confidenceScore = confidenceScore;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
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
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Long getDeadlineTimestamp() {
        return deadlineTimestamp;
    }

    public void setDeadlineTimestamp(Long deadlineTimestamp) {
        this.deadlineTimestamp = deadlineTimestamp;
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
}
