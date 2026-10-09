package com.mnesa.android.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room persistence entity for offline-first capture records.
 */
@Entity(
        tableName = "captures",
        indices = {
                @Index(value = {"user_id"}),
                @Index(value = {"user_id", "idempotency_key"}, unique = true)
        }
)
public class CaptureEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    @ColumnInfo(name = "source_type")
    private String sourceType;

    @ColumnInfo(name = "original_text")
    private String originalText;

    @ColumnInfo(name = "original_url")
    private String originalUrl;

    @ColumnInfo(name = "canonical_url")
    private String canonicalUrl;

    @ColumnInfo(name = "source_domain")
    private String sourceDomain;

    @NonNull
    @ColumnInfo(name = "idempotency_key")
    private String idempotencyKey;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "backend_job_id")
    private String backendJobId;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public CaptureEntity(@NonNull String id,
                         @NonNull String userId,
                         @NonNull String sourceType,
                         String originalText,
                         String originalUrl,
                         String canonicalUrl,
                         String sourceDomain,
                         @NonNull String idempotencyKey,
                         @NonNull String status,
                         String backendJobId,
                         long createdAt,
                         long updatedAt) {
        this.id = id;
        this.userId = userId;
        this.sourceType = sourceType;
        this.originalText = originalText;
        this.originalUrl = originalUrl;
        this.canonicalUrl = canonicalUrl;
        this.sourceDomain = sourceDomain;
        this.idempotencyKey = idempotencyKey;
        this.status = status;
        this.backendJobId = backendJobId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(@NonNull String sourceType) {
        this.sourceType = sourceType;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    public String getSourceDomain() {
        return sourceDomain;
    }

    public void setSourceDomain(String sourceDomain) {
        this.sourceDomain = sourceDomain;
    }

    @NonNull
    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(@NonNull String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public String getBackendJobId() {
        return backendJobId;
    }

    public void setBackendJobId(String backendJobId) {
        this.backendJobId = backendJobId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
