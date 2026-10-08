package com.mnesa.android.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room persistence entity for reminders associated with opportunities.
 */
@Entity(tableName = "reminders")
public class ReminderEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "opportunity_id")
    private String opportunityId;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "trigger_timestamp")
    private long triggerTimestamp;

    @NonNull
    @ColumnInfo(name = "reminder_type")
    private String reminderType;

    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public ReminderEntity(@NonNull String id,
                          @NonNull String opportunityId,
                          @NonNull String userId,
                          @NonNull String title,
                          long triggerTimestamp,
                          @NonNull String reminderType,
                          @NonNull String status,
                          long createdAt) {
        this.id = id;
        this.opportunityId = opportunityId;
        this.userId = userId;
        this.title = title;
        this.triggerTimestamp = triggerTimestamp;
        this.reminderType = reminderType;
        this.status = status;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(@NonNull String opportunityId) {
        this.opportunityId = opportunityId;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public long getTriggerTimestamp() {
        return triggerTimestamp;
    }

    public void setTriggerTimestamp(long triggerTimestamp) {
        this.triggerTimestamp = triggerTimestamp;
    }

    @NonNull
    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(@NonNull String reminderType) {
        this.reminderType = reminderType;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
