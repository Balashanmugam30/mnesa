package com.mnesa.android.presentation.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.repository.OpportunityRepository;
import com.mnesa.android.domain.repository.ReminderRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

/**
 * ViewModel powering the Home dashboard screen.
 */
public class HomeViewModel extends BaseViewModel {

    private final OpportunityRepository opportunityRepository;
    private final ReminderRepository reminderRepository;
    private final String userId;

    private final MutableLiveData<List<Opportunity>> needsAttentionLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Opportunity>> upcomingLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Opportunity>> recentlySavedLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> countTrackedLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> countUrgentLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> countRemindersLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isEmptyLiveData = new MutableLiveData<>(true);
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);

    public HomeViewModel(OpportunityRepository opportunityRepository,
                         ReminderRepository reminderRepository,
                         String userId) {
        this.opportunityRepository = opportunityRepository;
        this.reminderRepository = reminderRepository;
        this.userId = (userId != null && !userId.trim().isEmpty()) ? userId : "default_user";
        loadDashboard();
    }

    public LiveData<List<Opportunity>> getNeedsAttention() {
        return needsAttentionLiveData;
    }

    public LiveData<List<Opportunity>> getUpcoming() {
        return upcomingLiveData;
    }

    public LiveData<List<Opportunity>> getRecentlySaved() {
        return recentlySavedLiveData;
    }

    public LiveData<Integer> getCountTracked() {
        return countTrackedLiveData;
    }

    public LiveData<Integer> getCountUrgent() {
        return countUrgentLiveData;
    }

    public LiveData<Integer> getCountReminders() {
        return countRemindersLiveData;
    }

    public LiveData<Boolean> getIsEmpty() {
        return isEmptyLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void loadDashboard() {
        isLoadingLiveData.setValue(true);

        // Stream all opportunities to determine overall empty state and recent saves
        addDisposable(
                opportunityRepository.getAllOpportunities(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            isLoadingLiveData.setValue(false);
                            boolean empty = (list == null || list.isEmpty());
                            isEmptyLiveData.setValue(empty);
                            countTrackedLiveData.setValue(list != null ? list.size() : 0);
                        }, throwable -> isLoadingLiveData.setValue(false))
        );

        // Stream Needs Attention (< 7 days)
        addDisposable(
                opportunityRepository.getNeedsAttention(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(list -> {
                            needsAttentionLiveData.setValue(list);
                            countUrgentLiveData.setValue(list != null ? list.size() : 0);
                        }, throwable -> {})
        );

        // Stream Upcoming
        addDisposable(
                opportunityRepository.getUpcoming(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(upcomingLiveData::setValue, throwable -> {})
        );

        // Stream Recently Saved
        addDisposable(
                opportunityRepository.getRecentlySaved(userId, 3)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(recentlySavedLiveData::setValue, throwable -> {})
        );

        // Stream Reminders Count
        addDisposable(
                reminderRepository.getScheduledCount(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(countRemindersLiveData::setValue, throwable -> {})
        );
    }

    public void seedSampleData() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                opportunityRepository.seedSampleData(userId)
                        .andThen(reminderRepository.seedSampleReminders(userId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadDashboard, throwable -> isLoadingLiveData.setValue(false))
        );
    }

    public void clearData() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                opportunityRepository.clearDataForUser(userId)
                        .andThen(reminderRepository.clearRemindersForUser(userId))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::loadDashboard, throwable -> isLoadingLiveData.setValue(false))
        );
    }
}
