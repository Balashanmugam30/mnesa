package com.mnesa.backend.modules.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.user.domain.ReminderTimingPreference;
import com.mnesa.backend.modules.user.dto.UpdatePreferencesRequest;
import com.mnesa.backend.modules.user.dto.UserPreferencesDto;
import com.mnesa.backend.modules.user.dto.UserProfileResponse;
import com.mnesa.backend.modules.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/v1/users/me without token returns 401 Unauthorized")
    void getProfileUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("GET /api/v1/users/me with valid auth returns 200 OK and profile")
    void getProfileAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "sarah@example.com", "USER");

        UserProfileResponse profileResponse = UserProfileResponse.builder()
                .id(userId)
                .email("sarah@example.com")
                .fullName("Sarah Connor")
                .role("USER")
                .status("ACTIVE")
                .createdAt(Instant.now())
                .preferences(UserPreferencesDto.builder()
                        .interests(List.of("INTERNSHIP", "HACKATHON"))
                        .reminderTiming(ReminderTimingPreference.STANDARD)
                        .emailNotificationsEnabled(true)
                        .pushNotificationsEnabled(true)
                        .build())
                .build();

        when(userService.getCurrentUser(eq(userId))).thenReturn(profileResponse);

        mockMvc.perform(get("/api/v1/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("sarah@example.com"))
                .andExpect(jsonPath("$.data.fullName").value("Sarah Connor"))
                .andExpect(jsonPath("$.data.preferences.interests[0]").value("INTERNSHIP"));
    }

    @Test
    @DisplayName("PUT /api/v1/users/me/preferences updates and returns preferences")
    void updatePreferencesAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "sarah@example.com", "USER");

        UpdatePreferencesRequest request = UpdatePreferencesRequest.builder()
                .interests(List.of("JOB", "SCHOLARSHIP"))
                .reminderTiming(ReminderTimingPreference.AGGRESSIVE)
                .emailNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .build();

        UserPreferencesDto updatedPrefs = UserPreferencesDto.builder()
                .interests(List.of("JOB", "SCHOLARSHIP"))
                .reminderTiming(ReminderTimingPreference.AGGRESSIVE)
                .emailNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .build();

        when(userService.updatePreferences(eq(userId), any(UpdatePreferencesRequest.class))).thenReturn(updatedPrefs);

        mockMvc.perform(put("/api/v1/users/me/preferences")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reminderTiming").value("AGGRESSIVE"))
                .andExpect(jsonPath("$.data.emailNotificationsEnabled").value(false));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/me cascades deletion and returns 204 No Content")
    void deleteAccountAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "sarah@example.com", "USER");

        doNothing().when(userService).deleteAccount(eq(userId));

        mockMvc.perform(delete("/api/v1/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isNoContent());
    }
}
