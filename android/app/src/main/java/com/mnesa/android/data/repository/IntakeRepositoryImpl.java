package com.mnesa.android.data.repository;

import android.content.Context;
import com.google.gson.Gson;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.core.sync.SyncScheduler;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.local.dao.CaptureDao;
import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.CaptureEntity;
import com.mnesa.android.data.local.entity.OpportunityEntity;
import com.mnesa.android.data.local.entity.SyncQueueEntity;
import com.mnesa.android.data.remote.api.IntakeApiService;
import com.mnesa.android.data.remote.dto.IntakeRequestDto;
import com.mnesa.android.data.remote.dto.IntakeResponseDto;
import com.mnesa.android.domain.model.IntakePayload;
import com.mnesa.android.domain.repository.IntakeRepository;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.UUID;

public class IntakeRepositoryImpl implements IntakeRepository {

    private final Context context;
    private final CaptureDao captureDao;
    private final OpportunityDao opportunityDao;
    private final SyncQueueDao syncQueueDao;
    private final IntakeApiService apiService;
    private final SecureTokenManager tokenManager;
    private final Gson gson = new Gson();

    public IntakeRepositoryImpl(Context context) {
        this.context = context.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(this.context);
        this.captureDao = db.captureDao();
        this.opportunityDao = db.opportunityDao();
        this.syncQueueDao = db.syncQueueDao();
        this.apiService = ApiClient.getInstance(this.context).getIntakeApiService();
        this.tokenManager = new SecureTokenManager(this.context);
    }

    // Constructor for testing
    public IntakeRepositoryImpl(Context context,
                                CaptureDao captureDao,
                                OpportunityDao opportunityDao,
                                SyncQueueDao syncQueueDao,
                                IntakeApiService apiService,
                                SecureTokenManager tokenManager) {
        this.context = context != null ? context.getApplicationContext() : null;
        this.captureDao = captureDao;
        this.opportunityDao = opportunityDao;
        this.syncQueueDao = syncQueueDao;
        this.apiService = apiService;
        this.tokenManager = tokenManager;
    }

    @Override
    public Single<IntakeResponseDto> processAndSubmit(IntakePayload payload, String idempotencyKey) {
        String userId = tokenManager != null && tokenManager.getUserId() != null
                ? tokenManager.getUserId()
                : "local_user";
        String captureId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        CaptureEntity entity = new CaptureEntity(
                captureId,
                userId,
                payload.getSourceType(),
                payload.getRawText(),
                payload.getExtractedUrl(),
                payload.getExtractedUrl(),
                payload.getSourceDomain(),
                idempotencyKey,
                "QUEUED",
                null,
                now,
                now
        );

        String oppTitle = createPreviewTitle(payload);
        OpportunityEntity opp = new OpportunityEntity(
                captureId,
                userId,
                oppTitle,
                payload.getSourceDomain() != null ? payload.getSourceDomain() : "Shared Opportunity",
                "OTHER",
                payload.getRawText(),
                payload.getExtractedUrl(),
                null,
                null,
                null,
                null,
                null,
                null,
                "MEDIUM",
                "CAPTURED",
                1.0f,
                now,
                now,
                "PENDING_SYNC"
        );

        IntakeRequestDto requestDto = new IntakeRequestDto(
                idempotencyKey,
                payload.getRawText(),
                payload.getExtractedUrl(),
                payload.getSourceType(),
                captureId,
                payload.getMimeType(),
                payload.getFileSizeBytes() > 0 ? payload.getFileSizeBytes() : null,
                null
        );

        return captureDao.insert(entity)
                .andThen(opportunityDao.insertOpportunity(opp))
                .andThen(apiService.submitIntake(requestDto))
                .flatMap(apiResponse -> {
                    IntakeResponseDto response = apiResponse.getData();
                    entity.setStatus("SYNCED");
                    entity.setBackendJobId(response.getJobId());
                    if (response.getCanonicalUrl() != null) {
                        entity.setCanonicalUrl(response.getCanonicalUrl());
                    }
                    opp.setSyncState("SYNCED");
                    return captureDao.update(entity)
                            .andThen(opportunityDao.updateOpportunity(opp))
                            .andThen(Single.just(response));
                })
                .onErrorResumeNext(throwable -> enqueueOfflineSync(entity, opp, requestDto)
                        .andThen(Single.error(throwable)));
    }

    @Override
    public Single<CaptureEntity> saveOfflineCapture(IntakePayload payload, String idempotencyKey) {
        String userId = tokenManager != null && tokenManager.getUserId() != null
                ? tokenManager.getUserId()
                : "local_user";
        String captureId = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        CaptureEntity entity = new CaptureEntity(
                captureId,
                userId,
                payload.getSourceType(),
                payload.getRawText(),
                payload.getExtractedUrl(),
                payload.getExtractedUrl(),
                payload.getSourceDomain(),
                idempotencyKey,
                "QUEUED",
                null,
                now,
                now
        );

        String oppTitle = createPreviewTitle(payload);
        OpportunityEntity opp = new OpportunityEntity(
                captureId,
                userId,
                oppTitle,
                payload.getSourceDomain() != null ? payload.getSourceDomain() : "Shared Opportunity",
                "OTHER",
                payload.getRawText(),
                payload.getExtractedUrl(),
                null,
                null,
                null,
                null,
                null,
                null,
                "MEDIUM",
                "CAPTURED",
                1.0f,
                now,
                now,
                "QUEUED_OFFLINE"
        );

        IntakeRequestDto requestDto = new IntakeRequestDto(
                idempotencyKey,
                payload.getRawText(),
                payload.getExtractedUrl(),
                payload.getSourceType(),
                captureId,
                payload.getMimeType(),
                payload.getFileSizeBytes() > 0 ? payload.getFileSizeBytes() : null,
                null
        );

        return captureDao.insert(entity)
                .andThen(opportunityDao.insertOpportunity(opp))
                .andThen(enqueueOfflineSync(entity, opp, requestDto))
                .andThen(Single.just(entity));
    }

    @Override
    public Observable<List<CaptureEntity>> getRecentCaptures() {
        String userId = tokenManager != null && tokenManager.getUserId() != null
                ? tokenManager.getUserId()
                : "local_user";
        return captureDao.getCapturesForUser(userId);
    }

    private io.reactivex.rxjava3.core.Completable enqueueOfflineSync(CaptureEntity capture, OpportunityEntity opp, IntakeRequestDto request) {
        String operationId = UUID.randomUUID().toString();
        String payloadJson = gson.toJson(request);

        SyncQueueEntity queueItem = new SyncQueueEntity(
                operationId,
                capture.getUserId(),
                "CAPTURE",
                capture.getId(),
                "CREATE",
                payloadJson,
                System.currentTimeMillis(),
                0,
                null,
                null,
                "PENDING",
                null
        );

        return syncQueueDao.enqueue(queueItem)
                .doOnComplete(() -> {
                    if (context != null) {
                        SyncScheduler.triggerSync(context);
                    }
                });
    }

    private String createPreviewTitle(IntakePayload payload) {
        if (payload.getRawText() != null && !payload.getRawText().isBlank()) {
            String text = payload.getRawText().trim();
            return text.length() > 60 ? text.substring(0, 60).trim() + "…" : text;
        }
        if (payload.getExtractedUrl() != null) {
            return payload.getExtractedUrl();
        }
        return "Shared Opportunity";
    }
}
