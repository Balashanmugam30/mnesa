package com.mnesa.backend.modules.device.repository;

import com.mnesa.backend.modules.device.domain.DeviceInstallation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceInstallationRepository extends JpaRepository<DeviceInstallation, UUID> {

    Optional<DeviceInstallation> findByInstallationId(String installationId);

    List<DeviceInstallation> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
