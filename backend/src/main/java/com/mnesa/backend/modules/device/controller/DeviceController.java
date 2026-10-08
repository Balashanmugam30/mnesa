package com.mnesa.backend.modules.device.controller;

import com.mnesa.backend.common.dto.ApiResponse;
import com.mnesa.backend.modules.auth.security.UserPrincipal;
import com.mnesa.backend.modules.device.dto.DeviceResponse;
import com.mnesa.backend.modules.device.dto.RegisterDeviceRequest;
import com.mnesa.backend.modules.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@Tag(name = "Device Management", description = "Endpoints for registering device installations and push notification tokens")
@SecurityRequirement(name = "BearerAuth")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register or refresh device", description = "Registers an Android or Web installation, device metadata, and FCM push token")
    public ResponseEntity<ApiResponse<DeviceResponse>> registerDevice(@AuthenticationPrincipal UserPrincipal principal,
                                                                      @Valid @RequestBody RegisterDeviceRequest request) {
        DeviceResponse response = deviceService.registerDevice(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
