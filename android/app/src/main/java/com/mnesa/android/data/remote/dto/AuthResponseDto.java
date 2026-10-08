package com.mnesa.android.data.remote.dto;

public class AuthResponseDto {
    private UserDto user;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;

    public AuthResponseDto() {}

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
