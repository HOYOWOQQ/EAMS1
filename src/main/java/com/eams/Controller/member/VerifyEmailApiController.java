package com.eams.Controller.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.Service.member.EmailService;
import com.eams.Service.member.MemberService;
import com.eams.utils.TokenUtil;
import com.eams.Entity.member.Member;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/member")
public class VerifyEmailApiController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private EmailService emailService;

    // 發送驗證信件
    @PostMapping("/verify-email/request")
    public ResponseEntity<?> requestEmailVerification(@RequestBody Map<String, String> requestData) {
        try {
            String account = requestData.get("account");
            
            System.out.println("[VerifyEmailApiController] 驗證信請求，帳號: " + account);
            
            // 驗證參數
            if (account == null || account.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "請輸入帳號"));
            }
            
            // 查找會員
            Member member = memberService.findByAccount(account.trim());
            
            if (member == null) {
                System.out.println("[VerifyEmailApiController] 查無帳號: " + account);
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "查無此帳號"
                ));
            }
            
            if (member.isVerified()) {
                System.out.println("[VerifyEmailApiController] 帳號已驗證: " + account);
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "帳號已完成驗證"
                ));
            }
            
            System.out.println("[VerifyEmailApiController] 找到未驗證會員: " + member.getName());
            
            // 生成驗證Token
            String token = TokenUtil.generateToken();
            
            try {
                emailService.sendVerificationEmail(
                    member.getEmail(), 
                    member.getName(), 
                    member.getId(), 
                    token
                );
                
                System.out.println("[VerifyEmailApiController] 驗證信已發送至: " + member.getEmail());
                
            } catch (Exception emailError) {
                System.err.println("[VerifyEmailApiController] 發送郵件失敗: " + emailError.getMessage());
            }
            
            // 更新會員的驗證Token資訊
            member.setEmailToken(token);
            member.setTokenExpiry(LocalDateTime.now().plusMinutes(10));
            memberService.updateMember(member);
            
            // 建構驗證連結
            String verifyUrl = "/EAMS/verifyEmail?id=" + member.getId() + "&token=" + token;
            
            System.out.println("[VerifyEmailApiController] 驗證信已發送");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "驗證信已寄出！請查收並點擊信中連結完成驗證。",
                "testUrl", verifyUrl
            ));
            
        } catch (Exception e) {
            System.err.println("[VerifyEmailApiController] 處理驗證信請求時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "系統錯誤，請稍後再試"));
        }
    }

    // 驗證Email Token
    @GetMapping("/verify-email/confirm")
    public ResponseEntity<?> confirmEmailVerification(@RequestParam String id, @RequestParam String token) {
        try {
            System.out.println("[VerifyEmailApiController] 驗證Token，ID: " + id + ", Token: " + token);
            
            int memberId = Integer.parseInt(id);
            Member member = memberService.getMemberById(memberId);
            
            if (member == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false, 
                    "message", "無效的驗證連結！"
                ));
            }
            
            if (member.getEmailToken() == null || !token.equals(member.getEmailToken())) {
                return ResponseEntity.ok(Map.of(
                    "success", false, 
                    "message", "驗證失敗，連結無效或已過期！"
                ));
            }
            
            // 檢查Token是否過期
            if (member.getTokenExpiry() != null && member.getTokenExpiry().isBefore(LocalDateTime.now())) {
                return ResponseEntity.ok(Map.of(
                    "success", false, 
                    "message", "驗證連結已過期，請重新申請！"
                ));
            }
            
            // 驗證成功，更新會員狀態
            member.setVerified(true);
            member.setEmailToken(null);
            member.setTokenExpiry(null);
            member.setUpdateTime(LocalDateTime.now());
            memberService.updateMember(member);
            
            System.out.println("[VerifyEmailApiController] 信箱驗證成功，用戶: " + member.getName());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "信箱驗證成功，請重新登入！",
                "member", Map.of(
                    "name", member.getName(),
                    "account", member.getAccount()
                )
            ));
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("success", false, "message", "無效的連結格式！"));
        } catch (Exception e) {
            System.err.println("[VerifyEmailApiController] 驗證Token時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "系統錯誤，請稍後再試！"));
        }
    }
}