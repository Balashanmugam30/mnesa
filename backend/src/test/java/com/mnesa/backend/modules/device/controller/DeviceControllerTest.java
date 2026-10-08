package com.mnesa.backend.modules.device.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.device.domain.DevicePlatform;
import com.mnesa.backend.modules.device.dto.DeviceResponse;
import com.mnesa.backend.modules.device.dto.RegisterDeviceRequest;
import com.mnesa.backend.modules.device.service.DeviceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceService deviceService;

    @Test
    @DisplayName("POST /api/v1/devices/register registers device and push token")
    void registerDeviceAuthenticated() throws Exception {
        UUID userId = UUID.randomUUID();
        UserPrincipal principal = UserPrincipal.create(userId, "device-user@example.com", "USER");

        RegisterDeviceRequest request = RegisterDeviceRequest.builder()
                .installationId("inst-uuid-1234")
                .platform(DevicePlatform.ANDROID)
                .pushToken("fcm-token-xyz")
                .appVersion("1.0.0")
                .deviceModel("Pixel 8 Pro")
                .osVersion("Android 15")
                .build();

        DeviceResponse response = DeviceResponse.builder()
                .id(UUID.randomUUID())
                .installationId("inst-uuid-1234")
                .platform(DevicePlatform.ANDROID)
                .pushToken("fcm-token-xyz")
                .appVersion("1.0.0")
                .lastActiveAt(Instant.now())
                .build();

        when(deviceService.registerDevice(eq(userId), any(RegisterDeviceRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/devices/register")
                        .with(SecurityMockMvcRequestPostProcessors.user(principal))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.installationId").value("inst-uuid-1234"))
                .andExpect(jsonPath("$.data.platform").value("ANDROID"));
    }
}
