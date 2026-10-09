package com.mnesa.backend.modules.user.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.auth.repository.AuthIdentityRepository;
import com.mnesa.backend.modules.auth.repository.PasswordResetTokenRepository;
import com.mnesa.backend.modules.auth.repository.RefreshTokenRepository;
import com.mnesa.backend.modules.device.repository.DeviceInstallationRepository;
import com.mnesa.backend.modules.user.domain.ReminderTimingPreference;
import com.mnesa.backend.modules.user.domain.User;
import com.mnesa.backend.modules.user.domain.UserPreferences;
import com.mnesa.backend.modules.user.dto.UpdatePreferencesRequest;
import com.mnesa.backend.modules.user.dto.UpdateProfileRequest;
import com.mnesa.backend.modules.user.dto.UserPreferencesDto;
import com.mnesa.backend.modules.user.dto.UserProfileResponse;
import com.mnesa.backend.modules.attachment.repository.AttachmentRepository;
import com.mnesa.backend.modules.user.repository.UserPreferencesRepository;
import com.mnesa.backend.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DeviceInstallationRepository deviceInstallationRepository;
    private final AttachmentRepository attachmentRepository;

    public UserService(UserRepository userRepository,
                       UserPreferencesRepository userPreferencesRepository,
                       AuthIdentityRepository authIdentityRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordResetTokenRepository passwordResetTokenRepository,
                       DeviceInstallationRepository deviceInstallationRepository,
                       AttachmentRepository attachmentRepository) {
        this.userRepository = userRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.authIdentityRepository = authIdentityRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.deviceInstallationRepository = deviceInstallationRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(UUID userId) {
        User user = getUserOrThrow(userId);
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(user));

        return toProfileResponse(user, preferences);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = getUserOrThrow(userId);
        user.setFullName(request.getFullName().trim());
        final User savedUser = userRepository.save(user);

        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(savedUser));

        return toProfileResponse(savedUser, preferences);
    }

    @Transactional(readOnly = true)
    public UserPreferencesDto getPreferences(UUID userId) {
        User user = getUserOrThrow(userId);
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(user));

        return toPreferencesDto(preferences);
    }

    @Transactional
    public UserPreferencesDto updatePreferences(UUID userId, UpdatePreferencesRequest request) {
        User user = getUserOrThrow(userId);
        UserPreferences preferences = userPreferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferences(user));

        if (request.getInterests() != null) {
            preferences.setInterests(request.getInterests());
        }
        if (request.getReminderTiming() != null) {
            preferences.setReminderTiming(request.getReminderTiming());
        }
        if (request.getEmailNotificationsEnabled() != null) {
            preferences.setEmailNotificationsEnabled(request.getEmailNotificationsEnabled());
        }
        if (request.getPushNotificationsEnabled() != null) {
            preferences.setPushNotificationsEnabled(request.getPushNotificationsEnabled());
        }
        if (request.getTimezone() != null && !request.getTimezone().isBlank()) {
            preferences.setTimezone(request.getTimezone());
        }

        preferences = userPreferencesRepository.save(preferences);
        return toPreferencesDto(preferences);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = getUserOrThrow(userId);

        log.info("Executing GDPR-compliant cascading account deletion for user ID: {}", userId);

        // Delete associated records
        deviceInstallationRepository.deleteByUserId(userId);
        refreshTokenRepository.deleteByUserId(userId);
        passwordResetTokenRepository.deleteByUserId(userId);
        authIdentityRepository.deleteByUserId(userId);
        userPreferencesRepository.deleteByUserId(userId);
        attachmentRepository.deleteByUserId(userId);

        // Delete user
        userRepository.delete(user);
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private UserPreferences createDefaultPreferences(User user) {
        UserPreferences defaultPrefs = UserPreferences.builder()
                .user(user)
                .interests(new ArrayList<>())
                .reminderTiming(ReminderTimingPreference.STANDARD)
                .emailNotificationsEnabled(true)
                .pushNotificationsEnabled(true)
                .build();
        return userPreferencesRepository.save(defaultPrefs);
    }

    private UserProfileResponse toProfileResponse(User user, UserPreferences preferences) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .preferences(toPreferencesDto(preferences))
                .build();
    }

    private UserPreferencesDto toPreferencesDto(UserPreferences preferences) {
        return UserPreferencesDto.builder()
                .interests(preferences.getInterests())
                .reminderTiming(preferences.getReminderTiming())
                .emailNotificationsEnabled(preferences.isEmailNotificationsEnabled())
                .pushNotificationsEnabled(preferences.isPushNotificationsEnabled())
                .timezone(preferences.getTimezone())
                .build();
    }
}
