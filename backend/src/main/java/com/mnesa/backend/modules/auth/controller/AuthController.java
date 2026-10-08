package com.mnesa.backend.modules.auth.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.dto.*;
import com.mnesa.backend.modules.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, login, token refresh, and password recovery")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new account", description = "Creates a new user account with email and password and returns access/refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticates with email and password and returns access/refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Exchanges a valid refresh token for a new access token and rotated refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Revokes the active refresh token session")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Logged out successfully")
                .build());
    }

    @PostMapping("/google")
    @Operation(summary = "Google Sign-In", description = "Authenticates or registers via Google ID token")
    public ResponseEntity<ApiResponse<AuthResponse>> googleSignIn(@Valid @RequestBody GoogleAuthRequest request) {
        AuthResponse response = authService.googleSignIn(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Sends password reset instructions to the provided email if registered")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("If an account exists for this email, password reset instructions have been sent.")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets user password using the provided reset token")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password has been reset successfully. Please log in with your new password.")
                .build());
    }
}
