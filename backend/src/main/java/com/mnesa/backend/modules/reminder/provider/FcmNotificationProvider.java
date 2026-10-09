package com.mnesa.backend.modules.reminder.provider;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Production Firebase Cloud Messaging provider boundary.
 * Connects to live FCM when credentials are provided via configuration,
 * or safely reports credential status in development/CI environments.
 */
@Slf4j
@Component("fcmNotificationProvider")
public class FcmNotificationProvider implements NotificationProvider {

    public static final String PROVIDER_NAME = "FCM";

    @Value("${mnesa.firebase.credentials-path:}")
    private String credentialsPath;

    private boolean initialized = false;

    @PostConstruct
    public void init() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = null;

                if (credentialsPath != null && !credentialsPath.isBlank()) {
                    Path path = Paths.get(credentialsPath);
                    if (Files.exists(path)) {
                        serviceAccount = new FileInputStream(path.toFile());
                    }
                }

                String envCreds = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                if (serviceAccount == null && envCreds != null && !envCreds.isBlank()) {
                    Path path = Paths.get(envCreds);
                    if (Files.exists(path)) {
                        serviceAccount = new FileInputStream(path.toFile());
                    }
                }

                if (serviceAccount != null) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                    FirebaseApp.initializeApp(options);
                    initialized = true;
                    log.info("[FCM] FirebaseApp successfully initialized for push delivery.");
                } else {
                    log.info("[FCM] No Firebase service account credentials found. FCM will run in non-initialized mode (safe for dev/CI).");
                }
            } else {
                initialized = true;
                log.info("[FCM] Using existing initialized FirebaseApp.");
            }
        } catch (Exception e) {
            log.warn("[FCM] Could not initialize FirebaseApp: {}. Live FCM push disabled.", e.getMessage());
            initialized = false;
        }
    }

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public NotificationDeliveryResult sendPushNotification(String pushToken, String title, String body, Map<String, String> data) {
        if (pushToken == null || pushToken.isBlank()) {
            return NotificationDeliveryResult.failure("Missing or blank push token");
        }

        if (!initialized) {
            log.warn("[FCM] Cannot dispatch live push to token {}: FirebaseApp not initialized with credentials.",
                    pushToken.length() > 6 ? pushToken.substring(0, 6) + "..." : pushToken);
            return NotificationDeliveryResult.failure("FirebaseApp not initialized: missing service account credentials");
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .setToken(pushToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }

            String messageId = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("[FCM] Successfully sent push notification, messageId: {}", messageId);
            return NotificationDeliveryResult.success(messageId);
        } catch (Exception e) {
            log.error("[FCM] Failed to send push notification to token {}: {}", pushToken, e.getMessage());
            return NotificationDeliveryResult.failure(e.getMessage());
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
