package com.mnesa.backend.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FetchAndExtractRequestDto {

    private String url;

    @JsonProperty("raw_text")
    private String rawText;

    @JsonProperty("user_notes")
    private String userNotes;
}
