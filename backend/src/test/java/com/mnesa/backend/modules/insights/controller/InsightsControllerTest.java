package com.mnesa.backend.modules.insights.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.insights.dto.ActivityTrendPointDto;
import com.mnesa.backend.modules.insights.dto.InsightsDto;
import com.mnesa.backend.modules.insights.service.InsightsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InsightsService insightsService;

    @Test
    @DisplayName("GET /api/v1/insights unauthenticated returns 401")
    void testGetInsightsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/insights"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/insights authenticated returns 200 with metrics")
    void testGetInsightsAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "test@mnesa.ai", "USER");

        InsightsDto dto = InsightsDto.builder()
                .totalSaved(12)
                .applicationsCompleted(5)
                .upcomingDeadlines(3)
                .missedOpportunities(1)
                .categoryDistribution(Map.of("INTERNSHIP", 7L, "GRANT", 5L))
                .statusDistribution(Map.of("SAVED", 6L, "APPLIED", 5L, "MISSED", 1L))
                .activityTrends(List.of(
                        ActivityTrendPointDto.builder().date("2026-10-01").count(2).build(),
                        ActivityTrendPointDto.builder().date("2026-10-02").count(4).build()
                ))
                .build();

        when(insightsService.getInsights(userId)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/insights")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalSaved").value(12))
                .andExpect(jsonPath("$.data.applicationsCompleted").value(5))
                .andExpect(jsonPath("$.data.upcomingDeadlines").value(3))
                .andExpect(jsonPath("$.data.categoryDistribution.INTERNSHIP").value(7))
                .andExpect(jsonPath("$.data.activityTrends[0].date").value("2026-10-01"));
    }
}
