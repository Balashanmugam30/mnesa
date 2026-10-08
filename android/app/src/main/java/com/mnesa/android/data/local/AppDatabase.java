package com.mnesa.android.data.local;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.dao.ReminderDao;
import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.OpportunityEntity;
import com.mnesa.android.data.local.entity.ReminderEntity;
import com.mnesa.android.data.local.entity.SyncQueueEntity;

/**
 * Main Room Database for MNESA offline cache and Single Source of Truth.
 * Version 2 introduces strict user scoping, reminders, and durable sync queue.
 */
@Database(
        entities = {OpportunityEntity.class, ReminderEntity.class, SyncQueueEntity.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "mnesa_database";
    private static volatile AppDatabase INSTANCE;

    public abstract OpportunityDao opportunityDao();
    public abstract ReminderDao reminderDao();
    public abstract SyncQueueDao syncQueueDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Update opportunities table with new columns for Phase 03
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

            // Create reminders table
            database.execSQL("CREATE TABLE IF NOT EXISTS reminders ("
                    + "id TEXT PRIMARY KEY NOT NULL, "
                    + "opportunity_id TEXT NOT NULL, "
                    + "user_id TEXT NOT NULL, "
                    + "title TEXT NOT NULL, "
                    + "trigger_timestamp INTEGER NOT NULL, "
                    + "reminder_type TEXT NOT NULL, "
                    + "status TEXT NOT NULL, "
                    + "created_at INTEGER NOT NULL)");

            // Create sync_queue table
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

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
