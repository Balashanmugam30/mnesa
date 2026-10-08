package com.mnesa.backend.modules.user.repository;

import com.mnesa.backend.modules.user.domain.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, UUID> {

    Optional<UserPreferences> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
