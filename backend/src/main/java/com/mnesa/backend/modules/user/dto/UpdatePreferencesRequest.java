package com.mnesa.backend.modules.user.dto;

import com.mnesa.backend.modules.user.domain.ReminderTimingPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferencesRequest {
    private List<String> interests;
    private ReminderTimingPreference reminderTiming;
    private Boolean emailNotificationsEnabled;
    private Boolean pushNotificationsEnabled;
    private String timezone;
}
