package com.mnesa.backend.modules.reminder.service;

import com.mnesa.backend.modules.reminder.domain.DeliveryStatus;
import com.mnesa.backend.modules.reminder.domain.NotificationRecord;
import com.mnesa.backend.modules.reminder.domain.Reminder;
import com.mnesa.backend.modules.reminder.domain.ReminderStatus;
import com.mnesa.backend.modules.reminder.repository.ReminderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Background poller and durable scheduling engine.
 * Periodically identifies due reminders, locks/claims them to avoid duplicate dispatch,
 * and passes them to the notification dispatcher.
 */
@Slf4j
@Component
public class ReminderScheduler {

    private final ReminderRepository reminderRepository;
    private final NotificationDispatcherService dispatcherService;

    public ReminderScheduler(ReminderRepository reminderRepository,
                             NotificationDispatcherService dispatcherService) {
        this.reminderRepository = reminderRepository;
        this.dispatcherService = dispatcherService;
    }

    @Scheduled(fixedDelayString = "${mnesa.scheduler.reminder-delay-ms:30000}", initialDelay = 10000)
    public void processDueReminders() {
        try {
            Instant now = Instant.now();
            List<Reminder> dueReminders = reminderRepository.findDueReminders(now, PageRequest.of(0, 50));

            if (!dueReminders.isEmpty()) {
                log.info("[ReminderScheduler] Discovered {} due reminder(s) to process", dueReminders.size());
            }

            for (Reminder reminder : dueReminders) {
                processSingleReminder(reminder);
            }
        } catch (Exception e) {
            log.error("[ReminderScheduler] Error running scheduled reminder check: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void processSingleReminder(Reminder reminder) {
        try {
            reminder.claim();
            reminder = reminderRepository.saveAndFlush(reminder);

            NotificationRecord record = dispatcherService.dispatchReminderNotification(reminder);

            if (record.getDeliveryStatus() == DeliveryStatus.DELIVERED || record.getDeliveryStatus() == DeliveryStatus.OPENED) {
                reminder.markSent();
            } else {
                log.warn("[ReminderScheduler] Delivery was not confirmed for reminder {}. Setting status to FAILED.", reminder.getId());
                reminder.setStatus(ReminderStatus.FAILED);
            }

            reminderRepository.save(reminder);
        } catch (Exception e) {
            log.error("[ReminderScheduler] Exception processing reminder {}: {}", reminder.getId(), e.getMessage(), e);
            reminder.setStatus(ReminderStatus.FAILED);
            reminderRepository.save(reminder);
        }
    }
}
