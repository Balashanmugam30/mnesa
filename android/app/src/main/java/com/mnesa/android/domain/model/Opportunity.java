package com.mnesa.android.domain.model;

import java.util.Objects;

/**
 * Pure Java domain model representing an opportunity in MNESA.
 */
public class Opportunity {

    private final String id;
    private final String title;
    private final String organization;
    private final OpportunityType type;
    private final OpportunityStatus status;
    private final String sourceUrl;
    private final Long deadlineTimestamp;
    private final float confidenceScore;
    private final long createdAt;

    public Opportunity(String id,
                       String title,
                       String organization,
                       OpportunityType type,
                       OpportunityStatus status,
                       String sourceUrl,
                       Long deadlineTimestamp,
                       float confidenceScore,
                       long createdAt) {
        this.id = id;
        this.title = title != null ? title : "";
        this.organization = organization != null ? organization : "";
        this.type = type != null ? type : OpportunityType.OTHER;
        this.status = status != null ? status : OpportunityStatus.CAPTURED;
        this.sourceUrl = sourceUrl;
        this.deadlineTimestamp = deadlineTimestamp;
        this.confidenceScore = confidenceScore;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOrganization() {
        return organization;
    }

    public OpportunityType getType() {
        return type;
    }

    public OpportunityStatus getStatus() {
        return status;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public Long getDeadlineTimestamp() {
        return deadlineTimestamp;
    }

    public float getConfidenceScore() {
        return confidenceScore;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean hasDeadline() {
        return deadlineTimestamp != null && deadlineTimestamp > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Opportunity that = (Opportunity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
