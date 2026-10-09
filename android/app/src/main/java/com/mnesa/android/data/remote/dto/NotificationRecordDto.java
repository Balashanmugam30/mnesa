package com.mnesa.android.data.remote.dto;

public class NotificationRecordDto {
    private String id;
    private String userId;
    private String reminderId;
    private String opportunityId;
    private String title;
    private String body;
    private String channel;
    private String provider;
    private String deliveryStatus;
    private String deepLinkUri;
    private String openedAt;
    private String createdAt;

    public NotificationRecordDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getReminderId() { return reminderId; }
    public void setReminderId(String reminderId) { this.reminderId = reminderId; }

    public String getOpportunityId() { return opportunityId; }
    public void setOpportunityId(String opportunityId) { this.opportunityId = opportunityId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    public String getDeepLinkUri() { return deepLinkUri; }
    public void setDeepLinkUri(String deepLinkUri) { this.deepLinkUri = deepLinkUri; }

    public String getOpenedAt() { return openedAt; }
    public void setOpenedAt(String openedAt) { this.openedAt = openedAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
