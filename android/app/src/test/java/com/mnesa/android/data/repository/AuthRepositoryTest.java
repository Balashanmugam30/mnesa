package com.mnesa.android.data.repository;

import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.remote.api.AuthApiService;
import com.mnesa.android.data.remote.dto.*;
import com.mnesa.android.domain.model.AuthSession;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthRepositoryTest {

    @Mock
    private AuthApiService authApiService;

    @Mock
    private SecureTokenManager tokenManager;

    private AuthRepositoryImpl authRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authRepository = new AuthRepositoryImpl(authApiService, tokenManager);
    }

    @Test
    public void testLoginSuccessSavesSession() {
        ApiResponseDto<AuthResponseDto> apiResponse = new ApiResponseDto<>();
        AuthResponseDto authDto = new AuthResponseDto();
        UserDto userDto = new UserDto();
        userDto.setId("uuid-1234");
        userDto.setEmail("test@mnesa.ai");
        userDto.setFullName("Mnesa Tester");
        userDto.setRole("USER");
        userDto.setStatus("ACTIVE");
        authDto.setUser(userDto);
        authDto.setAccessToken("jwt-access-token");
        authDto.setRefreshToken("jwt-refresh-token");
        authDto.setExpiresIn(3600);
        apiResponse.setData(authDto);

        when(authApiService.login(any(LoginRequestDto.class))).thenReturn(Single.just(apiResponse));

        AuthSession session = authRepository.login("test@mnesa.ai", "password123").blockingGet();

        assertNotNull(session);
        assertEquals("jwt-access-token", session.getAccessToken());
        assertEquals("test@mnesa.ai", session.getUser().getEmail());
        verify(tokenManager).saveSession("jwt-access-token", "jwt-refresh-token", "uuid-1234", "test@mnesa.ai", "Mnesa Tester");
    }

    @Test
    public void testLogoutClearsSession() {
        when(tokenManager.getRefreshToken()).thenReturn("refresh-token-xyz");
        when(authApiService.logout(any())).thenReturn(Completable.complete());

        authRepository.logout().blockingAwait();

        verify(tokenManager).clearSession();
    }

    @Test
    public void testIsLoggedInDelegatesToTokenManager() {
        when(tokenManager.isLoggedIn()).thenReturn(true);
        assertTrue(authRepository.isLoggedIn());

        when(tokenManager.isLoggedIn()).thenReturn(false);
        assertFalse(authRepository.isLoggedIn());
    }
}
