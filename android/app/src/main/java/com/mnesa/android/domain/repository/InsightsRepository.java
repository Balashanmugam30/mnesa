package com.mnesa.android.domain.repository;

import com.mnesa.android.data.remote.dto.InsightsResponseDto;
import io.reactivex.rxjava3.core.Single;

public interface InsightsRepository {
    Single<InsightsResponseDto> getInsights();
}
