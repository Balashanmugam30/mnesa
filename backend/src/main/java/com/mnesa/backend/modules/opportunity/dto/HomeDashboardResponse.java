package com.mnesa.backend.modules.opportunity.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HomeDashboardResponse {

    private String greeting;
    private Map<String, Long> statusCounts;
    private long totalActive;
    private List<OpportunitySummaryResponse> needsAttention;
    private List<OpportunitySummaryResponse> upcoming;
    private List<OpportunitySummaryResponse> recentlySaved;
    private SuggestedActionResponse suggestedAction;
}
