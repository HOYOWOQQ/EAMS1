package com.eams.common.Security.Controllers;

import com.eams.Entity.member.Member;
import com.eams.Repository.member.MemberRepository;
import com.eams.common.Security.util.JwtTokenUtil;
import com.eams.common.log.util.UserContextUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    @GetMapping("/test-user")
    public ResponseEntity<?> testCurrentUser() {
       // 🔍 詳細調試
       userContextUtil.debugCurrentUser();
       
       Map<String, Object> result = new HashMap<>();
       result.put("userId", userContextUtil.getCurrentUserId());
       result.put("username", userContextUtil.getCurrentUsername());
       result.put("role", userContextUtil.getCurrentUserRole());
       result.put("position", userContextUtil.getCurrentUserPosition());
       result.put("permissions", userContextUtil.getCurrentUserPermissions());
       result.put("authType", userContextUtil.getCurrentAuthType());
       
       return ResponseEntity.ok(result);
    }

    @GetMapping("/hello")
    public Map<String, String> hello() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hello! 系統正常運行");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }

    // 新增：簡單的 GET 端點生成 token
    @GetMapping("/simple-token")
    public Map<String, String> generateSimpleToken(@RequestParam(defaultValue = "testuser") String username) {
        Map<String, String> response = new HashMap<>();
        try {
            String token = jwtTokenUtil.generateTestToken(username);
            response.put("success", "true");
            response.put("token", token);
            response.put("username", username);
            response.put("message", "Token 生成成功");
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", "Token 生成失敗: " + e.getMessage());
        }
        return response;
    }

    /**
     * 創建測試用戶
     */
    @GetMapping("/create-test-user")
    public Map<String, Object> createTestUser() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 檢查是否已存在
            if (memberRepository.existsByAccount("test")) {
                response.put("success", false);
                response.put("message", "測試用戶已存在");
                return response;
            }
            
            Member testUser = new Member();
            testUser.setAccount("test");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setName("測試用戶");
            testUser.setRole("teacher"); // 使用現有的有效角色
            testUser.setEmail("test@example.com");
            testUser.setVerified(true);
            testUser.setStatus(true);
            testUser.setCreateTime(LocalDateTime.now());
            testUser.setUpdateTime(LocalDateTime.now());
            
            memberRepository.save(testUser);
            
            response.put("success", true);
            response.put("message", "測試用戶創建成功");
            response.put("account", "test");
            response.put("password", "test123");
            response.put("role", "teacher");
            
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "創建失敗: " + e.getMessage());
            return response;
        }
    }

    /**
     * 重設特定用戶密碼
     */
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestBody Map<String, String> request) {
        String account = request.get("account");
        String newPassword = request.get("newPassword");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Member member = memberRepository.findMemberByAccount(account);
            
            if (member == null) {
                response.put("success", false);
                response.put("message", "帳號不存在");
                return response;
            }
            
            // 重設密碼
            member.setPassword(passwordEncoder.encode(newPassword));
            member.setUpdateTime(LocalDateTime.now());
            memberRepository.save(member);
            
            response.put("success", true);
            response.put("message", "密碼已重設為: " + newPassword);
            response.put("account", account);
            
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "重設失敗: " + e.getMessage());
            return response;
        }
    }

    /**
     * 列出所有用戶（僅供測試）
     */
    @GetMapping("/list-users")
    public Map<String, Object> listUsers() {
        try {
            var members = memberRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", members.size());
            response.put("users", members.stream().map(m -> {
                Map<String, Object> user = new HashMap<>();
                user.put("account", m.getAccount());
                user.put("name", m.getName());
                user.put("role", m.getRole());
                user.put("status", m.getStatus());
                user.put("verified", m.getVerified());
                return user;
            }).toList());
            
            return response;
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "查詢失敗: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/generate-token")
    public Map<String, String> generateToken(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        if (username == null || username.isEmpty()) {
            username = "testuser";
        }

        Map<String, String> response = new HashMap<>();
        try {
            String token = jwtTokenUtil.generateTestToken(username);
            response.put("success", "true");
            response.put("token", token);
            response.put("username", username);
            response.put("message", "Token 生成成功");
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", "Token 生成失敗: " + e.getMessage());
        }
        return response;
    }

    @PostMapping("/validate-token")
    public Map<String, String> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        
        Map<String, String> response = new HashMap<>();
        try {
            boolean isValid = jwtTokenUtil.validateToken(token);
            if (isValid) {
                String username = jwtTokenUtil.getUsernameFromToken(token);
                response.put("success", "true");
                response.put("valid", "true");
                response.put("username", username);
                response.put("message", "Token 有效");
            } else {
                response.put("success", "true");
                response.put("valid", "false");
                response.put("message", "Token 無效");
            }
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", "Token 驗證失敗: " + e.getMessage());
        }
        return response;
    }
}