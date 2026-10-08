package com.mnesa.android.data.repository;

import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.remote.api.AuthApiService;
import com.mnesa.android.data.remote.dto.*;
import com.mnesa.android.domain.model.AuthSession;
import com.mnesa.android.domain.model.User;
import com.mnesa.android.domain.repository.AuthRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class AuthRepositoryImpl implements AuthRepository {

    private final AuthApiService authApiService;
    private final SecureTokenManager tokenManager;

    public AuthRepositoryImpl(AuthApiService authApiService, SecureTokenManager tokenManager) {
        this.authApiService = authApiService;
        this.tokenManager = tokenManager;
    }

    @Override
    public Single<AuthSession> login(String email, String password) {
        return authApiService.login(new LoginRequestDto(email, password))
                .map(response -> {
                    AuthResponseDto dto = response.getData();
                    UserDto userDto = dto.getUser();
                    tokenManager.saveSession(
                            dto.getAccessToken(),
                            dto.getRefreshToken(),
                            userDto.getId(),
                            userDto.getEmail(),
                            userDto.getFullName()
                    );
                    User user = new User(userDto.getId(), userDto.getEmail(), userDto.getFullName(), userDto.getRole(), userDto.getStatus());
                    return new AuthSession(user, dto.getAccessToken(), dto.getRefreshToken(), dto.getExpiresIn());
                });
    }

    @Override
    public Single<AuthSession> register(String email, String password, String fullName) {
        return authApiService.register(new RegisterRequestDto(email, password, fullName))
                .map(response -> {
                    AuthResponseDto dto = response.getData();
                    UserDto userDto = dto.getUser();
                    tokenManager.saveSession(
                            dto.getAccessToken(),
                            dto.getRefreshToken(),
                            userDto.getId(),
                            userDto.getEmail(),
                            userDto.getFullName()
                    );
                    User user = new User(userDto.getId(), userDto.getEmail(), userDto.getFullName(), userDto.getRole(), userDto.getStatus());
                    return new AuthSession(user, dto.getAccessToken(), dto.getRefreshToken(), dto.getExpiresIn());
                });
    }

    @Override
    public Single<AuthSession> googleSignIn(String idToken) {
        return authApiService.googleSignIn(new GoogleAuthRequestDto(idToken))
                .map(response -> {
                    AuthResponseDto dto = response.getData();
                    UserDto userDto = dto.getUser();
                    tokenManager.saveSession(
                            dto.getAccessToken(),
                            dto.getRefreshToken(),
                            userDto.getId(),
                            userDto.getEmail(),
                            userDto.getFullName()
                    );
                    User user = new User(userDto.getId(), userDto.getEmail(), userDto.getFullName(), userDto.getRole(), userDto.getStatus());
                    return new AuthSession(user, dto.getAccessToken(), dto.getRefreshToken(), dto.getExpiresIn());
                });
    }

    @Override
    public Completable forgotPassword(String email) {
        return authApiService.forgotPassword(new ForgotPasswordRequestDto(email));
    }

    @Override
    public Completable logout() {
        String refreshToken = tokenManager.getRefreshToken();
        RefreshTokenRequestDto request = refreshToken != null ? new RefreshTokenRequestDto(refreshToken) : null;
        return authApiService.logout(request)
                .onErrorComplete()
                .doFinally(tokenManager::clearSession);
    }

    @Override
    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }

    @Override
    public String getCurrentUserEmail() {
        return tokenManager.getUserEmail();
    }

    @Override
    public String getCurrentUserName() {
        return tokenManager.getUserName();
    }
}
