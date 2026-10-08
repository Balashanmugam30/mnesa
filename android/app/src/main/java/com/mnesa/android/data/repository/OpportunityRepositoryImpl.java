package com.mnesa.android.data.repository;

import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.entity.OpportunityEntity;
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
 * Implementation of OpportunityRepository bridging Room local cache.
 */
public class OpportunityRepositoryImpl implements OpportunityRepository {

    private final OpportunityDao opportunityDao;

    public OpportunityRepositoryImpl(OpportunityDao opportunityDao) {
        this.opportunityDao = opportunityDao;
    }

    @Override
    public Flowable<List<Opportunity>> getAllOpportunities() {
        return opportunityDao.getAllOpportunities().map(this::mapEntitiesToDomain);
    }

    @Override
    public Single<Opportunity> getOpportunityById(String id) {
        return opportunityDao.getOpportunityById(id).map(this::mapEntityToDomain);
    }

    @Override
    public Completable saveOpportunity(Opportunity opportunity) {
        return opportunityDao.insertOpportunity(mapDomainToEntity(opportunity));
    }

    @Override
    public Completable deleteOpportunity(String id) {
        return opportunityDao.deleteOpportunityById(id);
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
            type = OpportunityType.valueOf(entity.getType());
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
                entity.getTitle(),
                entity.getOrganization(),
                type,
                status,
                entity.getSourceUrl(),
                entity.getDeadlineTimestamp(),
                entity.getConfidenceScore(),
                entity.getCreatedAt()
        );
    }

    private OpportunityEntity mapDomainToEntity(Opportunity domain) {
        return new OpportunityEntity(
                domain.getId(),
                domain.getTitle(),
                domain.getOrganization(),
                domain.getType().name(),
                domain.getStatus().name(),
                domain.getSourceUrl(),
                domain.getDeadlineTimestamp(),
                domain.getConfidenceScore(),
                domain.getCreatedAt()
        );
    }
}
