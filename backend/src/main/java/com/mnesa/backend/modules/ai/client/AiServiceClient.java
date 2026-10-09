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
}
