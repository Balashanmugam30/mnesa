package com.mnesa.android.domain.repository;

import com.mnesa.android.domain.model.Reminder;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Domain repository contract for reminders.
 */
public interface ReminderRepository {

    Flowable<List<Reminder>> getAllReminders(String userId);

    Flowable<List<Reminder>> getRemindersForOpportunity(String oppId, String userId);

    Completable saveReminder(Reminder reminder);

    Completable deleteReminder(String id, String userId);

    Completable clearRemindersForUser(String userId);

    Single<Integer> getScheduledCount(String userId);

    Completable seedSampleReminders(String userId);
}
