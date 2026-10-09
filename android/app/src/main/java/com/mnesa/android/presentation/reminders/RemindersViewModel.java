package com.mnesa.android.presentation.reminders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.Reminder;
import com.mnesa.android.domain.repository.ReminderRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

/**
 * ViewModel powering the Reminders tab with filtering, snoozing, and sync.
 */
public class RemindersViewModel extends BaseViewModel {

    private final ReminderRepository reminderRepository;
    private final String userId;

    private String currentFilter = "upcoming";

    private final MutableLiveData<List<Reminder>> remindersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> activeFilterLiveData = new MutableLiveData<>("upcoming");

    public RemindersViewModel(ReminderRepository reminderRepository, String userId) {
        this.reminderRepository = reminderRepository;
        this.userId = (userId != null && !userId.trim().isEmpty()) ? userId : "default_user";
        loadReminders();
    }

    public LiveData<List<Reminder>> getReminders() {
        return remindersLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public LiveData<String> getActiveFilter() {
        return activeFilterLiveData;
    }

    public void setFilter(String filter) {
        this.currentFilter = filter != null ? filter : "upcoming";
        this.activeFilterLiveData.setValue(this.currentFilter);
        loadReminders();
    }

    public void loadReminders() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                reminderRepository.getRemindersFiltered(userId, currentFilter)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                list -> {
                                    isLoadingLiveData.setValue(false);
                                    remindersLiveData.setValue(list);
                                },
                                throwable -> {
                                    isLoadingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void snoozeReminder(String reminderId, int minutes) {
        addDisposable(
                reminderRepository.snoozeReminder(reminderId, minutes, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadReminders, throwable -> errorMessageLiveData.setValue(throwable.getMessage()))
        );
    }

    public void dismissReminder(String reminderId) {
        addDisposable(
                reminderRepository.dismissReminder(reminderId, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadReminders, throwable -> errorMessageLiveData.setValue(throwable.getMessage()))
        );
    }

    public void deleteReminder(String reminderId) {
        addDisposable(
                reminderRepository.deleteReminder(reminderId, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadReminders, throwable -> errorMessageLiveData.setValue(throwable.getMessage()))
        );
    }

    public void sync() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                reminderRepository.syncReminders(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadReminders, throwable -> {
                            isLoadingLiveData.setValue(false);
                            loadReminders();
                        })
        );
    }

    public void seedSampleReminders() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                reminderRepository.seedSampleReminders(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadReminders, throwable -> {
                            isLoadingLiveData.setValue(false);
                            errorMessageLiveData.setValue(throwable.getMessage());
                        })
        );
    }
}
