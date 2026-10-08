package com.mnesa.android.domain.repository;

import com.mnesa.android.domain.model.SyncOperation;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * Domain repository contract for durable synchronization queue.
 */
public interface SyncRepository {

    Flowable<List<SyncOperation>> getPendingOperations(String userId);

    Completable enqueueOperation(SyncOperation operation);

    Completable markSuccess(String operationId);

    Completable markFailed(String operationId, String error);

    Single<Integer> getPendingCount();

    Completable clearForUser(String userId);
}
