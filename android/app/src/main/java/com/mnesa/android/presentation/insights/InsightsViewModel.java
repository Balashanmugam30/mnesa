package com.mnesa.android.presentation.insights;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.repository.InsightsRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class InsightsViewModel extends BaseViewModel {

    private final InsightsRepository repository;
    private final MutableLiveData<InsightsUiState> uiStateLiveData = new MutableLiveData<>();

    public InsightsViewModel(InsightsRepository repository) {
        this.repository = repository;
        loadInsights();
    }

    public LiveData<InsightsUiState> getUiState() {
        return uiStateLiveData;
    }

    public void loadInsights() {
        uiStateLiveData.setValue(InsightsUiState.loading());

        addDisposable(
                repository.getInsights()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                dto -> uiStateLiveData.setValue(InsightsUiState.fromDto(dto)),
                                throwable -> uiStateLiveData.setValue(
                                        InsightsUiState.error(
                                                throwable.getMessage() != null
                                                        ? throwable.getMessage()
                                                        : "Failed to load insights"
                                        )
                                )
                        )
        );
    }
}
