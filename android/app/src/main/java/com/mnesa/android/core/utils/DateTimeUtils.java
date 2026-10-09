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

    /**
     * Formats an epoch millisecond timestamp into a relative deadline string.
     */
    public static String formatRelativeDeadline(long timestampMs) {
        long now = System.currentTimeMillis();
        long days = getDaysRemaining(timestampMs, now);
        return formatUrgencyLabel(days);
    }

    public static String formatRelativeDeadline(Long timestampMs) {
        if (timestampMs == null || timestampMs <= 0) {
            return "";
        }
        return formatRelativeDeadline(timestampMs.longValue());
    }

    /**
     * Formats an epoch millisecond timestamp into relative past/future time (e.g., "in 2h", "10m ago").
     */
    public static String formatRelativeTimestamp(long timestampMs) {
        if (timestampMs <= 0) return "";
        long now = System.currentTimeMillis();
        long diff = timestampMs - now;
        if (Math.abs(diff) < 60000) {
            return "just now";
        }
        if (diff > 0) {
            long minutes = diff / (60 * 1000);
            if (minutes < 60) return "in " + minutes + "m";
            long hours = minutes / 60;
            if (hours < 24) return "in " + hours + "h";
            long days = hours / 24;
            return "in " + days + "d";
        } else {
            long ago = -diff;
            long minutes = ago / (60 * 1000);
            if (minutes < 60) return minutes + "m ago";
            long hours = minutes / 60;
            if (hours < 24) return hours + "h ago";
            long days = hours / 24;
            return days + "d ago";
        }
    }

    /**
     * Formats an epoch millisecond timestamp into a full date and time string.
     */
    public static String formatFullDate(long timestampMs) {
        if (timestampMs <= 0) return "";
        java.time.LocalDateTime dt = Instant.ofEpochMilli(timestampMs)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a");
        return dt.format(formatter);
    }
}
