package com.mnesa.backend.modules.opportunity.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.opportunity.dto.HomeDashboardResponse;
import com.mnesa.backend.modules.opportunity.service.OpportunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
@Tag(name = "Home Dashboard", description = "Dashboard aggregation for home screen with urgency, metrics, and actions")
@SecurityRequirement(name = "BearerAuth")
public class HomeController {

    private final OpportunityService opportunityService;

    @GetMapping
    @Operation(summary = "Get home dashboard aggregation", description = "Retrieves aggregated home metrics, urgency buckets, upcoming deadlines, and deterministic action suggestions")
    public ResponseEntity<ApiResponse<HomeDashboardResponse>> getHomeDashboard(
            @AuthenticationPrincipal UserPrincipal principal) {

        HomeDashboardResponse dashboard = opportunityService.getHomeDashboard(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(dashboard));
    }
}
