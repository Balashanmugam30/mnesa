package com.mnesa.android.core.notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.mnesa.android.R;
import com.mnesa.android.presentation.main.MainActivity;
import com.mnesa.android.presentation.opportunities.OpportunityDetailActivity;

/**
 * Utility helper managing Android system notification channels and local presentation.
 */
public class NotificationHelper {

    public static final String CHANNEL_REMINDERS_ID = "mnesa_reminders_channel";
    public static final String CHANNEL_UPDATES_ID = "mnesa_updates_channel";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                // High importance channel for deadline and preparation reminders
                NotificationChannel reminderChannel = new NotificationChannel(
                        CHANNEL_REMINDERS_ID,
                        "Opportunity Reminders",
                        NotificationManager.IMPORTANCE_HIGH
                );
                reminderChannel.setDescription("High priority alerts for opportunity deadlines and preparation steps");
                reminderChannel.enableLights(true);
                reminderChannel.enableVibration(true);
                reminderChannel.setShowBadge(true);
                manager.createNotificationChannel(reminderChannel);

                // Default importance channel for updates and general messages
                NotificationChannel updatesChannel = new NotificationChannel(
                        CHANNEL_UPDATES_ID,
                        "MNESA System Updates",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                updatesChannel.setDescription("Updates and sync notifications");
                updatesChannel.setShowBadge(false);
                manager.createNotificationChannel(updatesChannel);
            }
        }
    }

    public static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    public static void showReminderNotification(Context context,
                                                String reminderId,
                                                String opportunityId,
                                                String title,
                                                String body,
                                                String deepLinkUri) {
        if (!hasNotificationPermission(context)) {
            return;
        }

        Intent intent;
        if (opportunityId != null && !opportunityId.trim().isEmpty()) {
            intent = new Intent(context, OpportunityDetailActivity.class);
            intent.putExtra(OpportunityDetailActivity.EXTRA_OPPORTUNITY_ID, opportunityId);
        } else {
            intent = new Intent(context, MainActivity.class);
        }

        if (deepLinkUri != null && !deepLinkUri.trim().isEmpty()) {
            intent.setData(Uri.parse(deepLinkUri));
            intent.putExtra("deep_link_uri", deepLinkUri);
        }
        if (reminderId != null) {
            intent.putExtra("reminder_id", reminderId);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int requestCode = reminderId != null ? reminderId.hashCode() : (int) System.currentTimeMillis();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_REMINDERS_ID)
                .setSmallIcon(R.drawable.ic_nav_reminders)
                .setContentTitle(title != null ? title : "MNESA Reminder")
                .setContentText(body != null ? body : "You have an upcoming opportunity deadline.")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Add action button to open
        builder.addAction(R.drawable.ic_open_in_new, "View Details", pendingIntent);

        int notificationId = reminderId != null ? Math.abs(reminderId.hashCode()) : (int) (System.currentTimeMillis() % 100000);
        NotificationManagerCompat.from(context).notify(notificationId, builder.build());
    }
}
