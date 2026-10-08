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
 * ViewModel powering the Reminders tab.
 */
public class RemindersViewModel extends BaseViewModel {

    private final ReminderRepository reminderRepository;
    private final String userId;

    private final MutableLiveData<List<Reminder>> remindersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

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

    public void loadReminders() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                reminderRepository.getAllReminders(userId)
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
