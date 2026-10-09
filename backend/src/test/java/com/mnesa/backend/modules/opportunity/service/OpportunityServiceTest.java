package com.mnesa.backend.modules.opportunity.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.opportunity.domain.*;
import com.mnesa.backend.modules.opportunity.dto.*;
import com.mnesa.backend.modules.opportunity.exception.InvalidLifecycleTransitionException;
import com.mnesa.backend.modules.opportunity.repository.OpportunityActivityRepository;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import com.mnesa.backend.modules.opportunity.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private OpportunityActivityRepository activityRepository;

    @Mock
    private TagRepository tagRepository;

    private OpportunityTransitionService transitionService;
    private OpportunityService opportunityService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        transitionService = new OpportunityTransitionService();
        opportunityService = new OpportunityService(
                opportunityRepository,
                activityRepository,
                tagRepository,
                transitionService
        );
    }

    @Test
    @DisplayName("Create opportunity sets SAVED status and logs CREATED activity")
    void createOpportunitySuccess() {
        CreateOpportunityRequest request = CreateOpportunityRequest.builder()
                .title("SWE Summer Internship")
                .organization("Google")
                .category("INTERNSHIP")
                .deadlineAt(Instant.now().plus(14, ChronoUnit.DAYS))
                .priority("HIGH")
                .tags(List.of("python", "cloud"))
                .build();

        Tag tagPython = Tag.builder().id(UUID.randomUUID()).userId(userId).name("python").build();
        Tag tagCloud = Tag.builder().id(UUID.randomUUID()).userId(userId).name("cloud").build();
        when(tagRepository.findByUserIdAndName(eq(userId), eq("python"))).thenReturn(Optional.of(tagPython));
        when(tagRepository.findByUserIdAndName(eq(userId), eq("cloud"))).thenReturn(Optional.of(tagCloud));

        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> {
            Opportunity o = invocation.getArgument(0);
            o.setId(UUID.randomUUID());
            o.setCreatedAt(Instant.now());
            o.setUpdatedAt(Instant.now());
            return o;
        });

        OpportunityResponse response = opportunityService.createOpportunity(userId, request);

        assertNotNull(response);
        assertEquals("SWE Summer Internship", response.getTitle());
        assertEquals("Google", response.getOrganization());
        assertEquals("INTERNSHIP", response.getCategory());
        assertEquals("SAVED", response.getStatus());
        assertEquals(2, response.getTags().size());

        verify(activityRepository, times(1)).save(any(OpportunityActivity.class));
    }

    @Test
    @DisplayName("Update opportunity modifies fields without clearing unmentioned values")
    void updateOpportunityPartial() {
        UUID oppId = UUID.randomUUID();
        Opportunity existing = Opportunity.builder()
                .id(oppId)
                .userId(userId)
                .title("Initial Title")
                .organization("Meta")
                .opportunityType(OpportunityType.JOB)
                .status(OpportunityStatus.SAVED)
                .notes("Original notes")
                .build();

        when(opportunityRepository.findByIdAndUserId(oppId, userId)).thenReturn(Optional.of(existing));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateOpportunityRequest request = UpdateOpportunityRequest.builder()
                .title("Updated Title")
                .build();

        OpportunityResponse response = opportunityService.updateOpportunity(userId, oppId, request);

        assertEquals("Updated Title", response.getTitle());
        assertEquals("Meta", response.getOrganization());
        assertEquals("Original notes", response.getNotes());
        verify(activityRepository, times(1)).save(any(OpportunityActivity.class));
    }

    @Test
    @DisplayName("Update status validates lifecycle transition and records history")
    void updateStatusValidTransition() {
        UUID oppId = UUID.randomUUID();
        Opportunity existing = Opportunity.builder()
                .id(oppId)
                .userId(userId)
                .title("Hackathon")
                .status(OpportunityStatus.SAVED)
                .build();

        when(opportunityRepository.findByIdAndUserId(oppId, userId)).thenReturn(Optional.of(existing));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStatusRequest request = new UpdateStatusRequest("APPLYING", "Started drafting pitch deck");
        OpportunityResponse response = opportunityService.updateStatus(userId, oppId, request);

        assertEquals("APPLYING", response.getStatus());
        verify(activityRepository, times(1)).save(argThat(act ->
                "STATUS_CHANGED".equals(act.getActionType()) &&
                        "SAVED".equals(act.getOldStatus()) &&
                        "APPLYING".equals(act.getNewStatus())
        ));
    }

    @Test
    @DisplayName("Archive and Restore preserves previous status")
    void archiveAndRestore() {
        UUID oppId = UUID.randomUUID();
        Opportunity existing = Opportunity.builder()
                .id(oppId)
                .userId(userId)
                .title("Research Fellowship")
                .status(OpportunityStatus.APPLIED)
                .build();

        when(opportunityRepository.findByIdAndUserId(oppId, userId)).thenReturn(Optional.of(existing));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(inv -> inv.getArgument(0));

        // 1. Archive
        OpportunityResponse archived = opportunityService.archiveOpportunity(userId, oppId);
        assertEquals("ARCHIVED", archived.getStatus());
        assertEquals("APPLIED", archived.getPreviousStatus());
        assertNotNull(archived.getArchivedAt());

        // 2. Restore
        OpportunityResponse restored = opportunityService.restoreOpportunity(userId, oppId);
        assertEquals("APPLIED", restored.getStatus());
        assertNull(restored.getArchivedAt());
    }

    @Test
    @DisplayName("Search opportunities delegates to specification and returns paged results")
    void searchOpportunitiesSuccess() {
        Opportunity opp = Opportunity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Found Opportunity")
                .opportunityType(OpportunityType.INTERNSHIP)
                .status(OpportunityStatus.SAVED)
                .build();

        Page<Opportunity> page = new PageImpl<>(List.of(opp));
        when(opportunityRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<OpportunitySummaryResponse> result = opportunityService.searchOpportunities(
                userId, "Found", "INTERNSHIP", null, null, null, null, null, null, null, false,
                PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Found Opportunity", result.getContent().get(0).getTitle());
    }

    @Test
    @DisplayName("Home dashboard aggregates counts, urgent items, and suggested action")
    void homeDashboardAggregation() {
        when(opportunityRepository.countByUserIdAndStatus(eq(userId), any())).thenReturn(2L);
        when(opportunityRepository.countByUserIdAndStatusNot(eq(userId), eq(OpportunityStatus.ARCHIVED))).thenReturn(5L);

        Opportunity urgentOpp = Opportunity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .title("Urgent Hackathon")
                .status(OpportunityStatus.APPLYING)
                .deadlineAt(Instant.now().plus(2, ChronoUnit.DAYS))
                .build();

        when(opportunityRepository.findUpcomingBetween(eq(userId), eq(OpportunityStatus.ARCHIVED), any(), any(), any()))
                .thenReturn(List.of(urgentOpp));

        HomeDashboardResponse dashboard = opportunityService.getHomeDashboard(userId);

        assertNotNull(dashboard);
        assertNotNull(dashboard.getGreeting());
        assertEquals(5L, dashboard.getTotalActive());
        assertEquals(1, dashboard.getNeedsAttention().size());
        assertNotNull(dashboard.getSuggestedAction());
        assertEquals("SUBMIT_APPLICATION", dashboard.getSuggestedAction().getActionType());
    }

    @Test
    @DisplayName("Cross-user access throws ResourceNotFoundException (enforcing user isolation)")
    void crossUserIsolation() {
        UUID oppId = UUID.randomUUID();
        when(opportunityRepository.findByIdAndUserId(oppId, userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                opportunityService.getOpportunity(userId, oppId));
    }
}
