package com.mnesa.backend.modules.device.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.device.domain.DeviceInstallation;
import com.mnesa.backend.modules.device.domain.DevicePlatform;
import com.mnesa.backend.modules.device.dto.DeviceResponse;
import com.mnesa.backend.modules.device.dto.RegisterDeviceRequest;
import com.mnesa.backend.modules.device.repository.DeviceInstallationRepository;
import com.mnesa.backend.modules.user.domain.User;
import com.mnesa.backend.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class DeviceService {

    private final DeviceInstallationRepository deviceInstallationRepository;
    private final UserRepository userRepository;

    public DeviceService(DeviceInstallationRepository deviceInstallationRepository,
                         UserRepository userRepository) {
        this.deviceInstallationRepository = deviceInstallationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DeviceResponse registerDevice(UUID userId, RegisterDeviceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        DeviceInstallation installation = deviceInstallationRepository.findByInstallationId(request.getInstallationId())
                .orElseGet(() -> DeviceInstallation.builder()
                        .installationId(request.getInstallationId())
                        .user(user)
                        .platform(request.getPlatform() != null ? request.getPlatform() : DevicePlatform.ANDROID)
                        .build());

        installation.setUser(user);
        if (request.getPlatform() != null) {
            installation.setPlatform(request.getPlatform());
        }
        if (request.getPushToken() != null) {
            installation.setPushToken(request.getPushToken());
        }
        if (request.getAppVersion() != null) {
            installation.setAppVersion(request.getAppVersion());
        }
        if (request.getDeviceModel() != null) {
            installation.setDeviceModel(request.getDeviceModel());
        }
        if (request.getOsVersion() != null) {
            installation.setOsVersion(request.getOsVersion());
        }
        installation.setLastActiveAt(Instant.now());

        installation = deviceInstallationRepository.save(installation);

        return DeviceResponse.builder()
                .id(installation.getId())
                .installationId(installation.getInstallationId())
                .platform(installation.getPlatform())
                .pushToken(installation.getPushToken())
                .appVersion(installation.getAppVersion())
                .lastActiveAt(installation.getLastActiveAt())
                .build();
    }
}
