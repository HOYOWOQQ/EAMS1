package com.eams.Controller.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.eams.Service.member.MemberService;
import com.eams.Entity.member.Member;
import com.eams.common.log.util.UserContextUtil;
import com.eams.common.log.annotation.LogOperation;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/member")
@Slf4j
public class ChangePasswordApiController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 檢查密碼強度
     */
    private boolean isPasswordStrong(String password) {
        // 至少8個字元
        if (password.length() < 8) {
            return false;
        }

        // 包含英文字母
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        
        // 包含數字
        boolean hasNumber = password.matches(".*\\d.*");

        return hasLetter && hasNumber;
    }
    
    /**
     * 驗證密碼修改權限（只能修改自己的密碼）
     */
    private boolean canChangePassword(Integer targetUserId) {
        Long currentUserIdLong = userContextUtil.getCurrentUserId();
        if (currentUserIdLong == null) {
            return false;
        }
        
        Integer currentUserId = currentUserIdLong.intValue();
        boolean canChange = currentUserId.equals(targetUserId);
        
        log.debug("🔒 密碼修改權限檢查 - 當前用戶: {}, 目標用戶: {}, 可修改: {}", 
            currentUserId, targetUserId, canChange);
        
        return canChange;
    }

    // ===== API 端點 =====

    // 取得密碼修改頁面資料
    @GetMapping("/change-password")
    public ResponseEntity<?> getChangePasswordData(HttpSession session) {
        try {
            log.debug("🔍 獲取密碼修改頁面資料");
            
            // 使用 UserContextUtil 檢查登入狀態
            Long currentUserIdLong = userContextUtil.getCurrentUserId();
            String currentUsername = userContextUtil.getCurrentUsername();
            String authType = userContextUtil.getCurrentAuthType();
            
            log.debug("🔍 當前用戶信息 - ID: {}, 用戶名: {}, 認證方式: {}", 
                currentUserIdLong, currentUsername, authType);

            if (currentUserIdLong == null) {
                log.warn("❌ 未登入用戶嘗試訪問密碼修改頁面");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            Integer currentUserId = currentUserIdLong.intValue();

            // 取得會員資料
            Member member;
            try {
                member = memberService.getMemberById(currentUserId);
                log.debug("✅ 成功獲取會員資料");
            } catch (Exception e) {
                log.error("❌ 查詢會員資料時發生錯誤 - 用戶ID: {}", currentUserId, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "查詢會員資料時發生錯誤: " + e.getMessage()));
            }

            if (member == null) {
                log.warn("❌ 查無會員 - 用戶ID: {}", currentUserId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "查無此會員"));
            }

            log.debug("✅ 找到會員: {} (ID: {})", member.getName(), member.getId());

            // 回傳必要資料
            try {
                log.debug("🔍 準備建立回應資料");
                
                // 處理可能為null的lastPwdChange
                Object lastPwdChangeValue = member.getLastPwdChange();
                String memberNameValue = member.getName() != null ? member.getName() : "未知";
                
                log.debug("📋 會員資料 - 姓名: {}, 上次密碼變更: {}", memberNameValue, lastPwdChangeValue);
                
                // 使用HashMap避免Map.of()的null限制
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("lastPwdChange", lastPwdChangeValue); // 允許null
                responseData.put("memberName", memberNameValue);
                responseData.put("authType", authType);
                responseData.put("currentUser", Map.of(
                    "id", currentUserId,
                    "username", currentUsername
                ));
                
                log.info("✅ 用戶 {} ({}) 獲取密碼修改頁面資料成功", currentUsername, currentUserId);
                return ResponseEntity.ok(responseData);
                
            } catch (Exception e) {
                log.error("❌ 建立回應資料時發生錯誤", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "建立回應時發生錯誤: " + e.getMessage()));
            }

        } catch (Exception e) {
            log.error("❌ 取得密碼修改頁面資料時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "系統錯誤: " + e.getMessage()));
        }
    }

    // 修改密碼
    @PostMapping("/change-password")
    @LogOperation(
        type = "PASSWORD_CHANGE",
        name = "修改密碼",
        description = "用戶修改自己的登入密碼",
        targetType = "MEMBER",
        logArgs = false, // 不記錄參數（包含密碼）
        logResult = false // 不記錄結果（可能包含敏感信息）
    )
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> passwordData,
            HttpSession session) {

        try {
            log.info("🔍 密碼修改請求");
            
            // 使用 UserContextUtil 檢查登入狀態
            Long currentUserIdLong = userContextUtil.getCurrentUserId();
            String currentUsername = userContextUtil.getCurrentUsername();
            String authType = userContextUtil.getCurrentAuthType();
            
            log.debug("🔍 密碼修改 - 用戶ID: {}, 用戶名: {}, 認證方式: {}", 
                currentUserIdLong, currentUsername, authType);

            if (currentUserIdLong == null) {
                log.warn("❌ 未登入用戶嘗試修改密碼");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            Integer currentUserId = currentUserIdLong.intValue();

            // 取得參數
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            String confirmPassword = passwordData.get("confirmPassword");

            log.debug("🔍 密碼修改請求 - 用戶: {} ({})", currentUsername, currentUserId);

            // 基本驗證
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "請輸入目前密碼"));
            }

            if (newPassword == null || newPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "請輸入新密碼"));
            }

            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "請確認新密碼"));
            }

            // 取得會員資料
            Member member = memberService.getMemberById(currentUserId);
            if (member == null) {
                log.warn("❌ 查無會員 - 用戶ID: {}", currentUserId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "查無此會員"));
            }

            // 🔒 權限檢查（確保只能修改自己的密碼）
            if (!canChangePassword(currentUserId)) {
                log.warn("❌ 權限檢查失敗 - 用戶ID: {}", currentUserId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "無權限修改密碼"));
            }

            // 驗證舊密碼
            if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
                log.warn("❌ 舊密碼驗證失敗 - 用戶: {}", currentUsername);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "目前密碼錯誤"));
            }

            // 密碼規則驗證
            if (newPassword.length() < 8) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "新密碼長度至少8個字元"));
            }

            // 檢查密碼強度
            if (!isPasswordStrong(newPassword)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "新密碼必須包含英文字母和數字"));
            }

            // 確認密碼一致
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "兩次輸入的新密碼不一致"));
            }

            // 檢查新密碼是否與舊密碼相同
            if (newPassword.equals(oldPassword)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "新密碼不可與目前密碼相同"));
            }

            log.debug("✅ 密碼驗證通過，開始更新 - 用戶: {}", currentUsername);

            // 更新密碼
            member.setPassword(passwordEncoder.encode(newPassword.trim()));
            member.setLastPwdChange(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            memberService.updateMember(member);

            log.info("✅ 密碼更新成功 - 用戶: {} ({}), 認證方式: {}", 
                currentUsername, currentUserId, authType);

            // 🆕 增強的回應處理
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密碼修改成功");
            response.put("authType", authType);
            response.put("timestamp", LocalDateTime.now());
            
            // 根據認證方式決定後續處理
            if ("SESSION".equals(authType)) {
                // Session 認證：使Session失效（強制重新登入）
                session.invalidate();
                response.put("requireLogin", true);
                response.put("message", "密碼修改成功，請重新登入");
                log.info("✅ Session已失效，需要重新登入");
            } else if ("JWT".equals(authType)) {
                // JWT 認證：建議重新獲取token，但不強制
                response.put("requireLogin", false);
                response.put("message", "密碼修改成功，建議重新登入以獲取新的Token");
                response.put("suggestion", "建議重新登入獲取新的認證Token");
                log.info("✅ JWT認證用戶密碼修改完成，建議重新獲取Token");
            } else {
                // 其他情況
                response.put("requireLogin", false);
                response.put("message", "密碼修改成功");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ 修改密碼時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "密碼修改失敗：" + e.getMessage()));
        }
    }

    // 🆕 新增：檢查密碼強度的 API
    @PostMapping("/check-password-strength")
    public ResponseEntity<?> checkPasswordStrength(@RequestBody Map<String, String> data) {
        try {
            // 檢查登入狀態
            Long currentUserId = userContextUtil.getCurrentUserId();
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String password = data.get("password");
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "請提供密碼"));
            }

            // 檢查各項強度條件
            boolean lengthOk = password.length() >= 8;
            boolean hasLetter = password.matches(".*[a-zA-Z].*");
            boolean hasNumber = password.matches(".*\\d.*");
            boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
            
            int score = 0;
            if (lengthOk) score++;
            if (hasLetter) score++;
            if (hasNumber) score++;
            if (hasSpecial) score++;

            String strength;
            if (score < 2) {
                strength = "弱";
            } else if (score < 3) {
                strength = "中";
            } else {
                strength = "強";
            }

            Map<String, Object> response = Map.of(
                "valid", lengthOk && hasLetter && hasNumber,
                "strength", strength,
                "score", score,
                "checks", Map.of(
                    "length", lengthOk,
                    "hasLetter", hasLetter,
                    "hasNumber", hasNumber,
                    "hasSpecial", hasSpecial
                ),
                "suggestions", getSuggestions(lengthOk, hasLetter, hasNumber, hasSpecial)
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ 檢查密碼強度時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "檢查密碼強度失敗"));
        }
    }

    // 🆕 新增：獲取密碼修改歷史（僅顯示修改時間）
    @GetMapping("/password-history")
    public ResponseEntity<?> getPasswordHistory() {
        try {
            // 檢查登入狀態
            Long currentUserIdLong = userContextUtil.getCurrentUserId();
            if (currentUserIdLong == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            Integer currentUserId = currentUserIdLong.intValue();
            Member member = memberService.getMemberById(currentUserId);
            
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "查無此會員"));
            }

            Map<String, Object> response = Map.of(
                "lastPwdChange", member.getLastPwdChange(),
                "createTime", member.getCreateTime(),
                "updateTime", member.getUpdateTime(),
                "memberName", member.getName()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ 獲取密碼修改歷史時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取密碼歷史失敗"));
        }
    }

    // ===== 私有輔助方法 =====

    /**
     * 獲取密碼改進建議
     */
    private java.util.List<String> getSuggestions(boolean lengthOk, boolean hasLetter, 
                                                  boolean hasNumber, boolean hasSpecial) {
        java.util.List<String> suggestions = new java.util.ArrayList<>();
        
        if (!lengthOk) {
            suggestions.add("密碼長度至少需要8個字元");
        }
        if (!hasLetter) {
            suggestions.add("請加入英文字母");
        }
        if (!hasNumber) {
            suggestions.add("請加入數字");
        }
        if (!hasSpecial) {
            suggestions.add("建議加入特殊符號以增加安全性");
        }
        
        if (suggestions.isEmpty()) {
            suggestions.add("密碼強度良好");
        }
        
        return suggestions;
    }
}