package com.mnesa.android.presentation.opportunities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.data.remote.dto.OpportunityActivityDto;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

/**
 * ViewModel for viewing details, audit timeline, and managing lifecycle state of an opportunity.
 */
public class OpportunityDetailViewModel extends BaseViewModel {

    private final OpportunityRepository opportunityRepository;
    private final String userId;

    private final MutableLiveData<Opportunity> opportunityLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<OpportunityActivityDto>> historyLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> actionSuccessLiveData = new MutableLiveData<>();

    public OpportunityDetailViewModel(OpportunityRepository opportunityRepository, String userId) {
        this.opportunityRepository = opportunityRepository;
        this.userId = (userId != null && !userId.trim().isEmpty()) ? userId : "default_user";
    }

    public LiveData<Opportunity> getOpportunity() {
        return opportunityLiveData;
    }

    public LiveData<List<OpportunityActivityDto>> getHistory() {
        return historyLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public LiveData<String> getActionSuccess() {
        return actionSuccessLiveData;
    }

    public void loadOpportunity(String id) {
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        addDisposable(
                opportunityRepository.getOpportunityById(id, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                opp -> {
                                    isLoadingLiveData.setValue(false);
                                    opportunityLiveData.setValue(opp);
                                    loadHistory(id);
                                },
                                throwable -> {
                                    isLoadingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void loadHistory(String id) {
        addDisposable(
                opportunityRepository.getOpportunityHistory(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                historyLiveData::setValue,
                                throwable -> {}
                        )
        );
    }

    public void updateStatus(String newStatus, String comment) {
        Opportunity current = opportunityLiveData.getValue();
        if (current == null) return;

        isLoadingLiveData.setValue(true);
        addDisposable(
                opportunityRepository.updateStatus(current.getId(), newStatus, comment, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    actionSuccessLiveData.setValue("Status updated to " + newStatus);
                                    loadOpportunity(current.getId());
                                },
                                throwable -> {
                                    isLoadingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void archiveOpportunity() {
        Opportunity current = opportunityLiveData.getValue();
        if (current == null) return;

        isLoadingLiveData.setValue(true);
        addDisposable(
                opportunityRepository.archiveOpportunity(current.getId(), userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    actionSuccessLiveData.setValue("Opportunity archived");
                                    loadOpportunity(current.getId());
                                },
                                throwable -> {
                                    isLoadingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void restoreOpportunity() {
        Opportunity current = opportunityLiveData.getValue();
        if (current == null) return;

        isLoadingLiveData.setValue(true);
        addDisposable(
                opportunityRepository.restoreOpportunity(current.getId(), userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    actionSuccessLiveData.setValue("Opportunity restored");
                                    loadOpportunity(current.getId());
                                },
                                throwable -> {
                                    isLoadingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void deleteOpportunity() {
        Opportunity current = opportunityLiveData.getValue();
        if (current == null) return;

        isLoadingLiveData.setValue(true);
        addDisposable(
                opportunityRepository.deleteOpportunity(current.getId(), userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> actionSuccessLiveData.setValue("Opportunity deleted"),
                                throwable -> {
                                    isLoadingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }
}
