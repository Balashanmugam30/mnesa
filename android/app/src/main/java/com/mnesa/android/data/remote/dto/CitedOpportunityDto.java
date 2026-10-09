package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class CitedOpportunityDto {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("organization")
    private String organization;

    @SerializedName("category")
    private String category;

    @SerializedName("deadlineAt")
    private String deadlineAt;

    @SerializedName("status")
    private String status;

    @SerializedName("priority")
    private String priority;

    public CitedOpportunityDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDeadlineAt() { return deadlineAt; }
    public void setDeadlineAt(String deadlineAt) { this.deadlineAt = deadlineAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}
