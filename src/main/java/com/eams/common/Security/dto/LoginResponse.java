package com.eams.common.Security.dto;

import java.util.List;

public class LoginResponse {
    private String tokenType = "Bearer";
    private String accessToken;
    private String refreshToken;
    private Integer userId;
    private String username;
    private String name;
    private String email;
    private List<String> roles;
    private List<String> permissions;

    public LoginResponse(String accessToken, String refreshToken, Integer userId, 
                        String username, String name, String email, 
                        List<String> roles, List<String> permissions) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.permissions = permissions;
    }

    // Getters and Setters
    public String getTokenType() { return tokenType; }
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
    public List<String> getPermissions() { return permissions; }
}