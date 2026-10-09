package com.mnesa.backend.modules.opportunity.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.opportunity.dto.*;
import com.mnesa.backend.modules.opportunity.service.OpportunityService;
import com.mnesa.backend.modules.opportunity.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
@Tag(name = "Opportunity Management", description = "Endpoints for opportunity CRUD, lifecycle transitions, search, and history")
@SecurityRequirement(name = "BearerAuth")
public class OpportunityController {

    private final OpportunityService opportunityService;
    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Search and filter opportunities", description = "List opportunities with server-side pagination, search, category, status, and tag filters")
    public ResponseEntity<ApiResponse<Page<OpportunitySummaryResponse>>> listOpportunities(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant deadlineBefore,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant deadlineAfter,
            @RequestParam(required = false) String organization,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "false") boolean includeArchived,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Page<OpportunitySummaryResponse> result = opportunityService.searchOpportunities(
                principal.getId(), q, category, status, priority, deadlineBefore, deadlineAfter,
                organization, location, tag, includeArchived, pageable);

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping
    @Operation(summary = "Create opportunity manually", description = "Manually adds a new opportunity without requiring prior AI extraction")
    public ResponseEntity<ApiResponse<OpportunityResponse>> createOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateOpportunityRequest request) {

        OpportunityResponse created = opportunityService.createOpportunity(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Opportunity created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get opportunity details", description = "Retrieves full canonical details of an opportunity with authorization check")
    public ResponseEntity<ApiResponse<OpportunityResponse>> getOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        OpportunityResponse response = opportunityService.getOpportunity(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update opportunity", description = "Updates supported fields of an existing opportunity")
    public ResponseEntity<ApiResponse<OpportunityResponse>> updateOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOpportunityRequest request) {

        OpportunityResponse updated = opportunityService.updateOpportunity(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Opportunity updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete opportunity", description = "Permanently deletes an opportunity record")
    public ResponseEntity<ApiResponse<Void>> deleteOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        opportunityService.deleteOpportunity(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Opportunity deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transition lifecycle status", description = "Updates opportunity lifecycle state with domain validation and history recording")
    public ResponseEntity<ApiResponse<OpportunityResponse>> updateStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusRequest request) {

        OpportunityResponse updated = opportunityService.updateStatus(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Status updated to " + updated.getStatus()));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive opportunity", description = "Moves opportunity to ARCHIVED status while preserving previous state for restore")
    public ResponseEntity<ApiResponse<OpportunityResponse>> archiveOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        OpportunityResponse archived = opportunityService.archiveOpportunity(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(archived, "Opportunity archived"));
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore archived opportunity", description = "Restores an archived opportunity to its previous active state")
    public ResponseEntity<ApiResponse<OpportunityResponse>> restoreOpportunity(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        OpportunityResponse restored = opportunityService.restoreOpportunity(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(restored, "Opportunity restored"));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Get opportunity history", description = "Retrieves chronological audit activity for the specified opportunity")
    public ResponseEntity<ApiResponse<List<OpportunityActivityResponse>>> getOpportunityHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID id) {

        List<OpportunityActivityResponse> history = opportunityService.getOpportunityHistory(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }

    @GetMapping("/tags")
    @Operation(summary = "List user tags", description = "Lists all tags created by the authenticated user")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getTags(
            @AuthenticationPrincipal UserPrincipal principal) {

        List<TagResponse> tags = tagService.getTags(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(tags));
    }

    private Sort parseSort(String sortParam) {
        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sortParam.split(",");
        String property = parts[0].trim();
        // Allowlist valid sort properties to prevent SQL injection or bad queries
        if (!List.of("createdAt", "deadlineAt", "title", "priority", "updatedAt").contains(property)) {
            property = "createdAt";
        }
        Sort.Direction direction = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, property).and(Sort.by(Sort.Direction.DESC, "id"));
    }
}
