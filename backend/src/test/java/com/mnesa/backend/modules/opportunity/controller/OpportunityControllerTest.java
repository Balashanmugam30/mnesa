package com.mnesa.backend.modules.opportunity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.opportunity.dto.*;
import com.mnesa.backend.modules.opportunity.service.OpportunityService;
import com.mnesa.backend.modules.opportunity.service.TagService;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpportunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OpportunityService opportunityService;

    @MockBean
    private TagService tagService;

    @Test
    @DisplayName("GET /api/v1/opportunities unauthenticated returns 401")
    void listOpportunitiesUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/opportunities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/opportunities authenticated returns 200 with paged opportunities")
    void listOpportunitiesAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        OpportunitySummaryResponse summary = OpportunitySummaryResponse.builder()
                .id(UUID.randomUUID())
                .title("Data Science Internship")
                .organization("Spotify")
                .category("INTERNSHIP")
                .status("SAVED")
                .build();

        when(opportunityService.searchOpportunities(eq(userId), any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(false), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(summary)));

        mockMvc.perform(get("/api/v1/opportunities")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Data Science Internship"));
    }

    @Test
    @DisplayName("POST /api/v1/opportunities with valid request returns 201 Created")
    void createOpportunitySuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        CreateOpportunityRequest request = CreateOpportunityRequest.builder()
                .title("ML Fellowship")
                .organization("OpenAI")
                .category("FELLOWSHIP")
                .build();

        OpportunityResponse created = OpportunityResponse.builder()
                .id(UUID.randomUUID())
                .title("ML Fellowship")
                .organization("OpenAI")
                .category("OTHER")
                .status("SAVED")
                .build();

        when(opportunityService.createOpportunity(eq(userId), any())).thenReturn(created);

        mockMvc.perform(post("/api/v1/opportunities")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("ML Fellowship"));
    }

    @Test
    @DisplayName("POST /api/v1/opportunities with blank title returns 400 Bad Request")
    void createOpportunityBlankTitle() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        CreateOpportunityRequest request = CreateOpportunityRequest.builder()
                .title("")
                .category("JOB")
                .build();

        mockMvc.perform(post("/api/v1/opportunities")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("GET /api/v1/opportunities/{id} returns details")
    void getOpportunityDetails() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID oppId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        OpportunityResponse response = OpportunityResponse.builder()
                .id(oppId)
                .title("Backend Engineer")
                .status("SAVED")
                .build();

        when(opportunityService.getOpportunity(userId, oppId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/opportunities/" + oppId)
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(oppId.toString()))
                .andExpect(jsonPath("$.data.title").value("Backend Engineer"));
    }

    @Test
    @DisplayName("PATCH /api/v1/opportunities/{id}/status transitions state")
    void transitionStatus() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID oppId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        UpdateStatusRequest request = new UpdateStatusRequest("APPLIED", "Submitted via portal");

        OpportunityResponse response = OpportunityResponse.builder()
                .id(oppId)
                .status("APPLIED")
                .build();

        when(opportunityService.updateStatus(eq(userId), eq(oppId), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/opportunities/" + oppId + "/status")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPLIED"));
    }

    @Test
    @DisplayName("POST /api/v1/opportunities/{id}/archive archives opportunity")
    void archiveOpportunity() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID oppId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        OpportunityResponse response = OpportunityResponse.builder()
                .id(oppId)
                .status("ARCHIVED")
                .build();

        when(opportunityService.archiveOpportunity(userId, oppId)).thenReturn(response);

        mockMvc.perform(post("/api/v1/opportunities/" + oppId + "/archive")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ARCHIVED"));
    }

    @Test
    @DisplayName("GET /api/v1/home returns home dashboard aggregation")
    void getHomeDashboard() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        HomeDashboardResponse dashboard = HomeDashboardResponse.builder()
                .greeting("Good afternoon.")
                .statusCounts(Map.of("SAVED", 3L, "APPLYING", 1L))
                .totalActive(4L)
                .suggestedAction(SuggestedActionResponse.builder().title("Review").build())
                .build();

        when(opportunityService.getHomeDashboard(userId)).thenReturn(dashboard);

        mockMvc.perform(get("/api/v1/home")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.greeting").value("Good afternoon."))
                .andExpect(jsonPath("$.data.totalActive").value(4));
    }

    @Test
    @DisplayName("GET /api/v1/categories returns category catalog with counts")
    void getCategories() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "student@mnesa.ai", "USER");

        List<CategorySummaryResponse> list = List.of(
                new CategorySummaryResponse("INTERNSHIP", "Internship", 5),
                new CategorySummaryResponse("JOB", "Job", 3)
        );

        when(opportunityService.getCategories(userId)).thenReturn(list);

        mockMvc.perform(get("/api/v1/categories")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].category").value("INTERNSHIP"))
                .andExpect(jsonPath("$.data[0].activeCount").value(5));
    }
}
