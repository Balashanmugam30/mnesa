package com.mnesa.backend.modules.assistant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.assistant.dto.AssistantQueryRequest;
import com.mnesa.backend.modules.assistant.dto.AssistantQueryResponse;
import com.mnesa.backend.modules.assistant.dto.CitedOpportunityDto;
import com.mnesa.backend.modules.assistant.service.AssistantService;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AssistantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AssistantService assistantService;

    @Test
    @DisplayName("POST /api/v1/assistant/query unauthenticated returns 401")
    void testQueryUnauthenticated() throws Exception {
        AssistantQueryRequest request = AssistantQueryRequest.builder()
                .question("What deadlines do I have this week?")
                .build();

        mockMvc.perform(post("/api/v1/assistant/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/assistant/query with blank question returns 400")
    void testQueryBlankQuestion() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "test@mnesa.ai", "USER");

        AssistantQueryRequest request = AssistantQueryRequest.builder()
                .question("")
                .build();

        mockMvc.perform(post("/api/v1/assistant/query")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/assistant/query authenticated returns 200 with grounded response")
    void testQuerySuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "test@mnesa.ai", "USER");

        AssistantQueryResponse response = AssistantQueryResponse.builder()
                .question("What deadlines do I have this week?")
                .answer("You have 1 deadline approaching: Google AI Residency.")
                .intent("UPCOMING_DEADLINES")
                .citedOpportunities(List.of(
                        CitedOpportunityDto.builder()
                                .id(UUID.randomUUID())
                                .title("Google AI Residency")
                                .organization("Google")
                                .category("INTERNSHIP")
                                .deadlineAt(Instant.now().plusSeconds(86400))
                                .status("SAVED")
                                .priority("HIGH")
                                .build()
                ))
                .actionSuggestions(List.of("Set preparation reminder"))
                .build();

        when(assistantService.askAssistant(eq(userId), any(AssistantQueryRequest.class)))
                .thenReturn(response);

        AssistantQueryRequest request = AssistantQueryRequest.builder()
                .question("What deadlines do I have this week?")
                .timezone("America/New_York")
                .build();

        mockMvc.perform(post("/api/v1/assistant/query")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.intent").value("UPCOMING_DEADLINES"))
                .andExpect(jsonPath("$.data.answer").value("You have 1 deadline approaching: Google AI Residency."))
                .andExpect(jsonPath("$.data.citedOpportunities[0].title").value("Google AI Residency"));
    }
}
