package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class HomeDashboardDto {

    @SerializedName("totalCount")
    private long totalCount;

    @SerializedName("statusCounts")
    private Map<String, Long> statusCounts;

    @SerializedName("needsAttention")
    private List<OpportunitySummaryDto> needsAttention;

    @SerializedName("upcomingDeadlines")
    private List<OpportunitySummaryDto> upcomingDeadlines;

    @SerializedName("recentlySaved")
    private List<OpportunitySummaryDto> recentlySaved;

    @SerializedName("suggestedAction")
    private SuggestedActionDto suggestedAction;

    public HomeDashboardDto() {}

    public long getTotalCount() { return totalCount; }
    public void setTotalCount(long totalCount) { this.totalCount = totalCount; }

    public Map<String, Long> getStatusCounts() { return statusCounts; }
    public void setStatusCounts(Map<String, Long> statusCounts) { this.statusCounts = statusCounts; }

    public List<OpportunitySummaryDto> getNeedsAttention() { return needsAttention; }
    public void setNeedsAttention(List<OpportunitySummaryDto> needsAttention) { this.needsAttention = needsAttention; }

    public List<OpportunitySummaryDto> getUpcomingDeadlines() { return upcomingDeadlines; }
    public void setUpcomingDeadlines(List<OpportunitySummaryDto> upcomingDeadlines) { this.upcomingDeadlines = upcomingDeadlines; }

    public List<OpportunitySummaryDto> getRecentlySaved() { return recentlySaved; }
    public void setRecentlySaved(List<OpportunitySummaryDto> recentlySaved) { this.recentlySaved = recentlySaved; }

    public SuggestedActionDto getSuggestedAction() { return suggestedAction; }
    public void setSuggestedAction(SuggestedActionDto suggestedAction) { this.suggestedAction = suggestedAction; }
}
