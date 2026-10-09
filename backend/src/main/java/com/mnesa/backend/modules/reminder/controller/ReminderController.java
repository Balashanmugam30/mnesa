package com.mnesa.backend.modules.reminder.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.reminder.dto.*;
import com.mnesa.backend.modules.reminder.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Reminders & Notifications", description = "Endpoints for scheduling, snoozing, and managing smart reminders")
@SecurityRequirement(name = "BearerAuth")
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping("/reminders")
    @Operation(summary = "List reminders", description = "List reminders with filters (all, upcoming, needs_attention, snoozed, completed)")
    public ResponseEntity<ApiResponse<Page<ReminderDto>>> getReminders(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "upcoming") String filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReminderDto> reminders = reminderService.getRemindersForUser(principal.getId(), filter, pageable);
        return ResponseEntity.ok(ApiResponse.ok(reminders));
    }

    @GetMapping("/opportunities/{opportunityId}/reminders")
    @Operation(summary = "List reminders for opportunity", description = "List all reminders created for a specific opportunity")
    public ResponseEntity<ApiResponse<List<ReminderDto>>> getOpportunityReminders(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID opportunityId) {

        List<ReminderDto> reminders = reminderService.getRemindersForOpportunity(principal.getId(), opportunityId);
        return ResponseEntity.ok(ApiResponse.ok(reminders));
    }

    @PostMapping("/opportunities/{opportunityId}/reminders")
    @Operation(summary = "Create reminder for opportunity", description = "Create a new scheduled reminder for an opportunity")
    public ResponseEntity<ApiResponse<ReminderDto>> createReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID opportunityId,
            @Valid @RequestBody CreateReminderRequest request) {

        request.setOpportunityId(opportunityId);
        ReminderDto reminder = reminderService.createReminder(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(reminder, "Reminder created successfully"));
    }

    @GetMapping("/opportunities/{opportunityId}/reminder-suggestions")
    @Operation(summary = "Get smart reminder suggestions", description = "Deterministic recommendations based on opportunity deadlines, effort, and user preferences")
    public ResponseEntity<ApiResponse<List<ReminderSuggestionDto>>> getReminderSuggestions(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID opportunityId) {

        List<ReminderSuggestionDto> suggestions = reminderService.getSuggestions(principal.getId(), opportunityId);
        return ResponseEntity.ok(ApiResponse.ok(suggestions));
    }

    @PutMapping("/reminders/{id}")
    @Operation(summary = "Update reminder", description = "Update scheduled time, notes, title, or timezone of an existing reminder")
    public ResponseEntity<ApiResponse<ReminderDto>> updateReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateReminderRequest request) {

        ReminderDto updated = reminderService.updateReminder(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Reminder updated successfully"));
    }

    @PostMapping("/reminders/{id}/snooze")
    @Operation(summary = "Snooze reminder", description = "Snooze reminder for preset minutes (e.g. 60, 180, 1440) or custom timestamp")
    public ResponseEntity<ApiResponse<ReminderDto>> snoozeReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @RequestBody(required = false) SnoozeReminderRequest request) {

        if (request == null) {
            request = SnoozeReminderRequest.builder().snoozeDurationMinutes(60).build();
        }
        ReminderDto snoozed = reminderService.snoozeReminder(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(snoozed, "Reminder snoozed successfully"));
    }

    @PostMapping("/reminders/{id}/dismiss")
    @Operation(summary = "Dismiss reminder", description = "Dismiss an active or triggered reminder")
    public ResponseEntity<ApiResponse<ReminderDto>> dismissReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        ReminderDto dismissed = reminderService.dismissReminder(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(dismissed, "Reminder dismissed"));
    }

    @DeleteMapping("/reminders/{id}")
    @Operation(summary = "Cancel reminder", description = "Cancel or delete a reminder")
    public ResponseEntity<ApiResponse<Void>> deleteReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        reminderService.deleteOrCancelReminder(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Reminder cancelled successfully"));
    }
}
