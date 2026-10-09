package com.mnesa.backend.modules.reminder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReminderRequest {
    private String title;
    private String notes;
    private String reminderType;
    private Instant scheduledAt;
    private String targetTimezone;
}
