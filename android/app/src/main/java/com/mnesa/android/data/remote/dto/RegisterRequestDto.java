package com.mnesa.android.data.remote.dto;

public class RegisterRequestDto {
    private String email;
    private String password;
    private String fullName;

    public RegisterRequestDto(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
}
