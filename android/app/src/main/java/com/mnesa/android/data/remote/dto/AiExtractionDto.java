package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class AiExtractionDto {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("organization")
    private String organization;

    @SerializedName("category")
    private String category;

    @SerializedName("summary")
    private String summary;

    @SerializedName("deadline_at")
    private String deadlineAt;

    @SerializedName("deadline_raw")
    private String deadlineRaw;

    @SerializedName("deadline_ambiguous")
    private boolean deadlineAmbiguous;

    @SerializedName("registration_url")
    private String registrationUrl;

    @SerializedName("location")
    private String location;

    @SerializedName("work_mode")
    private String workMode;

    @SerializedName("priority")
    private String priority;

    @SerializedName("priority_reason")
    private String priorityReason;

    @SerializedName("overall_confidence")
    private Float overallConfidence;

    @SerializedName("validation_status")
    private String validationStatus;

    @SerializedName("evidence_snippets")
    private List<String> evidenceSnippets = new ArrayList<>();

    @SerializedName("warning_messages")
    private List<String> warningMessages = new ArrayList<>();

    public AiExtractionDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDeadlineAt() { return deadlineAt; }
    public void setDeadlineAt(String deadlineAt) { this.deadlineAt = deadlineAt; }

    public String getDeadlineRaw() { return deadlineRaw; }
    public void setDeadlineRaw(String deadlineRaw) { this.deadlineRaw = deadlineRaw; }

    public boolean isDeadlineAmbiguous() { return deadlineAmbiguous; }
    public void setDeadlineAmbiguous(boolean deadlineAmbiguous) { this.deadlineAmbiguous = deadlineAmbiguous; }

    public String getRegistrationUrl() { return registrationUrl; }
    public void setRegistrationUrl(String registrationUrl) { this.registrationUrl = registrationUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getPriorityReason() { return priorityReason; }
    public void setPriorityReason(String priorityReason) { this.priorityReason = priorityReason; }

    public Float getOverallConfidence() { return overallConfidence; }
    public void setOverallConfidence(Float overallConfidence) { this.overallConfidence = overallConfidence; }

    public String getValidationStatus() { return validationStatus; }
    public void setValidationStatus(String validationStatus) { this.validationStatus = validationStatus; }

    public List<String> getEvidenceSnippets() { return evidenceSnippets; }
    public void setEvidenceSnippets(List<String> evidenceSnippets) { this.evidenceSnippets = evidenceSnippets; }

    public List<String> getWarningMessages() { return warningMessages; }
    public void setWarningMessages(List<String> warningMessages) { this.warningMessages = warningMessages; }
}
