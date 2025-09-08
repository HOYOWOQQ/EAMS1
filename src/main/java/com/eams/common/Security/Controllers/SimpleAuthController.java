package com.eams.common.Security.Controllers;

import com.eams.common.Security.Services.CustomUserDetails;
import com.eams.common.Security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.DisabledException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class SimpleAuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {

        String account = loginRequest.get("account");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        
//        response.put("success", false);
//        response.put("message", "請先驗證信箱後再登入");
//        return ResponseEntity.status(422).body(response);
        
        try {
            // 使用 Spring Security 認證
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(account, password)
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            
            // 檢查帳號是否被管理員停用
            if (userDetails.getStatus() != null && !userDetails.getStatus()) {
                response.put("success", false);
                response.put("message", "帳號已停用，請聯絡管理員");
                return ResponseEntity.status(403).body(response);
            }
            
            // 檢查帳號是否已驗證
            if (!userDetails.isEmailVerified()) {
                response.put("success", false);
                response.put("message", "請先驗證信箱後再登入");
                return ResponseEntity.status(422).body(response);
            }
            
            // 生成 JWT Token
            String accessToken = jwtTokenUtil.generateAccessToken(userDetails);
            
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("message", "登入成功");
            response.put("tokenType", "Bearer");
            response.put("accessToken", accessToken);
            response.put("userId", userDetails.getId());
            response.put("username", userDetails.getUsername());
            response.put("name", userDetails.getName());
            response.put("email", userDetails.getEmail());
            response.put("role", userDetails.getRole());
            response.put("roles", roles);
            response.put("permissions", userDetails.getPermissions()); // 新增：回傳權限列表

            return ResponseEntity.ok(response);

        } catch (DisabledException e) {
            response.put("success", false);
            response.put("message", "帳號已停用，請聯絡管理員");
            return ResponseEntity.status(403).body(response);
        } catch (BadCredentialsException e) {
            response.put("success", false);
            response.put("message", "帳號或密碼錯誤");
            return ResponseEntity.status(401).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "登入失敗: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("未認證的用戶");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", userDetails.getId());
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("name", userDetails.getName());
        userInfo.put("email", userDetails.getEmail());
        userInfo.put("phone", userDetails.getPhone());
        userInfo.put("role", userDetails.getRole());
        userInfo.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(userInfo);
    }
}