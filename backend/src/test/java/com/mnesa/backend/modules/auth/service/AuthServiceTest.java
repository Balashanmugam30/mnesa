package com.mnesa.backend.modules.auth.service;

import com.mnesa.backend.common.exception.AuthenticationFailedException;
import com.mnesa.backend.common.exception.DuplicateResourceException;
import com.mnesa.backend.modules.auth.dto.AuthResponse;
import com.mnesa.backend.modules.auth.dto.LoginRequest;
import com.mnesa.backend.modules.auth.dto.RefreshTokenRequest;
import com.mnesa.backend.modules.auth.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("register creates user, identities, preferences, and returns JWT tokens")
    void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test.engineer@mnesa.ai")
                .password("Password123!Secure")
                .fullName("Test Engineer")
                .build();

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("test.engineer@mnesa.ai", response.getUser().getEmail());
        assertEquals("Test Engineer", response.getUser().getFullName());
    }

    @Test
    @DisplayName("register throws DuplicateResourceException on existing email")
    void testRegisterDuplicateEmail() {
        RegisterRequest request = RegisterRequest.builder()
                .email("duplicate@mnesa.ai")
                .password("Password123!Secure")
                .fullName("Original User")
                .build();
        authService.register(request);

        RegisterRequest duplicate = RegisterRequest.builder()
                .email("duplicate@mnesa.ai")
                .password("DifferentPassword123!")
                .fullName("Duplicate User")
                .build();

        assertThrows(DuplicateResourceException.class, () -> authService.register(duplicate));
    }

    @Test
    @DisplayName("login validates password with Argon2id and returns valid JWT pair")
    void testLoginSuccess() {
        RegisterRequest regRequest = RegisterRequest.builder()
                .email("login.user@mnesa.ai")
                .password("CorrectPassword123!")
                .fullName("Login User")
                .build();
        authService.register(regRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("login.user@mnesa.ai")
                .password("CorrectPassword123!")
                .build();

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    @DisplayName("login throws AuthenticationFailedException on invalid password")
    void testLoginInvalidPassword() {
        RegisterRequest regRequest = RegisterRequest.builder()
                .email("invalid.login@mnesa.ai")
                .password("CorrectPassword123!")
                .fullName("Invalid User")
                .build();
        authService.register(regRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("invalid.login@mnesa.ai")
                .password("WrongPassword999!")
                .build();

        assertThrows(AuthenticationFailedException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("refreshToken rotates refresh token and returns new tokens")
    void testRefreshTokenRotation() {
        RegisterRequest regRequest = RegisterRequest.builder()
                .email("refresh.user@mnesa.ai")
                .password("CorrectPassword123!")
                .fullName("Refresh User")
                .build();
        AuthResponse initialAuth = authService.register(regRequest);

        RefreshTokenRequest refreshReq = RefreshTokenRequest.builder()
                .refreshToken(initialAuth.getRefreshToken())
                .build();

        AuthResponse refreshedAuth = authService.refreshToken(refreshReq);

        assertNotNull(refreshedAuth);
        assertNotNull(refreshedAuth.getAccessToken());
        assertNotNull(refreshedAuth.getRefreshToken());
        assertNotEquals(initialAuth.getRefreshToken(), refreshedAuth.getRefreshToken());
    }
}
