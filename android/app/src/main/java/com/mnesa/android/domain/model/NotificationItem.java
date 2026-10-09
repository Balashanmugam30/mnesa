package com.mnesa.android.domain.model;

import java.util.Objects;

/**
 * Domain model representing a system or in-app notification delivered to the user.
 */
public class NotificationItem {

    private final String id;
    private final String userId;
    private final String reminderId;
    private final String opportunityId;
    private final String title;
    private final String body;
    private final String channel;
    private final String provider;
    private final String deliveryStatus;
    private final String deepLinkUri;
    private final Long openedAt;
    private final long createdAt;

    public NotificationItem(String id,
                            String userId,
                            String reminderId,
                            String opportunityId,
                            String title,
                            String body,
                            String channel,
                            String provider,
                            String deliveryStatus,
                            String deepLinkUri,
                            Long openedAt,
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
        this.openedAt = openedAt;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getReminderId() { return reminderId; }
    public String getOpportunityId() { return opportunityId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getChannel() { return channel; }
    public String getProvider() { return provider; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public String getDeepLinkUri() { return deepLinkUri; }
    public Long getOpenedAt() { return openedAt; }
    public long getCreatedAt() { return createdAt; }
    public boolean isOpened() { return openedAt != null && openedAt > 0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationItem that = (NotificationItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
