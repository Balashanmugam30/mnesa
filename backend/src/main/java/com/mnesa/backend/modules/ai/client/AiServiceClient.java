package com.mnesa.backend.modules.ai.client;

import com.mnesa.backend.modules.ai.dto.ExtractionResultDto;
import com.mnesa.backend.modules.ai.dto.FetchAndExtractRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);

    private final RestClient restClient;

    public AiServiceClient(@Value("${mnesa.ai.service-url:http://localhost:8000}") String aiServiceUrl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(20));

        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Dispatches fetch and extraction request to internal Python AI Service.
     */
    public ExtractionResultDto extractOpportunity(FetchAndExtractRequestDto request) {
        try {
            log.info("Calling internal AI service to extract opportunity (url: [{}])", request.getUrl());
            ExtractionResultDto response = restClient.post()
                    .uri("/api/v1/fetch-and-extract")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ExtractionResultDto.class);

            if (response == null) {
                return ExtractionResultDto.builder()
                        .success(false)
                        .validationStatus("FAILED")
                        .errorMessage("Empty response received from AI service")
                        .build();
            }

            return response;
        } catch (Exception e) {
            log.warn("AI service call failed or timed out: {}", e.getMessage());
            return ExtractionResultDto.builder()
                    .success(false)
                    .validationStatus("PROVIDER_UNAVAILABLE")
                    .errorMessage("AI service communication failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Dispatches OCR and opportunity extraction for screenshots / images.
     */
    public com.mnesa.backend.modules.ai.dto.MultiCandidateExtractionResultDto extractImage(com.mnesa.backend.modules.ai.dto.ImageExtractionRequestDto request) {
        try {
            log.info("Calling internal AI service to extract screenshot/image opportunities");
            com.mnesa.backend.modules.ai.dto.MultiCandidateExtractionResultDto response = restClient.post()
                    .uri("/api/v1/extract/image")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(com.mnesa.backend.modules.ai.dto.MultiCandidateExtractionResultDto.class);

            if (response == null) {
                return com.mnesa.backend.modules.ai.dto.MultiCandidateExtractionResultDto.builder()
                        .success(false)
                        .validationStatus("FAILED")
                        .errorMessage("Empty response received from AI image extraction service")
                        .build();
            }

            return response;
        } catch (Exception e) {
            log.warn("AI service image extraction failed or timed out: {}", e.getMessage());
            return com.mnesa.backend.modules.ai.dto.MultiCandidateExtractionResultDto.builder()
                    .success(false)
                    .validationStatus("PROVIDER_UNAVAILABLE")
                    .errorMessage("AI image extraction service communication failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Queries Personal AI Assistant with bounded context records.
     */
    public com.mnesa.backend.modules.ai.dto.AssistantQueryResponseDto generateAssistantResponse(com.mnesa.backend.modules.ai.dto.AssistantQueryRequestDto request) {
        try {
            log.info("Calling internal AI service assistant for query: [{}]", request.getQuery());
            com.mnesa.backend.modules.ai.dto.AssistantQueryResponseDto response = restClient.post()
                    .uri("/api/v1/assistant/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(com.mnesa.backend.modules.ai.dto.AssistantQueryResponseDto.class);

            if (response == null) {
                return com.mnesa.backend.modules.ai.dto.AssistantQueryResponseDto.builder()
                        .answer("I am unable to generate a response at this moment.")
                        .intent("GENERAL_QUERY")
                        .build();
            }

            return response;
        } catch (Exception e) {
            log.warn("AI service assistant query failed or timed out: {}", e.getMessage());
            return null; // Signals caller to use deterministic local synthesis fallback
        }
    }
}

