package com.mnesa.android.presentation.insights;

import com.mnesa.android.data.remote.dto.ActivityTrendPointDto;
import com.mnesa.android.data.remote.dto.InsightsResponseDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class InsightsUiState {

    private final boolean loading;
    private final boolean error;
    private final String errorMessage;
    private final long totalSaved;
    private final long applicationsCompleted;
    private final long upcomingDeadlines;
    private final long missedOpportunities;
    private final Map<String, Long> categoryDistribution;
    private final Map<String, Long> statusDistribution;
    private final Map<String, Long> priorityDistribution;
    private final List<ActivityTrendPointDto> activityTrends;

    public InsightsUiState(boolean loading,
                           boolean error,
                           String errorMessage,
                           long totalSaved,
                           long applicationsCompleted,
                           long upcomingDeadlines,
                           long missedOpportunities,
                           Map<String, Long> categoryDistribution,
                           Map<String, Long> statusDistribution,
                           Map<String, Long> priorityDistribution,
                           List<ActivityTrendPointDto> activityTrends) {
        this.loading = loading;
        this.error = error;
        this.errorMessage = errorMessage;
        this.totalSaved = totalSaved;
        this.applicationsCompleted = applicationsCompleted;
        this.upcomingDeadlines = upcomingDeadlines;
        this.missedOpportunities = missedOpportunities;
        this.categoryDistribution = categoryDistribution != null ? categoryDistribution : Collections.emptyMap();
        this.statusDistribution = statusDistribution != null ? statusDistribution : Collections.emptyMap();
        this.priorityDistribution = priorityDistribution != null ? priorityDistribution : Collections.emptyMap();
        this.activityTrends = activityTrends != null ? activityTrends : Collections.emptyList();
    }

    public static InsightsUiState loading() {
        return new InsightsUiState(true, false, null, 0, 0, 0, 0, null, null, null, null);
    }

    public static InsightsUiState fromDto(InsightsResponseDto dto) {
        return new InsightsUiState(
                false,
                false,
                null,
                dto.getTotalSaved(),
                dto.getApplicationsCompleted(),
                dto.getUpcomingDeadlines(),
                dto.getMissedOpportunities(),
                dto.getCategoryDistribution(),
                dto.getStatusDistribution(),
                dto.getPriorityDistribution(),
                dto.getActivityTrends()
        );
    }

    public static InsightsUiState error(String errorMessage) {
        return new InsightsUiState(false, true, errorMessage, 0, 0, 0, 0, null, null, null, null);
    }

    public boolean isLoading() { return loading; }
    public boolean isError() { return error; }
    public String getErrorMessage() { return errorMessage; }
    public long getTotalSaved() { return totalSaved; }
    public long getApplicationsCompleted() { return applicationsCompleted; }
    public long getUpcomingDeadlines() { return upcomingDeadlines; }
    public long getMissedOpportunities() { return missedOpportunities; }
    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public Map<String, Long> getPriorityDistribution() { return priorityDistribution; }
    public List<ActivityTrendPointDto> getActivityTrends() { return activityTrends; }
}
