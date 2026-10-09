package com.mnesa.android.core.notification;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.local.entity.NotificationEntity;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.Map;
import java.util.UUID;

/**
 * Native FCM push notification listener.
 * Handles incoming push messages, writes delivery records to Room, and alerts the user.
 */
public class MnesaFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MnesaFCM";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i(TAG, "New FCM push token generated");
        // Store locally in secure token manager for subsequent device registration requests
        SecureTokenManager tokenManager = new SecureTokenManager(getApplicationContext());
        tokenManager.savePushToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        String title = "MNESA Reminder";
        String body = "You have an opportunity update.";

        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) {
                title = message.getNotification().getTitle();
            }
            if (message.getNotification().getBody() != null) {
                body = message.getNotification().getBody();
            }
        }

        Map<String, String> data = message.getData();
        if (data != null && !data.isEmpty()) {
            if (data.containsKey("title")) {
                title = data.get("title");
            }
            if (data.containsKey("body")) {
                body = data.get("body");
            }
        }

        String reminderId = (data != null) ? data.get("reminderId") : null;
        String opportunityId = (data != null) ? data.get("opportunityId") : null;
        String deepLink = (data != null) ? data.get("deepLink") : null;

        Log.i(TAG, "Received push notification: title=" + title + " reminderId=" + reminderId);

        // Store notification record in Room offline cache
        SecureTokenManager tokenManager = new SecureTokenManager(getApplicationContext());
        String userId = tokenManager.getUserId();
        if (userId != null && !userId.trim().isEmpty()) {
            NotificationEntity entity = new NotificationEntity(
                    UUID.randomUUID().toString(),
                    userId,
                    reminderId,
                    opportunityId,
                    title,
                    body,
                    "PUSH",
                    "FCM",
                    "DELIVERED",
                    deepLink,
                    null,
                    null,
                    System.currentTimeMillis()
            );

            AppDatabase.getInstance(getApplicationContext())
                    .notificationDao()
                    .insertNotification(entity)
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {}, throwable -> Log.w(TAG, "Failed to persist notification: " + throwable.getMessage()));
        }

        // Show local system notification
        NotificationHelper.showReminderNotification(this, reminderId, opportunityId, title, body, deepLink);
    }
}
