package com.mnesa.backend.modules.reminder.domain;

/**
 * Dispatch and delivery states for notifications.
 */
public enum DeliveryStatus {
    PENDING,
    DELIVERED,
    FAILED,
    OPENED
}
