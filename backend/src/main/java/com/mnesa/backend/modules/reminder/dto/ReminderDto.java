package com.mnesa.backend.modules.reminder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDto {
    private UUID id;
    private UUID opportunityId;
    private String opportunityTitle;
    private String opportunityCategory;
    private Instant opportunityDeadline;
    private UUID userId;
    private String title;
    private String notes;
    private String reminderType;
    private Instant scheduledAt;
    private String targetTimezone;
    private String status;
    private Instant snoozeUntil;
    private int snoozeCount;
    private String smartReason;
    private Instant sentAt;
    private Instant createdAt;
    private Instant updatedAt;
}
