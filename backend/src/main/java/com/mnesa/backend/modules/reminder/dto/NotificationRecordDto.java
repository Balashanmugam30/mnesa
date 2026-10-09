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
public class NotificationRecordDto {
    private UUID id;
    private UUID userId;
    private UUID reminderId;
    private UUID opportunityId;
    private String title;
    private String body;
    private String channel;
    private String provider;
    private String deliveryStatus;
    private String deepLinkUri;
    private Instant openedAt;
    private Instant createdAt;
}
