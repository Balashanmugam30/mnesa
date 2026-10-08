package com.mnesa.backend.health;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.health.dto.HealthStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controller exposing system health probes for client and load-balancer readiness.
 */
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "System health, readiness, and diagnostic probes")
public class HealthController {

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @GetMapping
    @Operation(summary = "Check backend service health and runtime status")
    public ResponseEntity<ApiResponse<HealthStatusResponse>> checkHealth() {
        HealthStatusResponse status = HealthStatusResponse.builder()
                .status("UP")
                .version("0.1.0-SNAPSHOT")
                .environment(activeProfile)
                .timestamp(Instant.now())
                .components(Map.of(
                        "database", "PostgreSQL 16",
                        "monolith", "Spring Boot 3.4",
                        "security", "Spring Security 6"
                ))
                .build();

        return ResponseEntity.ok(ApiResponse.ok(status, "MNESA Backend is healthy and operational"));
    }
}
