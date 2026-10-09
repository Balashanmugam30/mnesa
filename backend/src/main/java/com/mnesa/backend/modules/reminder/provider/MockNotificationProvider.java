package com.mnesa.backend.modules.reminder.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Deterministic notification delivery provider for local development, CI pipelines, and unit tests.
 * Safely captures delivery payloads without requiring external Firebase cloud credentials.
 */
@Slf4j
@Component("mockNotificationProvider")
public class MockNotificationProvider implements NotificationProvider {

    public static final String PROVIDER_NAME = "MOCK_DEVELOPMENT";

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public NotificationDeliveryResult sendPushNotification(String pushToken, String title, String body, Map<String, String> data) {
        log.info("[MockNotificationProvider] Dispatched simulated push to token prefix: {}*** | title: '{}' | body: '{}' | data: {}",
                pushToken != null && pushToken.length() > 6 ? pushToken.substring(0, 6) : "null",
                title,
                body,
                data);

        String messageId = "mock-msg-" + UUID.randomUUID();
        return NotificationDeliveryResult.success(messageId);
    }
}
