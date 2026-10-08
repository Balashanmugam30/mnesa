package com.mnesa.backend.modules.device.dto;

import com.mnesa.backend.modules.device.domain.DevicePlatform;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDeviceRequest {

    @NotBlank(message = "Installation ID is required")
    private String installationId;

    @Builder.Default
    private DevicePlatform platform = DevicePlatform.ANDROID;

    private String pushToken;
    private String appVersion;
    private String deviceModel;
    private String osVersion;
}
