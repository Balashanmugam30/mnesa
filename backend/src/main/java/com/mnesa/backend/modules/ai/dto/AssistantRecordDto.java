package com.mnesa.backend.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssistantRecordDto {

    private String id;
    private String title;
    private String organization;
    private String category;

    @JsonProperty("deadline_at")
    private String deadlineAt;

    private String status;
    private String priority;
}
