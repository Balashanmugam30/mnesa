package com.mnesa.android.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room persistence entity for received notifications and delivery events.
 */
@Entity(tableName = "notifications")
public class NotificationEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "user_id")
    private String userId;

    @Nullable
    @ColumnInfo(name = "reminder_id")
    private String reminderId;

    @Nullable
    @ColumnInfo(name = "opportunity_id")
    private String opportunityId;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "body")
    private String body;

    @NonNull
    @ColumnInfo(name = "channel")
    private String channel;

    @NonNull
    @ColumnInfo(name = "provider")
    private String provider;

    @NonNull
    @ColumnInfo(name = "delivery_status")
    private String deliveryStatus;

    @Nullable
    @ColumnInfo(name = "deep_link_uri")
    private String deepLinkUri;

    @Nullable
    @ColumnInfo(name = "metadata_json")
    private String metadataJson;

    @Nullable
    @ColumnInfo(name = "opened_at")
    private Long openedAt;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public NotificationEntity(@NonNull String id,
                              @NonNull String userId,
                              @Nullable String reminderId,
                              @Nullable String opportunityId,
                              @NonNull String title,
                              @NonNull String body,
                              @NonNull String channel,
                              @NonNull String provider,
                              @NonNull String deliveryStatus,
                              @Nullable String deepLinkUri,
                              @Nullable String metadataJson,
                              @Nullable Long openedAt,
                              long createdAt) {
        this.id = id;
        this.userId = userId;
        this.reminderId = reminderId;
        this.opportunityId = opportunityId;
        this.title = title;
        this.body = body;
        this.channel = channel;
        this.provider = provider;
        this.deliveryStatus = deliveryStatus;
        this.deepLinkUri = deepLinkUri;
        this.metadataJson = metadataJson;
        this.openedAt = openedAt;
        this.createdAt = createdAt;
    }

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    @Nullable
    public String getReminderId() { return reminderId; }
    public void setReminderId(@Nullable String reminderId) { this.reminderId = reminderId; }

    @Nullable
    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(@Nullable String opportunityId) { this.opportunityId = opportunityId; }

    @NonNull
    public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    @NonNull
    public String getBody() { return body; }
    public void setBody(@NonNull String body) { this.body = body; }

    @NonNull
    public String getChannel() { return channel; }
    public void setChannel(@NonNull String channel) { this.channel = channel; }

    @NonNull
    public String getProvider() { return provider; }
    public void setProvider(@NonNull String provider) { this.provider = provider; }

    @NonNull
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(@NonNull String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    @Nullable
    public String getDeepLinkUri() { return deepLinkUri; }
    public void setDeepLinkUri(@Nullable String deepLinkUri) { this.deepLinkUri = deepLinkUri; }

    @Nullable
    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(@Nullable String metadataJson) { this.metadataJson = metadataJson; }

    @Nullable
    public Long getOpenedAt() { return openedAt; }
    public void setOpenedAt(@Nullable Long openedAt) { this.openedAt = openedAt; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
