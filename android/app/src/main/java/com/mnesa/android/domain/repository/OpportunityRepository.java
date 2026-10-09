package com.mnesa.android.domain.repository;

import com.mnesa.android.data.remote.dto.*;
import com.mnesa.android.domain.model.Opportunity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Domain repository contract for opportunities with user isolation, offline cache,
 * and remote synchronization.
 */
public interface OpportunityRepository {

    Flowable<List<Opportunity>> getAllOpportunities();

    Flowable<List<Opportunity>> getAllOpportunities(String userId);

    Flowable<List<Opportunity>> getOpportunitiesByCategory(String userId, String category);

    Flowable<List<Opportunity>> searchOpportunities(String userId, String query);

    Flowable<List<Opportunity>> filterOpportunities(String userId, String query, String category, String status, boolean includeArchived);

    Flowable<List<Opportunity>> getArchivedOpportunities(String userId);

    Flowable<List<Opportunity>> getNeedsAttention(String userId);

    Flowable<List<Opportunity>> getUpcoming(String userId);

    Flowable<List<Opportunity>> getRecentlySaved(String userId, int limit);

    Single<Opportunity> getOpportunityById(String id);

    Single<Opportunity> getOpportunityById(String id, String userId);

    Completable saveOpportunity(Opportunity opportunity);

    Single<Opportunity> createOpportunity(CreateOpportunityRequestDto request, String userId);

    Single<Opportunity> updateOpportunity(String id, UpdateOpportunityRequestDto request, String userId);

    Completable updateStatus(String id, String newStatus, String comment, String userId);

    Completable archiveOpportunity(String id, String userId);

    Completable restoreOpportunity(String id, String userId);

    Completable deleteOpportunity(String id);

    Completable deleteOpportunity(String id, String userId);

    Single<List<OpportunityActivityDto>> getOpportunityHistory(String id);

    Single<HomeDashboardDto> getHomeDashboard();

    Single<List<CategorySummaryDto>> getCategories();

    Single<List<TagDto>> getTags();

    Completable refreshOpportunities(String userId);

    Completable seedSampleData(String userId);

    Completable clearDataForUser(String userId);

    Single<Integer> getOpportunityCount(String userId);

    Single<Integer> getUrgentCount(String userId);
}
