package com.eams.common.Security.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.eams.common.Security.Services.CustomUserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 生成 Access Token (用於登入)
    public String generateAccessToken(CustomUserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getId())
                .claim("name", userDetails.getName())
                .claim("email", userDetails.getEmail())
                .claim("role", userDetails.getRole())
                .claim("roles", roles)
                .claim("permissions", userDetails.getPermissions()) // 新增：具體權限
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    // 生成簡單的測試 Token
    public String generateTestToken(String username) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    // 從 token 獲取用戶名
    public String getUsernameFromToken(String token) {
        Claims c = claimsOrNull(token);
        return c == null ? null : c.getSubject();
    }


    // 從 token 獲取用戶 ID
    public Integer getUserIdFromToken(String token) {
        Claims claims = claimsOrNull(token);
        if (claims == null) return null;

        Object raw = claims.get("userId");
        if (raw == null) raw = claims.get("id"); // 容錯

        if (raw instanceof Number n) return n.intValue();
        if (raw instanceof String s) {
            try { return Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) {}
        }
        return null;
    }


    // 驗證 token
    public boolean validateToken(String token) {
        return claimsOrNull(token) != null;
    }

    // 檢查 token 是否過期
    public boolean isTokenExpired(String token) {
        Claims c = claimsOrNull(token);
        return c == null || c.getExpiration() == null || c.getExpiration().before(new Date());
    }
    
    
 // 🆕 添加到您的 JwtTokenUtil.java 中
    /**
     * 從 token 獲取所有 Claims
     */
    public Claims getClaimsFromToken(String token) {
        if (token == null || token.isBlank()) return null;
        String clean = token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())       // JJWT 0.12.x 正確用法
                    .build()
                    .parseSignedClaims(clean)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
        	System.out.println("獲取 JWT Claims 失敗: " + e.getMessage());
            return null;
        }
    }
    
    
    
 // ① 清 token
    public String cleanToken(String token) {
        if (token == null || token.isBlank()) return null;
        return token.startsWith("Bearer ") ? token.substring(7).trim() : token.trim();
    }

    // ② 單一入口：解析並驗簽（所有方法都用它）
    private Claims claimsOrNull(String token) {
        String clean = cleanToken(token);
        if (clean == null) return null;
        try {
            return Jwts.parser()
                       .verifyWith(getSigningKey())
                       .build()
                       .parseSignedClaims(clean)
                       .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }


    
    
}