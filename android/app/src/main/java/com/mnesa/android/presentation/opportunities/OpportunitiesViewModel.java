package com.mnesa.android.presentation.opportunities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

/**
 * ViewModel powering the All Opportunities browsing screen with search,
 * category & status filters, and lifecycle transitions.
 */
public class OpportunitiesViewModel extends BaseViewModel {

    private final OpportunityRepository opportunityRepository;
    private final String userId;

    private final MutableLiveData<List<Opportunity>> opportunitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> activeFilterLiveData = new MutableLiveData<>("ALL");
    private final MutableLiveData<String> activeStatusLiveData = new MutableLiveData<>("ALL");
    private final MutableLiveData<String> searchQueryLiveData = new MutableLiveData<>("");

    public OpportunitiesViewModel(OpportunityRepository opportunityRepository, String userId) {
        this.opportunityRepository = opportunityRepository;
        this.userId = (userId != null && !userId.trim().isEmpty()) ? userId : "default_user";
        loadOpportunities("ALL");
    }

    public LiveData<List<Opportunity>> getOpportunities() {
        return opportunitiesLiveData;
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

    public LiveData<String> getActiveStatus() {
        return activeStatusLiveData;
    }

    public LiveData<String> getSearchQuery() {
        return searchQueryLiveData;
    }

    public void setCategoryFilter(String category) {
        activeFilterLiveData.setValue(category);
        String currentQuery = searchQueryLiveData.getValue();
        String currentStatus = activeStatusLiveData.getValue();

        if ((currentQuery == null || currentQuery.isEmpty()) && ("ALL".equalsIgnoreCase(currentStatus) || currentStatus == null)) {
            loadOpportunities(category);
        } else {
            applyFilters();
        }
    }

    public void setStatusFilter(String status) {
        activeStatusLiveData.setValue(status);
        applyFilters();
    }

    public void setSearchQuery(String query) {
        searchQueryLiveData.setValue(query);
        applyFilters();
    }

    public void applyFilters() {
        String query = searchQueryLiveData.getValue();
        String category = activeFilterLiveData.getValue();
        String status = activeStatusLiveData.getValue();
        boolean isArchivedTab = "ARCHIVED".equalsIgnoreCase(status);

        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        Flowable<List<Opportunity>> flowable = opportunityRepository.filterOpportunities(userId, query, category, status, isArchivedTab);
        if (flowable == null) {
            flowable = opportunityRepository.getOpportunitiesByCategory(userId, category);
        }

        if (flowable != null) {
            addDisposable(
                    flowable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    list -> {
                                        isLoadingLiveData.setValue(false);
                                        opportunitiesLiveData.setValue(list);
                                    },
                                    throwable -> {
                                        isLoadingLiveData.setValue(false);
                                        errorMessageLiveData.setValue(throwable.getMessage());
                                    }
                            )
            );
        } else {
            isLoadingLiveData.setValue(false);
        }
    }

    public void loadOpportunities(String category) {
        activeFilterLiveData.setValue(category);
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        Flowable<List<Opportunity>> flowable = opportunityRepository.getOpportunitiesByCategory(userId, category);
        if (flowable != null) {
            addDisposable(
                    flowable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    list -> {
                                        isLoadingLiveData.setValue(false);
                                        opportunitiesLiveData.setValue(list);
                                    },
                                    throwable -> {
                                        isLoadingLiveData.setValue(false);
                                        errorMessageLiveData.setValue(throwable.getMessage());
                                    }
                            )
            );
        } else {
            isLoadingLiveData.setValue(false);
        }
    }

    public void refreshFromServer() {
        isLoadingLiveData.setValue(true);
        Completable refreshCompletable = opportunityRepository.refreshOpportunities(userId);
        if (refreshCompletable != null) {
            addDisposable(
                    refreshCompletable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    this::applyFilters,
                                    throwable -> {
                                        isLoadingLiveData.setValue(false);
                                        errorMessageLiveData.setValue(throwable.getMessage());
                                    }
                            )
            );
        } else {
            isLoadingLiveData.setValue(false);
        }
    }

    public void seedSampleData() {
        isLoadingLiveData.setValue(true);
        Completable seedCompletable = opportunityRepository.seedSampleData(userId);
        if (seedCompletable != null) {
            addDisposable(
                    seedCompletable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> loadOpportunities(activeFilterLiveData.getValue()),
                                    throwable -> {
                                        isLoadingLiveData.setValue(false);
                                        errorMessageLiveData.setValue(throwable.getMessage());
                                    }
                            )
            );
        } else {
            isLoadingLiveData.setValue(false);
        }
    }

    public void deleteOpportunity(String id) {
        Completable delCompletable = opportunityRepository.deleteOpportunity(id, userId);
        if (delCompletable != null) {
            addDisposable(
                    delCompletable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::applyFilters, throwable -> {})
            );
        }
    }

    public void archiveOpportunity(String id) {
        Completable arcCompletable = opportunityRepository.archiveOpportunity(id, userId);
        if (arcCompletable != null) {
            addDisposable(
                    arcCompletable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::applyFilters, throwable -> {})
            );
        }
    }

    public void restoreOpportunity(String id) {
        Completable resCompletable = opportunityRepository.restoreOpportunity(id, userId);
        if (resCompletable != null) {
            addDisposable(
                    resCompletable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(this::applyFilters, throwable -> {})
            );
        }
    }
}
