package com.mnesa.android.data.repository;

import com.mnesa.android.data.local.dao.ReminderDao;
import com.mnesa.android.data.local.entity.ReminderEntity;
import com.mnesa.android.data.sample.SampleDataProvider;
import com.mnesa.android.domain.model.Reminder;
import com.mnesa.android.domain.repository.ReminderRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ReminderRepository managing local reminders in Room.
 */
public class ReminderRepositoryImpl implements ReminderRepository {

    private final ReminderDao reminderDao;
    private static final String DEFAULT_USER_ID = "default_user";

    public ReminderRepositoryImpl(ReminderDao reminderDao) {
        this.reminderDao = reminderDao;
    }

    @Override
    public Flowable<List<Reminder>> getAllReminders(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.getAllReminders(effectiveUserId).map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Reminder>> getRemindersForOpportunity(String oppId, String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.getRemindersForOpportunity(oppId, effectiveUserId).map(this::mapEntitiesToDomain);
    }

    @Override
    public Completable saveReminder(Reminder reminder) {
        return reminderDao.insertReminder(mapDomainToEntity(reminder));
    }

    @Override
    public Completable deleteReminder(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.deleteReminderById(id, effectiveUserId);
    }

    @Override
    public Completable clearRemindersForUser(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.deleteAllForUser(effectiveUserId);
    }

    @Override
    public Single<Integer> getScheduledCount(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.countScheduledForUser(effectiveUserId);
    }

    @Override
    public Completable seedSampleReminders(String userId) {
        String effectiveUserId = resolveUserId(userId);
        List<Reminder> sampleReminders = SampleDataProvider.getSampleReminders(effectiveUserId);
        List<ReminderEntity> entities = new ArrayList<>();
        for (Reminder rem : sampleReminders) {
            entities.add(mapDomainToEntity(rem));
        }
        return reminderDao.insertAll(entities);
    }

    private String resolveUserId(String userId) {
        return (userId != null && !userId.trim().isEmpty()) ? userId : DEFAULT_USER_ID;
    }

    private List<Reminder> mapEntitiesToDomain(List<ReminderEntity> entities) {
        List<Reminder> result = new ArrayList<>();
        if (entities != null) {
            for (ReminderEntity entity : entities) {
                result.add(mapEntityToDomain(entity));
            }
        }
        return result;
    }

    private Reminder mapEntityToDomain(ReminderEntity entity) {
        return new Reminder(
                entity.getId(),
                entity.getOpportunityId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getTriggerTimestamp(),
                entity.getReminderType(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    private ReminderEntity mapDomainToEntity(Reminder domain) {
        return new ReminderEntity(
                domain.getId(),
                domain.getOpportunityId(),
                domain.getUserId(),
                domain.getTitle(),
                domain.getTriggerTimestamp(),
                domain.getReminderType(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }
}
