package com.mnesa.android.domain.model;

public class User {
    private final String id;
    private final String email;
    private final String fullName;
    private final String role;
    private final String status;

    public User(String id, String email, String fullName, String role, String status) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}
