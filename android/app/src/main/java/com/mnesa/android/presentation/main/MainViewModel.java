package com.mnesa.android.presentation.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.List;

/**
 * ViewModel powering the main opportunity dashboard.
 */
public class MainViewModel extends BaseViewModel {

    private final OpportunityRepository repository;
    private final MutableLiveData<List<Opportunity>> opportunitiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    public MainViewModel(OpportunityRepository repository) {
        this.repository = repository;
        loadOpportunities();
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

    public void loadOpportunities() {
        isLoadingLiveData.setValue(true);
        addDisposable(
                repository.getAllOpportunities()
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
}
