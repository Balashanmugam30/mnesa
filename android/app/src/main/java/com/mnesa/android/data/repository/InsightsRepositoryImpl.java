package com.mnesa.android.data.repository;

import android.content.Context;
import com.mnesa.android.core.network.ApiClient;
import com.mnesa.android.data.remote.api.InsightsApiService;
import com.mnesa.android.data.remote.dto.InsightsResponseDto;
import com.mnesa.android.domain.repository.InsightsRepository;
import io.reactivex.rxjava3.core.Single;

public class InsightsRepositoryImpl implements InsightsRepository {

    private final InsightsApiService apiService;

    public InsightsRepositoryImpl(Context context) {
        this.apiService = ApiClient.getInstance(context).getInsightsApiService();
    }

    public InsightsRepositoryImpl(InsightsApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<InsightsResponseDto> getInsights() {
        return apiService.getInsights()
                .map(response -> {
                    if (response != null && response.getData() != null) {
                        return response.getData();
                    }
                    throw new IllegalStateException("Empty response from insights service");
                });
    }
}
