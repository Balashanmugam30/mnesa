package com.mnesa.backend.modules.reminder.service;

import com.mnesa.backend.modules.device.domain.DeviceInstallation;
import com.mnesa.backend.modules.device.repository.DeviceInstallationRepository;
import com.mnesa.backend.modules.opportunity.domain.Opportunity;
import com.mnesa.backend.modules.reminder.domain.*;
import com.mnesa.backend.modules.reminder.provider.FcmNotificationProvider;
import com.mnesa.backend.modules.reminder.provider.MockNotificationProvider;
import com.mnesa.backend.modules.reminder.provider.NotificationDeliveryResult;
import com.mnesa.backend.modules.reminder.provider.NotificationProvider;
import com.mnesa.backend.modules.reminder.repository.NotificationRecordRepository;
import com.mnesa.backend.modules.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Orchestrates delivery attempts across available notification providers (FCM or fallback mock)
 * and records an auditable trail in the notification_records database.
 */
@Slf4j
@Service
public class NotificationDispatcherService {

    private final DeviceInstallationRepository deviceInstallationRepository;
    private final NotificationRecordRepository notificationRecordRepository;
    private final FcmNotificationProvider fcmNotificationProvider;
    private final MockNotificationProvider mockNotificationProvider;

    public NotificationDispatcherService(DeviceInstallationRepository deviceInstallationRepository,
                                         NotificationRecordRepository notificationRecordRepository,
                                         FcmNotificationProvider fcmNotificationProvider,
                                         MockNotificationProvider mockNotificationProvider) {
        this.deviceInstallationRepository = deviceInstallationRepository;
        this.notificationRecordRepository = notificationRecordRepository;
        this.fcmNotificationProvider = fcmNotificationProvider;
        this.mockNotificationProvider = mockNotificationProvider;
    }

    @Transactional
    public NotificationRecord dispatchReminderNotification(Reminder reminder) {
        User user = reminder.getUser();
        Opportunity opportunity = reminder.getOpportunity();

        String title = reminder.getTitle();
        String body = reminder.getNotes() != null && !reminder.getNotes().isBlank()
                ? reminder.getNotes()
                : (opportunity != null ? "Reminder for " + opportunity.getTitle() : "Scheduled reminder");

        String deepLink = opportunity != null
                ? "mnesa://opportunity/" + opportunity.getId()
                : "mnesa://reminders";

        Map<String, String> data = new HashMap<>();
        data.put("reminderId", reminder.getId().toString());
        if (opportunity != null) {
            data.put("opportunityId", opportunity.getId().toString());
        }
        data.put("deepLink", deepLink);

        NotificationProvider provider = fcmNotificationProvider.isInitialized()
                ? fcmNotificationProvider
                : mockNotificationProvider;

        List<DeviceInstallation> devices = deviceInstallationRepository.findByUserId(user.getId());
        String pushToken = (devices != null && !devices.isEmpty()) ? devices.get(0).getPushToken() : null;

        NotificationRecord record = NotificationRecord.builder()
                .user(user)
                .reminder(reminder)
                .opportunity(opportunity)
                .title(title)
                .body(body)
                .channel(NotificationChannel.PUSH)
                .provider(provider.getProviderName())
                .deliveryStatus(DeliveryStatus.PENDING)
                .deepLinkUri(deepLink)
                .build();

        record = notificationRecordRepository.save(record);

        NotificationDeliveryResult result = provider.sendPushNotification(pushToken, title, body, data);

        if (result.isSuccess()) {
            record.markDelivered();
            log.info("Notification successfully delivered to user {} via {} [msgId: {}]",
                    user.getId(), provider.getProviderName(), result.getMessageId());
        } else {
            record.markFailed(result.getErrorMessage());
            log.warn("Notification delivery failed for user {}: {}", user.getId(), result.getErrorMessage());
        }

        return notificationRecordRepository.save(record);
    }
}
