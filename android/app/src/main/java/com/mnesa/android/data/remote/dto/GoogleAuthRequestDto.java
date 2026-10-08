package com.mnesa.android.data.remote.dto;

public class GoogleAuthRequestDto {
    private String idToken;

    public GoogleAuthRequestDto(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() { return idToken; }
}
