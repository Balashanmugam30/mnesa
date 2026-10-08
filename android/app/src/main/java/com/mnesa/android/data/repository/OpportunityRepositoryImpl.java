package com.mnesa.android.data.repository;

import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.entity.OpportunityEntity;
import com.mnesa.android.data.sample.SampleDataProvider;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of OpportunityRepository bridging Room local cache and user scoping.
 */
public class OpportunityRepositoryImpl implements OpportunityRepository {

    private final OpportunityDao opportunityDao;
    private static final String DEFAULT_USER_ID = "default_user";

    public OpportunityRepositoryImpl(OpportunityDao opportunityDao) {
        this.opportunityDao = opportunityDao;
    }

    @Override
    public Flowable<List<Opportunity>> getAllOpportunities() {
        return getAllOpportunities(DEFAULT_USER_ID);
    }

    @Override
    public Flowable<List<Opportunity>> getAllOpportunities(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return opportunityDao.getAllOpportunities(effectiveUserId).map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Opportunity>> getOpportunitiesByCategory(String userId, String category) {
        String effectiveUserId = resolveUserId(userId);
        if (category == null || category.equalsIgnoreCase("ALL")) {
            return getAllOpportunities(effectiveUserId);
        }
        return opportunityDao.getOpportunitiesByCategory(effectiveUserId, category.toUpperCase())
                .map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Opportunity>> getNeedsAttention(String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();
        long sevenDaysThreshold = now + (7L * 24 * 60 * 60 * 1000);
        return opportunityDao.getNeedsAttention(effectiveUserId, now, sevenDaysThreshold)
                .map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Opportunity>> getUpcoming(String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();
        return opportunityDao.getUpcoming(effectiveUserId, now).map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Opportunity>> getRecentlySaved(String userId, int limit) {
        String effectiveUserId = resolveUserId(userId);
        return opportunityDao.getRecentlySaved(effectiveUserId, limit).map(this::mapEntitiesToDomain);
    }

    @Override
    public Single<Opportunity> getOpportunityById(String id) {
        return getOpportunityById(id, DEFAULT_USER_ID);
    }

    @Override
    public Single<Opportunity> getOpportunityById(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        return opportunityDao.getOpportunityById(id, effectiveUserId).map(this::mapEntityToDomain);
    }

    @Override
    public Completable saveOpportunity(Opportunity opportunity) {
        return opportunityDao.insertOpportunity(mapDomainToEntity(opportunity));
    }

    @Override
    public Completable deleteOpportunity(String id) {
        return deleteOpportunity(id, DEFAULT_USER_ID);
    }

    @Override
    public Completable deleteOpportunity(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        return opportunityDao.deleteOpportunityById(id, effectiveUserId);
    }

    @Override
    public Completable seedSampleData(String userId) {
        String effectiveUserId = resolveUserId(userId);
        List<Opportunity> sampleOpportunities = SampleDataProvider.getSampleOpportunities(effectiveUserId);
        List<OpportunityEntity> entities = new ArrayList<>();
        for (Opportunity opp : sampleOpportunities) {
            entities.add(mapDomainToEntity(opp));
        }
        return opportunityDao.insertAll(entities);
    }

    @Override
    public Completable clearDataForUser(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return opportunityDao.deleteAllForUser(effectiveUserId);
    }

    @Override
    public Single<Integer> getOpportunityCount(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return opportunityDao.countForUser(effectiveUserId);
    }

    @Override
    public Single<Integer> getUrgentCount(String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();
        long sevenDaysThreshold = now + (7L * 24 * 60 * 60 * 1000);
        return opportunityDao.countUrgentForUser(effectiveUserId, now, sevenDaysThreshold);
    }

    private String resolveUserId(String userId) {
        return (userId != null && !userId.trim().isEmpty()) ? userId : DEFAULT_USER_ID;
    }

    private List<Opportunity> mapEntitiesToDomain(List<OpportunityEntity> entities) {
        List<Opportunity> result = new ArrayList<>();
        if (entities != null) {
            for (OpportunityEntity entity : entities) {
                result.add(mapEntityToDomain(entity));
            }
        }
        return result;
    }

    private Opportunity mapEntityToDomain(OpportunityEntity entity) {
        OpportunityType type;
        try {
            type = OpportunityType.valueOf(entity.getCategory());
        } catch (Exception e) {
            type = OpportunityType.OTHER;
        }

        OpportunityStatus status;
        try {
            status = OpportunityStatus.valueOf(entity.getStatus());
        } catch (Exception e) {
            status = OpportunityStatus.CAPTURED;
        }

        return new Opportunity(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getOrganization(),
                type,
                entity.getCategory(),
                status,
                entity.getDescription(),
                entity.getSourceUrl(),
                entity.getRegistrationUrl(),
                entity.getDeadlineTimestamp(),
                entity.getDeadlineTimezone(),
                entity.getEligibility(),
                entity.getLocation(),
                entity.getEstimatedEffort(),
                entity.getPriority(),
                entity.getConfidenceScore(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getSyncState()
        );
    }

    private OpportunityEntity mapDomainToEntity(Opportunity domain) {
        String category = domain.getCategory() != null ? domain.getCategory() :
                (domain.getType() != null ? domain.getType().name() : "OTHER");
        String status = domain.getStatus() != null ? domain.getStatus().name() : "CAPTURED";

        return new OpportunityEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getTitle(),
                domain.getOrganization(),
                category,
                domain.getDescription(),
                domain.getSourceUrl(),
                domain.getRegistrationUrl(),
                domain.getDeadlineTimestamp(),
                domain.getDeadlineTimezone(),
                domain.getEligibility(),
                domain.getLocation(),
                domain.getEstimatedEffort(),
                domain.getPriority(),
                status,
                domain.getConfidenceScore(),
                domain.getCreatedAt(),
                domain.getUpdatedAt(),
                domain.getSyncState()
        );
    }
}
