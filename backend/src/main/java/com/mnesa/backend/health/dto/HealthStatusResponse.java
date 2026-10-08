package com.mnesa.backend.health.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * DTO detailing the health status of the MNESA backend.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatusResponse {

    private String status;
    private String version;
    private String environment;
    private Instant timestamp;
    private Map<String, String> components;
}
