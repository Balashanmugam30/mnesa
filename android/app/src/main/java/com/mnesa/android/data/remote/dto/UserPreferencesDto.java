package com.mnesa.android.data.remote.dto;

import java.util.ArrayList;
import java.util.List;

public class UserPreferencesDto {
    private List<String> interests = new ArrayList<>();
    private String reminderTiming;
    private boolean emailNotificationsEnabled;
    private boolean pushNotificationsEnabled;

    public UserPreferencesDto() {}

    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }

    public String getReminderTiming() { return reminderTiming; }
    public void setReminderTiming(String reminderTiming) { this.reminderTiming = reminderTiming; }

    public boolean isEmailNotificationsEnabled() { return emailNotificationsEnabled; }
    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) { this.emailNotificationsEnabled = emailNotificationsEnabled; }

    public boolean isPushNotificationsEnabled() { return pushNotificationsEnabled; }
    public void setPushNotificationsEnabled(boolean pushNotificationsEnabled) { this.pushNotificationsEnabled = pushNotificationsEnabled; }
}
