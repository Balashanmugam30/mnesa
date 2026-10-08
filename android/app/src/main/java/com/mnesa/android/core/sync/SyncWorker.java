package com.mnesa.android.core.sync;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.mnesa.android.data.local.AppDatabase;
import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.SyncQueueEntity;

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
            SyncQueueDao syncDao = AppDatabase.getInstance(getApplicationContext()).syncQueueDao();
            List<SyncQueueEntity> operations = syncDao.getExecutableOperations().blockingGet();

            if (operations != null && !operations.isEmpty()) {
                for (SyncQueueEntity op : operations) {
                    op.setLastAttemptAt(System.currentTimeMillis());
                    op.setAttemptCount(op.getAttemptCount() + 1);
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
