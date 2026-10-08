package com.mnesa.android.domain.model;

import java.util.Objects;

/**
 * Pure Java domain model representing a scheduled reminder.
 */
public class Reminder {

    private final String id;
    private final String opportunityId;
    private final String userId;
    private final String title;
    private final long triggerTimestamp;
    private final String reminderType;
    private final String status;
    private final long createdAt;

    public Reminder(String id,
                    String opportunityId,
                    String userId,
                    String title,
                    long triggerTimestamp,
                    String reminderType,
                    String status,
                    long createdAt) {
        this.id = id;
        this.opportunityId = opportunityId != null ? opportunityId : "";
        this.userId = userId != null ? userId : "";
        this.title = title != null ? title : "";
        this.triggerTimestamp = triggerTimestamp;
        this.reminderType = reminderType != null ? reminderType : "STANDARD";
        this.status = status != null ? status : "SCHEDULED";
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getOpportunityId() {
        return opportunityId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public long getTriggerTimestamp() {
        return triggerTimestamp;
    }

    public String getReminderType() {
        return reminderType;
    }

    public String getStatus() {
        return status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(id, reminder.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
