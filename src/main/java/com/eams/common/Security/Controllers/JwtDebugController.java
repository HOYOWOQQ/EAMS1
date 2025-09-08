package com.eams.common.Security.Controllers;


import com.eams.common.Security.Services.CustomUserDetails;
import com.eams.common.Security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class JwtDebugController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    /**
     * 測試 JWT Token 解析
     */
    @GetMapping("/jwt-info")
    public ResponseEntity<?> getJwtInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 1. 檢查 Authorization Header
            String authHeader = request.getHeader("Authorization");
            response.put("authHeader", authHeader);
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                response.put("tokenReceived", true);
                response.put("tokenLength", token.length());
                response.put("tokenPreview", token.substring(0, Math.min(50, token.length())) + "...");
                
                // 2. 驗證 Token
                boolean isValid = jwtTokenUtil.validateToken(token);
                response.put("tokenValid", isValid);
                
                if (isValid) {
                    // 3. 解析 Token 內容
                    String username = jwtTokenUtil.getUsernameFromToken(token);
                    Integer userId = jwtTokenUtil.getUserIdFromToken(token);
                    
                    response.put("usernameFromToken", username);
                    response.put("userIdFromToken", userId);
                }
            } else {
                response.put("tokenReceived", false);
                response.put("message", "沒有收到 Authorization header 或格式不正確");
            }
            
            // 4. 檢查 SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            response.put("authenticationExists", auth != null);
            response.put("isAuthenticated", auth != null && auth.isAuthenticated());
            
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
                response.put("securityContextUser", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "name", user.getName(),
                    "permissionCount", user.getPermissions().size()
                ));
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 測試權限檢查
     */
    @GetMapping("/test-permission")
    public ResponseEntity<?> testPermission(@RequestParam String permission) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            if (auth == null || !auth.isAuthenticated()) {
                response.put("error", "未認證");
                return ResponseEntity.status(401).body(response);
            }
            
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            boolean hasPermission = user.getPermissions().contains(permission);
            
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("testPermission", permission);
            response.put("hasPermission", hasPermission);
            response.put("userPermissions", user.getPermissions());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 測試所有 Headers
     */
    @GetMapping("/headers")
    public ResponseEntity<?> getAllHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.put(headerName, request.getHeader(headerName));
        });
        
        return ResponseEntity.ok(Map.of("headers", headers));
    }
}