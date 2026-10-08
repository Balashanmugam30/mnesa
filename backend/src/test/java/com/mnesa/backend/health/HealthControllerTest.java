package com.mnesa.backend.health;

import com.mnesa.backend.common.filter.CorrelationIdFilter;
import com.mnesa.backend.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
@Import({SecurityConfig.class, CorrelationIdFilter.class})
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/health returns 200 OK with UP status and correlation ID")
    void shouldReturnHealthStatusOk() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .header("X-Correlation-ID", "test-corr-id-123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Correlation-ID", "test-corr-id-123"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.version").value("0.1.0-SNAPSHOT"))
                .andExpect(jsonPath("$.data.components.database").value("PostgreSQL 16"))
                .andExpect(jsonPath("$.correlationId").value("test-corr-id-123"));
    }

    @Test
    @DisplayName("GET /api/v1/health automatically generates correlation ID if absent")
    void shouldGenerateCorrelationIdWhenMissing() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Correlation-ID"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"));
    }
}
