package com.mnesa.android.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.mnesa.android.data.local.entity.CandidateEntity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

@Dao
public interface CandidateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<CandidateEntity> candidates);

    @Query("SELECT * FROM ai_candidates WHERE job_id = :jobId ORDER BY candidate_index ASC")
    Single<List<CandidateEntity>> getCandidatesByJobId(String jobId);

    @Query("UPDATE ai_candidates SET is_confirmed = 1 WHERE id = :candidateId")
    Completable markConfirmed(String candidateId);

    @Query("DELETE FROM ai_candidates WHERE job_id = :jobId")
    Completable deleteByJobId(String jobId);
}
