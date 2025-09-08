package com.eams.Service.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private GmailOAuth2Service gmailOAuth2Service;

    /**
     * 發送帳號驗證郵件 (使用 OAuth2)
     */
    public void sendVerificationEmail(String toEmail, String memberName, int memberId, String token) {
        try {
            // 檢查 Gmail 服務是否可用
            if (!gmailOAuth2Service.isGmailServiceAvailable()) {
                System.err.println("[EmailService] Gmail 服務未設定，請完成 OAuth2 授權");
                System.err.println("[EmailService] 請訪問: http://localhost:8080/EAMS/oauth2/authorize");
                throw new RuntimeException("郵件服務未設定，請聯絡系統管理員");
            }

            gmailOAuth2Service.sendVerificationEmail(toEmail, memberName, memberId, token);
            System.out.println("[EmailService] OAuth2 驗證郵件已發送至: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("[EmailService] OAuth2 發送驗證郵件失敗: " + e.getMessage());
            throw new RuntimeException("發送驗證郵件失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 發送密碼重設郵件 (使用 OAuth2)
     */
    public void sendPasswordResetEmail(String toEmail, String memberName, int memberId, String token) {
        try {
            // 檢查 Gmail 服務是否可用
            if (!gmailOAuth2Service.isGmailServiceAvailable()) {
                System.err.println("[EmailService] Gmail 服務未設定，請完成 OAuth2 授權");
                System.err.println("[EmailService] 請訪問: http://localhost:8080/EAMS/oauth2/authorize");
                throw new RuntimeException("郵件服務未設定，請聯絡系統管理員");
            }

            gmailOAuth2Service.sendPasswordResetEmail(toEmail, memberName, memberId, token);
            System.out.println("[EmailService] OAuth2 密碼重設郵件已發送至: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("[EmailService] OAuth2 發送密碼重設郵件失敗: " + e.getMessage());
            throw new RuntimeException("發送密碼重設郵件失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 檢查郵件服務是否可用
     */
    public boolean isEmailServiceAvailable() {
        return gmailOAuth2Service.isGmailServiceAvailable();
    }
}