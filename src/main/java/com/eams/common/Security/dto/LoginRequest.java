package com.eams.common.Security.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "帳號不能為空")
    private String account;

    @NotBlank(message = "密碼不能為空")
    private String password;

    // Getters and Setters
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}