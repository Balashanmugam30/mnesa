package com.mnesa.android.data.remote.dto;

public class ReminderDto {
    private String id;
    private String opportunityId;
    private String opportunityTitle;
    private String opportunityCategory;
    private String opportunityDeadline;
    private String userId;
    private String title;
    private String notes;
    private String reminderType;
    private String scheduledAt;
    private String targetTimezone;
    private String status;
    private String snoozeUntil;
    private int snoozeCount;
    private String smartReason;
    private String sentAt;
    private String createdAt;
    private String updatedAt;

    public ReminderDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getOpportunityTitle() { return opportunityTitle; }
    public void setOpportunityTitle(String opportunityTitle) { this.opportunityTitle = opportunityTitle; }

    public String getOpportunityCategory() { return opportunityCategory; }
    public void setOpportunityCategory(String opportunityCategory) { this.opportunityCategory = opportunityCategory; }

    public String getOpportunityDeadline() { return opportunityDeadline; }
    public void setOpportunityDeadline(String opportunityDeadline) { this.opportunityDeadline = opportunityDeadline; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getReminderType() { return reminderType; }
    public void setReminderType(String reminderType) { this.reminderType = reminderType; }

    public String getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(String scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getTargetTimezone() { return targetTimezone; }
    public void setTargetTimezone(String targetTimezone) { this.targetTimezone = targetTimezone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSnoozeUntil() { return snoozeUntil; }
    public void setSnoozeUntil(String snoozeUntil) { this.snoozeUntil = snoozeUntil; }

    public int getSnoozeCount() { return snoozeCount; }
    public void setSnoozeCount(int snoozeCount) { this.snoozeCount = snoozeCount; }

    public String getSmartReason() { return smartReason; }
    public void setSmartReason(String smartReason) { this.smartReason = smartReason; }

    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
