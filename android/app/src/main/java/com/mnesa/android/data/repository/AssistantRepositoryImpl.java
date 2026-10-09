package com.mnesa.android.data.repository;

import android.content.Context;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.remote.api.AssistantApiService;
import com.mnesa.android.data.remote.dto.AssistantQueryRequestDto;
import com.mnesa.android.data.remote.dto.AssistantQueryResponseDto;
import com.mnesa.android.domain.repository.AssistantRepository;
import io.reactivex.rxjava3.core.Single;

public class AssistantRepositoryImpl implements AssistantRepository {

    private final AssistantApiService apiService;

    public AssistantRepositoryImpl(Context context) {
        this.apiService = ApiClient.getInstance(context).getAssistantApiService();
    }

    public AssistantRepositoryImpl(AssistantApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<AssistantQueryResponseDto> askAssistant(String question, String timezone) {
        AssistantQueryRequestDto request = new AssistantQueryRequestDto(question, timezone != null ? timezone : "UTC");
        return apiService.askAssistant(request)
                .map(response -> {
                    if (response != null && response.getData() != null) {
                        return response.getData();
                    }
                    throw new IllegalStateException("Empty response from AI assistant");
                });
    }
}
