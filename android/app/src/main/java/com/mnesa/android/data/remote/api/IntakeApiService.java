package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.ApiResponseDto;
import com.mnesa.android.data.remote.dto.IntakeRequestDto;
import com.mnesa.android.data.remote.dto.IntakeResponseDto;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IntakeApiService {

    @POST("intake")
    Single<ApiResponseDto<IntakeResponseDto>> submitIntake(@Body IntakeRequestDto request);
}
