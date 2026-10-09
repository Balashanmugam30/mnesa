package com.mnesa.backend.modules.reminder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateReminderRequest {

    private UUID opportunityId;

    @NotBlank(message = "Title is required")
    private String title;

    private String notes;

    @Builder.Default
    private String reminderType = "CUSTOM";

    @NotNull(message = "Scheduled time is required")
    private Instant scheduledAt;

    @Builder.Default
    private String targetTimezone = "UTC";

    private String smartReason;
}
