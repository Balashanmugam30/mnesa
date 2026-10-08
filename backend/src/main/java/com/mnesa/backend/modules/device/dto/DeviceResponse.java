package com.mnesa.backend.modules.device.dto;

import com.mnesa.backend.modules.device.domain.DevicePlatform;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {
    private UUID id;
    private String installationId;
    private DevicePlatform platform;
    private String pushToken;
    private String appVersion;
    private Instant lastActiveAt;
}
