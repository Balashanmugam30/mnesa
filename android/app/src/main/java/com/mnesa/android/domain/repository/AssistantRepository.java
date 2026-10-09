package com.mnesa.android.domain.repository;

import com.mnesa.android.data.remote.dto.AssistantQueryResponseDto;
import io.reactivex.rxjava3.core.Single;

public interface AssistantRepository {
    Single<AssistantQueryResponseDto> askAssistant(String question, String timezone);
}
