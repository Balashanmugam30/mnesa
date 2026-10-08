package com.mnesa.android.domain.repository;

import com.mnesa.android.domain.model.Opportunity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Domain repository contract for opportunities with user isolation and offline cache.
 */
public interface OpportunityRepository {

    Flowable<List<Opportunity>> getAllOpportunities();

    Flowable<List<Opportunity>> getAllOpportunities(String userId);

    Flowable<List<Opportunity>> getOpportunitiesByCategory(String userId, String category);

    Flowable<List<Opportunity>> getNeedsAttention(String userId);

    Flowable<List<Opportunity>> getUpcoming(String userId);

    Flowable<List<Opportunity>> getRecentlySaved(String userId, int limit);

    Single<Opportunity> getOpportunityById(String id);

    Single<Opportunity> getOpportunityById(String id, String userId);

    Completable saveOpportunity(Opportunity opportunity);

    Completable deleteOpportunity(String id);

    Completable deleteOpportunity(String id, String userId);

    Completable seedSampleData(String userId);

    Completable clearDataForUser(String userId);

    Single<Integer> getOpportunityCount(String userId);

    Single<Integer> getUrgentCount(String userId);
}
