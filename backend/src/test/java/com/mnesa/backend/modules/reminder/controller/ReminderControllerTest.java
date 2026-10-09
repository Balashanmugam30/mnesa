package com.mnesa.backend.modules.reminder.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.reminder.dto.*;
import com.mnesa.backend.modules.reminder.service.ReminderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReminderService reminderService;

    @Test
    @DisplayName("GET /api/v1/reminders unauthenticated returns 401")
    void listRemindersUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/reminders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/reminders authenticated returns 200")
    void listRemindersAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "user@mnesa.ai", "USER");

        ReminderDto dto = ReminderDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Prepare Application")
                .reminderType("PREPARATION")
                .status("SCHEDULED")
                .scheduledAt(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();

        when(reminderService.getRemindersForUser(eq(userId), eq("upcoming"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(dto)));

        mockMvc.perform(get("/api/v1/reminders")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Prepare Application"));
    }

    @Test
    @DisplayName("POST /api/v1/opportunities/{id}/reminders creates reminder and returns 201")
    void createReminderSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "user@mnesa.ai", "USER");

        CreateReminderRequest request = CreateReminderRequest.builder()
                .title("Submit Recommendation Letter")
                .scheduledAt(Instant.now().plus(3, ChronoUnit.DAYS))
                .reminderType("CUSTOM")
                .targetTimezone("UTC")
                .build();

        ReminderDto responseDto = ReminderDto.builder()
                .id(UUID.randomUUID())
                .opportunityId(opportunityId)
                .userId(userId)
                .title("Submit Recommendation Letter")
                .reminderType("CUSTOM")
                .status("SCHEDULED")
                .scheduledAt(request.getScheduledAt())
                .build();

        when(reminderService.createReminder(eq(userId), any(CreateReminderRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/opportunities/" + opportunityId + "/reminders")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Submit Recommendation Letter"));
    }

    @Test
    @DisplayName("POST /api/v1/reminders/{id}/snooze snoozes reminder and returns 200")
    void snoozeReminderSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID reminderId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "user@mnesa.ai", "USER");

        SnoozeReminderRequest request = SnoozeReminderRequest.builder()
                .snoozeDurationMinutes(60)
                .build();

        ReminderDto responseDto = ReminderDto.builder()
                .id(reminderId)
                .userId(userId)
                .title("Submit Application")
                .status("SNOOZED")
                .snoozeCount(1)
                .build();

        when(reminderService.snoozeReminder(eq(userId), eq(reminderId), any(SnoozeReminderRequest.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/reminders/" + reminderId + "/snooze")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SNOOZED"));
    }

    @Test
    @DisplayName("GET /api/v1/opportunities/{id}/reminder-suggestions returns suggestions list")
    void getSuggestionsSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID opportunityId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "user@mnesa.ai", "USER");

        ReminderSuggestionDto suggestion = ReminderSuggestionDto.builder()
                .reminderType("PREPARATION")
                .title("Prepare Essays")
                .priority("HIGH")
                .reason("Based on deadline proximity")
                .suggestedScheduledAt(Instant.now().plus(4, ChronoUnit.DAYS))
                .build();

        when(reminderService.getSuggestions(userId, opportunityId)).thenReturn(List.of(suggestion));

        mockMvc.perform(get("/api/v1/opportunities/" + opportunityId + "/reminder-suggestions")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Prepare Essays"));
    }
}
