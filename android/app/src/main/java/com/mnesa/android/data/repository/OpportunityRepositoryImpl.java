package com.mnesa.android.data.repository;

import android.content.Context;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.entity.OpportunityEntity;
import com.mnesa.android.data.remote.api.OpportunityApiService;
import com.mnesa.android.data.remote.dto.*;
import com.mnesa.android.data.sample.SampleDataProvider;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of OpportunityRepository bridging Room local cache, user scoping,
 * and remote OpportunityApiService.
 */
public class OpportunityRepositoryImpl implements OpportunityRepository {

    private final OpportunityDao opportunityDao;
    private final OpportunityApiService apiService;
    private static final String DEFAULT_USER_ID = "default_user";

    public OpportunityRepositoryImpl(OpportunityDao opportunityDao) {
        this(opportunityDao, null);
    }

    public OpportunityRepositoryImpl(OpportunityDao opportunityDao, OpportunityApiService apiService) {
        this.opportunityDao = opportunityDao;
        this.apiService = apiService;
    }

    public OpportunityRepositoryImpl(Context context) {
        this(AppDatabase.getInstance(context).opportunityDao(),
             ApiClient.getInstance(context).getOpportunityApiService());
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
    public Flowable<List<Opportunity>> searchOpportunities(String userId, String query) {
        String effectiveUserId = resolveUserId(userId);
        if (query == null || query.trim().isEmpty()) {
            return getAllOpportunities(effectiveUserId);
        }
        return opportunityDao.searchOpportunities(effectiveUserId, query.trim())
                .map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Opportunity>> filterOpportunities(String userId, String query, String category, String status, boolean includeArchived) {
        String effectiveUserId = resolveUserId(userId);
        String cat = (category == null || category.equalsIgnoreCase("ALL")) ? null : category.toUpperCase();
        String st = (status == null || status.equalsIgnoreCase("ALL")) ? null : status.toUpperCase();
        String q = (query == null || query.trim().isEmpty()) ? null : query.trim();
        int archivedFlag = includeArchived ? 1 : 0;

        return opportunityDao.filterOpportunities(effectiveUserId, q, cat, st, archivedFlag)
                .map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Opportunity>> getArchivedOpportunities(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return opportunityDao.getArchivedOpportunities(effectiveUserId).map(this::mapEntitiesToDomain);
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
        return opportunityDao.getOpportunityById(id, effectiveUserId)
                .map(this::mapEntityToDomain)
                .onErrorResumeNext(err -> {
                    if (apiService != null) {
                        return apiService.getOpportunityById(id)
                                .map(res -> mapDtoToDomain(res.getData()))
                                .flatMap(opp -> opportunityDao.insertOpportunity(mapDomainToEntity(opp))
                                        .toSingleDefault(opp));
                    }
                    return Single.error(err);
                });
    }

    @Override
    public Completable saveOpportunity(Opportunity opportunity) {
        return opportunityDao.insertOpportunity(mapDomainToEntity(opportunity));
    }

    @Override
    public Single<Opportunity> createOpportunity(CreateOpportunityRequestDto request, String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();

        if (apiService != null) {
            return apiService.createOpportunity(request)
                    .flatMap(res -> {
                        Opportunity opp = mapDtoToDomain(res.getData());
                        return opportunityDao.insertOpportunity(mapDomainToEntity(opp))
                                .toSingleDefault(opp);
                    })
                    .onErrorResumeNext(err -> {
                        Opportunity localOpp = createLocalOpportunity(request, effectiveUserId, now);
                        return opportunityDao.insertOpportunity(mapDomainToEntity(localOpp))
                                .toSingleDefault(localOpp);
                    });
        } else {
            Opportunity localOpp = createLocalOpportunity(request, effectiveUserId, now);
            return opportunityDao.insertOpportunity(mapDomainToEntity(localOpp))
                    .toSingleDefault(localOpp);
        }
    }

    @Override
    public Single<Opportunity> updateOpportunity(String id, UpdateOpportunityRequestDto request, String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();

        if (apiService != null) {
            return apiService.updateOpportunity(id, request)
                    .flatMap(res -> {
                        Opportunity opp = mapDtoToDomain(res.getData());
                        return opportunityDao.insertOpportunity(mapDomainToEntity(opp))
                                .toSingleDefault(opp);
                    })
                    .onErrorResumeNext(err -> updateLocalOpportunity(id, request, effectiveUserId, now));
        } else {
            return updateLocalOpportunity(id, request, effectiveUserId, now);
        }
    }

    @Override
    public Completable updateStatus(String id, String newStatus, String comment, String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();

        Completable localUpdate = opportunityDao.updateStatus(id, effectiveUserId, newStatus, now);

        if (apiService != null) {
            return apiService.updateStatus(id, new UpdateStatusRequestDto(newStatus, comment))
                    .ignoreElement()
                    .andThen(localUpdate)
                    .onErrorResumeNext(err -> localUpdate);
        } else {
            return localUpdate;
        }
    }

    @Override
    public Completable archiveOpportunity(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();

        Completable localArchive = opportunityDao.updateStatus(id, effectiveUserId, "ARCHIVED", now);

        if (apiService != null) {
            return apiService.archiveOpportunity(id)
                    .ignoreElement()
                    .andThen(localArchive)
                    .onErrorResumeNext(err -> localArchive);
        } else {
            return localArchive;
        }
    }

    @Override
    public Completable restoreOpportunity(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        long now = System.currentTimeMillis();

        Completable localRestore = opportunityDao.updateStatus(id, effectiveUserId, "SAVED", now);

        if (apiService != null) {
            return apiService.restoreOpportunity(id)
                    .ignoreElement()
                    .andThen(localRestore)
                    .onErrorResumeNext(err -> localRestore);
        } else {
            return localRestore;
        }
    }

    @Override
    public Completable deleteOpportunity(String id) {
        return deleteOpportunity(id, DEFAULT_USER_ID);
    }

    @Override
    public Completable deleteOpportunity(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        Completable localDelete = opportunityDao.deleteOpportunityById(id, effectiveUserId);

        if (apiService != null) {
            return apiService.deleteOpportunity(id)
                    .andThen(localDelete)
                    .onErrorResumeNext(err -> localDelete);
        } else {
            return localDelete;
        }
    }

    @Override
    public Single<List<OpportunityActivityDto>> getOpportunityHistory(String id) {
        if (apiService != null) {
            return apiService.getOpportunityHistory(id)
                    .map(ApiResponseDto::getData)
                    .onErrorReturnItem(Collections.emptyList());
        }
        return Single.just(Collections.emptyList());
    }

    @Override
    public Single<HomeDashboardDto> getHomeDashboard() {
        if (apiService != null) {
            return apiService.getHomeDashboard()
                    .map(ApiResponseDto::getData)
                    .onErrorReturn(err -> new HomeDashboardDto());
        }
        return Single.just(new HomeDashboardDto());
    }

    @Override
    public Single<List<CategorySummaryDto>> getCategories() {
        if (apiService != null) {
            return apiService.getCategories()
                    .map(ApiResponseDto::getData)
                    .onErrorReturnItem(Collections.emptyList());
        }
        return Single.just(Collections.emptyList());
    }

    @Override
    public Single<List<TagDto>> getTags() {
        if (apiService != null) {
            return apiService.getTags()
                    .map(ApiResponseDto::getData)
                    .onErrorReturnItem(Collections.emptyList());
        }
        return Single.just(Collections.emptyList());
    }

    @Override
    public Completable refreshOpportunities(String userId) {
        String effectiveUserId = resolveUserId(userId);
        if (apiService != null) {
            return apiService.searchOpportunities(null, null, null, null, null, null, null, null, null, false, 0, 100, null)
                    .map(ApiResponseDto::getData)
                    .map(PageResponseDto::getContent)
                    .flatMapCompletable(summaries -> {
                        List<Completable> fetchSingles = new ArrayList<>();
                        for (OpportunitySummaryDto summary : summaries) {
                            fetchSingles.add(apiService.getOpportunityById(summary.getId())
                                    .map(ApiResponseDto::getData)
                                    .flatMapCompletable(dto -> opportunityDao.insertOpportunity(mapDomainToEntity(mapDtoToDomain(dto)))));
                        }
                        return Completable.concat(fetchSingles);
                    })
                    .onErrorComplete();
        }
        return Completable.complete();
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

    private Opportunity createLocalOpportunity(CreateOpportunityRequestDto req, String userId, long now) {
        String id = UUID.randomUUID().toString();
        OpportunityType type;
        try {
            type = OpportunityType.valueOf(req.getCategory());
        } catch (Exception e) {
            type = OpportunityType.OTHER;
        }

        return new Opportunity(
                id,
                userId,
                req.getTitle(),
                req.getOrganization(),
                type,
                req.getCategory(),
                OpportunityStatus.SAVED,
                req.getDescription(),
                req.getSourceUrl(),
                req.getRegistrationUrl(),
                req.getDeadlineTimestamp(),
                req.getDeadlineTimezone(),
                req.getEligibility(),
                req.getLocation(),
                req.getEstimatedEffort(),
                req.getPriority() != null ? req.getPriority() : "MEDIUM",
                1.0f,
                now,
                now,
                "LOCAL_NEW"
        );
    }

    private Single<Opportunity> updateLocalOpportunity(String id, UpdateOpportunityRequestDto req, String userId, long now) {
        return opportunityDao.getOpportunityById(id, userId)
                .map(existing -> {
                    OpportunityType type;
                    try {
                        type = OpportunityType.valueOf(req.getCategory() != null ? req.getCategory() : existing.getCategory());
                    } catch (Exception e) {
                        type = OpportunityType.OTHER;
                    }

                    OpportunityStatus status;
                    try {
                        status = OpportunityStatus.valueOf(existing.getStatus());
                    } catch (Exception e) {
                        status = OpportunityStatus.SAVED;
                    }

                    Opportunity updated = new Opportunity(
                            existing.getId(),
                            existing.getUserId(),
                            req.getTitle() != null ? req.getTitle() : existing.getTitle(),
                            req.getOrganization() != null ? req.getOrganization() : existing.getOrganization(),
                            type,
                            req.getCategory() != null ? req.getCategory() : existing.getCategory(),
                            status,
                            req.getDescription() != null ? req.getDescription() : existing.getDescription(),
                            req.getSourceUrl() != null ? req.getSourceUrl() : existing.getSourceUrl(),
                            req.getRegistrationUrl() != null ? req.getRegistrationUrl() : existing.getRegistrationUrl(),
                            req.getDeadlineTimestamp() != null ? req.getDeadlineTimestamp() : existing.getDeadlineTimestamp(),
                            req.getDeadlineTimezone() != null ? req.getDeadlineTimezone() : existing.getDeadlineTimezone(),
                            req.getEligibility() != null ? req.getEligibility() : existing.getEligibility(),
                            req.getLocation() != null ? req.getLocation() : existing.getLocation(),
                            req.getEstimatedEffort() != null ? req.getEstimatedEffort() : existing.getEstimatedEffort(),
                            req.getPriority() != null ? req.getPriority() : existing.getPriority(),
                            existing.getConfidenceScore(),
                            existing.getCreatedAt(),
                            now,
                            "LOCAL_MODIFIED"
                    );
                    opportunityDao.insertOpportunity(mapDomainToEntity(updated)).blockingAwait();
                    return updated;
                });
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
            status = OpportunityStatus.SAVED;
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
        String status = domain.getStatus() != null ? domain.getStatus().name() : "SAVED";

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

    private Opportunity mapDtoToDomain(OpportunityDto dto) {
        if (dto == null) return null;
        OpportunityType type;
        try {
            type = OpportunityType.valueOf(dto.getCategory());
        } catch (Exception e) {
            type = OpportunityType.OTHER;
        }

        OpportunityStatus status;
        try {
            status = OpportunityStatus.valueOf(dto.getStatus());
        } catch (Exception e) {
            status = OpportunityStatus.SAVED;
        }

        return new Opportunity(
                dto.getId(),
                dto.getUserId() != null ? dto.getUserId() : "",
                dto.getTitle() != null ? dto.getTitle() : "",
                dto.getOrganization() != null ? dto.getOrganization() : "",
                type,
                dto.getCategory() != null ? dto.getCategory() : "OTHER",
                status,
                dto.getDescription(),
                dto.getSourceUrl(),
                dto.getRegistrationUrl(),
                dto.getDeadlineTimestamp(),
                dto.getDeadlineTimezone(),
                dto.getEligibility(),
                dto.getLocation(),
                dto.getEstimatedEffort(),
                dto.getPriority() != null ? dto.getPriority() : "MEDIUM",
                dto.getConfidenceScore() != null ? dto.getConfidenceScore() : 1.0f,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                "SYNCED"
        );
    }
}
