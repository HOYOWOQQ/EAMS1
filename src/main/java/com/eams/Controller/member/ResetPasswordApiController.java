package com.eams.Controller.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.eams.Service.member.MemberService;
import com.eams.Service.member.EmailService;
import com.eams.utils.TokenUtil;
import com.eams.Entity.member.Member;

import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/member")
public class ResetPasswordApiController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;
    
    // 密碼驗證規則
    private static final Pattern LETTER_PATTERN = Pattern.compile("[a-zA-Z]");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d");

    // 發送重設密碼連結
    @PostMapping("/reset-password/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> requestData) {
        try {
            String account = requestData.get("account");
            
            System.out.println("[ResetPasswordApiController] 重設密碼請求，帳號: " + account);
            
            // 驗證參數
            if (account == null || account.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "請輸入帳號"));
            }
            
            // 查找會員
            Member member = memberService.findByAccount(account.trim());
            
            if (member != null) {
                System.out.println("[ResetPasswordApiController] 找到會員: " + member.getName());
                
                // 生成重設密碼Token
                String token = TokenUtil.generateToken();
                Timestamp expiry = Timestamp.valueOf(LocalDateTime.now().plusMinutes(10));
                
                // 更新會員的重設Token資訊
                member.setResetToken(token);
                member.setResetExpiry(expiry.toLocalDateTime());
                memberService.updateMember(member);
                
                //建構完整的重設連結
                String resetUrl = frontendUrl + "/resetPassword?id=" + member.getId() + "&token=" + token;
                
                //使用新的 EmailService 發送密碼重設郵件
                try {
                    emailService.sendPasswordResetEmail(
                        member.getEmail(), 
                        member.getName(), 
                        member.getId(), 
                        token
                    );
                    System.out.println("[ResetPasswordApiController] 密碼重設郵件已發送至: " + member.getEmail());
                } catch (Exception emailError) {
                    System.err.println("[ResetPasswordApiController] 發送郵件失敗: " + emailError.getMessage());
                    // 郵件發送失敗不影響 Token 的生成，但要告知用戶
                    return ResponseEntity.ok(Map.of(
                        "success", false,
                        "message", "系統無法發送郵件，請聯絡管理員或稍後再試"
                    ));
                }
                
                System.out.println("[ResetPasswordApiController] 重設密碼郵件處理完成");
                
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "重設密碼信已發送至您的註冊信箱！請查收並點擊信中連結完成密碼重設。",
                    "testUrl", resetUrl
                ));
                
            } else {
                System.out.println("[ResetPasswordApiController] 查無帳號: " + account);
                
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "查無此帳號，請確認帳號是否正確"
                ));
            }
            
        } catch (Exception e) {
            System.err.println("[ResetPasswordApiController] 處理重設密碼請求時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "系統錯誤，請稍後再試"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> requestData) {
        // 直接調用統一的重設密碼請求API
        return requestPasswordReset(requestData);
    }

    // 驗證重設Token
    @GetMapping("/reset-password/verify")
    public ResponseEntity<?> verifyResetToken(@RequestParam String id, @RequestParam String token) {
        try {
            System.out.println("[ResetPasswordApiController] 驗證重設Token，ID: " + id + ", Token: " + token);
            
            int memberId = Integer.parseInt(id);
            Member member = memberService.getMemberById(memberId);
            
            if (member == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false, 
                    "message", "無效的用戶！"
                ));
            }
            
            if (member.getResetToken() == null || !token.equals(member.getResetToken())) {
                return ResponseEntity.ok(Map.of(
                    "success", false, 
                    "message", "驗證失敗或連結過期，請重新申請重設密碼！"
                ));
            }
            
            // 檢查Token是否過期
            if (member.getResetExpiry() != null && member.getResetExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.ok(Map.of(
                    "success", false, 
                    "message", "重設連結已過期，請重新申請！"
                ));
            }
            
            System.out.println("[ResetPasswordApiController] Token驗證成功");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "member", Map.of(
                    "id", member.getId(),
                    "name", member.getName(),
                    "account", member.getAccount()
                )
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "無效的連結格式！"));
        } catch (Exception e) {
            System.err.println("[ResetPasswordApiController] 驗證Token時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "系統錯誤，請稍後再試"));
        }
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<?> confirmPasswordReset(@RequestBody Map<String, String> requestData) {
        try {
            String idStr = requestData.get("id");
            String token = requestData.get("token");
            String newPassword = requestData.get("newPassword");
            String confirmPassword = requestData.get("confirmPassword");
            
            System.out.println("[ResetPasswordApiController] 確認重設密碼，ID: " + idStr);
            
            // 基本參數驗證
            if (idStr == null || token == null || newPassword == null || confirmPassword == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "缺少必要參數！"));
            }
            
            int id = Integer.parseInt(idStr);
            Member member = memberService.getMemberById(id);
            
            // 驗證會員和Token
            if (member == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "無效的用戶！"));
            }
            
            if (member.getResetToken() == null || !token.equals(member.getResetToken())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "驗證失敗或連結過期，請重新申請重設密碼！"));
            }
            
            // 檢查Token是否過期
            if (member.getResetExpiry() != null && member.getResetExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "重設連結已過期，請重新申請！"));
            }
            
            // 密碼確認
            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "兩次輸入的密碼不一致，請重新確認"));
            }
            
            // 密碼強度驗證
            String passwordValidationResult = validatePassword(newPassword);
            if (passwordValidationResult != null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", passwordValidationResult));
            }
            
            // 檢查新密碼是否與舊密碼相同
            if (passwordEncoder.matches(newPassword, member.getPassword())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "新密碼不可與目前密碼相同，請設定不同的密碼"));
            }
            
            // 更新密碼並清除Token
            member.setPassword(passwordEncoder.encode(newPassword));
            member.setLastPwdChange(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());
            member.setResetToken(null);
            member.setResetExpiry(null);
            memberService.updateMember(member);
            
            System.out.println("[ResetPasswordApiController] 密碼重設成功，用戶: " + member.getName());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "密碼重設成功！請使用新密碼重新登入"
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "無效的用戶 ID！"));
        } catch (Exception e) {
            System.err.println("[ResetPasswordApiController] 重設密碼時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "系統發生錯誤，請稍後再試"));
        }
    }
    
    /**
     * 驗證密碼強度
     */
    private String validatePassword(String password) {
        if (password == null) {
            return "密碼不可為空";
        }
        
        if (password.length() < 8) {
            return "密碼長度至少需要 8 個字元";
        }
        
        if (!LETTER_PATTERN.matcher(password).find()) {
            return "密碼必須包含至少一個英文字母";
        }
        
        if (!NUMBER_PATTERN.matcher(password).find()) {
            return "密碼必須包含至少一個數字";
        }
        
        return null;
    }
}