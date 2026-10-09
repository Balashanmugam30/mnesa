package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.ApiResponseDto;
import com.mnesa.android.data.remote.dto.InsightsResponseDto;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;

public interface InsightsApiService {

    @GET("insights")
    Single<ApiResponseDto<InsightsResponseDto>> getInsights();
}
