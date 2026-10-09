package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsightsResponseDto {

    @SerializedName("totalSaved")
    private long totalSaved;

    @SerializedName("applicationsCompleted")
    private long applicationsCompleted;

    @SerializedName("upcomingDeadlines")
    private long upcomingDeadlines;

    @SerializedName("missedOpportunities")
    private long missedOpportunities;

    @SerializedName("categoryDistribution")
    private Map<String, Long> categoryDistribution = new HashMap<>();

    @SerializedName("statusDistribution")
    private Map<String, Long> statusDistribution = new HashMap<>();

    @SerializedName("priorityDistribution")
    private Map<String, Long> priorityDistribution = new HashMap<>();

    @SerializedName("activityTrends")
    private List<ActivityTrendPointDto> activityTrends = new ArrayList<>();

    public InsightsResponseDto() {}

    public long getTotalSaved() { return totalSaved; }
    public void setTotalSaved(long totalSaved) { this.totalSaved = totalSaved; }

    public long getApplicationsCompleted() { return applicationsCompleted; }
    public void setApplicationsCompleted(long applicationsCompleted) { this.applicationsCompleted = applicationsCompleted; }

    public long getUpcomingDeadlines() { return upcomingDeadlines; }
    public void setUpcomingDeadlines(long upcomingDeadlines) { this.upcomingDeadlines = upcomingDeadlines; }

    public long getMissedOpportunities() { return missedOpportunities; }
    public void setMissedOpportunities(long missedOpportunities) { this.missedOpportunities = missedOpportunities; }

    public Map<String, Long> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(Map<String, Long> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public Map<String, Long> getStatusDistribution() { return statusDistribution; }
    public void setStatusDistribution(Map<String, Long> statusDistribution) { this.statusDistribution = statusDistribution; }

    public Map<String, Long> getPriorityDistribution() { return priorityDistribution; }
    public void setPriorityDistribution(Map<String, Long> priorityDistribution) { this.priorityDistribution = priorityDistribution; }

    public List<ActivityTrendPointDto> getActivityTrends() { return activityTrends; }
    public void setActivityTrends(List<ActivityTrendPointDto> activityTrends) { this.activityTrends = activityTrends; }
}
