package com.mnesa.android.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.mnesa.android.data.local.entity.CaptureEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

@Dao
public interface CaptureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(CaptureEntity capture);

    @Update
    Completable update(CaptureEntity capture);

    @Query("SELECT * FROM captures WHERE id = :id LIMIT 1")
    Single<CaptureEntity> getById(String id);

    @Query("SELECT * FROM captures WHERE user_id = :userId AND idempotency_key = :idempotencyKey LIMIT 1")
    Maybe<CaptureEntity> findByIdempotencyKey(String userId, String idempotencyKey);

    @Query("SELECT * FROM captures WHERE user_id = :userId ORDER BY created_at DESC")
    Observable<List<CaptureEntity>> getCapturesForUser(String userId);

    @Query("SELECT * FROM captures WHERE status = 'QUEUED' ORDER BY created_at ASC")
    Single<List<CaptureEntity>> getPendingCaptures();
}
