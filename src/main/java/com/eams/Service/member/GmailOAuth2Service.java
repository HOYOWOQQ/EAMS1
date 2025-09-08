package com.eams.Service.member;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

@Service
public class GmailOAuth2Service {

    @Value("${google.oauth2.client-id}")
    private String clientId;

    @Value("${google.oauth2.client-secret}")
    private String clientSecret;

    @Value("${google.oauth2.refresh-token:}")
    private String refreshToken;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // 懶加載的 Gmail 服務
    private Gmail gmailService;
    private boolean serviceInitialized = false;

    /**
     * 懶加載方式取得 Gmail 服務
     */
    private Gmail getGmailService() throws Exception {
        if (!serviceInitialized) {
            initializeGmailService();
            serviceInitialized = true;
        }
        return gmailService;
    }

    /**
     * 初始化 Gmail 服務
     */
    private void initializeGmailService() throws Exception {
        // 檢查必要的配置
        if (!StringUtils.hasText(refreshToken)) {
            System.err.println("[GmailOAuth2Service] Refresh token 未設定，請先完成 OAuth2 授權");
            throw new RuntimeException("Refresh token 未設定，請訪問 /oauth2/authorize 完成授權");
        }

        if (!StringUtils.hasText(clientId) || !StringUtils.hasText(clientSecret)) {
            throw new RuntimeException("OAuth2 憑證未設定，請檢查 application.properties");
        }

        try {
            System.out.println("[GmailOAuth2Service] 初始化 Gmail 服務...");
            
            // 使用 refresh token 取得新的 access token
            GoogleTokenResponse tokenResponse = new GoogleRefreshTokenRequest(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                refreshToken,
                clientId,
                clientSecret
            ).execute();

            System.out.println("[GmailOAuth2Service] 成功取得 Access Token");

            // 建立 Gmail 服務
            this.gmailService = new Gmail.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                request -> {
                    request.getHeaders().setAuthorization("Bearer " + tokenResponse.getAccessToken());
                })
                .setApplicationName("EAMS System")
                .build();

            System.out.println("[GmailOAuth2Service] Gmail 服務初始化成功");

        } catch (Exception e) {
            System.err.println("[GmailOAuth2Service] Gmail 服務初始化失敗: " + e.getMessage());
            throw new RuntimeException("Gmail 服務初始化失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 Gmail API 發送驗證郵件
     */
    public void sendVerificationEmail(String toEmail, String memberName, int memberId, String token) {
        try {
            Gmail gmail = getGmailService();
            
            // 建構驗證連結
            String verifyUrl = frontendUrl + "/verifyEmail?id=" + memberId + "&token=" + token;
            
            // 建立郵件訊息
            MimeMessage emailContent = createVerificationEmail(toEmail, memberName, verifyUrl);
            
            // 轉換為 Gmail API 格式
            Message message = createMessageWithEmail(emailContent);
            
            // 發送郵件
            gmail.users().messages().send("me", message).execute();
            
            System.out.println("[GmailOAuth2Service] 驗證郵件已發送至: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("[GmailOAuth2Service] 發送驗證郵件失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("發送驗證郵件失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 使用 Gmail API 發送密碼重設郵件
     */
    public void sendPasswordResetEmail(String toEmail, String memberName, int memberId, String token) {
        try {
            Gmail gmail = getGmailService();
            
            // 建構重設連結
            String resetUrl = frontendUrl + "/resetPassword?id=" + memberId + "&token=" + token;
            
            // 建立郵件訊息
            MimeMessage emailContent = createPasswordResetEmail(toEmail, memberName, resetUrl);
            
            // 轉換為 Gmail API 格式
            Message message = createMessageWithEmail(emailContent);
            
            // 發送郵件
            gmail.users().messages().send("me", message).execute();
            
            System.out.println("[GmailOAuth2Service] 密碼重設郵件已發送至: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("[GmailOAuth2Service] 發送密碼重設郵件失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("發送密碼重設郵件失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 檢查 Gmail 服務是否可用
     */
    public boolean isGmailServiceAvailable() {
        return StringUtils.hasText(refreshToken) && 
               StringUtils.hasText(clientId) && 
               StringUtils.hasText(clientSecret);
    }

    /**
     * 建立驗證郵件內容
     */
    private MimeMessage createVerificationEmail(String toEmail, String memberName, String verifyUrl) 
            throws MessagingException {
        
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmail));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject("【教務管理系統】帳號驗證通知", "UTF-8");
        
        // HTML 郵件內容
        String htmlContent = buildVerificationEmailHtml(memberName, verifyUrl);
        email.setContent(htmlContent, "text/html; charset=UTF-8");
        
        return email;
    }

    /**
     * 建立密碼重設郵件內容
     */
    private MimeMessage createPasswordResetEmail(String toEmail, String memberName, String resetUrl) 
            throws MessagingException {
        
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmail));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject("【教務管理系統】密碼重設通知", "UTF-8");
        
        // HTML 郵件內容
        String htmlContent = buildPasswordResetEmailHtml(memberName, resetUrl);
        email.setContent(htmlContent, "text/html; charset=UTF-8");
        
        return email;
    }

    /**
     * 將 MimeMessage 轉換為 Gmail API Message
     */
    private Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);
        
        Message message = new Message();
        message.setRaw(encodedEmail);
        
        return message;
    }

    /**
     * 建構驗證郵件的 HTML 內容
     */
    private String buildVerificationEmailHtml(String memberName, String verifyUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>帳號驗證</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: ##333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { 
                        background: linear-gradient(135deg, ##667eea 0%%, ##764ba2 100%%); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center; 
                        border-radius: 12px 12px 0 0; 
                    }
                    .header h1 { margin: 0 0 10px 0; font-size: 28px; font-weight: 700; }
                    .header h2 { margin: 0; font-size: 18px; font-weight: 400; opacity: 0.9; }
                    .content { 
                        background: ##ffffff; 
                        padding: 40px 30px; 
                        border-radius: 0 0 12px 12px; 
                        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                    }
                    .button { 
                        display: inline-block; 
                        background: linear-gradient(135deg, ##4CAF50 0%%, ##45a049 100%%); 
                        color: white; 
                        padding: 16px 32px; 
                        text-decoration: none; 
                        border-radius: 8px; 
                        font-weight: 600;
                        font-size: 16px;
                        margin: 25px 0; 
                        transition: transform 0.2s;
                    }
                    .button:hover { transform: translateY(-2px); }
                    .footer { 
                        text-align: center; 
                        margin-top: 30px; 
                        color: ##888; 
                        font-size: 12px; 
                        line-height: 1.5;
                    }
                    .warning { 
                        background: linear-gradient(135deg, ##fff3cd 0%%, ##ffeaa7 100%%); 
                        border-left: 4px solid ##f39c12; 
                        padding: 20px; 
                        border-radius: 8px; 
                        margin: 25px 0; 
                    }
                    .warning strong { color: ##d68910; }
                    .code-block {
                        background: ##f8f9fa;
                        border: 1px solid ##e9ecef;
                        border-radius: 4px;
                        padding: 10px;
                        font-family: 'Courier New', monospace;
                        word-break: break-all;
                        margin: 10px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🎓 教務管理系統</h1>
                        <h2>帳號驗證通知</h2>
                    </div>
                    <div class="content">
                        <p style="font-size: 16px; margin-bottom: 20px;">
                            親愛的 <strong style="color: ##667eea;">%s</strong>，您好！
                        </p>
                        
                        <p style="margin-bottom: 25px;">
                            感謝您註冊教務管理系統，為了確保帳號安全，請點擊下方按鈕完成信箱驗證：
                        </p>
                        
                        <div style="text-align: center; margin: 30px 0;">
                            <table border="0" cellpadding="0" cellspacing="0">
							    <tr>
							        <td style="background-color: #3b82f6; border-radius: 8px; padding: 0;">
							            <a href="%s" style="display: inline-block; padding: 16px 32px; font-family: Arial, sans-serif; font-size: 16px; font-weight: bold; color: #ffffff; text-decoration: none; border-radius: 8px;">
							                驗證我的帳號
							            </a>
							        </td>
							    </tr>
							</table>
                        </div>
                        
                        <div class="warning">
                            <strong>⏰ 重要提醒：</strong>
                            <ul style="margin: 10px 0 0 20px; padding: 0;">
                                <li>此驗證連結將在 <strong>10分鐘後失效</strong></li>
                                <li>為確保安全，請勿將此連結分享給他人</li>
                                <li>如果按鈕無法點擊，請複製以下連結到瀏覽器：</li>
                            </ul>
                            <div class="code-block">%s</div>
                        </div>
                        
                        <p style="margin: 25px 0 15px 0;">
                            驗證完成後，您就可以正常登入系統使用所有功能了！
                        </p>
                        
                        <p style="color: ##888; font-style: italic;">
                            如果您沒有申請此帳號，請忽略此郵件。
                        </p>
                    </div>
                    <div class="footer">
                        <p>此郵件由系統自動發送，請勿直接回覆</p>
                        <p>© 2024 教務管理系統 - 保留所有權利</p>
                        <p>如有問題請聯絡系統管理員</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(memberName, verifyUrl, verifyUrl);
    }

    /**
     * 建構密碼重設郵件的 HTML 內容（解決格式化錯誤）
     */
    private String buildPasswordResetEmailHtml(String memberName, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>密碼重設</title>
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: ##333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { 
                        background: linear-gradient(135deg, ##f59e0b 0%%, ##d97706 100%%); 
                        color: white; 
                        padding: 40px 30px; 
                        text-align: center; 
                        border-radius: 12px 12px 0 0; 
                    }
                    .header h1 { margin: 0 0 10px 0; font-size: 28px; font-weight: 700; }
                    .header h2 { margin: 0; font-size: 18px; font-weight: 400; opacity: 0.9; }
                    .content { 
                        background: ##ffffff; 
                        padding: 40px 30px; 
                        border-radius: 0 0 12px 12px; 
                        box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                    }
                    .button { 
                        display: inline-block; 
                        background: linear-gradient(135deg, ##ef4444 0%%, ##dc2626 100%%); 
                        color: white; 
                        padding: 16px 32px; 
                        text-decoration: none; 
                        border-radius: 8px; 
                        font-weight: 600;
                        font-size: 16px;
                        margin: 25px 0; 
                        transition: transform 0.2s;
                    }
                    .button:hover { transform: translateY(-2px); }
                    .footer { 
                        text-align: center; 
                        margin-top: 30px; 
                        color: ##888; 
                        font-size: 12px; 
                        line-height: 1.5;
                    }
                    .warning { 
                        background: linear-gradient(135deg, ##fef3c7 0%%, ##fde68a 100%%); 
                        border-left: 4px solid ##f59e0b; 
                        padding: 20px; 
                        border-radius: 8px; 
                        margin: 25px 0; 
                    }
                    .warning strong { color: ##92400e; }
                    .code-block {
                        background: ##f8f9fa;
                        border: 1px solid ##e9ecef;
                        border-radius: 4px;
                        padding: 10px;
                        font-family: 'Courier New', monospace;
                        word-break: break-all;
                        margin: 10px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🔐 教務管理系統</h1>
                        <h2>密碼重設通知</h2>
                    </div>
                    <div class="content">
                        <p style="font-size: 16px; margin-bottom: 20px;">
                            親愛的 <strong style="color: ##f59e0b;">%s</strong>，您好！
                        </p>
                        
                        <p style="margin-bottom: 25px;">
                            我們收到您的密碼重設請求。請點擊下方按鈕前往重設密碼頁面：
                        </p>
                        
                        <div style="text-align: center; margin: 30px 0;">
        		            <table border="0" cellpadding="0" cellspacing="0">
			                    <tr>
			                        <td style="background-color: #dc3545; border-radius: 8px; padding: 0;">
			                            <a href="%s" style="display: inline-block; padding: 16px 32px; font-family: Arial, sans-serif; font-size: 16px; font-weight: bold; color: #ffffff; text-decoration: none; border-radius: 8px;">
			                                立即重設密碼
			                            </a>
			                        </td>
			                    </tr>
			                </table>
                        </div>
                        
                        <div class="warning">
                            <strong>⏰ 重要提醒：</strong>
                            <ul style="margin: 10px 0 0 20px; padding: 0;">
                                <li>此重設連結將在 <strong>10分鐘後失效</strong></li>
                                <li>如果您沒有申請密碼重設，請忽略此郵件</li>
                                <li>為確保安全，請勿將此連結分享給他人</li>
                                <li>如果按鈕無法點擊，請複製以下連結到瀏覽器：</li>
                            </ul>
                            <div class="code-block">%s</div>
                        </div>
                        
                        <p style="margin: 25px 0 15px 0;">
                            重設完成後，請使用新密碼重新登入系統。
                        </p>
                        
                        <p style="color: ##888; font-style: italic;">
                            如果您沒有申請密碼重設，您的帳號可能存在安全風險，請聯絡系統管理員。
                        </p>
                    </div>
                    <div class="footer">
                        <p>此郵件由系統自動發送，請勿直接回覆</p>
                        <p>© 2024 教務管理系統 - 保留所有權利</p>
                        <p>如有問題請聯絡系統管理員</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(memberName, resetUrl, resetUrl);
    }
    
    
    /**
     * 發送通用通知郵件
     */
    public void sendNotificationEmail(String toEmail, String title, String content, String actionUrl) {
        try {
            Gmail gmail = getGmailService();
            
            // 建立郵件訊息
            MimeMessage emailContent = createNotificationEmail(toEmail, title, content, actionUrl);
            
            // 轉換為 Gmail API 格式
            Message message = createMessageWithEmail(emailContent);
            
            // 發送郵件
            gmail.users().messages().send("me", message).execute();
            
            System.out.println("[GmailOAuth2Service] 通知郵件已發送至: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("[GmailOAuth2Service] 發送通知郵件失敗: " + e.getMessage());
            throw new RuntimeException("發送通知郵件失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 建立通知郵件內容
     */
    private MimeMessage createNotificationEmail(String toEmail, String title, String content, String actionUrl) 
            throws MessagingException {
        
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(fromEmail));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(toEmail));
        email.setSubject(title, "UTF-8");
        
        // HTML 郵件內容
        String htmlContent = buildNotificationEmailHtml(title, content, actionUrl);
        email.setContent(htmlContent, "text/html; charset=UTF-8");
        
        return email;
    }

    /**
     * 建構通知郵件的 HTML 內容
     */
    private String buildNotificationEmailHtml(String title, String content, String actionUrl) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'></head><body>");
        html.append("<div style='max-width: 600px; margin: 0 auto; font-family: Arial, sans-serif;'>");
        html.append("<h2 style='color: #333;'>").append(title).append("</h2>");
        html.append("<div style='line-height: 1.6; color: #555;'>").append(content).append("</div>");
        
        if (actionUrl != null && !actionUrl.trim().isEmpty()) {
            html.append("<div style='margin: 20px 0;'>");
            html.append("<a href='").append(actionUrl).append("' style='");
            html.append("background-color: #007bff; color: white; padding: 10px 20px; ");
            html.append("text-decoration: none; border-radius: 5px; display: inline-block;'>");
            html.append("點擊查看詳情</a>");
            html.append("</div>");
        }
        
        html.append("<hr style='margin: 30px 0; border: none; border-top: 1px solid #eee;'>");
        html.append("<small style='color: #888;'>此郵件由系統自動發送，請勿回覆。</small>");
        html.append("</div></body></html>");
        
        return html.toString();
    }
}