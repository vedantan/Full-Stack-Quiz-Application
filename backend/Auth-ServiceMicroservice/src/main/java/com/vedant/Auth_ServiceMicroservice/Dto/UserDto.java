package com.vedant.Auth_ServiceMicroservice.Dto;

public class UserDto {
    private String username;
    private String role;

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserDto(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
