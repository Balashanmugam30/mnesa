package com.mnesa.android.core.sync;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.gson.Gson;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.local.dao.CaptureDao;
import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.CaptureEntity;
import com.mnesa.android.data.local.entity.SyncQueueEntity;
import com.mnesa.android.data.remote.dto.ApiResponseDto;
import com.mnesa.android.data.remote.dto.IntakeRequestDto;
import com.mnesa.android.data.remote.dto.IntakeResponseDto;

import java.util.List;

/**
 * Durable WorkManager background worker processing queued mutations under connected network constraint.
 */
public class SyncWorker extends Worker {

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            SyncQueueDao syncDao = db.syncQueueDao();
            CaptureDao captureDao = db.captureDao();
            List<SyncQueueEntity> operations = syncDao.getExecutableOperations().blockingGet();

            if (operations != null && !operations.isEmpty()) {
                Gson gson = new Gson();
                for (SyncQueueEntity op : operations) {
                    op.setLastAttemptAt(System.currentTimeMillis());
                    op.setAttemptCount(op.getAttemptCount() + 1);

                    if ("CAPTURE".equals(op.getEntityType()) && op.getPayloadJson() != null) {
                        try {
                            IntakeRequestDto requestDto = gson.fromJson(op.getPayloadJson(), IntakeRequestDto.class);
                            ApiResponseDto<IntakeResponseDto> apiResponse = ApiClient.getInstance(getApplicationContext())
                                    .getIntakeApiService()
                                    .submitIntake(requestDto)
                                    .blockingGet();

                            if (apiResponse != null && apiResponse.getData() != null) {
                                CaptureEntity capture = captureDao.getById(op.getEntityId()).blockingGet();
                                if (capture != null) {
                                    capture.setStatus("SYNCED");
                                    capture.setBackendJobId(apiResponse.getData().getJobId());
                                    captureDao.update(capture).blockingAwait();
                                }
                            }
                        } catch (Exception ex) {
                            op.setState("FAILED");
                            op.setErrorCategory("NETWORK_RETRY");
                            syncDao.update(op).blockingAwait();
                            return Result.retry();
                        }
                    }

                    op.setState("SUCCEEDED");
                    syncDao.update(op).blockingAwait();
                }
                syncDao.deleteSucceeded().blockingAwait();
            }
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
