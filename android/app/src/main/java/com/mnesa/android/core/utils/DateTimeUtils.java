package com.mnesa.android.core.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Utility functions for date, time, and deadline urgency formatting.
 */
public final class DateTimeUtils {

    private DateTimeUtils() {
        // Prevent instantiation
    }

    /**
     * Calculates days remaining until the deadline from the current time.
     */
    public static long getDaysRemaining(long deadlineTimestampMs, long currentTimestampMs) {
        LocalDate deadlineDate = Instant.ofEpochMilli(deadlineTimestampMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentDate = Instant.ofEpochMilli(currentTimestampMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return ChronoUnit.DAYS.between(currentDate, deadlineDate);
    }

    /**
     * Formats days remaining into human-readable urgency text.
     */
    public static String formatUrgencyLabel(long daysRemaining) {
        if (daysRemaining < 0) {
            return "Expired";
        } else if (daysRemaining == 0) {
            return "Due today";
        } else if (daysRemaining == 1) {
            return "1 day left";
        } else {
            return daysRemaining + " days left";
        }
    }
}
