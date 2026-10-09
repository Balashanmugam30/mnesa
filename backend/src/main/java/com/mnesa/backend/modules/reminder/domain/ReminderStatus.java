package com.mnesa.backend.modules.reminder.domain;

/**
 * Lifecycle statuses for opportunity reminders.
 */
public enum ReminderStatus {
    SCHEDULED,
    PENDING,
    CLAIMED,
    TRIGGERED,
    SENT,
    SNOOZED,
    CANCELLED,
    DISMISSED,
    FAILED
}
