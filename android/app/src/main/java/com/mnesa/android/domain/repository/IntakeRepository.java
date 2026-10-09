package com.mnesa.android.domain.repository;

import com.mnesa.android.data.local.entity.CaptureEntity;
import com.mnesa.android.data.remote.dto.ConfirmOpportunityRequestDto;
import com.mnesa.android.data.remote.dto.IntakeJobStatusDto;
import com.mnesa.android.data.remote.dto.IntakeResponseDto;
import com.mnesa.android.domain.model.IntakePayload;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface IntakeRepository {

    Single<IntakeResponseDto> processAndSubmit(IntakePayload payload, String idempotencyKey);

    Single<CaptureEntity> saveOfflineCapture(IntakePayload payload, String idempotencyKey);

    Observable<List<CaptureEntity>> getRecentCaptures();

    Single<IntakeJobStatusDto> pollJobStatus(String jobId);

    Single<Boolean> confirmOpportunity(String jobId, ConfirmOpportunityRequestDto request);
}
