package com.mnesa.android.data.remote.dto;

import java.util.List;

public class UpdatePreferencesRequestDto {
    private List<String> interests;
    private String reminderTiming;
    private Boolean emailNotificationsEnabled;
    private Boolean pushNotificationsEnabled;

    public UpdatePreferencesRequestDto(List<String> interests, String reminderTiming, Boolean emailNotificationsEnabled, Boolean pushNotificationsEnabled) {
        this.interests = interests;
        this.reminderTiming = reminderTiming;
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.pushNotificationsEnabled = pushNotificationsEnabled;
    }

    public List<String> getInterests() { return interests; }
    public String getReminderTiming() { return reminderTiming; }
    public Boolean getEmailNotificationsEnabled() { return emailNotificationsEnabled; }
    public Boolean getPushNotificationsEnabled() { return pushNotificationsEnabled; }
}
