package com.mnesa.android.data.remote.dto;

import com.google.gson.annotations.SerializedName;

public class AiCandidateDto {

    @SerializedName("id")
    private String id;

    @SerializedName("candidate_index")
    private int candidateIndex;

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

    @SerializedName("confidence_score")
    private Float confidenceScore;

    @SerializedName("is_confirmed")
    private boolean isConfirmed;

    public AiCandidateDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getCandidateIndex() { return candidateIndex; }
    public void setCandidateIndex(int candidateIndex) { this.candidateIndex = candidateIndex; }

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

    public Float getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Float confidenceScore) { this.confidenceScore = confidenceScore; }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }
}
