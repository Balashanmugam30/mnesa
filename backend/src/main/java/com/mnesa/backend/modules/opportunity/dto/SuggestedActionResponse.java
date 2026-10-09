package com.mnesa.backend.modules.opportunity.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestedActionResponse {

    private String title;
    private String description;
    private UUID opportunityId;
    private String actionType;
}
