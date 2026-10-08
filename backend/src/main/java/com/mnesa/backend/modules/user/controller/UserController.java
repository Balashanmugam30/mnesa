package com.mnesa.backend.modules.user.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.user.dto.UpdatePreferencesRequest;
import com.mnesa.backend.modules.user.dto.UpdateProfileRequest;
import com.mnesa.backend.modules.user.dto.UserPreferencesDto;
import com.mnesa.backend.modules.user.dto.UserProfileResponse;
import com.mnesa.backend.modules.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Identity & Profile", description = "Endpoints for managing authenticated user profile, preferences, and account lifecycle")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns profile details and preferences of the authenticated user")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserProfileResponse profile = userService.getCurrentUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates full name and personal details of the authenticated user")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@AuthenticationPrincipal UserPrincipal principal,
                                                                         @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updated = userService.updateProfile(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @GetMapping("/me/preferences")
    @Operation(summary = "Get user preferences", description = "Returns opportunity interests, notification settings, and reminder cadences")
    public ResponseEntity<ApiResponse<UserPreferencesDto>> getPreferences(@AuthenticationPrincipal UserPrincipal principal) {
        UserPreferencesDto preferences = userService.getPreferences(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(preferences));
    }

    @PutMapping("/me/preferences")
    @Operation(summary = "Update user preferences", description = "Updates opportunity interests, notification toggles, and reminder cadences")
    public ResponseEntity<ApiResponse<UserPreferencesDto>> updatePreferences(@AuthenticationPrincipal UserPrincipal principal,
                                                                             @Valid @RequestBody UpdatePreferencesRequest request) {
        UserPreferencesDto updated = userService.updatePreferences(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete account", description = "Permanently deletes user account, authentication identities, and associated data")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal UserPrincipal principal) {
        userService.deleteAccount(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
