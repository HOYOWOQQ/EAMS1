package com.eams.Controller.member;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.GmailScopes;

import java.io.IOException;
import java.util.Arrays;

@Controller
@RequestMapping("/oauth2")
public class OAuth2AuthController {

    @Value("${google.oauth2.client-id:}")
    private String clientId;

    @Value("${google.oauth2.client-secret:}")
    private String clientSecret;

    private final String REDIRECT_URI = "http://localhost:8080/EAMS/oauth2/callback";

    /**
     * 初始化 OAuth2 授權流程
     */
    @GetMapping("/authorize")
    @ResponseBody
    public String authorize() {
        try {
            if (clientId.isEmpty() || clientSecret.isEmpty()) {
                return """
                    <html>
                    <head><title>OAuth2 設定錯誤</title></head>
                    <body>
                        <h2>❌ OAuth2 設定錯誤</h2>
                        <p>請先在 application.properties 中設定：</p>
                        <pre>
google.oauth2.client-id=您的CLIENT_ID
google.oauth2.client-secret=您的CLIENT_SECRET
                        </pre>
                    </body>
                    </html>
                    """;
            }

            GoogleClientSecrets.Details clientDetails = new GoogleClientSecrets.Details();
            clientDetails.setClientId(clientId);
            clientDetails.setClientSecret(clientSecret);

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setInstalled(clientDetails);

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientSecrets,
                    Arrays.asList(GmailScopes.GMAIL_SEND))
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();

            String authorizationUrl = flow.newAuthorizationUrl()
                    .setRedirectUri(REDIRECT_URI)
                    .build();

            return """
                <html>
                <head><title>Gmail OAuth2 授權</title></head>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 50px auto; padding: 20px;">
                    <h2>🔐 Gmail OAuth2 授權設定</h2>
                    <p>請點擊下方連結完成授權，以便系統能夠發送郵件：</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" target="_blank" 
                           style="display: inline-block; background: #4285f4; color: white; padding: 15px 30px; 
                                  text-decoration: none; border-radius: 5px; font-weight: bold;">
                            🔗 授權 Gmail 存取權限
                        </a>
                    </div>
                    <div style="background: #f0f8ff; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p><strong>📝 步驟說明：</strong></p>
                        <ol>
                            <li>點擊上方按鈕</li>
                            <li>登入您的 Gmail 帳號</li>
                            <li>授權應用程式存取 Gmail</li>
                            <li>完成後會自動跳轉並顯示 Refresh Token</li>
                            <li>將 Token 複製到 application.properties</li>
                        </ol>
                    </div>
                </body>
                </html>
                """.formatted(authorizationUrl);

        } catch (Exception e) {
            return "授權過程中發生錯誤: " + e.getMessage();
        }
    }

    /**
     * OAuth2 回調處理
     */
    @GetMapping("/callback")
    @ResponseBody
    public String callback(@RequestParam("code") String authorizationCode) {
        try {
            GoogleClientSecrets.Details clientDetails = new GoogleClientSecrets.Details();
            clientDetails.setClientId(clientId);
            clientDetails.setClientSecret(clientSecret);

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setInstalled(clientDetails);

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    clientSecrets,
                    Arrays.asList(GmailScopes.GMAIL_SEND))
                    .setAccessType("offline")
                    .setApprovalPrompt("force")
                    .build();

            GoogleTokenResponse tokenResponse = flow.newTokenRequest(authorizationCode)
                    .setRedirectUri(REDIRECT_URI)
                    .execute();

            String refreshToken = tokenResponse.getRefreshToken();
            String accessToken = tokenResponse.getAccessToken();

            return """
                <html>
                <head><title>OAuth2 授權完成</title></head>
                <body style="font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px;">
                    <h2>✅ Gmail OAuth2 授權成功！</h2>
                    <p>請將以下 Refresh Token 複製到您的 application.properties 檔案中：</p>
                    
                    <div style="background: #f5f5f5; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #4CAF50;">
                        <h3>🔑 Refresh Token:</h3>
                        <textarea readonly style="width: 100%%; height: 60px; font-family: monospace; font-size: 12px; padding: 10px;">%s</textarea>
                    </div>
                    
                    <div style="background: #e8f5e8; padding: 20px; border-radius: 8px; margin: 20px 0;">
                        <h3>⚙️ 設定方式：</h3>
                        <p>在 <code>application.properties</code> 中新增或更新：</p>
                        <pre style="background: #f8f8f8; padding: 10px; border-radius: 4px; overflow-x: auto;">google.oauth2.refresh-token=%s</pre>
                    </div>
                    
                    <div style="background: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ffc107;">
                        <h3>⚠️ 重要提醒：</h3>
                        <ul>
                            <li>請妥善保管這個 Refresh Token</li>
                            <li>更新 application.properties 後需要重新啟動應用程式</li>
                            <li>Token 具有長期有效性，無需頻繁重新授權</li>
                        </ul>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <button onclick="window.close()" 
                                style="background: #4CAF50; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer;">
                            關閉視窗
                        </button>
                    </div>
                </body>
                </html>
                """.formatted(
                    refreshToken != null ? refreshToken : "無法取得 Refresh Token，請重新嘗試",
                    refreshToken != null ? refreshToken : "N/A"
                );

        } catch (IOException e) {
            return "處理授權回調時發生錯誤: " + e.getMessage();
        }
    }
}