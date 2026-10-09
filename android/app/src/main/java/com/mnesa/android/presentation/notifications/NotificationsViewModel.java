package com.mnesa.android.presentation.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.NotificationItem;
import com.mnesa.android.domain.repository.NotificationRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

/**
 * ViewModel powering the Notifications inbox activity with unread badge updates and deep linking.
 */
public class NotificationsViewModel extends BaseViewModel {

    private final NotificationRepository notificationRepository;
    private final String userId;

    private final MutableLiveData<List<NotificationItem>> notificationsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> unreadCountLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public NotificationsViewModel(NotificationRepository notificationRepository, String userId) {
        this.notificationRepository = notificationRepository;
        this.userId = (userId != null && !userId.trim().isEmpty()) ? userId : "default_user";
        observeNotifications();
        observeUnreadCount();
        sync();
    }

    public LiveData<List<NotificationItem>> getNotifications() {
        return notificationsLiveData;
    }

    public LiveData<Integer> getUnreadCount() {
        return unreadCountLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public void observeNotifications() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                notificationRepository.getNotifications(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                list -> {
                                    isLoadingLiveData.setValue(false);
                                    notificationsLiveData.setValue(list);
                                },
                                throwable -> {
                                    isLoadingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void observeUnreadCount() {
        addDisposable(
                notificationRepository.getUnreadCount(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                unreadCountLiveData::setValue,
                                throwable -> unreadCountLiveData.setValue(0)
                        )
        );
    }

    public void markOpened(String id) {
        addDisposable(
                notificationRepository.markOpened(id, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::observeUnreadCount, throwable -> errorMessageLiveData.setValue(throwable.getMessage()))
        );
    }

    public void markAllOpened() {
        addDisposable(
                notificationRepository.markAllOpened(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::observeUnreadCount, throwable -> errorMessageLiveData.setValue(throwable.getMessage()))
        );
    }

    public void sync() {
        addDisposable(
                notificationRepository.syncNotifications(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::observeUnreadCount, throwable -> {})
        );
    }
}
