package com.mnesa.android.data.remote.dto;

public class UserProfileDto {
    private String id;
    private String email;
    private String fullName;
    private String role;
    private String status;
    private UserPreferencesDto preferences;

    public UserProfileDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UserPreferencesDto getPreferences() { return preferences; }
    public void setPreferences(UserPreferencesDto preferences) { this.preferences = preferences; }
}
