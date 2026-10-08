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

    @Query("SELECT * FROM reminders WHERE opportunity_id = :oppId AND user_id = :userId ORDER BY trigger_timestamp ASC")
    Flowable<List<ReminderEntity>> getRemindersForOpportunity(String oppId, String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertReminder(ReminderEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<ReminderEntity> entities);

    @Query("DELETE FROM reminders WHERE id = :id AND user_id = :userId")
    Completable deleteReminderById(String id, String userId);

    @Query("DELETE FROM reminders WHERE user_id = :userId")
    Completable deleteAllForUser(String userId);

    @Query("SELECT COUNT(*) FROM reminders WHERE user_id = :userId AND status = 'SCHEDULED'")
    Single<Integer> countScheduledForUser(String userId);
}
