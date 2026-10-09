package com.mnesa.backend.modules.opportunity.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryResponse {

    private String category;
    private String displayName;
    private long activeCount;
}
