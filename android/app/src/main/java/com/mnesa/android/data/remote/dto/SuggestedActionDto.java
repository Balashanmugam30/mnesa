package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class SuggestedActionDto {

    @SerializedName("opportunityId")
    private String opportunityId;

    @SerializedName("title")
    private String title;

    @SerializedName("actionText")
    private String actionText;

    @SerializedName("reason")
    private String reason;

    @SerializedName("urgency")
    private String urgency;

    @SerializedName("targetUrl")
    private String targetUrl;

    public SuggestedActionDto() {}

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getActionText() { return actionText; }
    public void setActionText(String actionText) { this.actionText = actionText; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
}
