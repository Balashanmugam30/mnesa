package com.mnesa.backend.modules.reminder.provider;

import java.util.Map;

/**
 * Pluggable boundary interface for notification delivery services.
 */
public interface NotificationProvider {

    String getProviderName();

    NotificationDeliveryResult sendPushNotification(String pushToken, String title, String body, Map<String, String> data);
}
