package com.mnesa.android.presentation.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.data.remote.dto.SuggestedActionDto;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.repository.OpportunityRepository;
import com.mnesa.android.domain.repository.ReminderRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel powering the Home dashboard screen with real metrics, urgency buckets,
 * and suggested follow-through actions.
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
    private final MutableLiveData<SuggestedActionDto> suggestedActionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Long>> statusCountsLiveData = new MutableLiveData<>(new HashMap<>());
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

    public LiveData<SuggestedActionDto> getSuggestedAction() {
        return suggestedActionLiveData;
    }

    public LiveData<Map<String, Long>> getStatusCounts() {
        return statusCountsLiveData;
    }

    public LiveData<Boolean> getIsEmpty() {
        return isEmptyLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public void loadDashboard() {
        isLoadingLiveData.setValue(true);

        // Stream all opportunities to determine overall empty state and count
        Flowable<List<Opportunity>> allFlowable = opportunityRepository.getAllOpportunities(userId);
        if (allFlowable != null) {
            addDisposable(
                    allFlowable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(list -> {
                                isLoadingLiveData.setValue(false);
                                boolean empty = (list == null || list.isEmpty());
                                isEmptyLiveData.setValue(empty);
                                countTrackedLiveData.setValue(list != null ? list.size() : 0);
                            }, throwable -> isLoadingLiveData.setValue(false))
            );
        }

        // Stream Needs Attention (< 7 days)
        Flowable<List<Opportunity>> needsAttentionFlowable = opportunityRepository.getNeedsAttention(userId);
        if (needsAttentionFlowable != null) {
            addDisposable(
                    needsAttentionFlowable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(list -> {
                                needsAttentionLiveData.setValue(list);
                                countUrgentLiveData.setValue(list != null ? list.size() : 0);
                            }, throwable -> {})
            );
        }

        // Stream Upcoming
        Flowable<List<Opportunity>> upcomingFlowable = opportunityRepository.getUpcoming(userId);
        if (upcomingFlowable != null) {
            addDisposable(
                    upcomingFlowable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(upcomingLiveData::setValue, throwable -> {})
            );
        }

        // Stream Recently Saved
        Flowable<List<Opportunity>> recentlySavedFlowable = opportunityRepository.getRecentlySaved(userId, 3);
        if (recentlySavedFlowable != null) {
            addDisposable(
                    recentlySavedFlowable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(recentlySavedLiveData::setValue, throwable -> {})
            );
        }

        // Stream Reminders Count
        Single<Integer> remindersCountSingle = reminderRepository.getScheduledCount(userId);
        if (remindersCountSingle != null) {
            addDisposable(
                    remindersCountSingle
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(countRemindersLiveData::setValue, throwable -> {})
            );
        }

        // Load Remote Home Dashboard (Suggested actions & Status Breakdown)
        Single<com.mnesa.android.data.remote.dto.HomeDashboardDto> dashboardSingle = opportunityRepository.getHomeDashboard();
        if (dashboardSingle != null) {
            addDisposable(
                    dashboardSingle
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(dashboard -> {
                                if (dashboard != null) {
                                    if (dashboard.getSuggestedAction() != null) {
                                        suggestedActionLiveData.setValue(dashboard.getSuggestedAction());
                                    }
                                    if (dashboard.getStatusCounts() != null) {
                                        statusCountsLiveData.setValue(dashboard.getStatusCounts());
                                    }
                                }
                            }, throwable -> {})
            );
        }
    }

    public void seedSampleData() {
        isLoadingLiveData.setValue(true);
        Completable seedOpp = opportunityRepository.seedSampleData(userId);
        Completable seedRem = reminderRepository.seedSampleReminders(userId);

        if (seedOpp != null && seedRem != null) {
            addDisposable(
                    seedOpp.andThen(seedRem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::loadDashboard, throwable -> isLoadingLiveData.setValue(false))
            );
        } else if (seedOpp != null) {
            addDisposable(
                    seedOpp.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::loadDashboard, throwable -> isLoadingLiveData.setValue(false))
            );
        } else {
            loadDashboard();
        }
    }

    public void clearData() {
        isLoadingLiveData.setValue(true);
        Completable clearOpp = opportunityRepository.clearDataForUser(userId);
        Completable clearRem = reminderRepository.clearRemindersForUser(userId);

        if (clearOpp != null && clearRem != null) {
            addDisposable(
                    clearOpp.andThen(clearRem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::loadDashboard, throwable -> isLoadingLiveData.setValue(false))
            );
        } else if (clearOpp != null) {
            addDisposable(
                    clearOpp.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::loadDashboard, throwable -> isLoadingLiveData.setValue(false))
            );
        } else {
            loadDashboard();
        }
    }
}
