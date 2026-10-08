package com.mnesa.android.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.mnesa.android.data.local.entity.OpportunityEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Room Data Access Object for Opportunities.
 */
@Dao
public interface OpportunityDao {

    @Query("SELECT * FROM opportunities ORDER BY created_at DESC")
    Flowable<List<OpportunityEntity>> getAllOpportunities();

    @Query("SELECT * FROM opportunities WHERE id = :id LIMIT 1")
    Single<OpportunityEntity> getOpportunityById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOpportunity(OpportunityEntity entity);

    @Query("DELETE FROM opportunities WHERE id = :id")
    Completable deleteOpportunityById(String id);
}
