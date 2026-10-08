package com.mnesa.android.domain.repository;

import com.mnesa.android.domain.model.Opportunity;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Domain repository contract for opportunities.
 */
public interface OpportunityRepository {

    Flowable<List<Opportunity>> getAllOpportunities();

    Single<Opportunity> getOpportunityById(String id);

    Completable saveOpportunity(Opportunity opportunity);

    Completable deleteOpportunity(String id);
}
