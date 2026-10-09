package com.mnesa.backend.modules.reminder.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import com.mnesa.backend.modules.reminder.domain.Reminder;
import com.mnesa.backend.modules.reminder.domain.ReminderStatus;
import com.mnesa.backend.modules.reminder.domain.ReminderType;
import com.mnesa.backend.modules.reminder.dto.*;
import com.mnesa.backend.modules.reminder.repository.ReminderRepository;
import com.mnesa.backend.modules.user.domain.User;
import com.mnesa.backend.modules.user.repository.UserPreferencesRepository;
import com.mnesa.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private SmartReminderSuggestionService suggestionService;

    private ReminderService reminderService;

    private final UUID userId = UUID.randomUUID();
    private final UUID otherUserId = UUID.randomUUID();
    private final UUID opportunityId = UUID.randomUUID();
    private final UUID reminderId = UUID.randomUUID();

    private User testUser;
    private Opportunity testOpportunity;
    private Reminder testReminder;

    @BeforeEach
    void setUp() {
        reminderService = new ReminderService(
                reminderRepository,
                opportunityRepository,
                userRepository,
                userPreferencesRepository,
                suggestionService
        );

        testUser = User.builder().id(userId).email("user@mnesa.ai").build();

        testOpportunity = Opportunity.builder()
                .id(opportunityId)
                .userId(userId)
                .title("Google Summer of Code")
                .opportunityType(OpportunityType.INTERNSHIP)
                .status(OpportunityStatus.SAVED)
                .deadlineAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        testReminder = Reminder.builder()
                .id(reminderId)
                .user(testUser)
                .opportunity(testOpportunity)
                .title("Prepare Proposal")
                .reminderType(ReminderType.PREPARATION)
                .scheduledAt(Instant.now().plus(3, ChronoUnit.DAYS))
                .status(ReminderStatus.SCHEDULED)
                .snoozeCount(0)
                .build();
    }

    @Test
    @DisplayName("Create reminder successfully saves and returns DTO")
    void createReminderSuccess() {
        CreateReminderRequest request = CreateReminderRequest.builder()
                .opportunityId(opportunityId)
                .title("Prepare Proposal")
                .scheduledAt(Instant.now().plus(2, ChronoUnit.DAYS))
                .reminderType("PREPARATION")
                .targetTimezone("UTC")
                .build();

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(testOpportunity));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(invocation -> {
            Reminder r = invocation.getArgument(0);
            r.setId(reminderId);
            return r;
        });

        ReminderDto result = reminderService.createReminder(userId, request);

        assertNotNull(result);
        assertEquals("Prepare Proposal", result.getTitle());
        assertEquals("PREPARATION", result.getReminderType());
        assertEquals("SCHEDULED", result.getStatus());
        verify(reminderRepository).save(any(Reminder.class));
    }

    @Test
    @DisplayName("Create reminder fails if opportunity belongs to another user")
    void createReminderForbiddenForOtherUser() {
        Opportunity foreignOpportunity = Opportunity.builder()
                .id(opportunityId)
                .userId(otherUserId)
                .title("Foreign")
                .build();

        when(opportunityRepository.findById(opportunityId)).thenReturn(Optional.of(foreignOpportunity));

        CreateReminderRequest request = CreateReminderRequest.builder()
                .opportunityId(opportunityId)
                .title("Test")
                .scheduledAt(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();

        assertThrows(AccessDeniedException.class, () -> reminderService.createReminder(userId, request));
    }

    @Test
    @DisplayName("Snooze reminder increments counter and updates snoozeUntil")
    void snoozeReminderSuccess() {
        when(reminderRepository.findById(reminderId)).thenReturn(Optional.of(testReminder));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(i -> i.getArgument(0));

        SnoozeReminderRequest request = SnoozeReminderRequest.builder()
                .snoozeDurationMinutes(120)
                .build();

        ReminderDto result = reminderService.snoozeReminder(userId, reminderId, request);

        assertNotNull(result);
        assertEquals(ReminderStatus.SNOOZED.name(), result.getStatus());
        assertEquals(1, result.getSnoozeCount());
        assertNotNull(result.getSnoozeUntil());
        assertTrue(result.getSnoozeUntil().isAfter(Instant.now()));
    }

    @Test
    @DisplayName("Dismiss reminder marks status DISMISSED")
    void dismissReminderSuccess() {
        when(reminderRepository.findById(reminderId)).thenReturn(Optional.of(testReminder));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(i -> i.getArgument(0));

        ReminderDto result = reminderService.dismissReminder(userId, reminderId);

        assertEquals(ReminderStatus.DISMISSED.name(), result.getStatus());
    }

    @Test
    @DisplayName("Auto-cancel cancels pending reminders when opportunity is APPLIED")
    void autoCancelRemindersOnOpportunityApplied() {
        Reminder pendingReminder = Reminder.builder()
                .id(UUID.randomUUID())
                .status(ReminderStatus.SCHEDULED)
                .build();

        when(reminderRepository.findByOpportunityId(opportunityId)).thenReturn(List.of(pendingReminder));

        reminderService.autoCancelRemindersForOpportunity(opportunityId, OpportunityStatus.APPLIED);

        assertEquals(ReminderStatus.CANCELLED, pendingReminder.getStatus());
        verify(reminderRepository).save(pendingReminder);
    }

    @Test
    @DisplayName("Get reminders filtered by upcoming returns only SCHEDULED and SNOOZED")
    void getRemindersUpcomingFilter() {
        Page<Reminder> page = new PageImpl<>(List.of(testReminder), PageRequest.of(0, 20), 1);
        when(reminderRepository.findByUserIdAndStatusInOrderByScheduledAtAsc(eq(userId), any(), any()))
                .thenReturn(page);

        Page<ReminderDto> result = reminderService.getRemindersForUser(userId, "upcoming", PageRequest.of(0, 20));

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
