package com.mnesa.backend.modules.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.common.exception.AuthenticationFailedException;
import com.mnesa.backend.common.exception.DuplicateResourceException;
import com.mnesa.backend.modules.auth.dto.*;
import com.mnesa.backend.modules.auth.security.JwtTokenProvider;
import com.mnesa.backend.modules.auth.service.AuthService;
import com.mnesa.backend.modules.user.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/v1/auth/register returns 201 Created on valid input")
    void registerSuccess() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("alex@example.com")
                .password("SecurePass123!")
                .fullName("Alex Chen")
                .build();

        AuthResponse response = AuthResponse.builder()
                .user(UserDto.builder()
                        .id(UUID.randomUUID())
                        .email("alex@example.com")
                        .fullName("Alex Chen")
                        .role("USER")
                        .status("ACTIVE")
                        .build())
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.user.email").value("alex@example.com"))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("mock-refresh-token"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register returns 400 Bad Request on invalid email")
    void registerInvalidEmail() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("not-an-email")
                .password("SecurePass123!")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Content"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register returns 409 Conflict when email already registered")
    void registerDuplicateEmail() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@example.com")
                .password("SecurePass123!")
                .build();

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new DuplicateResourceException("An account with email existing@example.com already exists"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 200 OK with tokens on valid credentials")
    void loginSuccess() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("alex@example.com")
                .password("SecurePass123!")
                .build();

        AuthResponse response = AuthResponse.builder()
                .user(UserDto.builder()
                        .id(UUID.randomUUID())
                        .email("alex@example.com")
                        .fullName("Alex Chen")
                        .role("USER")
                        .status("ACTIVE")
                        .build())
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .tokenType("Bearer")
                .expiresIn(3600)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mock-access-token"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login returns 401 Unauthorized on invalid credentials")
    void loginInvalidCredentials() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("alex@example.com")
                .password("WrongPassword!")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new AuthenticationFailedException("Invalid email or password"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/v1/auth/forgot-password returns 200 OK")
    void forgotPasswordSuccess() throws Exception {
        ForgotPasswordRequest request = ForgotPasswordRequest.builder()
                .email("alex@example.com")
                .build();

        mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
