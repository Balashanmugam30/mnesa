package com.mnesa.android.data.repository;

import com.mnesa.android.data.local.dao.NotificationDao;
import com.mnesa.android.data.local.entity.NotificationEntity;
import com.mnesa.android.data.remote.api.ReminderApiService;
import com.mnesa.android.data.remote.dto.NotificationRecordDto;
import com.mnesa.android.domain.model.NotificationItem;
import com.mnesa.android.domain.repository.NotificationRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationDao notificationDao;
    private final ReminderApiService reminderApiService;

    public NotificationRepositoryImpl(NotificationDao notificationDao) {
        this(notificationDao, null);
    }

    public NotificationRepositoryImpl(NotificationDao notificationDao, ReminderApiService reminderApiService) {
        this.notificationDao = notificationDao;
        this.reminderApiService = reminderApiService;
    }

    @Override
    public Flowable<List<NotificationItem>> getNotifications(String userId) {
        return notificationDao.getAllNotifications(userId).map(this::mapEntitiesToDomain);
    }

    @Override
    public Single<Integer> getUnreadCount(String userId) {
        return notificationDao.countUnread(userId);
    }

    @Override
    public Completable markOpened(String id, String userId) {
        long now = System.currentTimeMillis();
        Completable local = notificationDao.markOpened(id, now);
        if (reminderApiService != null) {
            return local.andThen(reminderApiService.markNotificationOpened(id).ignoreElement().onErrorComplete());
        }
        return local;
    }

    @Override
    public Completable markAllOpened(String userId) {
        long now = System.currentTimeMillis();
        return notificationDao.markAllOpened(userId, now);
    }

    @Override
    public Completable syncNotifications(String userId) {
        if (reminderApiService == null) {
            return Completable.complete();
        }
        return reminderApiService.getNotifications(0, 50)
                .flatMapCompletable(resp -> {
                    if (resp.isSuccess() && resp.getData() != null && resp.getData().getContent() != null) {
                        List<NotificationEntity> entities = new ArrayList<>();
                        for (NotificationRecordDto dto : resp.getData().getContent()) {
                            entities.add(mapDtoToEntity(dto));
                        }
                        return notificationDao.insertAll(entities);
                    }
                    return Completable.complete();
                })
                .onErrorComplete();
    }

    @Override
    public Completable saveNotification(NotificationItem item) {
        return notificationDao.insertNotification(mapDomainToEntity(item));
    }

    private List<NotificationItem> mapEntitiesToDomain(List<NotificationEntity> entities) {
        List<NotificationItem> result = new ArrayList<>();
        if (entities != null) {
            for (NotificationEntity e : entities) {
                result.add(new NotificationItem(
                        e.getId(),
                        e.getUserId(),
                        e.getReminderId(),
                        e.getOpportunityId(),
                        e.getTitle(),
                        e.getBody(),
                        e.getChannel(),
                        e.getProvider(),
                        e.getDeliveryStatus(),
                        e.getDeepLinkUri(),
                        e.getOpenedAt(),
                        e.getCreatedAt()
                ));
            }
        }
        return result;
    }

    private NotificationEntity mapDomainToEntity(NotificationItem item) {
        return new NotificationEntity(
                item.getId(),
                item.getUserId(),
                item.getReminderId(),
                item.getOpportunityId(),
                item.getTitle(),
                item.getBody(),
                item.getChannel(),
                item.getProvider(),
                item.getDeliveryStatus(),
                item.getDeepLinkUri(),
                null,
                item.getOpenedAt(),
                item.getCreatedAt()
        );
    }

    private NotificationEntity mapDtoToEntity(NotificationRecordDto dto) {
        Long opened = null;
        if (dto.getOpenedAt() != null) {
            try {
                opened = Instant.parse(dto.getOpenedAt()).toEpochMilli();
            } catch (Exception ignored) {}
        }

        long created = System.currentTimeMillis();
        if (dto.getCreatedAt() != null) {
            try {
                created = Instant.parse(dto.getCreatedAt()).toEpochMilli();
            } catch (Exception ignored) {}
        }

        return new NotificationEntity(
                dto.getId(),
                dto.getUserId(),
                dto.getReminderId(),
                dto.getOpportunityId(),
                dto.getTitle() != null ? dto.getTitle() : "Notification",
                dto.getBody() != null ? dto.getBody() : "",
                dto.getChannel() != null ? dto.getChannel() : "PUSH",
                dto.getProvider() != null ? dto.getProvider() : "MOCK_DEVELOPMENT",
                dto.getDeliveryStatus() != null ? dto.getDeliveryStatus() : "DELIVERED",
                dto.getDeepLinkUri(),
                null,
                opened,
                created
        );
    }
}
