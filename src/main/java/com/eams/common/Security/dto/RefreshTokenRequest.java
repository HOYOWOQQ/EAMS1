package com.eams.common.Security.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequest {
    @NotBlank(message = "Refresh token 不能為空")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}