package com.mnesa.backend.modules.reminder.service;

import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.reminder.domain.ReminderType;
import com.mnesa.backend.modules.reminder.dto.ReminderSuggestionDto;
import com.mnesa.backend.modules.user.domain.ReminderTimingPreference;
import com.mnesa.backend.modules.user.domain.UserPreferences;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Intelligent recommendation engine for opportunity reminder schedules.
 * Generates deterministic, explainable suggestions based on deadline proximity,
 * opportunity category, estimated effort, and user timezone preferences.
 */
@Slf4j
@Service
public class SmartReminderSuggestionService {

    public List<ReminderSuggestionDto> generateSuggestions(Opportunity opportunity, UserPreferences preferences) {
        List<ReminderSuggestionDto> suggestions = new ArrayList<>();
        Instant now = Instant.now();

        String tz = (preferences != null && preferences.getTimezone() != null)
                ? preferences.getTimezone()
                : (opportunity.getDeadlineTimezone() != null ? opportunity.getDeadlineTimezone() : "UTC");

        ZoneId zoneId;
        try {
            zoneId = ZoneId.of(tz);
        } catch (Exception e) {
            zoneId = ZoneId.of("UTC");
            tz = "UTC";
        }

        Instant deadline = opportunity.getDeadlineAt();
        if (deadline == null) {
            // General preparation reminder for opportunities without explicit deadline
            suggestions.add(ReminderSuggestionDto.builder()
                    .reminderType(ReminderType.PREPARATION.name())
                    .title("Review opportunity: " + truncate(opportunity.getTitle(), 50))
                    .suggestedScheduledAt(now.plus(2, ChronoUnit.DAYS))
                    .suggestedTimezone(tz)
                    .reason("No deadline specified. Set a follow-up review date to assess eligibility and next steps.")
                    .priority("LOW")
                    .build());
            return suggestions;
        }

        if (deadline.isBefore(now)) {
            log.info("Deadline for opportunity {} is already past. No future suggestions generated.", opportunity.getId());
            return suggestions;
        }

        long hoursUntilDeadline = ChronoUnit.HOURS.between(now, deadline);
        ReminderTimingPreference timingPref = preferences != null ? preferences.getReminderTiming() : ReminderTimingPreference.STANDARD;

        // 1. Preparation Reminder (Early phase)
        if (hoursUntilDeadline >= 72) { // More than 3 days
            long prepDaysBefore = calculatePrepDays(opportunity.getOpportunityType(), hoursUntilDeadline, timingPref);
            Instant prepTime = alignToHour(deadline.minus(prepDaysBefore, ChronoUnit.DAYS), zoneId, 9);

            if (prepTime.isAfter(now) && prepTime.isBefore(deadline)) {
                String reason = buildPrepReason(opportunity.getOpportunityType(), prepDaysBefore);
                suggestions.add(ReminderSuggestionDto.builder()
                        .reminderType(ReminderType.PREPARATION.name())
                        .title("Prepare application: " + truncate(opportunity.getTitle(), 50))
                        .suggestedScheduledAt(prepTime)
                        .suggestedTimezone(tz)
                        .reason(reason)
                        .priority("MEDIUM")
                        .build());
            }
        }

        // 2. Approaching Deadline Reminder (Mid phase)
        if (hoursUntilDeadline >= 24) { // More than 24 hours
            long hoursBefore = hoursUntilDeadline >= 96 ? 48 : 24;
            Instant approachTime = alignToHour(deadline.minus(hoursBefore, ChronoUnit.HOURS), zoneId, 10);

            if (approachTime.isAfter(now) && approachTime.isBefore(deadline)) {
                suggestions.add(ReminderSuggestionDto.builder()
                        .reminderType(ReminderType.APPROACHING_DEADLINE.name())
                        .title("Deadline in " + (hoursBefore / 24) + " day(s): " + truncate(opportunity.getTitle(), 50))
                        .suggestedScheduledAt(approachTime)
                        .suggestedTimezone(tz)
                        .reason("Ensure all forms, resumes, and prerequisites are assembled before the closing date.")
                        .priority("HIGH")
                        .build());
            }
        }

        // 3. Final Hours Reminder (Urgent closing call)
        if (hoursUntilDeadline >= 4) {
            long finalHoursBefore = hoursUntilDeadline >= 12 ? 6 : 2;
            Instant finalTime = deadline.minus(finalHoursBefore, ChronoUnit.HOURS);

            if (finalTime.isAfter(now)) {
                suggestions.add(ReminderSuggestionDto.builder()
                        .reminderType(ReminderType.FINAL_HOURS.name())
                        .title("Final submission reminder: " + truncate(opportunity.getTitle(), 50))
                        .suggestedScheduledAt(finalTime)
                        .suggestedTimezone(tz)
                        .reason("Portal closes in " + finalHoursBefore + " hours. Final chance to submit your application.")
                        .priority("URGENT")
                        .build());
            }
        } else if (hoursUntilDeadline > 0) {
            // Under 4 hours left: immediate final reminder
            suggestions.add(ReminderSuggestionDto.builder()
                    .reminderType(ReminderType.FINAL_HOURS.name())
                    .title("Urgent: Deadline closes very soon for " + truncate(opportunity.getTitle(), 45))
                    .suggestedScheduledAt(now.plus(15, ChronoUnit.MINUTES))
                    .suggestedTimezone(tz)
                    .reason("Application deadline is imminent. Act immediately.")
                    .priority("URGENT")
                    .build());
        }

        return suggestions;
    }

    private long calculatePrepDays(OpportunityType type, long hoursUntilDeadline, ReminderTimingPreference timingPref) {
        long totalDays = hoursUntilDeadline / 24;
        long baseDays;

        if (type == OpportunityType.SCHOLARSHIP || type == OpportunityType.INTERNSHIP || type == OpportunityType.JOB) {
            baseDays = totalDays >= 14 ? 7 : (totalDays >= 7 ? 4 : 2);
        } else if (type == OpportunityType.HACKATHON || type == OpportunityType.COMPETITION) {
            baseDays = totalDays >= 10 ? 5 : (totalDays >= 5 ? 3 : 2);
        } else {
            baseDays = totalDays >= 7 ? 3 : 1;
        }

        if (timingPref == ReminderTimingPreference.AGGRESSIVE) {
            baseDays = Math.min(baseDays + 2, totalDays - 1);
        }

        return Math.max(1, Math.min(baseDays, totalDays - 1));
    }

    private String buildPrepReason(OpportunityType type, long daysBefore) {
        if (type == OpportunityType.SCHOLARSHIP) {
            return "Recommended " + daysBefore + " days in advance to allow adequate time for essays, transcripts, and references.";
        } else if (type == OpportunityType.INTERNSHIP || type == OpportunityType.JOB) {
            return "Recommended " + daysBefore + " days in advance to customize your resume and cover letter for this role.";
        } else if (type == OpportunityType.HACKATHON || type == OpportunityType.COMPETITION) {
            return "Recommended " + daysBefore + " days in advance for team formation and project ideation.";
        }
        return "Recommended " + daysBefore + " days in advance to gather materials and review submission criteria.";
    }

    private Instant alignToHour(Instant instant, ZoneId zoneId, int targetHour) {
        ZonedDateTime zdt = instant.atZone(zoneId);
        ZonedDateTime aligned = zdt.withHour(targetHour).withMinute(0).withSecond(0).withNano(0);
        return aligned.toInstant();
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "Opportunity";
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
