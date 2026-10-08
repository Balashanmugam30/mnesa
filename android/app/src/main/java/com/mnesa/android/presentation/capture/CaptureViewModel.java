package com.mnesa.android.presentation.capture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.model.Opportunity;
import com.mnesa.android.domain.model.OpportunityStatus;
import com.mnesa.android.domain.model.OpportunityType;
import com.mnesa.android.domain.repository.OpportunityRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.UUID;

/**
 * ViewModel powering the instantaneous Share Intent capture flow.
 */
public class CaptureViewModel extends BaseViewModel {

    private final OpportunityRepository repository;
    private final MutableLiveData<Boolean> captureCompletedLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> previewTextLiveData = new MutableLiveData<>();

    public CaptureViewModel(OpportunityRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getCaptureCompleted() {
        return captureCompletedLiveData;
    }

    public LiveData<String> getPreviewText() {
        return previewTextLiveData;
    }

    public void processSharedContent(String rawContent, String sourceUrl) {
        previewTextLiveData.setValue(rawContent != null ? rawContent : "Opportunity Shared");

        String title = rawContent != null && rawContent.length() > 60
                ? rawContent.substring(0, 60).trim() + "…"
                : (rawContent != null ? rawContent.trim() : "Captured Opportunity");

        Opportunity opportunity = new Opportunity(
                UUID.randomUUID().toString(),
                title,
                "Shared Opportunity",
                OpportunityType.OTHER,
                OpportunityStatus.CAPTURED,
                sourceUrl,
                null,
                1.0f,
                System.currentTimeMillis()
        );

        addDisposable(
                repository.saveOpportunity(opportunity)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> captureCompletedLiveData.setValue(true),
                                throwable -> captureCompletedLiveData.setValue(true)
                        )
        );
    }
}
