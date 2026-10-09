package com.mnesa.backend.modules.reminder.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import com.mnesa.backend.modules.reminder.domain.Reminder;
import com.mnesa.backend.modules.reminder.domain.ReminderStatus;
import com.mnesa.backend.modules.reminder.domain.ReminderType;
import com.mnesa.backend.modules.reminder.dto.*;
import com.mnesa.backend.modules.reminder.repository.ReminderRepository;
import com.mnesa.backend.modules.user.domain.User;
import com.mnesa.backend.modules.user.domain.UserPreferences;
import com.mnesa.backend.modules.user.repository.UserPreferencesRepository;
import com.mnesa.backend.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final SmartReminderSuggestionService suggestionService;

    public ReminderService(ReminderRepository reminderRepository,
                           OpportunityRepository opportunityRepository,
                           UserRepository userRepository,
                           UserPreferencesRepository userPreferencesRepository,
                           SmartReminderSuggestionService suggestionService) {
        this.reminderRepository = reminderRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.suggestionService = suggestionService;
    }

    @Transactional(readOnly = true)
    public Page<ReminderDto> getRemindersForUser(UUID userId, String filter, Pageable pageable) {
        Page<Reminder> page;
        String normalizedFilter = filter != null ? filter.toLowerCase().trim() : "upcoming";

        switch (normalizedFilter) {
            case "snoozed":
                page = reminderRepository.findByUserIdAndStatusOrderByScheduledAtAsc(userId, ReminderStatus.SNOOZED, pageable);
                break;
            case "history":
            case "completed":
                page = reminderRepository.findByUserIdAndStatusInOrderByScheduledAtAsc(
                        userId,
                        Set.of(ReminderStatus.SENT, ReminderStatus.DISMISSED, ReminderStatus.CANCELLED),
                        pageable);
                break;
            case "needs_attention":
                // Upcoming within 24 hours
                page = reminderRepository.findByUserIdAndStatusInOrderByScheduledAtAsc(
                        userId,
                        Set.of(ReminderStatus.SCHEDULED, ReminderStatus.SNOOZED),
                        pageable);
                Instant threshold = Instant.now().plus(24, ChronoUnit.HOURS);
                List<Reminder> filtered = page.getContent().stream()
                        .filter(r -> r.getEffectiveTriggerTime().isBefore(threshold))
                        .toList();
                return new PageImpl<>(filtered.stream().map(this::toDto).toList(), pageable, filtered.size());
            case "all":
                page = reminderRepository.findByUserIdOrderByScheduledAtAsc(userId, pageable);
                break;
            case "upcoming":
            default:
                page = reminderRepository.findByUserIdAndStatusInOrderByScheduledAtAsc(
                        userId,
                        Set.of(ReminderStatus.SCHEDULED, ReminderStatus.SNOOZED),
                        pageable);
                break;
        }

        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> getRemindersForOpportunity(UUID userId, UUID opportunityId) {
        verifyOpportunityOwnership(userId, opportunityId);
        return reminderRepository.findByOpportunityIdAndUserId(opportunityId, userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReminderDto createReminder(UUID userId, CreateReminderRequest request) {
        UUID oppId = request.getOpportunityId();
        if (oppId == null) {
            throw new IllegalArgumentException("Opportunity ID is required to create a reminder");
        }
        Opportunity opportunity = verifyOpportunityOwnership(userId, oppId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ReminderType type = ReminderType.CUSTOM;
        if (request.getReminderType() != null) {
            try {
                type = ReminderType.valueOf(request.getReminderType().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        Reminder reminder = Reminder.builder()
                .opportunity(opportunity)
                .user(user)
                .title(request.getTitle())
                .notes(request.getNotes())
                .reminderType(type)
                .scheduledAt(request.getScheduledAt())
                .targetTimezone(request.getTargetTimezone() != null ? request.getTargetTimezone() : "UTC")
                .status(ReminderStatus.SCHEDULED)
                .smartReason(request.getSmartReason())
                .build();

        reminder = reminderRepository.save(reminder);
        log.info("Created reminder {} for opportunity {} by user {}", reminder.getId(), oppId, userId);
        return toDto(reminder);
    }

    @Transactional
    public ReminderDto updateReminder(UUID userId, UUID reminderId, UpdateReminderRequest request) {
        Reminder reminder = getReminderAndVerifyOwnership(userId, reminderId);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            reminder.setTitle(request.getTitle());
        }
        if (request.getNotes() != null) {
            reminder.setNotes(request.getNotes());
        }
        if (request.getScheduledAt() != null) {
            reminder.setScheduledAt(request.getScheduledAt());
            reminder.setStatus(ReminderStatus.SCHEDULED);
            reminder.setSnoozeUntil(null);
        }
        if (request.getTargetTimezone() != null) {
            reminder.setTargetTimezone(request.getTargetTimezone());
        }
        if (request.getReminderType() != null) {
            try {
                reminder.setReminderType(ReminderType.valueOf(request.getReminderType().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }

        return toDto(reminderRepository.save(reminder));
    }

    @Transactional
    public ReminderDto snoozeReminder(UUID userId, UUID reminderId, SnoozeReminderRequest request) {
        Reminder reminder = getReminderAndVerifyOwnership(userId, reminderId);

        Instant targetInstant;
        if (request.getCustomSnoozeUntil() != null) {
            targetInstant = request.getCustomSnoozeUntil();
        } else {
            int minutes = (request.getSnoozeDurationMinutes() != null && request.getSnoozeDurationMinutes() > 0)
                    ? request.getSnoozeDurationMinutes()
                    : 60; // Default 1 hour
            targetInstant = Instant.now().plus(minutes, ChronoUnit.MINUTES);
        }

        reminder.snooze(targetInstant);
        log.info("Snoozed reminder {} until {}", reminderId, targetInstant);
        return toDto(reminderRepository.save(reminder));
    }

    @Transactional
    public ReminderDto dismissReminder(UUID userId, UUID reminderId) {
        Reminder reminder = getReminderAndVerifyOwnership(userId, reminderId);
        reminder.dismiss();
        return toDto(reminderRepository.save(reminder));
    }

    @Transactional
    public void deleteOrCancelReminder(UUID userId, UUID reminderId) {
        Reminder reminder = getReminderAndVerifyOwnership(userId, reminderId);
        reminder.cancel();
        reminderRepository.save(reminder);
        log.info("Cancelled reminder {} by user {}", reminderId, userId);
    }

    @Transactional
    public void autoCancelRemindersForOpportunity(UUID opportunityId, OpportunityStatus newStatus) {
        if (newStatus == OpportunityStatus.APPLIED ||
            newStatus == OpportunityStatus.ARCHIVED ||
            newStatus == OpportunityStatus.SELECTED ||
            newStatus == OpportunityStatus.REJECTED ||
            newStatus == OpportunityStatus.MISSED) {

            List<Reminder> reminders = reminderRepository.findByOpportunityId(opportunityId);
            for (Reminder r : reminders) {
                if (r.getStatus() == ReminderStatus.SCHEDULED || r.getStatus() == ReminderStatus.SNOOZED) {
                    r.cancel();
                    reminderRepository.save(r);
                    log.info("Auto-cancelled reminder {} due to opportunity status {}", r.getId(), newStatus);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ReminderSuggestionDto> getSuggestions(UUID userId, UUID opportunityId) {
        Opportunity opportunity = verifyOpportunityOwnership(userId, opportunityId);
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId).orElse(null);
        return suggestionService.generateSuggestions(opportunity, preferences);
    }

    private Reminder getReminderAndVerifyOwnership(UUID userId, UUID reminderId) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new ResourceNotFoundException("Reminder", "id", reminderId));

        if (!reminder.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this reminder");
        }
        return reminder;
    }

    private Opportunity verifyOpportunityOwnership(UUID userId, UUID opportunityId) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity", "id", opportunityId));

        if (!opportunity.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this opportunity");
        }
        return opportunity;
    }

    public ReminderDto toDto(Reminder reminder) {
        Opportunity opp = reminder.getOpportunity();
        return ReminderDto.builder()
                .id(reminder.getId())
                .opportunityId(opp != null ? opp.getId() : null)
                .opportunityTitle(opp != null ? opp.getTitle() : null)
                .opportunityCategory(opp != null && opp.getOpportunityType() != null ? opp.getOpportunityType().name() : null)
                .opportunityDeadline(opp != null ? opp.getDeadlineAt() : null)
                .userId(reminder.getUser() != null ? reminder.getUser().getId() : null)
                .title(reminder.getTitle())
                .notes(reminder.getNotes())
                .reminderType(reminder.getReminderType() != null ? reminder.getReminderType().name() : "CUSTOM")
                .scheduledAt(reminder.getScheduledAt())
                .targetTimezone(reminder.getTargetTimezone())
                .status(reminder.getStatus() != null ? reminder.getStatus().name() : "SCHEDULED")
                .snoozeUntil(reminder.getSnoozeUntil())
                .snoozeCount(reminder.getSnoozeCount())
                .smartReason(reminder.getSmartReason())
                .sentAt(reminder.getSentAt())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }
}
