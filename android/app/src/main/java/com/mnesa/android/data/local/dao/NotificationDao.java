package com.mnesa.android.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.mnesa.android.data.local.entity.NotificationEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Room Data Access Object for local notifications history and badge count.
 */
@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    Flowable<List<NotificationEntity>> getAllNotifications(String userId);

    @Query("SELECT * FROM notifications WHERE user_id = :userId AND opened_at IS NULL ORDER BY created_at DESC")
    Flowable<List<NotificationEntity>> getUnreadNotifications(String userId);

    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND opened_at IS NULL")
    Single<Integer> countUnread(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertNotification(NotificationEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<NotificationEntity> entities);

    @Query("UPDATE notifications SET opened_at = :openedAt, delivery_status = 'OPENED' WHERE id = :id")
    Completable markOpened(String id, long openedAt);

    @Query("UPDATE notifications SET opened_at = :openedAt, delivery_status = 'OPENED' WHERE user_id = :userId AND opened_at IS NULL")
    Completable markAllOpened(String userId, long openedAt);

    @Query("DELETE FROM notifications WHERE id = :id AND user_id = :userId")
    Completable deleteById(String id, String userId);

    @Query("DELETE FROM notifications WHERE user_id = :userId")
    Completable deleteAllForUser(String userId);
}
