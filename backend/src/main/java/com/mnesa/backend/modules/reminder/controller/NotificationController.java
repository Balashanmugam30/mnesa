package com.mnesa.backend.modules.reminder.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.reminder.dto.NotificationRecordDto;
import com.mnesa.backend.modules.reminder.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for viewing notification delivery history and engagement")
@SecurityRequirement(name = "BearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "List user notifications", description = "Retrieve history of dispatched push and in-app notifications")
    public ResponseEntity<ApiResponse<Page<NotificationRecordDto>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationRecordDto> notifications = notificationService.getUserNotifications(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(notifications));
    }

    @PostMapping("/{id}/opened")
    @Operation(summary = "Mark notification opened", description = "Track notification engagement and deep-link clickthrough")
    public ResponseEntity<ApiResponse<NotificationRecordDto>> markOpened(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        NotificationRecordDto dto = notificationService.markNotificationOpened(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(dto, "Notification marked as opened"));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count", description = "Get count of unopened notifications for the badge indicator")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal principal) {

        long count = notificationService.getUnreadCount(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", count)));
    }
}
