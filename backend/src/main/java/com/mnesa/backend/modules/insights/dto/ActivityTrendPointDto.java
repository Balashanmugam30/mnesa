package com.mnesa.backend.modules.insights.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityTrendPointDto {

    private String date;
    private long count;
}
