package com.mnesa.android.core.network;

import com.mnesa.android.core.security.SecureTokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Injects Authorization Bearer token into outgoing HTTP requests when available.
 */
public class AuthInterceptor implements Interceptor {

    private final SecureTokenManager tokenManager;

    public AuthInterceptor(SecureTokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = tokenManager.getAccessToken();

        if (token != null && !token.isEmpty() && !original.url().encodedPath().contains("/auth/")) {
            Request authenticated = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(authenticated);
        }

        return chain.proceed(original);
    }
}
