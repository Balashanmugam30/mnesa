package com.mnesa.android.data.remote.dto;

public class RefreshTokenRequestDto {
    private String refreshToken;

    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() { return refreshToken; }
}
