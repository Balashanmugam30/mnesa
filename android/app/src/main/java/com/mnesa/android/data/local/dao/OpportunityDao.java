package com.mnesa.android.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.mnesa.android.data.local.entity.OpportunityEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Room Data Access Object for Opportunities with strict user isolation.
 */
@Dao
public interface OpportunityDao {

    @Query("SELECT * FROM opportunities WHERE user_id = :userId ORDER BY created_at DESC")
    Flowable<List<OpportunityEntity>> getAllOpportunities(String userId);

    @Query("SELECT * FROM opportunities WHERE user_id = :userId AND category = :category ORDER BY created_at DESC")
    Flowable<List<OpportunityEntity>> getOpportunitiesByCategory(String userId, String category);

    @Query("SELECT * FROM opportunities WHERE user_id = :userId AND deadline_timestamp IS NOT NULL AND deadline_timestamp > :now AND deadline_timestamp <= :threshold ORDER BY deadline_timestamp ASC")
    Flowable<List<OpportunityEntity>> getNeedsAttention(String userId, long now, long threshold);

    @Query("SELECT * FROM opportunities WHERE user_id = :userId AND (deadline_timestamp IS NULL OR deadline_timestamp > :now) ORDER BY COALESCE(deadline_timestamp, 9223372036854775807) ASC")
    Flowable<List<OpportunityEntity>> getUpcoming(String userId, long now);

    @Query("SELECT * FROM opportunities WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    Flowable<List<OpportunityEntity>> getRecentlySaved(String userId, int limit);

    @Query("SELECT * FROM opportunities WHERE id = :id AND user_id = :userId LIMIT 1")
    Single<OpportunityEntity> getOpportunityById(String id, String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOpportunity(OpportunityEntity entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<OpportunityEntity> entities);

    @Update
    Completable updateOpportunity(OpportunityEntity entity);

    @Query("DELETE FROM opportunities WHERE id = :id AND user_id = :userId")
    Completable deleteOpportunityById(String id, String userId);

    @Query("DELETE FROM opportunities WHERE user_id = :userId")
    Completable deleteAllForUser(String userId);

    @Query("SELECT COUNT(*) FROM opportunities WHERE user_id = :userId")
    Single<Integer> countForUser(String userId);

    @Query("SELECT COUNT(*) FROM opportunities WHERE user_id = :userId AND deadline_timestamp IS NOT NULL AND deadline_timestamp > :now AND deadline_timestamp <= :threshold")
    Single<Integer> countUrgentForUser(String userId, long now, long threshold);
}
