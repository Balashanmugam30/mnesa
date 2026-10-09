package com.mnesa.backend.modules.intake.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.intake.domain.Capture;
import com.mnesa.backend.modules.intake.domain.IntakeJob;
import com.mnesa.backend.modules.intake.dto.IntakeRequest;
import com.mnesa.backend.modules.intake.dto.IntakeResponse;
import com.mnesa.backend.modules.intake.service.IntakeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/intake")
@Tag(name = "Intake Pipeline", description = "Endpoints for sharing, intake ingestion, and capture processing")
@SecurityRequirement(name = "BearerAuth")
public class IntakeController {

    private final IntakeService intakeService;

    public IntakeController(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    @PostMapping
    @Operation(summary = "Submit opportunity intake", description = "Ingests shared URL, text snippet, or screenshot with idempotency and SSRF guard")
    public ResponseEntity<ApiResponse<IntakeResponse>> submitIntake(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody IntakeRequest request) {

        IntakeResponse response = intakeService.processIntake(principal.getId(), request);

        HttpStatus status = response.isDuplicate() ? HttpStatus.OK : HttpStatus.ACCEPTED;
        return ResponseEntity.status(status).body(ApiResponse.ok(response, response.getMessage()));
    }

    @GetMapping("/{captureId}")
    @Operation(summary = "Get capture record", description = "Retrieves raw capture record by capture ID for authenticated owner")
    public ResponseEntity<ApiResponse<Capture>> getCapture(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID captureId) {

        Capture capture = intakeService.getCapture(principal.getId(), captureId);
        return ResponseEntity.ok(ApiResponse.ok(capture));
    }

    @GetMapping("/jobs/{jobId}")
    @Operation(summary = "Get intake job status", description = "Retrieves processing status of an asynchronous intake job")
    public ResponseEntity<ApiResponse<IntakeJob>> getIntakeJob(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID jobId) {

        IntakeJob job = intakeService.getIntakeJob(principal.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.ok(job));
    }
}
