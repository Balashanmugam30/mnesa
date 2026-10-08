package com.mnesa.android.core.utils;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

public class DateTimeUtilsTest {

    @Test
    public void testDaysRemainingCalculation() {
        long now = Instant.now().toEpochMilli();
        long fiveDaysLater = Instant.now().plus(5, ChronoUnit.DAYS).toEpochMilli();

        long remaining = DateTimeUtils.getDaysRemaining(fiveDaysLater, now);
        assertEquals(5, remaining);
    }

    @Test
    public void testUrgencyLabelFormatting() {
        assertEquals("Expired", DateTimeUtils.formatUrgencyLabel(-1));
        assertEquals("Due today", DateTimeUtils.formatUrgencyLabel(0));
        assertEquals("1 day left", DateTimeUtils.formatUrgencyLabel(1));
        assertEquals("7 days left", DateTimeUtils.formatUrgencyLabel(7));
    }
}
