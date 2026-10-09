package com.mnesa.android.data.remote.api;

import com.mnesa.android.data.remote.dto.ApiResponseDto;
import com.mnesa.android.data.remote.dto.AssistantQueryRequestDto;
import com.mnesa.android.data.remote.dto.AssistantQueryResponseDto;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AssistantApiService {

    @POST("assistant/query")
    Single<ApiResponseDto<AssistantQueryResponseDto>> askAssistant(@Body AssistantQueryRequestDto request);
}
