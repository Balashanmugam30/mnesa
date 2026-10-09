package com.mnesa.android.presentation.capture;

import android.content.ContentResolver;
import android.content.Intent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.domain.IntakePayloadParser;
import com.mnesa.android.domain.model.CaptureState;
import com.mnesa.android.domain.model.IntakePayload;
import com.mnesa.android.domain.repository.IntakeRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.UUID;

/**
 * ViewModel governing the high-speed Share Intent intake pipeline and state transitions.
 */
public class CaptureViewModel extends BaseViewModel {

    private final IntakeRepository repository;
    private final IntakePayloadParser parser;
    private final MutableLiveData<CaptureUiState> uiStateLiveData = new MutableLiveData<>();

    private IntakePayload currentPayload;
    private String currentIdempotencyKey;

    public CaptureViewModel(IntakeRepository repository) {
        this(repository, new IntakePayloadParser());
    }

    public CaptureViewModel(IntakeRepository repository, IntakePayloadParser parser) {
        this.repository = repository;
        this.parser = parser;
    }

    public LiveData<CaptureUiState> getUiState() {
        return uiStateLiveData;
    }

    public void processIncomingIntent(Intent intent, ContentResolver contentResolver) {
        uiStateLiveData.setValue(CaptureUiState.validating());

        IntakePayload payload = parser.parse(intent, contentResolver);
        this.currentPayload = payload;

        if (!payload.isValid()) {
            uiStateLiveData.setValue(CaptureUiState.unsupported(payload.getValidationError()));
            return;
        }

        String title = getDisplayTitle(payload);
        String badge = getDisplayBadge(payload);
        this.currentIdempotencyKey = UUID.randomUUID().toString();

        uiStateLiveData.setValue(CaptureUiState.submitting(title, badge));

        addDisposable(
                repository.processAndSubmit(payload, currentIdempotencyKey)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> uiStateLiveData.setValue(
                                        CaptureUiState.acknowledged(title, badge, response.getMessage(), response.isDuplicate())
                                ),
                                throwable -> uiStateLiveData.setValue(
                                        CaptureUiState.retryableFailure(title, badge, "Network unavailable. Saved offline to sync queue.")
                                )
                        )
        );
    }

    public void retry() {
        if (currentPayload != null && currentIdempotencyKey != null) {
            String title = getDisplayTitle(currentPayload);
            String badge = getDisplayBadge(currentPayload);
            uiStateLiveData.setValue(CaptureUiState.submitting(title, badge));

            addDisposable(
                    repository.processAndSubmit(currentPayload, currentIdempotencyKey)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    response -> uiStateLiveData.setValue(
                                            CaptureUiState.acknowledged(title, badge, response.getMessage(), response.isDuplicate())
                                    ),
                                    throwable -> uiStateLiveData.setValue(
                                            CaptureUiState.retryableFailure(title, badge, "Retry failed. Maintained in local sync queue.")
                                    )
                            )
            );
        }
    }

    private String getDisplayTitle(IntakePayload payload) {
        if (payload.getRawText() != null && !payload.getRawText().isBlank()) {
            String text = payload.getRawText().trim();
            return text.length() > 60 ? text.substring(0, 60).trim() + "…" : text;
        }
        if (payload.getExtractedUrl() != null) {
            return payload.getExtractedUrl();
        }
        return "Shared Opportunity";
    }

    private String getDisplayBadge(IntakePayload payload) {
        if ("IMAGE".equalsIgnoreCase(payload.getSourceType())) {
            return "SCREENSHOT";
        }
        if (payload.getSourceDomain() != null) {
            String domain = payload.getSourceDomain().toUpperCase();
            if (domain.contains("LINKEDIN")) return "LINKEDIN";
            if (domain.contains("TWITTER") || domain.contains("X.COM")) return "X / TWITTER";
            if (domain.contains("GITHUB")) return "GITHUB";
            if (domain.contains("INSTAGRAM")) return "INSTAGRAM";
            return domain;
        }
        return payload.getSourceType() != null ? payload.getSourceType() : "OPPORTUNITY";
    }
}
