package com.mnesa.android.presentation.capture;

import android.content.ContentResolver;
import android.content.Intent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mnesa.android.core.base.BaseViewModel;
import com.mnesa.android.data.remote.dto.AiExtractionDto;
import com.mnesa.android.data.remote.dto.ConfirmOpportunityRequestDto;
import com.mnesa.android.data.remote.dto.IntakeJobStatusDto;
import com.mnesa.android.domain.IntakePayloadParser;
import com.mnesa.android.domain.model.CaptureState;
import com.mnesa.android.domain.model.IntakePayload;
import com.mnesa.android.domain.repository.IntakeRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * ViewModel governing the high-speed Share Intent intake and AI extraction lifecycle.
 */
public class CaptureViewModel extends BaseViewModel {

    private final IntakeRepository repository;
    private final IntakePayloadParser parser;
    private final MutableLiveData<CaptureUiState> uiStateLiveData = new MutableLiveData<>();

    private IntakePayload currentPayload;
    private String currentIdempotencyKey;
    private String currentJobId;
    private Disposable pollingDisposable;

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
                                response -> {
                                    if (response.isDuplicate()) {
                                        uiStateLiveData.setValue(
                                                CaptureUiState.acknowledged(title, badge, response.getMessage(), true)
                                        );
                                    } else if (response.getJobId() != null) {
                                        this.currentJobId = response.getJobId();
                                        uiStateLiveData.setValue(CaptureUiState.analyzing(title, badge, currentJobId));
                                        startPollingJob(currentJobId, title, badge);
                                    } else {
                                        uiStateLiveData.setValue(
                                                CaptureUiState.acknowledged(title, badge, response.getMessage(), false)
                                        );
                                    }
                                },
                                throwable -> uiStateLiveData.setValue(
                                        CaptureUiState.retryableFailure(title, badge, "Network unavailable. Saved offline to sync queue.")
                                )
                        )
        );
    }

    private void startPollingJob(String jobId, String defaultTitle, String defaultBadge) {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }

        pollingDisposable = Observable.interval(1500, TimeUnit.MILLISECONDS)
                .take(10) // Poll for at most 15 seconds
                .flatMapSingle(tick -> repository.pollJobStatus(jobId).subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        statusDto -> {
                            if ("COMPLETED".equalsIgnoreCase(statusDto.getStatus()) && statusDto.getExtraction() != null) {
                                handleExtractionCompleted(statusDto.getExtraction(), defaultTitle, jobId);
                                if (pollingDisposable != null) {
                                    pollingDisposable.dispose();
                                }
                            } else if ("FAILED".equalsIgnoreCase(statusDto.getStatus())) {
                                uiStateLiveData.setValue(
                                        CaptureUiState.acknowledged(defaultTitle, defaultBadge, "Saved offline. AI extraction will retry later.", false)
                                );
                                if (pollingDisposable != null) {
                                    pollingDisposable.dispose();
                                }
                            }
                        },
                        error -> {
                            // On polling error, keep acknowledged state
                            uiStateLiveData.setValue(
                                    CaptureUiState.acknowledged(defaultTitle, defaultBadge, "Saved. Analysis scheduled in background.", false)
                            );
                        }
                );

        addDisposable(pollingDisposable);
    }

    private void handleExtractionCompleted(AiExtractionDto extraction, String fallbackTitle, String jobId) {
        String title = extraction.getTitle() != null && !extraction.getTitle().isBlank()
                ? extraction.getTitle()
                : fallbackTitle;

        float conf = extraction.getOverallConfidence() != null ? extraction.getOverallConfidence() : 0.85f;
        String confPill;
        if (conf >= 0.85f) {
            confPill = "High Confidence (" + Math.round(conf * 100) + "%)";
        } else if (conf >= 0.50f) {
            confPill = "Needs Review (" + Math.round(conf * 100) + "%)";
        } else {
            confPill = "Incomplete (" + Math.round(conf * 100) + "%)";
        }

        String deadlineText = extraction.getDeadlineRaw() != null && !extraction.getDeadlineRaw().isBlank()
                ? extraction.getDeadlineRaw()
                : (extraction.getDeadlineAt() != null ? extraction.getDeadlineAt() : "No deadline detected");

        String evidence = extraction.getEvidenceSnippets() != null && !extraction.getEvidenceSnippets().isEmpty()
                ? extraction.getEvidenceSnippets().get(0)
                : null;

        uiStateLiveData.setValue(
                CaptureUiState.extractionSuccess(
                        title,
                        extraction.getOrganization(),
                        extraction.getCategory(),
                        extraction.getSummary(),
                        deadlineText,
                        extraction.isDeadlineAmbiguous(),
                        confPill,
                        conf,
                        evidence,
                        jobId
                )
        );
    }

    public void confirmOpportunity(String titleOverride, String categoryOverride, String deadlineOverride) {
        if (currentJobId == null) {
            return;
        }

        CaptureUiState state = uiStateLiveData.getValue();
        String title = titleOverride != null ? titleOverride : (state != null ? state.getTitle() : "Saved Opportunity");
        String category = categoryOverride != null ? categoryOverride : (state != null ? state.getCategory() : "OTHER");

        ConfirmOpportunityRequestDto request = new ConfirmOpportunityRequestDto(
                title,
                state != null ? state.getOrganization() : null,
                category,
                null,
                null
        );

        addDisposable(
                repository.confirmOpportunity(currentJobId, request)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                success -> uiStateLiveData.setValue(CaptureUiState.confirmed(title, category)),
                                error -> uiStateLiveData.setValue(CaptureUiState.confirmed(title, category))
                        )
        );
    }

    public void retry() {
        if (currentPayload != null && currentIdempotencyKey != null) {
            processIncomingIntent(new Intent(), null);
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
        if (payload.getSourceDomain() != null) {
            return payload.getSourceDomain().toUpperCase();
        }
        return payload.getSourceType() != null ? payload.getSourceType() : "OPPORTUNITY";
    }

    @Override
    protected void onCleared() {
        if (pollingDisposable != null && !pollingDisposable.isDisposed()) {
            pollingDisposable.dispose();
        }
        super.onCleared();
    }
}
