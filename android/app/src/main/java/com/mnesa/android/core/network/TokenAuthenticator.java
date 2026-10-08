package com.mnesa.android.core.network;

import com.mnesa.android.core.security.SecureTokenManager;
import com.mnesa.android.data.remote.api.AuthApiService;
import com.mnesa.android.data.remote.dto.ApiResponseDto;
import com.mnesa.android.data.remote.dto.AuthResponseDto;
import com.mnesa.android.data.remote.dto.RefreshTokenRequestDto;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Automatically intercepts 401 responses and refreshes the JWT access token synchronously.
 */
public class TokenAuthenticator implements Authenticator {

    private final SecureTokenManager tokenManager;
    private AuthApiService authApiService;

    public TokenAuthenticator(SecureTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    public void setAuthApiService(AuthApiService authApiService) {
        this.authApiService = authApiService;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        // Prevent infinite loops if refresh fails
        if (responseCount(response) >= 2) {
            return null;
        }

        String refreshToken = tokenManager.getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty() || authApiService == null) {
            tokenManager.clearSession();
            return null;
        }

        synchronized (this) {
            // Check if another thread already refreshed the token
            String currentToken = tokenManager.getAccessToken();
            String requestToken = response.request().header("Authorization");
            if (requestToken != null && !requestToken.equals("Bearer " + currentToken)) {
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + currentToken)
                        .build();
            }

            retrofit2.Response<ApiResponseDto<AuthResponseDto>> refreshResponse =
                    authApiService.refresh(new RefreshTokenRequestDto(refreshToken)).execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null && refreshResponse.body().getData() != null) {
                AuthResponseDto authData = refreshResponse.body().getData();
                tokenManager.saveAccessToken(authData.getAccessToken());
                if (authData.getRefreshToken() != null) {
                    tokenManager.saveSession(
                            authData.getAccessToken(),
                            authData.getRefreshToken(),
                            tokenManager.getUserId(),
                            tokenManager.getUserEmail(),
                            tokenManager.getUserName()
                    );
                }
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + authData.getAccessToken())
                        .build();
            } else {
                tokenManager.clearSession();
                return null;
            }
        }
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
