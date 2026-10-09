package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreateOpportunityRequestDto {

    @SerializedName("title")
    private String title;

    @SerializedName("organization")
    private String organization;

    @SerializedName("category")
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("sourceUrl")
    private String sourceUrl;

    @SerializedName("registrationUrl")
    private String registrationUrl;

    @SerializedName("deadlineTimestamp")
    private Long deadlineTimestamp;

    @SerializedName("deadlineTimezone")
    private String deadlineTimezone;

    @SerializedName("eligibility")
    private String eligibility;

    @SerializedName("location")
    private String location;

    @SerializedName("workMode")
    private String workMode;

    @SerializedName("estimatedEffort")
    private String estimatedEffort;

    @SerializedName("priority")
    private String priority;

    @SerializedName("notes")
    private String notes;

    @SerializedName("tagNames")
    private List<String> tagNames;

    public CreateOpportunityRequestDto() {}

    public CreateOpportunityRequestDto(String title, String organization, String category,
                                      String description, String sourceUrl, String registrationUrl,
                                      Long deadlineTimestamp, String deadlineTimezone,
                                      String eligibility, String location, String workMode,
                                      String estimatedEffort, String priority, String notes,
                                      List<String> tagNames) {
        this.title = title;
        this.organization = organization;
        this.category = category;
        this.description = description;
        this.sourceUrl = sourceUrl;
        this.registrationUrl = registrationUrl;
        this.deadlineTimestamp = deadlineTimestamp;
        this.deadlineTimezone = deadlineTimezone;
        this.eligibility = eligibility;
        this.location = location;
        this.workMode = workMode;
        this.estimatedEffort = estimatedEffort;
        this.priority = priority;
        this.notes = notes;
        this.tagNames = tagNames;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public String getRegistrationUrl() { return registrationUrl; }
    public void setRegistrationUrl(String registrationUrl) { this.registrationUrl = registrationUrl; }

    public Long getDeadlineTimestamp() { return deadlineTimestamp; }
    public void setDeadlineTimestamp(Long deadlineTimestamp) { this.deadlineTimestamp = deadlineTimestamp; }

    public String getDeadlineTimezone() { return deadlineTimezone; }
    public void setDeadlineTimezone(String deadlineTimezone) { this.deadlineTimezone = deadlineTimezone; }

    public String getEligibility() { return eligibility; }
    public void setEligibility(String eligibility) { this.eligibility = eligibility; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getEstimatedEffort() { return estimatedEffort; }
    public void setEstimatedEffort(String estimatedEffort) { this.estimatedEffort = estimatedEffort; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<String> getTagNames() { return tagNames; }
    public void setTagNames(List<String> tagNames) { this.tagNames = tagNames; }
}
