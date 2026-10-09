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
    private final String notes;
    private final String targetTimezone;
    private final Long snoozeUntil;
    private final int snoozeCount;
    private final String smartReason;
    private final String opportunityTitle;

    public Reminder(String id,
                    String opportunityId,
                    String userId,
                    String title,
                    long triggerTimestamp,
                    String reminderType,
                    String status,
                    long createdAt) {
        this(id, opportunityId, userId, title, triggerTimestamp, reminderType, status, createdAt, null, "UTC", null, 0, null, null);
    }

    public Reminder(String id,
                    String opportunityId,
                    String userId,
                    String title,
                    long triggerTimestamp,
                    String reminderType,
                    String status,
                    long createdAt,
                    String notes,
                    String targetTimezone,
                    Long snoozeUntil,
                    int snoozeCount,
                    String smartReason,
                    String opportunityTitle) {
        this.id = id;
        this.opportunityId = opportunityId != null ? opportunityId : "";
        this.userId = userId != null ? userId : "";
        this.title = title != null ? title : "";
        this.triggerTimestamp = triggerTimestamp;
        this.reminderType = reminderType != null ? reminderType : "STANDARD";
        this.status = status != null ? status : "SCHEDULED";
        this.createdAt = createdAt;
        this.notes = notes;
        this.targetTimezone = targetTimezone != null ? targetTimezone : "UTC";
        this.snoozeUntil = snoozeUntil;
        this.snoozeCount = snoozeCount;
        this.smartReason = smartReason;
        this.opportunityTitle = opportunityTitle;
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

    public String getNotes() {
        return notes;
    }

    public String getTargetTimezone() {
        return targetTimezone;
    }

    public Long getSnoozeUntil() {
        return snoozeUntil;
    }

    public int getSnoozeCount() {
        return snoozeCount;
    }

    public String getSmartReason() {
        return smartReason;
    }

    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public long getEffectiveTriggerTime() {
        return snoozeUntil != null && snoozeUntil > 0 ? snoozeUntil : triggerTimestamp;
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
