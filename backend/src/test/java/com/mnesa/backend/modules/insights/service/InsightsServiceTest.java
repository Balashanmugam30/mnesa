package com.mnesa.backend.modules.insights.service;

import com.mnesa.backend.modules.insights.dto.InsightsDto;
import com.mnesa.backend.modules.opportunity.domain.OpportunityActivity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.opportunity.repository.OpportunityActivityRepository;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightsServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private OpportunityActivityRepository activityRepository;

    private InsightsService insightsService;
    private UUID userId;

    @BeforeEach
    void setUp() {
        insightsService = new InsightsService(opportunityRepository, activityRepository);
        userId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should aggregate real database metrics and return comprehensive InsightsDto")
    void testGetInsightsSuccess() {
        when(opportunityRepository.countByUserIdAndStatusNot(userId, OpportunityStatus.ARCHIVED))
                .thenReturn(15L);

        when(opportunityRepository.countByUserIdAndStatusIn(eq(userId), any(Collection.class)))
                .thenReturn(6L);

        when(opportunityRepository.countByUserIdAndStatusNotInAndDeadlineAtIsNotNullAndDeadlineAtGreaterThanEqual(
                eq(userId), any(Collection.class), any(Instant.class)))
                .thenReturn(4L);

        when(opportunityRepository.countByUserIdAndStatus(userId, OpportunityStatus.MISSED))
                .thenReturn(1L);

        List<Object[]> categoryData = List.of(
                new Object[]{OpportunityType.INTERNSHIP, 8L},
                new Object[]{OpportunityType.SCHOLARSHIP, 4L},
                new Object[]{OpportunityType.JOB, 3L}
        );

        when(opportunityRepository.countGroupedByCategory(userId, OpportunityStatus.ARCHIVED))
                .thenReturn(categoryData);

        List<Object[]> statusData = List.of(
                new Object[]{OpportunityStatus.SAVED, 5L},
                new Object[]{OpportunityStatus.APPLIED, 6L},
                new Object[]{OpportunityStatus.MISSED, 1L}
        );
        when(opportunityRepository.countGroupedByStatus(userId))
                .thenReturn(statusData);

        List<Object[]> priorityData = List.of(
                new Object[]{"HIGH", 5L},
                new Object[]{"NORMAL", 10L}
        );
        when(opportunityRepository.countGroupedByPriority(userId, OpportunityStatus.ARCHIVED))
                .thenReturn(priorityData);

        OpportunityActivity act1 = OpportunityActivity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .actionType("SAVED")
                .createdAt(Instant.now().minusSeconds(3600))
                .build();
        when(activityRepository.findAllByUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(eq(userId), any(Instant.class)))
                .thenReturn(List.of(act1));

        InsightsDto result = insightsService.getInsights(userId);

        assertNotNull(result);
        assertEquals(15, result.getTotalSaved());
        assertEquals(6, result.getApplicationsCompleted());
        assertEquals(4, result.getUpcomingDeadlines());
        assertEquals(1, result.getMissedOpportunities());

        assertEquals(8L, result.getCategoryDistribution().get("INTERNSHIP"));
        assertEquals(4L, result.getCategoryDistribution().get("SCHOLARSHIP"));
        assertEquals(5L, result.getStatusDistribution().get("SAVED"));

        assertEquals(5L, result.getPriorityDistribution().get("HIGH"));

        assertEquals(14, result.getActivityTrends().size()); // 14-day window
    }
}
