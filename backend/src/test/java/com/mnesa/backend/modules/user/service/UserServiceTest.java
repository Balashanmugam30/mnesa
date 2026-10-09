package com.mnesa.backend.modules.user.service;

import com.mnesa.backend.common.exception.ResourceNotFoundException;
import com.mnesa.backend.modules.attachment.repository.AttachmentRepository;
import com.mnesa.backend.modules.auth.repository.AuthIdentityRepository;
import com.mnesa.backend.modules.auth.repository.PasswordResetTokenRepository;
import com.mnesa.backend.modules.auth.repository.RefreshTokenRepository;
import com.mnesa.backend.modules.device.repository.DeviceInstallationRepository;
import com.mnesa.backend.modules.user.domain.ReminderTimingPreference;
import com.mnesa.backend.modules.user.domain.User;
import com.mnesa.backend.modules.user.domain.UserPreferences;
import com.mnesa.backend.modules.user.domain.UserRole;
import com.mnesa.backend.modules.user.domain.UserStatus;
import com.mnesa.backend.modules.user.dto.UpdatePreferencesRequest;
import com.mnesa.backend.modules.user.dto.UserPreferencesDto;
import com.mnesa.backend.modules.user.dto.UserProfileResponse;
import com.mnesa.backend.modules.user.repository.UserPreferencesRepository;
import com.mnesa.backend.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPreferencesRepository userPreferencesRepository;

    @Mock
    private AuthIdentityRepository authIdentityRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private DeviceInstallationRepository deviceInstallationRepository;

    @Mock
    private AttachmentRepository attachmentRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User mockUser;
    private UserPreferences mockPreferences;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        mockUser = User.builder()
                .id(userId)
                .email("alex@example.com")
                .fullName("Alex Morgan")
                .passwordHash("argon2id$hashed")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        mockPreferences = UserPreferences.builder()
                .user(mockUser)
                .interests(new ArrayList<>(List.of("INTERNSHIP", "CONFERENCE")))
                .reminderTiming(ReminderTimingPreference.STANDARD)
                .emailNotificationsEnabled(true)
                .pushNotificationsEnabled(true)
                .timezone("America/New_York")
                .build();
    }

    @Test
    @DisplayName("getCurrentUser returns user profile and preferences")
    void getCurrentUserSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(mockPreferences));

        UserProfileResponse response = userService.getCurrentUser(userId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getEmail()).isEqualTo("alex@example.com");
        assertThat(response.getFullName()).isEqualTo("Alex Morgan");
        assertThat(response.getPreferences().getInterests()).contains("INTERNSHIP", "CONFERENCE");
        assertThat(response.getPreferences().getTimezone()).isEqualTo("America/New_York");
    }

    @Test
    @DisplayName("getCurrentUser throws ResourceNotFoundException if user does not exist")
    void getCurrentUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUser(userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updatePreferences updates user preferences fields correctly")
    void updatePreferencesSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userPreferencesRepository.findByUserId(userId)).thenReturn(Optional.of(mockPreferences));
        when(userPreferencesRepository.save(any(UserPreferences.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePreferencesRequest request = UpdatePreferencesRequest.builder()
                .interests(List.of("FELLOWSHIP"))
                .reminderTiming(ReminderTimingPreference.AGGRESSIVE)
                .emailNotificationsEnabled(false)
                .pushNotificationsEnabled(true)
                .timezone("UTC")
                .build();

        UserPreferencesDto result = userService.updatePreferences(userId, request);

        assertThat(result.getInterests()).containsExactly("FELLOWSHIP");
        assertThat(result.getReminderTiming()).isEqualTo(ReminderTimingPreference.AGGRESSIVE);
        assertThat(result.isEmailNotificationsEnabled()).isFalse();
        assertThat(result.getTimezone()).isEqualTo("UTC");
    }

    @Test
    @DisplayName("deleteAccount executes GDPR deletion across tokens, preferences, attachments, and user")
    void deleteAccountSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        userService.deleteAccount(userId);

        verify(deviceInstallationRepository).deleteByUserId(userId);
        verify(refreshTokenRepository).deleteByUserId(userId);
        verify(passwordResetTokenRepository).deleteByUserId(userId);
        verify(authIdentityRepository).deleteByUserId(userId);
        verify(userPreferencesRepository).deleteByUserId(userId);
        verify(attachmentRepository).deleteByUserId(userId);
        verify(userRepository).delete(mockUser);
    }
}
