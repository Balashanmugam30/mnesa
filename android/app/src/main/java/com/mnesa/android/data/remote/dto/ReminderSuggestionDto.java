package com.mnesa.android.data.remote.dto;

public class ReminderSuggestionDto {
    private String reminderType;
    private String title;
    private String suggestedScheduledAt;
    private String suggestedTimezone;
    private String reason;
    private String priority;

    public ReminderSuggestionDto() {}

    public String getReminderType() { return reminderType; }
    public void setReminderType(String reminderType) { this.reminderType = reminderType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSuggestedScheduledAt() { return suggestedScheduledAt; }
    public void setSuggestedScheduledAt(String suggestedScheduledAt) { this.suggestedScheduledAt = suggestedScheduledAt; }

    public String getSuggestedTimezone() { return suggestedTimezone; }
    public void setSuggestedTimezone(String suggestedTimezone) { this.suggestedTimezone = suggestedTimezone; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
