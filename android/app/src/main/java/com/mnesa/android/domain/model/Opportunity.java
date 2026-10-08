package com.mnesa.android.domain.model;

import java.util.Objects;

/**
 * Pure Java domain model representing an opportunity in MNESA.
 */
public class Opportunity {

    private final String id;
    private final String userId;
    private final String title;
    private final String organization;
    private final OpportunityType type;
    private final String category;
    private final OpportunityStatus status;
    private final String description;
    private final String sourceUrl;
    private final String registrationUrl;
    private final Long deadlineTimestamp;
    private final String deadlineTimezone;
    private final String eligibility;
    private final String location;
    private final String estimatedEffort;
    private final String priority;
    private final float confidenceScore;
    private final long createdAt;
    private final long updatedAt;
    private final String syncState;

    public Opportunity(String id,
                       String userId,
                       String title,
                       String organization,
                       OpportunityType type,
                       String category,
                       OpportunityStatus status,
                       String description,
                       String sourceUrl,
                       String registrationUrl,
                       Long deadlineTimestamp,
                       String deadlineTimezone,
                       String eligibility,
                       String location,
                       String estimatedEffort,
                       String priority,
                       float confidenceScore,
                       long createdAt,
                       long updatedAt,
                       String syncState) {
        this.id = id;
        this.userId = userId != null ? userId : "";
        this.title = title != null ? title : "";
        this.organization = organization != null ? organization : "";
        this.type = type != null ? type : OpportunityType.OTHER;
        this.category = category != null ? category : this.type.name();
        this.status = status != null ? status : OpportunityStatus.CAPTURED;
        this.description = description;
        this.sourceUrl = sourceUrl;
        this.registrationUrl = registrationUrl;
        this.deadlineTimestamp = deadlineTimestamp;
        this.deadlineTimezone = deadlineTimezone;
        this.eligibility = eligibility;
        this.location = location;
        this.estimatedEffort = estimatedEffort;
        this.priority = priority != null ? priority : "MEDIUM";
        this.confidenceScore = confidenceScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.syncState = syncState != null ? syncState : "SYNCED";
    }

    // Convenience constructor for backwards compatibility with Phase 01/02
    public Opportunity(String id,
                       String title,
                       String organization,
                       OpportunityType type,
                       OpportunityStatus status,
                       String sourceUrl,
                       Long deadlineTimestamp,
                       float confidenceScore,
                       long createdAt) {
        this(id, "", title, organization, type, type != null ? type.name() : "OTHER",
                status, null, sourceUrl, null, deadlineTimestamp, null, null, null, null,
                "MEDIUM", confidenceScore, createdAt, createdAt, "SYNCED");
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
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

    public String getCategory() {
        return category;
    }

    public OpportunityStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public Long getDeadlineTimestamp() {
        return deadlineTimestamp;
    }

    public String getDeadlineTimezone() {
        return deadlineTimezone;
    }

    public String getEligibility() {
        return eligibility;
    }

    public String getLocation() {
        return location;
    }

    public String getEstimatedEffort() {
        return estimatedEffort;
    }

    public String getPriority() {
        return priority;
    }

    public float getConfidenceScore() {
        return confidenceScore;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getSyncState() {
        return syncState;
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
