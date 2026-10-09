package com.mnesa.backend.modules.assistant.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssistantQueryResponse {

    private String question;
    private String answer;
    private String intent;

    @Builder.Default
    private List<CitedOpportunityDto> citedOpportunities = new ArrayList<>();

    @Builder.Default
    private List<String> actionSuggestions = new ArrayList<>();
}
