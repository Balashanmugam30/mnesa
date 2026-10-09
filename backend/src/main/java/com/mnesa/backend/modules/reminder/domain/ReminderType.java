package com.mnesa.backend.modules.reminder.domain;

/**
 * Cadence and classification types for reminders.
 */
public enum ReminderType {
    PREPARATION,
    APPROACHING_DEADLINE,
    FINAL_HOURS,
    CUSTOM,
    STANDARD,
    ONE_WEEK_BEFORE,
    THREE_DAYS_BEFORE,
    ONE_DAY_BEFORE,
    IMMEDIATE
}
