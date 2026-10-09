package com.mnesa.backend.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssistantQueryRequestDto {

    private String query;

    @JsonProperty("context_records")
    @Builder.Default
    private List<AssistantRecordDto> contextRecords = new ArrayList<>();

    @JsonProperty("user_timezone")
    @Builder.Default
    private String userTimezone = "UTC";
}
