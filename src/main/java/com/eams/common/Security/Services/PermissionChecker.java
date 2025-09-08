package com.eams.common.Security.Services;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.eams.common.Security.Repository.MemberRoleRepository;
import com.eams.common.Security.Repository.RolePermissionRepository;
import com.eams.common.Security.entity.MemberRole;
import com.eams.common.Security.entity.RolePermission;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PermissionChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionChecker.class);
    private static final String CACHE_NAME = "userPermissions";
    
    private final MemberRoleRepository memberRoleRepo;
    private final RolePermissionRepository rolePermissionRepo;
    private final CacheManager cacheManager;

    public PermissionChecker(
            MemberRoleRepository memberRoleRepo,
            RolePermissionRepository rolePermissionRepo,
            CacheManager cacheManager) {
        this.memberRoleRepo = memberRoleRepo;
        this.rolePermissionRepo = rolePermissionRepo;
        this.cacheManager = cacheManager;
    }

    // ========== 原有方法保持不變 ==========

    public CompletableFuture<Boolean> hasPermissionAsync(Integer userId, String permissionCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> permissions = getUserPermissionsAsync(userId).join();
                return permissions.contains(permissionCode);
            } catch (Exception ex) {
                logger.error("Error checking permission {} for user {}", permissionCode, userId, ex);
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> hasAnyPermissionAsync(Integer userId, String... permissionCodes) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> permissions = getUserPermissionsAsync(userId).join();
                return Arrays.stream(permissionCodes).anyMatch(permissions::contains);
            } catch (Exception ex) {
                logger.error("Error checking any permissions for user {}", userId, ex);
                return false;
            }
        });
    }

    public CompletableFuture<List<String>> getUserPermissionsAsync(Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = "user_permissions_" + userId;
            Cache cache = cacheManager.getCache(CACHE_NAME);
            
            if (cache != null) {
                Cache.ValueWrapper cached = cache.get(cacheKey);
                if (cached != null) {
                    @SuppressWarnings("unchecked")
                    List<String> cachedPermissions = (List<String>) cached.get();
                    return cachedPermissions;
                }
            }

            try {
                List<MemberRole> userRoles = memberRoleRepo.getUserActiveRoles(userId);
                Set<String> permissions = new HashSet<>();

                for (MemberRole role : userRoles) {
                    List<RolePermission> rolePermissions = rolePermissionRepo.getRolePermissions(role.getRoleId());
                    for (RolePermission permission : rolePermissions) {
                        if (permission.getIsActive() ) {
                            permissions.add(permission.getPermission().getPermissionCode());
                        }
                    }
                }

                List<String> result = new ArrayList<>(permissions);
                if (cache != null) {
                    cache.put(cacheKey, result);
                }
                return result;
            } catch (Exception ex) {
                logger.error("Error getting permissions for user {}", userId, ex);
                return new ArrayList<>();
            }
        });
    }

    public void invalidateUserPermissionsCache(Integer userId) {
        String cacheKey = "user_permissions_" + userId;
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.evict(cacheKey);
        }
    }

    // ========== 新增 JWT 支援方法 ==========

    /**
     * 從當前 JWT Token 中獲取用戶 ID
     */
    private Integer getCurrentUserIdFromJWT() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getId();
            }
        } catch (Exception ex) {
            logger.debug("無法從 JWT 獲取用戶 ID: {}", ex.getMessage());
        }
        return null;
    }

    /**
     * 從當前 JWT Token 中獲取權限列表（優先使用，避免資料庫查詢）
     */
    private List<String> getCurrentUserPermissionsFromJWT() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                List<String> permissions = userDetails.getPermissions();
                if (permissions != null && !permissions.isEmpty()) {
                    logger.debug("從 JWT 獲取到 {} 個權限", permissions.size());
                    return permissions;
                }
            }
        } catch (Exception ex) {
            logger.debug("無法從 JWT 獲取權限: {}", ex.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * 檢查當前用戶是否有指定權限（新增：JWT 優先，Session 備用）
     */
    public CompletableFuture<Boolean> hasCurrentUserPermissionAsync(String permissionCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 優先從 JWT 獲取權限（避免資料庫查詢）
                List<String> jwtPermissions = getCurrentUserPermissionsFromJWT();
                if (!jwtPermissions.isEmpty()) {
                    boolean hasPermission = jwtPermissions.contains(permissionCode);
                    logger.debug("JWT 權限檢查 [{}]: {}", permissionCode, hasPermission);
                    return hasPermission;
                }
                
                // 備用方案：從資料庫獲取（Session 登入時使用）
                Integer userId = getCurrentUserIdFromJWT();
                if (userId != null) {
                    logger.debug("使用資料庫權限檢查，用戶 ID: {}", userId);
                    return hasPermissionAsync(userId, permissionCode).join();
                }
                
                logger.warn("無法獲取當前用戶資訊進行權限檢查: {}", permissionCode);
                return false;
            } catch (Exception ex) {
                logger.error("Error checking current user permission {}", permissionCode, ex);
                return false;
            }
        });
    }

    /**
     * 檢查當前用戶是否有任一指定權限（新增：JWT 優先）
     */
    public CompletableFuture<Boolean> hasCurrentUserAnyPermissionAsync(String... permissionCodes) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 優先從 JWT 獲取權限
                List<String> jwtPermissions = getCurrentUserPermissionsFromJWT();
                if (!jwtPermissions.isEmpty()) {
                    boolean hasAny = Arrays.stream(permissionCodes).anyMatch(jwtPermissions::contains);
                    logger.debug("JWT 任一權限檢查 {}: {}", Arrays.toString(permissionCodes), hasAny);
                    return hasAny;
                }
                
                // 備用方案：從資料庫獲取
                Integer userId = getCurrentUserIdFromJWT();
                if (userId != null) {
                    logger.debug("使用資料庫任一權限檢查，用戶 ID: {}", userId);
                    return hasAnyPermissionAsync(userId, permissionCodes).join();
                }
                
                logger.warn("無法獲取當前用戶資訊進行任一權限檢查: {}", Arrays.toString(permissionCodes));
                return false;
            } catch (Exception ex) {
                logger.error("Error checking current user any permissions", ex);
                return false;
            }
        });
    }

    /**
     * 同步版本：檢查當前用戶權限（推薦使用，效能最佳）
     */
    public boolean hasCurrentUserPermission(String permissionCode) {
        try {
            // 優先從 JWT 獲取（同步，效能更好）
            List<String> jwtPermissions = getCurrentUserPermissionsFromJWT();
            if (!jwtPermissions.isEmpty()) {
                boolean hasPermission = jwtPermissions.contains(permissionCode);
                logger.debug("同步 JWT 權限檢查 [{}]: {}", permissionCode, hasPermission);
                return hasPermission;
            }
            
            // 備用方案：使用異步方法
            logger.debug("回退到異步權限檢查: {}", permissionCode);
            return hasCurrentUserPermissionAsync(permissionCode).join();
        } catch (Exception ex) {
            logger.error("Error checking current user permission sync {}", permissionCode, ex);
            return false;
        }
    }

    /**
     * 同步版本：檢查當前用戶任一權限
     */
    public boolean hasCurrentUserAnyPermission(String... permissionCodes) {
        try {
            // 優先從 JWT 獲取（同步，效能更好）
            List<String> jwtPermissions = getCurrentUserPermissionsFromJWT();
            if (!jwtPermissions.isEmpty()) {
                boolean hasAny = Arrays.stream(permissionCodes).anyMatch(jwtPermissions::contains);
                logger.debug("同步 JWT 任一權限檢查 {}: {}", Arrays.toString(permissionCodes), hasAny);
                return hasAny;
            }
            
            // 備用方案：使用異步方法
            return hasCurrentUserAnyPermissionAsync(permissionCodes).join();
        } catch (Exception ex) {
            logger.error("Error checking current user any permissions sync", ex);
            return false;
        }
    }

    /**
     * 獲取當前用戶 ID（兼容原有邏輯）
     */
    public Integer getCurrentUserId() {
        return getCurrentUserIdFromJWT();
    }

    /**
     * 獲取當前用戶的所有權限
     */
    public List<String> getCurrentUserPermissions() {
        List<String> jwtPermissions = getCurrentUserPermissionsFromJWT();
        if (!jwtPermissions.isEmpty()) {
            return jwtPermissions;
        }
        
        // 備用方案：從資料庫獲取
        Integer userId = getCurrentUserId();
        if (userId != null) {
            return getUserPermissionsAsync(userId).join();
        }
        
        return new ArrayList<>();
    }

    /**
     * 獲取當前用戶詳細資訊
     */
    public CustomUserDetails getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                return (CustomUserDetails) authentication.getPrincipal();
            }
        } catch (Exception ex) {
            logger.debug("無法獲取當前用戶: {}", ex.getMessage());
        }
        return null;
    }
}