package com.mnesa.backend.modules.insights.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsightsDto {

    private long totalSaved;
    private long applicationsCompleted;
    private long upcomingDeadlines;
    private long missedOpportunities;

    @Builder.Default
    private Map<String, Long> categoryDistribution = new HashMap<>();

    @Builder.Default
    private Map<String, Long> statusDistribution = new HashMap<>();

    @Builder.Default
    private Map<String, Long> priorityDistribution = new HashMap<>();

    @Builder.Default
    private List<ActivityTrendPointDto> activityTrends = new ArrayList<>();
}
