package com.mnesa.android.data.remote.dto;

public class UpdateReminderRequestDto {
    private String title;
    private String notes;
    private String reminderType;
    private String scheduledAt;
    private String targetTimezone;

    public UpdateReminderRequestDto() {}

    public UpdateReminderRequestDto(String title, String notes, String reminderType, String scheduledAt, String targetTimezone) {
        this.title = title;
        this.notes = notes;
        this.reminderType = reminderType;
        this.scheduledAt = scheduledAt;
        this.targetTimezone = targetTimezone;
    }

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
}
