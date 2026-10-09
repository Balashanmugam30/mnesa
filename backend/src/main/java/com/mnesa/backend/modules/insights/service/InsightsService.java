package com.mnesa.backend.modules.insights.service;

import com.mnesa.backend.modules.insights.dto.ActivityTrendPointDto;
import com.mnesa.backend.modules.insights.dto.InsightsDto;
import com.mnesa.backend.modules.opportunity.domain.OpportunityActivity;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import com.mnesa.backend.modules.opportunity.domain.OpportunityType;
import com.mnesa.backend.modules.opportunity.repository.OpportunityActivityRepository;
import com.mnesa.backend.modules.opportunity.repository.OpportunityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InsightsService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunityActivityRepository activityRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);

    public InsightsService(OpportunityRepository opportunityRepository,
                           OpportunityActivityRepository activityRepository) {
        this.opportunityRepository = opportunityRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional(readOnly = true)
    public InsightsDto getInsights(UUID userId) {
        Instant now = Instant.now();

        // 1. Authoritative Aggregated Metrics (Non-hallucinated counts)
        long totalSaved = opportunityRepository.countByUserIdAndStatusNot(userId, OpportunityStatus.ARCHIVED);

        long applicationsCompleted = opportunityRepository.countByUserIdAndStatusIn(
                userId,
                List.of(OpportunityStatus.APPLIED, OpportunityStatus.SELECTED)
        );

        List<OpportunityStatus> excludedFromUpcoming = List.of(
                OpportunityStatus.APPLIED,
                OpportunityStatus.SELECTED,
                OpportunityStatus.ARCHIVED,
                OpportunityStatus.REJECTED,
                OpportunityStatus.MISSED
        );
        long upcomingDeadlines = opportunityRepository.countByUserIdAndStatusNotInAndDeadlineAtIsNotNullAndDeadlineAtGreaterThanEqual(
                userId,
                excludedFromUpcoming,
                now
        );

        long missedOpportunities = opportunityRepository.countByUserIdAndStatus(userId, OpportunityStatus.MISSED);

        // 2. Category Distribution
        Map<String, Long> categoryDistribution = new HashMap<>();
        List<Object[]> categoryCounts = opportunityRepository.countGroupedByCategory(userId, OpportunityStatus.ARCHIVED);
        if (categoryCounts != null) {
            for (Object[] row : categoryCounts) {
                if (row.length >= 2 && row[0] != null) {
                    categoryDistribution.put(row[0].toString(), ((Number) row[1]).longValue());
                }
            }
        }

        // 3. Status Distribution
        Map<String, Long> statusDistribution = new HashMap<>();
        List<Object[]> statusCounts = opportunityRepository.countGroupedByStatus(userId);
        if (statusCounts != null) {
            for (Object[] row : statusCounts) {
                if (row.length >= 2 && row[0] != null) {
                    statusDistribution.put(row[0].toString(), ((Number) row[1]).longValue());
                }
            }
        }

        // 4. Priority Distribution
        Map<String, Long> priorityDistribution = new HashMap<>();
        List<Object[]> priorityCounts = opportunityRepository.countGroupedByPriority(userId, OpportunityStatus.ARCHIVED);
        if (priorityCounts != null) {
            for (Object[] row : priorityCounts) {
                if (row.length >= 2 && row[0] != null) {
                    priorityDistribution.put(row[0].toString(), ((Number) row[1]).longValue());
                }
            }
        }

        // 5. Activity Trends (Past 14 Days)
        Instant fourteenDaysAgo = now.minus(14, ChronoUnit.DAYS);
        List<OpportunityActivity> recentActivities = activityRepository
                .findAllByUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc(userId, fourteenDaysAgo);

        Map<String, Long> groupedTrends = recentActivities.stream()
                .filter(a -> a.getCreatedAt() != null)
                .collect(Collectors.groupingBy(
                        a -> DATE_FORMATTER.format(a.getCreatedAt()),
                        Collectors.counting()
                ));

        List<ActivityTrendPointDto> trendPoints = new ArrayList<>();
        for (int i = 13; i >= 0; i--) {
            String dayStr = DATE_FORMATTER.format(now.minus(i, ChronoUnit.DAYS));
            trendPoints.add(ActivityTrendPointDto.builder()
                    .date(dayStr)
                    .count(groupedTrends.getOrDefault(dayStr, 0L))
                    .build());
        }

        return InsightsDto.builder()
                .totalSaved(totalSaved)
                .applicationsCompleted(applicationsCompleted)
                .upcomingDeadlines(upcomingDeadlines)
                .missedOpportunities(missedOpportunities)
                .categoryDistribution(categoryDistribution)
                .statusDistribution(statusDistribution)
                .priorityDistribution(priorityDistribution)
                .activityTrends(trendPoints)
                .build();
    }
}
