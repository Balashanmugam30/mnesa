package com.mnesa.backend.modules.opportunity.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.opportunity.dto.CategorySummaryResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Opportunity Categories", description = "Endpoints for browsing category catalog and active opportunity counts")
@SecurityRequirement(name = "BearerAuth")
public class CategoryController {

    private final OpportunityService opportunityService;

    @GetMapping
    @Operation(summary = "Get category catalog", description = "Lists canonical opportunity categories with user's active count per category")
    public ResponseEntity<ApiResponse<List<CategorySummaryResponse>>> getCategories(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<CategorySummaryResponse> categories = opportunityService.getCategories(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }
}
