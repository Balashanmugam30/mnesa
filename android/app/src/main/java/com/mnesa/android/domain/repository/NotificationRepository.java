package com.mnesa.android.domain.repository;

import com.mnesa.android.domain.model.NotificationItem;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface NotificationRepository {

    Flowable<List<NotificationItem>> getNotifications(String userId);

    Single<Integer> getUnreadCount(String userId);

    Completable markOpened(String id, String userId);

    Completable markAllOpened(String userId);

    Completable syncNotifications(String userId);

    Completable saveNotification(NotificationItem item);
}
