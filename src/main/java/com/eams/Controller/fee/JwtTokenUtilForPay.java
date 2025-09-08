package com.eams.Controller.fee;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtTokenUtilForPay {

    // 用來簽章的密鑰，請務必保密，可存在 application.properties 或環境變數
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor("ThisIsMySuperSecretAndRidiculouslyPaymentToken".getBytes());

    // Token 有效期限（以秒為單位）=> 1小時
    private static final long EXPIRATION_IN_SECONDS = 60 * 60 ;

    /**
     * ✅ 產生繳費通知單用的 JWT Token
     *
     * @param noticeNo 通知單編號
     * @return JWT Token 字串
     */
    public static String generateToken(String noticeNo) {
        return Jwts.builder()
                .subject(noticeNo) // 將 noticeNo 存為 subject
                .issuedAt(new Date()) // 簽發時間
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_IN_SECONDS * 1000)) // 過期時間
                .signWith(SECRET_KEY) // 使用密鑰簽章
                .compact(); // 生成 JWT token
    }

    /**
     * ✅ 驗證 Token 並取得 Claims 內容
     *
     * @param token JWT 字串
     * @return Claims 解析後內容
     * @throws Exception 驗證錯誤時拋出
     */
    private static Claims getClaims(String token) throws Exception {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            throw new Exception("無效或過期的 Token");
        }
    }

    /**
     * ✅ 從 token 中取得通知單編號
     */
    public static String getNoticeNo(String token) throws Exception {
        return getClaims(token).getSubject();
    }

    /**
     * ✅ 驗證 Token 是否有效（簽名正確且未過期）
     */
    public static boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ✅ 取得 Token 的過期時間
     */
    public static Date getExpiration(String token) throws Exception {
        return getClaims(token).getExpiration();
    }

    /**
     * ✅ 如果有擴充 payload，可用此方法取得任意欄位
     */
    public static String getValue(String token, String key) throws Exception {
        Object value = getClaims(token).get(key);
        return value != null ? value.toString() : null;
    }
} 
