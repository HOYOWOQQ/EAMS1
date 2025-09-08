package com.eams.common.log.util; // 🔧 修正 package 路徑

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.eams.common.Security.Services.CustomUserDetails;
import com.eams.common.Security.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 用戶上下文工具類
 * 兼容 Session 和 JWT 兩種認證方式
 * ✅ 使用現有的 JwtTokenUtil
 */
@Component
@Slf4j
public class UserContextUtil {
    
    // JWT 相關配置
    private static final String JWT_HEADER = "Authorization";
    private static final String JWT_PREFIX = "Bearer ";
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 獲取當前用戶ID
     * 優先順序：SecurityContext > JWT > Session
     */
    public Long getCurrentUserId() {
        try {
            log.debug("🔍 開始獲取當前用戶ID");
            
            // 1) 檢查 SecurityContext
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                log.debug("SecurityContext Authentication: {}", auth);
                
                if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                    Integer id = userDetails.getId();
                    if (id != null) {
                        Long userId = id.longValue();
                        log.debug("✅ 從 SecurityContext 獲取用戶ID: {}", userId);
                        return userId;
                    }
                }
            } catch (Exception e) {
                log.debug("SecurityContext 檢查失敗: {}", e.getMessage());
            }

            // 2) 檢查 JWT
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                log.debug("檢查 JWT Token...");
                Long jwtUserId = getUserIdFromJWT(request);
                if (jwtUserId != null) {
                    log.debug("✅ 從 JWT 獲取用戶ID: {}", jwtUserId);
                    return jwtUserId;
                }
            }

            // 3) 檢查 Session
            if (request != null) {
                log.debug("檢查 Session...");
                Long sessionUserId = getUserIdFromSession(request);
                if (sessionUserId != null) {
                    log.debug("✅ 從 Session 獲取用戶ID: {}", sessionUserId);
                    return sessionUserId;
                }
            }

            log.debug("❌ 無法從任何來源獲取用戶ID");
            return null;
            
        } catch (Exception e) {
            log.error("獲取當前用戶ID時發生錯誤", e);
            return null;
        }
    }
    
    /**
     * 獲取當前用戶名
     * 優先順序：SecurityContext > JWT > Session
     */
    public String getCurrentUsername() {
        try {
            log.debug("🔍 開始獲取當前用戶名");
            
            // 1) 檢查 SecurityContext
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                    String username = userDetails.getName(); // 或 getUsername()
                    if (username != null && !username.trim().isEmpty()) {
                        log.debug("✅ 從 SecurityContext 獲取用戶名: {}", username);
                        return username.trim();
                    }
                }
            } catch (Exception e) {
                log.debug("SecurityContext 用戶名檢查失敗: {}", e.getMessage());
            }
            
            // 2) 檢查 JWT
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                String jwtUsername = getUsernameFromJWT(request);
                if (jwtUsername != null && !jwtUsername.trim().isEmpty()) {
                    log.debug("✅ 從 JWT 獲取用戶名: {}", jwtUsername);
                    return jwtUsername;
                }
            }
            
            // 3) 檢查 Session
            if (request != null) {
                String sessionUsername = getUsernameFromSession(request);
                if (sessionUsername != null && !sessionUsername.trim().isEmpty()) {
                    log.debug("✅ 從 Session 獲取用戶名: {}", sessionUsername);
                    return sessionUsername;
                }
            }
            
            log.debug("❌ 無法獲取用戶名，返回匿名用戶");
            return "匿名用戶";
            
        } catch (Exception e) {
            log.error("獲取當前用戶名時發生錯誤", e);
            return "匿名用戶";
        }
    }
    
    /**
     * 獲取當前用戶角色
     * 優先順序：SecurityContext > JWT > Session
     */
    public String getCurrentUserRole() {
        try {
            log.debug("🔍 開始獲取當前用戶角色");
            
            // 1) 檢查 SecurityContext
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                    String role = userDetails.getRole();
                    if (role != null && !role.trim().isEmpty()) {
                        log.debug("✅ 從 SecurityContext 獲取用戶角色: {}", role);
                        return role;
                    }
                }
            } catch (Exception e) {
                log.debug("SecurityContext 角色檢查失敗: {}", e.getMessage());
            }
            
            // 2) 檢查 JWT
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                String jwtRole = getUserRoleFromJWT(request);
                if (jwtRole != null && !jwtRole.trim().isEmpty()) {
                    log.debug("✅ 從 JWT 獲取用戶角色: {}", jwtRole);
                    return jwtRole;
                }
            }
            
            // 3) 檢查 Session
            if (request != null) {
                String sessionRole = getUserRoleFromSession(request);
                if (sessionRole != null && !sessionRole.trim().isEmpty()) {
                    log.debug("✅ 從 Session 獲取用戶角色: {}", sessionRole);
                    return sessionRole;
                }
            }
            
            log.debug("❌ 無法獲取用戶角色，返回 GUEST");
            return "GUEST";
            
        } catch (Exception e) {
            log.error("獲取當前用戶角色時發生錯誤", e);
            return "GUEST";
        }
    }
    
    /**
     * 獲取當前用戶職位
     */
    public String getCurrentUserPosition() {
        try {
            log.debug("🔍 開始獲取當前用戶職位");
            
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return null;
            }
            
            // JWT 方式
            String jwtPosition = getUserPositionFromJWT(request);
            if (jwtPosition != null && !jwtPosition.trim().isEmpty()) {
                log.debug("✅ 從 JWT 獲取用戶職位: {}", jwtPosition);
                return jwtPosition;
            }
            
            // Session 方式
            String sessionPosition = getUserPositionFromSession(request);
            if (sessionPosition != null && !sessionPosition.trim().isEmpty()) {
                log.debug("✅ 從 Session 獲取用戶職位: {}", sessionPosition);
                return sessionPosition;
            }
            
            log.debug("❌ 無法獲取用戶職位");
            return null;
            
        } catch (Exception e) {
            log.error("獲取當前用戶職位時發生錯誤", e);
            return null;
        }
    }
    
    /**
     * 獲取當前用戶權限列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getCurrentUserPermissions() {
        try {
            log.debug("🔍 開始獲取當前用戶權限");
            
            // 1) 檢查 SecurityContext
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
                    CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                    List<String> permissions = userDetails.getPermissions();
                    if (permissions != null && !permissions.isEmpty()) {
                        log.debug("✅ 從 SecurityContext 獲取用戶權限: {}", permissions);
                        return permissions;
                    }
                }
            } catch (Exception e) {
                log.debug("SecurityContext 權限檢查失敗: {}", e.getMessage());
            }
            
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return Arrays.asList();
            }
            
            // JWT 方式
            List<String> jwtPermissions = getUserPermissionsFromJWT(request);
            if (jwtPermissions != null && !jwtPermissions.isEmpty()) {
                log.debug("✅ 從 JWT 獲取用戶權限: {}", jwtPermissions);
                return jwtPermissions;
            }
            
            // Session 方式
            List<String> sessionPermissions = getUserPermissionsFromSession(request);
            if (sessionPermissions != null && !sessionPermissions.isEmpty()) {
                log.debug("✅ 從 Session 獲取用戶權限: {}", sessionPermissions);
                return sessionPermissions;
            }
            
            log.debug("❌ 無法獲取用戶權限");
            return Arrays.asList();
            
        } catch (Exception e) {
            log.error("獲取當前用戶權限時發生錯誤", e);
            return Arrays.asList();
        }
    }
    
    /**
     * 檢查當前用戶是否有特定權限
     */
    public boolean hasPermission(String permission) {
        List<String> permissions = getCurrentUserPermissions();
        boolean hasIt = permissions.contains(permission);
        log.debug("權限檢查 [{}]: {}", permission, hasIt);
        return hasIt;
    }
    
    /**
     * 檢查當前認證方式
     */
    public String getCurrentAuthType() {
        try {
            log.debug("🔍 檢查認證方式");
            
            // 檢查 SecurityContext
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
                    log.debug("✅ 認證方式: SECURITY_CONTEXT");
                    return "SECURITY_CONTEXT";
                }
            } catch (Exception e) {
                log.debug("SecurityContext 檢查失敗: {}", e.getMessage());
            }
            
            HttpServletRequest request = getCurrentRequest();
            if (request == null) {
                return "NONE";
            }
            
            // 檢查 JWT Token
            String token = extractJWTToken(request);
            if (token != null) {
                log.debug("發現 JWT Token: {}", token.substring(0, Math.min(20, token.length())) + "...");
                boolean isValid = jwtTokenUtil.validateToken(token);
                log.debug("JWT Token 有效性: {}", isValid);
                if (isValid) {
                    log.debug("✅ 認證方式: JWT");
                    return "JWT";
                }
            }
            
            // 檢查 Session
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object userId = session.getAttribute("id");
                log.debug("Session 用戶ID: {}", userId);
                if (userId != null) {
                    log.debug("✅ 認證方式: SESSION");
                    return "SESSION";
                }
            }
            
            log.debug("❌ 認證方式: NONE");
            return "NONE";
            
        } catch (Exception e) {
            log.error("檢查認證方式時發生錯誤", e);
            return "NONE";
        }
    }
    
    // ===== JWT 相關方法 =====
    
    /**
     * 從 JWT 獲取用戶ID
     */
    private Long getUserIdFromJWT(HttpServletRequest request) {
        try {
            String token = extractJWTToken(request);
            log.debug("提取的 JWT Token: {}", token != null ? "存在" : "不存在");
            
            if (token != null && jwtTokenUtil.validateToken(token)) {
                Integer userId = jwtTokenUtil.getUserIdFromToken(token);
                log.debug("JWT 中的用戶ID: {}", userId);
                return userId != null ? userId.longValue() : null;
            }
        } catch (Exception e) {
            log.debug("從 JWT 獲取用戶ID失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 JWT 獲取用戶名
     */
    private String getUsernameFromJWT(HttpServletRequest request) {
        try {
            String token = extractJWTToken(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                String username = jwtTokenUtil.getUsernameFromToken(token);
                if (username != null) {
                    // 清理用戶名中的換行符號
                    return username.replace("\n", "").replace("\r", "").trim();
                }
            }
        } catch (Exception e) {
            log.debug("從 JWT 獲取用戶名失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 JWT 獲取用戶角色
     */
    private String getUserRoleFromJWT(HttpServletRequest request) {
        try {
            String token = extractJWTToken(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                Claims claims = extractJWTClaims(token);
                if (claims != null) {
                    String role = claims.get("role", String.class);
                    log.debug("JWT 中的用戶角色: {}", role);
                    return role;
                }
            }
        } catch (Exception e) {
            log.debug("從 JWT 獲取用戶角色失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 JWT 獲取用戶職位
     */
    private String getUserPositionFromJWT(HttpServletRequest request) {
        try {
            String token = extractJWTToken(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                Claims claims = extractJWTClaims(token);
                if (claims != null) {
                    String position = claims.get("position", String.class);
                    log.debug("JWT 中的用戶職位: {}", position);
                    return position;
                }
            }
        } catch (Exception e) {
            log.debug("從 JWT 獲取用戶職位失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 JWT 獲取用戶權限
     */
    @SuppressWarnings("unchecked")
    private List<String> getUserPermissionsFromJWT(HttpServletRequest request) {
        try {
            String token = extractJWTToken(request);
            if (token != null && jwtTokenUtil.validateToken(token)) {
                Claims claims = extractJWTClaims(token);
                if (claims != null) {
                    Object permissions = claims.get("permissions");
                    log.debug("JWT 中的權限類型: {}", permissions != null ? permissions.getClass() : "null");
                    log.debug("JWT 中的權限內容: {}", permissions);
                    
                    if (permissions instanceof List) {
                        return (List<String>) permissions;
                    }
                }
            }
        } catch (Exception e) {
            log.debug("從 JWT 獲取用戶權限失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 提取 JWT Claims
     */
    private Claims extractJWTClaims(String token) {
        try {
            Claims claims = jwtTokenUtil.getClaimsFromToken(token);
            if (claims != null) {
                log.debug("成功解析 JWT Claims: {}", claims.keySet());
            }
            return claims;
        } catch (Exception e) {
            log.debug("解析 JWT Token 失敗: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 提取 JWT Token
     */
    private String extractJWTToken(HttpServletRequest request) {
        String authHeader = request.getHeader(JWT_HEADER);
        log.debug("Authorization Header: {}", authHeader);
        
        if (authHeader != null && authHeader.startsWith(JWT_PREFIX)) {
            String token = authHeader.substring(JWT_PREFIX.length()).trim();
            log.debug("提取的 Token 長度: {}", token.length());
            return token;
        }
        return null;
    }
    
    // ===== Session 相關方法 =====
    
    /**
     * 從 Session 獲取用戶ID
     */
    private Long getUserIdFromSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            log.debug("Session 存在: {}", session != null);
            
            if (session != null) {
                log.debug("Session ID: {}", session.getId());
                
                // 列出所有 session 屬性
                java.util.Enumeration<String> attributeNames = session.getAttributeNames();
                while (attributeNames.hasMoreElements()) {
                    String name = attributeNames.nextElement();
                    Object value = session.getAttribute(name);
                    log.debug("Session 屬性 - {}: {} ({})", name, value, 
                        value != null ? value.getClass().getSimpleName() : "null");
                }
                
                // 嘗試多種可能的 session key
                Object userId = session.getAttribute("id");
                if (userId instanceof Number) {
                    Long id = ((Number) userId).longValue();
                    log.debug("從 Session 'id' 獲取到用戶ID: {}", id);
                    return id;
                }
                if (userId instanceof String) {
                    try {
                        Long id = Long.parseLong((String) userId);
                        log.debug("從 Session 'id' (String) 獲取到用戶ID: {}", id);
                        return id;
                    } catch (NumberFormatException ignored) {
                        log.debug("Session 'id' 無法轉換為數字: {}", userId);
                    }
                }
                
                // 嘗試從用戶對象中獲取
                Object user = session.getAttribute("user");
                if (user != null) {
                    try {
                        Method getIdMethod = user.getClass().getMethod("getId");
                        Object id = getIdMethod.invoke(user);
                        if (id instanceof Number) {
                            Long userId2 = ((Number) id).longValue();
                            log.debug("從 Session 'user.getId()' 獲取到用戶ID: {}", userId2);
                            return userId2;
                        }
                    } catch (Exception ignored) {
                        log.debug("無法從 user 對象獲取 ID");
                    }
                }
            }
        } catch (Exception e) {
            log.debug("從 Session 獲取用戶ID失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 Session 獲取用戶名
     */
    private String getUsernameFromSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                // 嘗試多種可能的 session key
                Object username = session.getAttribute("name");
                if (username != null) {
                    log.debug("從 Session 'name' 獲取到用戶名: {}", username);
                    return username.toString();
                }
                
                username = session.getAttribute("username");
                if (username != null) {
                    log.debug("從 Session 'username' 獲取到用戶名: {}", username);
                    return username.toString();
                }
                
                // 嘗試從用戶對象中獲取
                Object user = session.getAttribute("member");
                if (user != null) {
                    try {
                        Method getUsernameMethod = user.getClass().getMethod("getUsername");
                        Object name = getUsernameMethod.invoke(user);
                        if (name != null) {
                            log.debug("從 Session 'member.getUsername()' 獲取到用戶名: {}", name);
                            return name.toString();
                        }
                    } catch (Exception ignored) {}
                    
                    try {
                        Method getNameMethod = user.getClass().getMethod("getName");
                        Object name = getNameMethod.invoke(user);
                        if (name != null) {
                            log.debug("從 Session 'member.getName()' 獲取到用戶名: {}", name);
                            return name.toString();
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            log.debug("從 Session 獲取用戶名失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 Session 獲取用戶角色
     */
    private String getUserRoleFromSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object role = session.getAttribute("role");
                if (role != null) {
                    log.debug("從 Session 'role' 獲取到用戶角色: {}", role);
                    return role.toString();
                }
                
                // 嘗試從用戶對象中獲取
                Object user = session.getAttribute("member");
                if (user != null) {
                    try {
                        Method getRoleMethod = user.getClass().getMethod("getRole");
                        Object userRoleObj = getRoleMethod.invoke(user);
                        if (userRoleObj != null) {
                            log.debug("從 Session 'member.getRole()' 獲取到用戶角色: {}", userRoleObj);
                            return userRoleObj.toString();
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception e) {
            log.debug("從 Session 獲取用戶角色失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 Session 獲取用戶職位
     */
    private String getUserPositionFromSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object position = session.getAttribute("position");
                if (position != null) {
                    log.debug("從 Session 'position' 獲取到用戶職位: {}", position);
                    return position.toString();
                }
            }
        } catch (Exception e) {
            log.debug("從 Session 獲取用戶職位失敗: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 從 Session 獲取用戶權限
     */
    @SuppressWarnings("unchecked")
    private List<String> getUserPermissionsFromSession(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object permissions = session.getAttribute("userPermissions");
                if (permissions instanceof List) {
                    log.debug("從 Session 'userPermissions' 獲取到用戶權限: {}", permissions);
                    return (List<String>) permissions;
                }
            }
        } catch (Exception e) {
            log.debug("從 Session 獲取用戶權限失敗: {}", e.getMessage());
        }
        return null;
    }
    
    // ===== 輔助方法 =====
    
    /**
     * 獲取當前請求
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
            log.debug("當前請求存在: {}", request != null);
            return request;
        } catch (Exception e) {
            log.warn("獲取當前請求失敗", e);
            return null;
        }
    }
    
    /**
     * 記錄詳細的調試信息
     */
    public void debugCurrentUser() {
        try {
            log.info("=== 🔍 詳細調試當前用戶信息 ===");
            
            // 基本信息
            log.info("認證方式: {}", getCurrentAuthType());
            log.info("用戶ID: {}", getCurrentUserId());
            log.info("用戶名: {}", getCurrentUsername());
            log.info("用戶角色: {}", getCurrentUserRole());
            log.info("用戶職位: {}", getCurrentUserPosition());
            log.info("用戶權限: {}", getCurrentUserPermissions());
            log.info("線程: {}", Thread.currentThread().getName());
            
            // SecurityContext 調試
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            log.info("SecurityContext Authentication: {}", auth);
            if (auth != null) {
                log.info("Authentication Principal: {}", auth.getPrincipal());
                log.info("Authentication Authorities: {}", auth.getAuthorities());
                log.info("Authentication Authenticated: {}", auth.isAuthenticated());
            }
            
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                // JWT 調試
                String token = extractJWTToken(request);
                log.info("JWT Token 存在: {}", token != null);
                if (token != null) {
                    log.info("JWT Token 長度: {}", token.length());
                    log.info("JWT Token 開頭: {}", token.substring(0, Math.min(20, token.length())));
                    log.info("JWT Token 有效: {}", jwtTokenUtil.validateToken(token));
                    
                    Claims claims = extractJWTClaims(token);
                    if (claims != null) {
                        log.info("JWT Claims: {}", claims);
                    }
                }
                
                // Session 調試
                HttpSession session = request.getSession(false);
                log.info("Session 存在: {}", session != null);
                if (session != null) {
                    log.info("Session ID: {}", session.getId());
                    java.util.Enumeration<String> attributeNames = session.getAttributeNames();
                    while (attributeNames.hasMoreElements()) {
                        String name = attributeNames.nextElement();
                        Object value = session.getAttribute(name);
                        log.info("Session 屬性 - {}: {} ({})", name, value, 
                            value != null ? value.getClass().getSimpleName() : "null");
                    }
                }
            }
            
            log.info("===============================");
            
        } catch (Exception e) {
            log.error("調試用戶信息時發生錯誤", e);
        }
    }
}