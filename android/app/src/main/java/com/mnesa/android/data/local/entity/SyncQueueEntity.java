package com.mnesa.android.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room persistence entity for the local sync queue operations.
 * Survives process death and app restarts.
 */
@Entity(tableName = "sync_queue")
public class SyncQueueEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "operation_id")
    private String operationId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    @ColumnInfo(name = "entity_type")
    private String entityType;

    @NonNull
    @ColumnInfo(name = "entity_id")
    private String entityId;

    @NonNull
    @ColumnInfo(name = "operation_type")
    private String operationType;

    @ColumnInfo(name = "payload_json")
    private String payloadJson;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "attempt_count")
    private int attemptCount;

    @ColumnInfo(name = "last_attempt_at")
    private Long lastAttemptAt;

    @ColumnInfo(name = "next_retry_at")
    private Long nextRetryAt;

    @NonNull
    @ColumnInfo(name = "state")
    private String state;

    @ColumnInfo(name = "error_category")
    private String errorCategory;

    public SyncQueueEntity(@NonNull String operationId,
                           @NonNull String userId,
                           @NonNull String entityType,
                           @NonNull String entityId,
                           @NonNull String operationType,
                           String payloadJson,
                           long createdAt,
                           int attemptCount,
                           Long lastAttemptAt,
                           Long nextRetryAt,
                           @NonNull String state,
                           String errorCategory) {
        this.operationId = operationId;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.operationType = operationType;
        this.payloadJson = payloadJson;
        this.createdAt = createdAt;
        this.attemptCount = attemptCount;
        this.lastAttemptAt = lastAttemptAt;
        this.nextRetryAt = nextRetryAt;
        this.state = state;
        this.errorCategory = errorCategory;
    }

    @NonNull
    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(@NonNull String operationId) {
        this.operationId = operationId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(@NonNull String entityType) {
        this.entityType = entityType;
    }

    @NonNull
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(@NonNull String entityId) {
        this.entityId = entityId;
    }

    @NonNull
    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(@NonNull String operationType) {
        this.operationType = operationType;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Long getLastAttemptAt() {
        return lastAttemptAt;
    }

    public void setLastAttemptAt(Long lastAttemptAt) {
        this.lastAttemptAt = lastAttemptAt;
    }

    public Long getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(Long nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    @NonNull
    public String getState() {
        return state;
    }

    public void setState(@NonNull String state) {
        this.state = state;
    }

    public String getErrorCategory() {
        return errorCategory;
    }

    public void setErrorCategory(String errorCategory) {
        this.errorCategory = errorCategory;
    }
}
