package com.mnesa.backend.modules.insights.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.insights.dto.InsightsDto;
import com.mnesa.backend.modules.insights.service.InsightsService;
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
@RequestMapping("/api/v1/insights")
@RequiredArgsConstructor
@Tag(name = "Insights & Analytics", description = "Authoritative user-scoped performance, category, and lifecycle analytics")
@SecurityRequirement(name = "BearerAuth")
public class InsightsController {

    private final InsightsService insightsService;

    @GetMapping
    @Operation(summary = "Get user opportunity insights", description = "Retrieves authoritative database-grounded counts, category breakdown, status lifecycle metrics, and 14-day activity trends")
    public ResponseEntity<ApiResponse<InsightsDto>> getInsights(
            @AuthenticationPrincipal UserPrincipal principal) {
        InsightsDto insights = insightsService.getInsights(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(insights));
    }
}

