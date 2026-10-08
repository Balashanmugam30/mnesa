package com.mnesa.android.data.repository;

import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.remote.api.UserApiService;
import com.mnesa.android.data.remote.dto.UpdatePreferencesRequestDto;
import com.mnesa.android.data.remote.dto.UpdateProfileRequestDto;
import com.mnesa.android.data.remote.dto.UserPreferencesDto;
import com.mnesa.android.data.remote.dto.UserProfileDto;
import com.mnesa.android.domain.model.User;
import com.mnesa.android.domain.model.UserPreferences;
import com.mnesa.android.domain.repository.UserRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class UserRepositoryImpl implements UserRepository {

    private final UserApiService userApiService;
    private final SecureTokenManager tokenManager;

    public UserRepositoryImpl(UserApiService userApiService, SecureTokenManager tokenManager) {
        this.userApiService = userApiService;
        this.tokenManager = tokenManager;
    }

    @Override
    public Single<User> getProfile() {
        return userApiService.getProfile()
                .map(response -> {
                    UserProfileDto dto = response.getData();
                    return new User(dto.getId(), dto.getEmail(), dto.getFullName(), dto.getRole(), dto.getStatus());
                });
    }

    @Override
    public Single<User> updateProfile(String fullName) {
        return userApiService.updateProfile(new UpdateProfileRequestDto(fullName))
                .map(response -> {
                    UserProfileDto dto = response.getData();
                    return new User(dto.getId(), dto.getEmail(), dto.getFullName(), dto.getRole(), dto.getStatus());
                });
    }

    @Override
    public Single<UserPreferences> getPreferences() {
        return userApiService.getPreferences()
                .map(response -> {
                    UserPreferencesDto dto = response.getData();
                    return new UserPreferences(
                            dto.getInterests(),
                            dto.getReminderTiming(),
                            dto.isEmailNotificationsEnabled(),
                            dto.isPushNotificationsEnabled()
                    );
                });
    }

    @Override
    public Single<UserPreferences> updatePreferences(UserPreferences preferences) {
        UpdatePreferencesRequestDto request = new UpdatePreferencesRequestDto(
                preferences.getInterests(),
                preferences.getReminderTiming(),
                preferences.isEmailNotificationsEnabled(),
                preferences.isPushNotificationsEnabled()
        );
        return userApiService.updatePreferences(request)
                .map(response -> {
                    UserPreferencesDto dto = response.getData();
                    return new UserPreferences(
                            dto.getInterests(),
                            dto.getReminderTiming(),
                            dto.isEmailNotificationsEnabled(),
                            dto.isPushNotificationsEnabled()
                    );
                });
    }

    @Override
    public Completable deleteAccount() {
        return userApiService.deleteAccount()
                .doFinally(tokenManager::clearSession);
    }
}
