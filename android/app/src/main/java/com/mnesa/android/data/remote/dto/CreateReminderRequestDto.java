package com.mnesa.android.data.remote.dto;

public class CreateReminderRequestDto {
    private String opportunityId;
    private String title;
    private String notes;
    private String reminderType;
    private String scheduledAt;
    private String targetTimezone;
    private String smartReason;

    public CreateReminderRequestDto() {}

    public CreateReminderRequestDto(String opportunityId, String title, String notes, String reminderType, String scheduledAt, String targetTimezone, String smartReason) {
        this.opportunityId = opportunityId;
        this.title = title;
        this.notes = notes;
        this.reminderType = reminderType;
        this.scheduledAt = scheduledAt;
        this.targetTimezone = targetTimezone;
        this.smartReason = smartReason;
    }

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

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

    public String getSmartReason() { return smartReason; }
    public void setSmartReason(String smartReason) { this.smartReason = smartReason; }
}
