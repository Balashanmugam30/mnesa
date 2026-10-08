package com.mnesa.android.core.sync;

import android.content.Context;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Schedules durable WorkManager sync tasks constrained to connected network states.
 */
public final class SyncScheduler {

    public static final String SYNC_WORK_NAME = "mnesa_sync_work";

    private SyncScheduler() {
    }

    public static void triggerSync(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest syncRequest = new OneTimeWorkRequest.Builder(SyncWorker.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
                .addTag(SYNC_WORK_NAME)
                .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniqueWork(SYNC_WORK_NAME, ExistingWorkPolicy.KEEP, syncRequest);
    }
}
