package com.mnesa.android.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.mnesa.android.data.local.entity.ReminderEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Room Data Access Object for Reminders with user isolation.
 */
@Dao
public interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE user_id = :userId ORDER BY trigger_timestamp ASC")
    Flowable<List<ReminderEntity>> getAllReminders(String userId);

    @Query("SELECT * FROM reminders WHERE user_id = :userId AND status IN (:statuses) ORDER BY trigger_timestamp ASC")
    Flowable<List<ReminderEntity>> getRemindersByStatusIn(String userId, List<String> statuses);

    @Query("SELECT * FROM reminders WHERE user_id = :userId AND status = :status ORDER BY trigger_timestamp ASC")
    Flowable<List<ReminderEntity>> getRemindersByStatus(String userId, String status);

    @Query("SELECT * FROM reminders WHERE opportunity_id = :oppId AND user_id = :userId ORDER BY trigger_timestamp ASC")
    Flowable<List<ReminderEntity>> getRemindersForOpportunity(String oppId, String userId);

    @Query("SELECT * FROM reminders WHERE id = :id AND user_id = :userId LIMIT 1")
    Single<ReminderEntity> getReminderById(String id, String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertReminder(ReminderEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<ReminderEntity> entities);

    @Query("UPDATE reminders SET status = :status WHERE id = :id")
    Completable updateStatus(String id, String status);

    @Query("UPDATE reminders SET snooze_until = :snoozeUntil, snooze_count = :snoozeCount, status = :status WHERE id = :id")
    Completable snoozeReminder(String id, long snoozeUntil, int snoozeCount, String status);

    @Query("DELETE FROM reminders WHERE id = :id AND user_id = :userId")
    Completable deleteReminderById(String id, String userId);

    @Query("DELETE FROM reminders WHERE user_id = :userId")
    Completable deleteAllForUser(String userId);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status IN ('SCHEDULED', 'SNOOZED')")
    Single<Integer> countScheduledForUser(String userId);
}
