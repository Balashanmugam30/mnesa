package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.*;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IntakeApiService {

    @POST("intake")
    Single<ApiResponseDto<IntakeResponseDto>> submitIntake(@Body IntakeRequestDto request);

    @GET("intake/jobs/{jobId}")
    Single<ApiResponseDto<IntakeJobStatusDto>> getJobStatus(@Path("jobId") String jobId);

    @POST("intake/jobs/{jobId}/confirm")
    Single<ApiResponseDto<Object>> confirmJob(@Path("jobId") String jobId, @Body ConfirmOpportunityRequestDto request);
}
