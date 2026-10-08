package com.mnesa.android.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.mnesa.android.data.local.dao.OpportunityDao;
import com.mnesa.android.data.local.entity.OpportunityEntity;

/**
 * Main Room Database for MNESA offline cache and Single Source of Truth.
 */
@Database(entities = {OpportunityEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "mnesa_database";
    private static volatile AppDatabase INSTANCE;

    public abstract OpportunityDao opportunityDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
