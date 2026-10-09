package com.mnesa.backend.modules.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.reminder.dto.NotificationRecordDto;
import com.mnesa.backend.modules.reminder.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificationService notificationService;

    @Test
    @DisplayName("GET /api/v1/notifications unauthenticated returns 401")
    void listNotificationsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/notifications authenticated returns 200 with notification list")
    void listNotificationsAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "user@mnesa.ai", "USER");

        NotificationRecordDto dto = NotificationRecordDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Reminder: Submit Grant")
                .body("2 days remaining")
                .channel("PUSH")
                .provider("MOCK_DEVELOPMENT")
                .deliveryStatus("DELIVERED")
                .deepLinkUri("mnesa://opportunity/123")
                .createdAt(Instant.now())
                .build();

        when(notificationService.getUserNotifications(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/v1/notifications")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Reminder: Submit Grant"));
    }

    @Test
    @DisplayName("POST /api/v1/notifications/{id}/opened marks notification opened and returns 200")
    void markNotificationOpenedSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID notifId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "user@mnesa.ai", "USER");

        NotificationRecordDto dto = NotificationRecordDto.builder()
                .id(notifId)
                .userId(userId)
                .title("Reminder: Submit Grant")
                .deliveryStatus("OPENED")
                .openedAt(Instant.now())
                .build();

        when(notificationService.markNotificationOpened(userId, notifId)).thenReturn(dto);

        mockMvc.perform(post("/api/v1/notifications/" + notifId + "/opened")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deliveryStatus").value("OPENED"));
    }

    @Test
    @DisplayName("GET /api/v1/notifications/unread-count returns unread count")
    void getUnreadCountSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "user@mnesa.ai", "USER");

        when(notificationService.getUnreadCount(userId)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.unreadCount").value(5));
    }
}
