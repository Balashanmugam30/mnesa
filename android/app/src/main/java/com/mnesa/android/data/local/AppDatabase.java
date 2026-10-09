package com.mnesa.android.data.local;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.mnesa.android.data.local.dao.CaptureDao;
import com.mnesa.android.data.local.dao.NotificationDao;
import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.dao.ReminderDao;
import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.CaptureEntity;
import com.mnesa.android.data.local.entity.NotificationEntity;
import com.mnesa.android.data.local.entity.OpportunityEntity;
import com.mnesa.android.data.local.entity.ReminderEntity;
import com.mnesa.android.data.local.entity.SyncQueueEntity;

/**
 * Main Room Database for MNESA offline cache and Single Source of Truth.
 * Version 4 introduces rich reminder intelligence fields and notifications history.
 */
@Database(
        entities = {OpportunityEntity.class, ReminderEntity.class, SyncQueueEntity.class, CaptureEntity.class, NotificationEntity.class},
        version = 4,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "mnesa_database";
    private static volatile AppDatabase INSTANCE;

    public abstract OpportunityDao opportunityDao();
    public abstract ReminderDao reminderDao();
    public abstract SyncQueueDao syncQueueDao();
    public abstract CaptureDao captureDao();
    public abstract NotificationDao notificationDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE opportunities ADD COLUMN user_id TEXT NOT NULL DEFAULT ''");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN category TEXT NOT NULL DEFAULT 'OTHER'");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN description TEXT");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN registration_url TEXT");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN deadline_timezone TEXT");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN eligibility TEXT");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN location TEXT");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN estimated_effort TEXT");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN priority TEXT NOT NULL DEFAULT 'MEDIUM'");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN updated_at INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE opportunities ADD COLUMN sync_state TEXT NOT NULL DEFAULT 'SYNCED'");

            database.execSQL("CREATE TABLE IF NOT EXISTS reminders ("
                    + "id TEXT PRIMARY KEY NOT NULL, "
                    + "opportunity_id TEXT NOT NULL, "
                    + "user_id TEXT NOT NULL, "
                    + "title TEXT NOT NULL, "
                    + "trigger_timestamp INTEGER NOT NULL, "
                    + "reminder_type TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "created_at INTEGER NOT NULL)");

            database.execSQL("CREATE TABLE IF NOT EXISTS sync_queue ("
                    + "operation_id TEXT PRIMARY KEY NOT NULL, "
                    + "user_id TEXT NOT NULL, "
                    + "entity_type TEXT NOT NULL, "
                    + "entity_id TEXT NOT NULL, "
                    + "operation_type TEXT NOT NULL, "
                    + "payload_json TEXT, "
                    + "created_at INTEGER NOT NULL, "
                    + "attempt_count INTEGER NOT NULL, "
                    + "last_attempt_at INTEGER, "
                    + "next_retry_at INTEGER, "
                    + "state TEXT NOT NULL, "
                    + "error_category TEXT)");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS captures ("
                    + "id TEXT PRIMARY KEY NOT NULL, "
                    + "user_id TEXT NOT NULL, "
                    + "source_type TEXT NOT NULL, "
                    + "original_text TEXT, "
                    + "original_url TEXT, "
                    + "canonical_url TEXT, "
                    + "source_domain TEXT, "
                    + "idempotency_key TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "backend_job_id TEXT, "
                    + "created_at INTEGER NOT NULL, "
                    + "updated_at INTEGER NOT NULL)");
            database.execSQL("CREATE INDEX IF NOT EXISTS idx_captures_user_id ON captures(user_id)");
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS idx_captures_user_id_idempotency_key ON captures(user_id, idempotency_key)");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE reminders ADD COLUMN notes TEXT");
            database.execSQL("ALTER TABLE reminders ADD COLUMN target_timezone TEXT NOT NULL DEFAULT 'UTC'");
            database.execSQL("ALTER TABLE reminders ADD COLUMN snooze_until INTEGER");
            database.execSQL("ALTER TABLE reminders ADD COLUMN snooze_count INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE reminders ADD COLUMN smart_reason TEXT");
            database.execSQL("ALTER TABLE reminders ADD COLUMN opportunity_title TEXT");

            database.execSQL("CREATE TABLE IF NOT EXISTS notifications ("
                    + "id TEXT PRIMARY KEY NOT NULL, "
                    + "user_id TEXT NOT NULL, "
                    + "reminder_id TEXT, "
                    + "opportunity_id TEXT, "
                    + "title TEXT NOT NULL, "
                    + "body TEXT NOT NULL, "
                    + "channel TEXT NOT NULL, "
                    + "provider TEXT NOT NULL, "
                    + "delivery_status TEXT NOT NULL, "
                    + "deep_link_uri TEXT, "
                    + "metadata_json TEXT, "
                    + "opened_at INTEGER, "
                    + "created_at INTEGER NOT NULL)");
            database.execSQL("CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id)");
        }
    };

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
