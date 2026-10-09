package com.mnesa.android.domain.repository;

import com.mnesa.android.data.remote.dto.CreateReminderRequestDto;
import com.mnesa.android.data.remote.dto.ReminderSuggestionDto;
import com.mnesa.android.domain.model.Reminder;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Domain repository contract for reminders and scheduling intelligence.
 */
public interface ReminderRepository {

    Flowable<List<Reminder>> getAllReminders(String userId);

    Flowable<List<Reminder>> getRemindersFiltered(String userId, String filter);

    Flowable<List<Reminder>> getRemindersForOpportunity(String oppId);

    Flowable<List<Reminder>> getRemindersForOpportunity(String oppId, String userId);

    Single<Reminder> getReminderById(String id, String userId);

    Completable saveReminder(Reminder reminder);

    Completable deleteReminder(String id, String userId);

    Completable snoozeReminder(String id, int minutes, String userId);

    Completable dismissReminder(String id, String userId);

    Completable clearRemindersForUser(String userId);

    Single<Integer> getScheduledCount(String userId);

    Completable seedSampleReminders(String userId);

    Completable syncReminders(String userId);

    Single<Reminder> createReminderRemote(String opportunityId, CreateReminderRequestDto request, String userId);

    Single<List<ReminderSuggestionDto>> getReminderSuggestions(String opportunityId);
}
