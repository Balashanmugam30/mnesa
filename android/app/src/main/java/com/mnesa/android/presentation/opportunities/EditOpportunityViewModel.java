package com.mnesa.android.presentation.opportunities;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.data.remote.dto.CreateOpportunityRequestDto;
import com.mnesa.android.data.remote.dto.UpdateOpportunityRequestDto;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel for creating and updating opportunities.
 */
public class EditOpportunityViewModel extends BaseViewModel {

    private final OpportunityRepository opportunityRepository;
    private final String userId;

    private final MutableLiveData<Opportunity> opportunityLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSavingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> saveSuccessLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public EditOpportunityViewModel(OpportunityRepository opportunityRepository, String userId) {
        this.opportunityRepository = opportunityRepository;
        this.userId = (userId != null && !userId.trim().isEmpty()) ? userId : "default_user";
    }

    public LiveData<Opportunity> getOpportunity() {
        return opportunityLiveData;
    }

    public LiveData<Boolean> getIsSaving() {
        return isSavingLiveData;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccessLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public void loadForEdit(String id) {
        isSavingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        addDisposable(
                opportunityRepository.getOpportunityById(id, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                opp -> {
                                    isSavingLiveData.setValue(false);
                                    opportunityLiveData.setValue(opp);
                                },
                                throwable -> {
                                    isSavingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void createOpportunity(CreateOpportunityRequestDto request) {
        isSavingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        addDisposable(
                opportunityRepository.createOpportunity(request, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                opp -> {
                                    isSavingLiveData.setValue(false);
                                    saveSuccessLiveData.setValue(true);
                                },
                                throwable -> {
                                    isSavingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void updateOpportunity(String id, UpdateOpportunityRequestDto request) {
        isSavingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        addDisposable(
                opportunityRepository.updateOpportunity(id, request, userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                opp -> {
                                    isSavingLiveData.setValue(false);
                                    saveSuccessLiveData.setValue(true);
                                },
                                throwable -> {
                                    isSavingLiveData.setValue(false);
                                    errorMessageLiveData.setValue(throwable.getMessage());
                                }
                        )
        );
    }
}
