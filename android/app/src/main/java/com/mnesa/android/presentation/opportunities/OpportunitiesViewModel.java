package com.mnesa.android.presentation.opportunities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

/**
 * ViewModel powering the All Opportunities browsing screen.
 */
public class OpportunitiesViewModel extends BaseViewModel {

    private final OpportunityRepository opportunityRepository;
    private final String userId;

    private final MutableLiveData<List<Opportunity>> opportunitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> activeFilterLiveData = new MutableLiveData<>("ALL");

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

    public void setCategoryFilter(String category) {
        activeFilterLiveData.setValue(category);
        loadOpportunities(category);
    }

    public void loadOpportunities(String category) {
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        addDisposable(
                opportunityRepository.getOpportunitiesByCategory(userId, category)
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
    }

    public void seedSampleData() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                opportunityRepository.seedSampleData(userId)
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
    }

    public void deleteOpportunity(String id) {
        addDisposable(
                opportunityRepository.deleteOpportunity(id, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {}, throwable -> {})
        );
    }
}
