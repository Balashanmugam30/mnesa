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
public class SnoozeReminderRequest {
    /**
     * Optional preset duration in minutes (e.g. 60 for 1h, 180 for 3h, 1440 for 24h).
     */
    private Integer snoozeDurationMinutes;

    /**
     * Optional explicit timestamp to snooze until.
     */
    private Instant customSnoozeUntil;
}
