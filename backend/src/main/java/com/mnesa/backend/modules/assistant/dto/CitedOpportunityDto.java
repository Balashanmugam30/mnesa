package com.mnesa.backend.modules.assistant.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitedOpportunityDto {

    private UUID id;
    private String title;
    private String organization;
    private String category;
    private Instant deadlineAt;
    private String status;
    private String priority;
}
