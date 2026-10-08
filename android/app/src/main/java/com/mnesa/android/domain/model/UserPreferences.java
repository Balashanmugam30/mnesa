package com.mnesa.android.domain.model;

import java.util.ArrayList;
import java.util.List;

public class UserPreferences {
    private final List<String> interests;
    private final String reminderTiming;
    private final boolean emailNotificationsEnabled;
    private final boolean pushNotificationsEnabled;

    public UserPreferences(List<String> interests, String reminderTiming, boolean emailNotificationsEnabled, boolean pushNotificationsEnabled) {
        this.interests = interests != null ? interests : new ArrayList<>();
        this.reminderTiming = reminderTiming != null ? reminderTiming : "STANDARD";
        this.emailNotificationsEnabled = emailNotificationsEnabled;
        this.pushNotificationsEnabled = pushNotificationsEnabled;
    }

    public List<String> getInterests() { return interests; }
    public String getReminderTiming() { return reminderTiming; }
    public boolean isEmailNotificationsEnabled() { return emailNotificationsEnabled; }
    public boolean isPushNotificationsEnabled() { return pushNotificationsEnabled; }
}
