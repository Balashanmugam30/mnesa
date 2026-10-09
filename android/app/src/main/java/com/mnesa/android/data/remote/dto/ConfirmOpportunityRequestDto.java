package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class ConfirmOpportunityRequestDto {

    @SerializedName("title")
    private String title;

    @SerializedName("organization")
    private String organization;

    @SerializedName("category")
    private String category;

    @SerializedName("deadline_at")
    private String deadlineAt;

    @SerializedName("candidate_id")
    private String candidateId;

    @SerializedName("notes")
    private String notes;

    public ConfirmOpportunityRequestDto() {}

    public ConfirmOpportunityRequestDto(String title, String organization, String category, String deadlineAt, String notes) {
        this(title, organization, category, deadlineAt, null, notes);
    }

    public ConfirmOpportunityRequestDto(String title, String organization, String category, String deadlineAt, String candidateId, String notes) {
        this.title = title;
        this.organization = organization;
        this.category = category;
        this.deadlineAt = deadlineAt;
        this.candidateId = candidateId;
        this.notes = notes;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDeadlineAt() { return deadlineAt; }
    public void setDeadlineAt(String deadlineAt) { this.deadlineAt = deadlineAt; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
