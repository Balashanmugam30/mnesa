package com.mnesa.backend.modules.auth.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Health endpoint permits unauthenticated access")
    void healthEndpointPermitsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Protected opportunity endpoints reject unauthenticated access with 401")
    void opportunitiesRejectUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/opportunities"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected reminder endpoints reject unauthenticated access with 401")
    void remindersRejectUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/reminders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected assistant endpoints reject unauthenticated access with 401")
    void assistantRejectsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/assistant/conversations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected user profile endpoints reject unauthenticated access with 401")
    void usersRejectUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("CORS preflight permits allowed origin https://mnesa.ai")
    void corsAllowsProductionOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/health")
                        .header(HttpHeaders.ORIGIN, "https://mnesa.ai")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "https://mnesa.ai"));
    }

    @Test
    @DisplayName("CORS preflight permits allowed origin http://localhost:3100")
    void corsAllowsPlaywrightTestOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/health")
                        .header(HttpHeaders.ORIGIN, "http://localhost:3100")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3100"));
    }

    @Test
    @DisplayName("CORS preflight rejects disallowed origin")
    void corsRejectsUnauthorizedOrigin() throws Exception {
        mockMvc.perform(options("/api/v1/health")
                        .header(HttpHeaders.ORIGIN, "https://unauthorized-attacker.example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden());
    }
}
