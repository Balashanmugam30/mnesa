package com.mnesa.backend.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeadlineDto {
    private Instant value;
    @JsonProperty("raw_text")
    private String rawText;
    private String timezone;
    @JsonProperty("is_ambiguous")
    private boolean isAmbiguous;
    @Builder.Default
    private Double confidence = 0.0;
    private String evidence;
}
