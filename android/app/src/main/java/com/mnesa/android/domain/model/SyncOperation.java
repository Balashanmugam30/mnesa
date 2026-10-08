package com.mnesa.android.domain.model;

import java.util.Objects;

/**
 * Pure Java domain model representing an idempotent synchronization operation.
 */
public class SyncOperation {

    private final String operationId;
    private final String userId;
    private final String entityType;
    private final String entityId;
    private final String operationType;
    private final String payloadJson;
    private final long createdAt;
    private final int attemptCount;
    private final String state;

    public SyncOperation(String operationId,
                         String userId,
                         String entityType,
                         String entityId,
                         String operationType,
                         String payloadJson,
                         long createdAt,
                         int attemptCount,
                         String state) {
        this.operationId = operationId;
        this.userId = userId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.operationType = operationType;
        this.payloadJson = payloadJson;
        this.createdAt = createdAt;
        this.attemptCount = attemptCount;
        this.state = state;
    }

    public String getOperationId() {
        return operationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getEntityType() {
        return entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncOperation that = (SyncOperation) o;
        return Objects.equals(operationId, that.operationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationId);
    }
}
