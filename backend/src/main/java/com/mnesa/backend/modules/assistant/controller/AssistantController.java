package com.mnesa.backend.modules.assistant.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.assistant.dto.AssistantQueryRequest;
import com.mnesa.backend.modules.assistant.dto.AssistantQueryResponse;
import com.mnesa.backend.modules.assistant.service.AssistantService;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/assistant")
@RequiredArgsConstructor
@Tag(name = "Personal AI Assistant", description = "User-scoped authenticated Q&A grounded strictly in saved opportunities")
@SecurityRequirement(name = "BearerAuth")
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/query")
    @Operation(summary = "Ask Personal AI Assistant", description = "Answers natural language queries about deadlines, priorities, and saved opportunities with grounded citations and zero prompt injection risk")
    public ResponseEntity<ApiResponse<AssistantQueryResponse>> askAssistant(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AssistantQueryRequest request) {
        AssistantQueryResponse response = assistantService.askAssistant(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

