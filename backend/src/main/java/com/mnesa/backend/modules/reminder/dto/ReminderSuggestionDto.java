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
public class ReminderSuggestionDto {
    private String reminderType;
    private String title;
    private Instant suggestedScheduledAt;
    private String suggestedTimezone;
    private String reason;
    private String priority;
}
