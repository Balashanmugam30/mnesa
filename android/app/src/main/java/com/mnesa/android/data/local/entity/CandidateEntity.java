package com.mnesa.android.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "ai_candidates",
        indices = {
                @Index(value = {"job_id"})
        }
)
public class CandidateEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "job_id")
    private String jobId;

    @ColumnInfo(name = "candidate_index")
    private int candidateIndex;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "organization")
    private String organization;

    @NonNull
    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "summary")
    private String summary;

    @ColumnInfo(name = "deadline_at")
    private Long deadlineAt;

    @ColumnInfo(name = "confidence_score")
    private float confidenceScore;

    @ColumnInfo(name = "is_confirmed")
    private boolean isConfirmed;

    public CandidateEntity(@NonNull String id, @NonNull String jobId, int candidateIndex,
                           @NonNull String title, String organization, @NonNull String category,
                           String summary, Long deadlineAt, float confidenceScore, boolean isConfirmed) {
        this.id = id;
        this.jobId = jobId;
        this.candidateIndex = candidateIndex;
        this.title = title;
        this.organization = organization;
        this.category = category;
        this.summary = summary;
        this.deadlineAt = deadlineAt;
        this.confidenceScore = confidenceScore;
        this.isConfirmed = isConfirmed;
    }

    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull public String getJobId() { return jobId; }
    public void setJobId(@NonNull String jobId) { this.jobId = jobId; }

    public int getCandidateIndex() { return candidateIndex; }
    public void setCandidateIndex(int candidateIndex) { this.candidateIndex = candidateIndex; }

    @NonNull public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public String getOrganization() { return organization; }
    public void setOrganization(String organization) { this.organization = organization; }

    @NonNull public String getCategory() { return category; }
    public void setCategory(@NonNull String category) { this.category = category; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Long getDeadlineAt() { return deadlineAt; }
    public void setDeadlineAt(Long deadlineAt) { this.deadlineAt = deadlineAt; }

    public float getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(float confidenceScore) { this.confidenceScore = confidenceScore; }

    public boolean isConfirmed() { return isConfirmed; }
    public void setConfirmed(boolean confirmed) { isConfirmed = confirmed; }
}
