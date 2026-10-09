package com.mnesa.backend.modules.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageExtractionRequestDto {

    @JsonProperty("image_base64")
    private String imageBase64;

    @JsonProperty("image_mime_type")
    private String imageMimeType;

    @JsonProperty("user_notes")
    private String userNotes;

    @JsonProperty("source_url")
    private String sourceUrl;
}
