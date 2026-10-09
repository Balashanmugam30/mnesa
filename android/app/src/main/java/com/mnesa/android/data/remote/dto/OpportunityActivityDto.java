package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class OpportunityActivityDto {

    @SerializedName("id")
    private String id;

    @SerializedName("activityType")
    private String activityType;

    @SerializedName("fromStatus")
    private String fromStatus;

    @SerializedName("toStatus")
    private String toStatus;

    @SerializedName("description")
    private String description;

    @SerializedName("comment")
    private String comment;

    @SerializedName("createdAt")
    private String createdAt;

    public OpportunityActivityDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getActivityType() { return activityType; }
    public void setActivityType(String activityType) { this.activityType = activityType; }

    public String getFromStatus() { return fromStatus; }
    public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }

    public String getToStatus() { return toStatus; }
    public void setToStatus(String toStatus) { this.toStatus = toStatus; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
