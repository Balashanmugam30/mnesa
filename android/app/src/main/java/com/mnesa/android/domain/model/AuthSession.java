package com.mnesa.android.domain.model;

public class AuthSession {
    private final User user;
    private final String accessToken;
    private final String refreshToken;
    private final long expiresIn;

    public AuthSession(User user, String accessToken, String refreshToken, long expiresIn) {
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    public User getUser() { return user; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long getExpiresIn() { return expiresIn; }
}
