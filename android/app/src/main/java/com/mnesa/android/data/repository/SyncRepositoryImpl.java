package com.mnesa.android.data.repository;

import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.SyncQueueEntity;
import com.mnesa.android.domain.model.SyncOperation;
import com.mnesa.android.domain.repository.SyncRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of SyncRepository managing the durable offline sync queue.
 */
public class SyncRepositoryImpl implements SyncRepository {

    private final SyncQueueDao syncQueueDao;
    private static final String DEFAULT_USER_ID = "default_user";

    public SyncRepositoryImpl(SyncQueueDao syncQueueDao) {
        this.syncQueueDao = syncQueueDao;
    }

    @Override
    public Flowable<List<SyncOperation>> getPendingOperations(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return syncQueueDao.getPendingForUser(effectiveUserId).map(this::mapEntitiesToDomain);
    }

    @Override
    public Completable enqueueOperation(SyncOperation operation) {
        return syncQueueDao.enqueue(mapDomainToEntity(operation));
    }

    @Override
    public Completable markSuccess(String operationId) {
        return syncQueueDao.delete(operationId);
    }

    @Override
    public Completable markFailed(String operationId, String error) {
        return syncQueueDao.getExecutableOperations()
                .flatMapCompletable(list -> {
                    for (SyncQueueEntity entity : list) {
                        if (entity.getOperationId().equals(operationId)) {
                            entity.setAttemptCount(entity.getAttemptCount() + 1);
                            entity.setLastAttemptAt(System.currentTimeMillis());
                            entity.setState(entity.getAttemptCount() >= 5 ? "FAILED_PERMANENT" : "FAILED");
                            entity.setErrorCategory(error);
                            return syncQueueDao.update(entity);
                        }
                    }
                    return Completable.complete();
                });
    }

    @Override
    public Single<Integer> getPendingCount() {
        return syncQueueDao.countPendingTotal();
    }

    @Override
    public Completable clearForUser(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return syncQueueDao.deleteAllForUser(effectiveUserId);
    }

    private String resolveUserId(String userId) {
        return (userId != null && !userId.trim().isEmpty()) ? userId : DEFAULT_USER_ID;
    }

    private List<SyncOperation> mapEntitiesToDomain(List<SyncQueueEntity> entities) {
        List<SyncOperation> result = new ArrayList<>();
        if (entities != null) {
            for (SyncQueueEntity entity : entities) {
                result.add(new SyncOperation(
                        entity.getOperationId(),
                        entity.getUserId(),
                        entity.getEntityType(),
                        entity.getEntityId(),
                        entity.getOperationType(),
                        entity.getPayloadJson(),
                        entity.getCreatedAt(),
                        entity.getAttemptCount(),
                        entity.getState()
                ));
            }
        }
        return result;
    }

    private SyncQueueEntity mapDomainToEntity(SyncOperation domain) {
        return new SyncQueueEntity(
                domain.getOperationId(),
                domain.getUserId(),
                domain.getEntityType(),
                domain.getEntityId(),
                domain.getOperationType(),
                domain.getPayloadJson(),
                domain.getCreatedAt(),
                domain.getAttemptCount(),
                null,
                null,
                domain.getState(),
                null
        );
    }
}
