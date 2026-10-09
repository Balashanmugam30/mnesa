package com.mnesa.backend.modules.reminder.service;

import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.reminder.dto.ReminderSuggestionDto;
import com.mnesa.backend.modules.user.domain.ReminderTimingPreference;
import com.mnesa.backend.modules.user.domain.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SmartReminderSuggestionServiceTest {

    private SmartReminderSuggestionService suggestionService;

    @BeforeEach
    void setUp() {
        suggestionService = new SmartReminderSuggestionService();
    }

    @Test
    @DisplayName("Generates preparation, approaching, and final suggestions for a scholarship deadline 14 days away")
    void generatesStandardCadenceForDistantDeadline() {
        Instant now = Instant.now();
        Instant deadline = now.plus(14, ChronoUnit.DAYS);

        Opportunity opportunity = Opportunity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Rhodes Scholarship 2026")
                .opportunityType(OpportunityType.SCHOLARSHIP)
                .deadlineAt(deadline)
                .deadlineTimezone("UTC")
                .build();

        UserPreferences preferences = UserPreferences.builder()
                .reminderTiming(ReminderTimingPreference.STANDARD)
                .timezone("UTC")
                .build();

        List<ReminderSuggestionDto> suggestions = suggestionService.generateSuggestions(opportunity, preferences);

        assertNotNull(suggestions);
        assertEquals(3, suggestions.size());

        ReminderSuggestionDto prep = suggestions.get(0);
        assertEquals("PREPARATION", prep.getReminderType());
        assertTrue(prep.getSuggestedScheduledAt().isBefore(deadline));
        assertTrue(prep.getSuggestedScheduledAt().isAfter(now));
        assertTrue(prep.getReason().contains("essays"));

        ReminderSuggestionDto approach = suggestions.get(1);
        assertEquals("APPROACHING_DEADLINE", approach.getReminderType());

        ReminderSuggestionDto finalCall = suggestions.get(2);
        assertEquals("FINAL_HOURS", finalCall.getReminderType());
        assertEquals("URGENT", finalCall.getPriority());
    }

    @Test
    @DisplayName("Generates urgent final suggestion for deadline closing in 2 hours")
    void generatesUrgentSuggestionForImminentDeadline() {
        Instant now = Instant.now();
        Instant deadline = now.plus(2, ChronoUnit.HOURS);

        Opportunity opportunity = Opportunity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Urgent Hackathon Submission")
                .opportunityType(OpportunityType.HACKATHON)
                .deadlineAt(deadline)
                .build();

        List<ReminderSuggestionDto> suggestions = suggestionService.generateSuggestions(opportunity, null);

        assertNotNull(suggestions);
        assertEquals(1, suggestions.size());
        assertEquals("FINAL_HOURS", suggestions.get(0).getReminderType());
        assertEquals("URGENT", suggestions.get(0).getPriority());
    }

    @Test
    @DisplayName("Returns empty suggestions for expired deadline")
    void returnsEmptyForPastDeadline() {
        Instant now = Instant.now();
        Instant pastDeadline = now.minus(2, ChronoUnit.DAYS);

        Opportunity opportunity = Opportunity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Past Event")
                .deadlineAt(pastDeadline)
                .build();

        List<ReminderSuggestionDto> suggestions = suggestionService.generateSuggestions(opportunity, null);
        assertNotNull(suggestions);
        assertTrue(suggestions.isEmpty());
    }

    @Test
    @DisplayName("Returns general review reminder when deadline is unspecified")
    void returnsGeneralReviewForNullDeadline() {
        Opportunity opportunity = Opportunity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Rolling Internship")
                .deadlineAt(null)
                .build();

        List<ReminderSuggestionDto> suggestions = suggestionService.generateSuggestions(opportunity, null);
        assertEquals(1, suggestions.size());
        assertEquals("PREPARATION", suggestions.get(0).getReminderType());
        assertTrue(suggestions.get(0).getReason().contains("No deadline specified"));
    }

    @Test
    @DisplayName("Respects user timezone when configured")
    void respectsCustomTimezone() {
        Instant deadline = Instant.now().plus(7, ChronoUnit.DAYS);
        Opportunity opportunity = Opportunity.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .title("Tech Conference")
                .deadlineAt(deadline)
                .build();

        UserPreferences preferences = UserPreferences.builder()
                .timezone("Asia/Kolkata")
                .build();

        List<ReminderSuggestionDto> suggestions = suggestionService.generateSuggestions(opportunity, preferences);
        assertFalse(suggestions.isEmpty());
        for (ReminderSuggestionDto s : suggestions) {
            assertEquals("Asia/Kolkata", s.getSuggestedTimezone());
        }
    }
}
