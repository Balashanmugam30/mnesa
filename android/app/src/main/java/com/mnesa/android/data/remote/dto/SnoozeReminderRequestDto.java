package com.mnesa.android.data.remote.dto;

public class SnoozeReminderRequestDto {
    private Integer snoozeDurationMinutes;
    private String customSnoozeUntil;

    public SnoozeReminderRequestDto() {}

    public SnoozeReminderRequestDto(Integer snoozeDurationMinutes, String customSnoozeUntil) {
        this.snoozeDurationMinutes = snoozeDurationMinutes;
        this.customSnoozeUntil = customSnoozeUntil;
    }

    public Integer getSnoozeDurationMinutes() { return snoozeDurationMinutes; }
    public void setSnoozeDurationMinutes(Integer snoozeDurationMinutes) { this.snoozeDurationMinutes = snoozeDurationMinutes; }

    public String getCustomSnoozeUntil() { return customSnoozeUntil; }
    public void setCustomSnoozeUntil(String customSnoozeUntil) { this.customSnoozeUntil = customSnoozeUntil; }
}
