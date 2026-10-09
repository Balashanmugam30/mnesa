package com.mnesa.android.data.repository;

import com.mnesa.android.data.local.dao.ReminderDao;
import com.mnesa.android.data.local.entity.ReminderEntity;
import com.mnesa.android.data.remote.api.ReminderApiService;
import com.mnesa.android.data.remote.dto.CreateReminderRequestDto;
import com.mnesa.android.data.remote.dto.ReminderDto;
import com.mnesa.android.data.remote.dto.ReminderSuggestionDto;
import com.mnesa.android.data.remote.dto.SnoozeReminderRequestDto;
import com.mnesa.android.data.sample.SampleDataProvider;
import com.mnesa.android.domain.model.Reminder;
import com.mnesa.android.domain.repository.ReminderRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of ReminderRepository managing offline Room persistence
 * and synchronization with the MNESA backend scheduling engine.
 */
public class ReminderRepositoryImpl implements ReminderRepository {

    private final ReminderDao reminderDao;
    private final ReminderApiService reminderApiService;
    private static final String DEFAULT_USER_ID = "default_user";

    public ReminderRepositoryImpl(ReminderDao reminderDao) {
        this(reminderDao, null);
    }

    public ReminderRepositoryImpl(ReminderDao reminderDao, ReminderApiService reminderApiService) {
        this.reminderDao = reminderDao;
        this.reminderApiService = reminderApiService;
    }

    @Override
    public Flowable<List<Reminder>> getAllReminders(String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.getAllReminders(effectiveUserId).map(this::mapEntitiesToDomain);
    }

    @Override
    public Flowable<List<Reminder>> getRemindersFiltered(String userId, String filter) {
        String effectiveUserId = resolveUserId(userId);
        String normFilter = filter != null ? filter.toLowerCase().trim() : "upcoming";

        switch (normFilter) {
            case "snoozed":
                return reminderDao.getRemindersByStatus(effectiveUserId, "SNOOZED").map(this::mapEntitiesToDomain);
            case "history":
            case "completed":
                return reminderDao.getRemindersByStatusIn(effectiveUserId, Arrays.asList("SENT", "DISMISSED", "CANCELLED"))
                        .map(this::mapEntitiesToDomain);
            case "needs_attention":
                long horizon = System.currentTimeMillis() + (24 * 60 * 60 * 1000L);
                return reminderDao.getRemindersByStatusIn(effectiveUserId, Arrays.asList("SCHEDULED", "SNOOZED"))
                        .map(entities -> {
                            List<ReminderEntity> urgent = new ArrayList<>();
                            for (ReminderEntity e : entities) {
                                long trigger = e.getSnoozeUntil() != null && e.getSnoozeUntil() > 0 ? e.getSnoozeUntil() : e.getTriggerTimestamp();
                                if (trigger <= horizon) {
                                    urgent.add(e);
                                }
                            }
                            return mapEntitiesToDomain(urgent);
                        });
            case "all":
                return reminderDao.getAllReminders(effectiveUserId).map(this::mapEntitiesToDomain);
            case "upcoming":
            default:
                return reminderDao.getRemindersByStatusIn(effectiveUserId, Arrays.asList("SCHEDULED", "SNOOZED"))
                        .map(this::mapEntitiesToDomain);
        }
    }

    @Override
    public Flowable<List<Reminder>> getRemindersForOpportunity(String oppId) {
        return getRemindersForOpportunity(oppId, DEFAULT_USER_ID);
    }

    @Override
    public Flowable<List<Reminder>> getRemindersForOpportunity(String oppId, String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.getRemindersForOpportunity(oppId, effectiveUserId).map(this::mapEntitiesToDomain);
    }

    @Override
    public Single<Reminder> getReminderById(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        return reminderDao.getReminderById(id, effectiveUserId).map(this::mapEntityToDomain);
    }

    @Override
    public Completable saveReminder(Reminder reminder) {
        return reminderDao.insertReminder(mapDomainToEntity(reminder));
    }

    @Override
    public Completable deleteReminder(String id, String userId) {
        String effectiveUserId = resolveUserId(userId);
        Completable local = reminderDao.deleteReminderById(id, effectiveUserId);
        if (reminderApiService != null) {
            return local.andThen(reminderApiService.deleteReminder(id).onErrorComplete());
        }
        return local;
    }

    @Override
    public Completable snoozeReminder(String id, int minutes, String userId) {
        long snoozeUntil = System.currentTimeMillis() + (minutes * 60 * 1000L);
        Completable local = reminderDao.snoozeReminder(id, snoozeUntil, 1, "SNOOZED");

        if (reminderApiService != null) {
            SnoozeReminderRequestDto request = new SnoozeReminderRequestDto(minutes, null);
            return local.andThen(reminderApiService.snoozeReminder(id, request).ignoreElement().onErrorComplete());
        }
        return local;
    }

    @Override
    public Completable dismissReminder(String id, String userId) {
        Completable local = reminderDao.updateStatus(id, "DISMISSED");
        if (reminderApiService != null) {
            return local.andThen(reminderApiService.dismissReminder(id).ignoreElement().onErrorComplete());
        }
        return local;
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

    @Override
    public Completable syncReminders(String userId) {
        if (reminderApiService == null) {
            return Completable.complete();
        }
        return reminderApiService.getReminders("all", 0, 100)
                .flatMapCompletable(resp -> {
                    if (resp.isSuccess() && resp.getData() != null && resp.getData().getContent() != null) {
                        List<ReminderEntity> entities = new ArrayList<>();
                        for (ReminderDto dto : resp.getData().getContent()) {
                            entities.add(mapDtoToEntity(dto));
                        }
                        return reminderDao.insertAll(entities);
                    }
                    return Completable.complete();
                })
                .onErrorComplete();
    }

    @Override
    public Single<Reminder> createReminderRemote(String opportunityId, CreateReminderRequestDto request, String userId) {
        if (reminderApiService == null) {
            // Local fallback
            long trigger = System.currentTimeMillis() + (24 * 60 * 60 * 1000L);
            Reminder r = new Reminder(
                    java.util.UUID.randomUUID().toString(),
                    opportunityId,
                    userId,
                    request.getTitle(),
                    trigger,
                    request.getReminderType() != null ? request.getReminderType() : "CUSTOM",
                    "SCHEDULED",
                    System.currentTimeMillis(),
                    request.getNotes(),
                    request.getTargetTimezone() != null ? request.getTargetTimezone() : "UTC",
                    null,
                    0,
                    request.getSmartReason(),
                    null
            );
            return saveReminder(r).toSingleDefault(r);
        }

        return reminderApiService.createReminder(opportunityId, request)
                .map(resp -> {
                    if (!resp.isSuccess() || resp.getData() == null) {
                        throw new RuntimeException(resp.getMessage() != null ? resp.getMessage() : "Failed to create reminder");
                    }
                    ReminderEntity entity = mapDtoToEntity(resp.getData());
                    reminderDao.insertReminder(entity).blockingAwait();
                    return mapEntityToDomain(entity);
                });
    }

    @Override
    public Single<List<ReminderSuggestionDto>> getReminderSuggestions(String opportunityId) {
        if (reminderApiService == null) {
            return Single.just(Collections.<ReminderSuggestionDto>emptyList());
        }
        return reminderApiService.getReminderSuggestions(opportunityId)
                .map(resp -> (resp.isSuccess() && resp.getData() != null) ? resp.getData() : Collections.<ReminderSuggestionDto>emptyList())
                .onErrorReturnItem(Collections.<ReminderSuggestionDto>emptyList());
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
                entity.getCreatedAt(),
                entity.getNotes(),
                entity.getTargetTimezone(),
                entity.getSnoozeUntil(),
                entity.getSnoozeCount(),
                entity.getSmartReason(),
                entity.getOpportunityTitle()
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
                domain.getCreatedAt(),
                domain.getNotes(),
                domain.getTargetTimezone(),
                domain.getSnoozeUntil(),
                domain.getSnoozeCount(),
                domain.getSmartReason(),
                domain.getOpportunityTitle()
        );
    }

    private ReminderEntity mapDtoToEntity(ReminderDto dto) {
        long trigger = System.currentTimeMillis();
        if (dto.getScheduledAt() != null) {
            try {
                trigger = Instant.parse(dto.getScheduledAt()).toEpochMilli();
            } catch (Exception ignored) {}
        }

        Long snooze = null;
        if (dto.getSnoozeUntil() != null) {
            try {
                snooze = Instant.parse(dto.getSnoozeUntil()).toEpochMilli();
            } catch (Exception ignored) {}
        }

        long created = System.currentTimeMillis();
        if (dto.getCreatedAt() != null) {
            try {
                created = Instant.parse(dto.getCreatedAt()).toEpochMilli();
            } catch (Exception ignored) {}
        }

        return new ReminderEntity(
                dto.getId(),
                dto.getOpportunityId() != null ? dto.getOpportunityId() : "",
                dto.getUserId() != null ? dto.getUserId() : "",
                dto.getTitle() != null ? dto.getTitle() : "Reminder",
                trigger,
                dto.getReminderType() != null ? dto.getReminderType() : "CUSTOM",
                dto.getStatus() != null ? dto.getStatus() : "SCHEDULED",
                created,
                dto.getNotes(),
                dto.getTargetTimezone() != null ? dto.getTargetTimezone() : "UTC",
                snooze,
                dto.getSnoozeCount(),
                dto.getSmartReason(),
                dto.getOpportunityTitle()
        );
    }
}
