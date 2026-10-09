package com.mnesa.backend.modules.reminder.provider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDeliveryResult {
    private boolean success;
    private String messageId;
    private String errorMessage;

    public static NotificationDeliveryResult success(String messageId) {
        return NotificationDeliveryResult.builder()
                .success(true)
                .messageId(messageId)
                .build();
    }

    public static NotificationDeliveryResult failure(String errorMessage) {
        return NotificationDeliveryResult.builder()
                .success(false)
                .errorMessage(errorMessage)
                .build();
    }
}
