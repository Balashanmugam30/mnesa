package com.mnesa.backend.modules.intake.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.intake.dto.IntakeRequest;
import com.mnesa.backend.modules.intake.dto.IntakeResponse;
import com.mnesa.backend.modules.intake.service.IntakeService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntakeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IntakeService intakeService;

    @Test
    @DisplayName("POST /api/v1/intake without authentication returns 401 Unauthorized")
    void submitIntakeUnauthenticated() throws Exception {
        IntakeRequest request = IntakeRequest.builder()
                .idempotencyKey("unauth-key")
                .url("https://example.com/job")
                .build();

        mockMvc.perform(post("/api/v1/intake")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/intake with valid request returns 202 Accepted")
    void submitIntakeSuccessReturnsAccepted() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        IntakeRequest request = IntakeRequest.builder()
                .idempotencyKey("idem-12345")
                .url("https://careers.google.com/jobs/12345")
                .text("Google Summer Internship 2026")
                .build();

        UUID captureId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        IntakeResponse response = IntakeResponse.builder()
                .captureId(captureId)
                .jobId(jobId)
                .status("RECEIVED")
                .sourceType("URL")
                .canonicalUrl("https://careers.google.com/jobs/12345")
                .message("Opportunity captured and queued for intelligent analysis")
                .duplicate(false)
                .createdAt(Instant.now())
                .build();

        when(intakeService.processIntake(eq(userId), any(IntakeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/intake")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.captureId").value(captureId.toString()))
                .andExpect(jsonPath("$.data.jobId").value(jobId.toString()))
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.duplicate").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/intake on duplicate request returns 200 OK")
    void submitIntakeDuplicateReturnsOk() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        IntakeRequest request = IntakeRequest.builder()
                .idempotencyKey("idem-dup")
                .url("https://example.com/already-captured")
                .build();

        UUID captureId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        IntakeResponse response = IntakeResponse.builder()
                .captureId(captureId)
                .jobId(jobId)
                .status("RECEIVED")
                .sourceType("URL")
                .message("Opportunity already captured (idempotent request)")
                .duplicate(true)
                .createdAt(Instant.now())
                .build();

        when(intakeService.processIntake(eq(userId), any(IntakeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/intake")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.duplicate").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/intake with blank idempotencyKey returns 400 Bad Request")
    void submitIntakeMissingIdempotencyKey() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        IntakeRequest request = IntakeRequest.builder()
                .idempotencyKey("")
                .url("https://example.com/job")
                .build();

        mockMvc.perform(post("/api/v1/intake")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }
}
