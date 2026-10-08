package com.mnesa.android.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.mnesa.android.data.local.entity.SyncQueueEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Room Data Access Object for durable sync queue operations.
 */
@Dao
public interface SyncQueueDao {

    @Query("SELECT * FROM sync_queue WHERE user_id = :userId AND state != 'SUCCEEDED' ORDER BY created_at ASC")
    Flowable<List<SyncQueueEntity>> getPendingForUser(String userId);

    @Query("SELECT * FROM sync_queue WHERE state = 'PENDING' OR (state = 'FAILED' AND attempt_count < 5) ORDER BY created_at ASC")
    Single<List<SyncQueueEntity>> getExecutableOperations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable enqueue(SyncQueueEntity entity);

    @Update
    Completable update(SyncQueueEntity entity);

    @Query("DELETE FROM sync_queue WHERE operation_id = :operationId")
    Completable delete(String operationId);

    @Query("DELETE FROM sync_queue WHERE state = 'SUCCEEDED'")
    Completable deleteSucceeded();

    @Query("DELETE FROM sync_queue WHERE user_id = :userId")
    Completable deleteAllForUser(String userId);

    @Query("SELECT COUNT(*) FROM sync_queue WHERE state != 'SUCCEEDED'")
    Single<Integer> countPendingTotal();
}
