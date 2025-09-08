package com.eams.common.Security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Security.entity.RolePermission;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    
    // 根據角色ID查找權限
    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.permission WHERE rp.roleId = :roleId AND rp.isActive = true AND (rp.expiresAt IS NULL OR rp.expiresAt > :currentTime)")
    List<RolePermission> findByRoleId(@Param("roleId") Integer roleId, @Param("currentTime") LocalDateTime currentTime);
    
    // 根據權限ID查找角色
    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.role WHERE rp.permissionId = :permissionId AND rp.isActive = true AND (rp.expiresAt IS NULL OR rp.expiresAt > :currentTime)")
    List<RolePermission> findByPermissionId(@Param("permissionId") Integer permissionId, @Param("currentTime") LocalDateTime currentTime);
    
    // 查找角色的所有權限（包含過期的）
    List<RolePermission> findByRoleIdOrderByGrantedAtDesc(Integer roleId);
    
    // 檢查角色是否有特定權限
    @Query("SELECT COUNT(rp) > 0 FROM RolePermission rp WHERE rp.roleId = :roleId AND rp.permissionId = :permissionId AND rp.isActive = true AND (rp.expiresAt IS NULL OR rp.expiresAt > :currentTime)")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") Integer roleId, @Param("permissionId") Integer permissionId, @Param("currentTime") LocalDateTime currentTime);
    
    // 查找即將過期的角色權限
    @Query("SELECT rp FROM RolePermission rp WHERE rp.expiresAt BETWEEN :startTime AND :endTime AND rp.isActive = true")
    List<RolePermission> findExpiringPermissions(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 根據授予者查找權限分配記錄
    List<RolePermission> findByGrantedByOrderByGrantedAtDesc(Integer grantedBy);
    
    // 刪除角色的特定權限
    void deleteByRoleIdAndPermissionId(Integer roleId, Integer permissionId);
    
    // 查找角色的有效權限代碼
    @Query("SELECT p.permissionCode FROM RolePermission rp JOIN rp.permission p WHERE rp.roleId = :roleId AND rp.isActive = true AND (rp.expiresAt IS NULL OR rp.expiresAt > :currentTime)")
    List<String> findPermissionCodesByRoleId(@Param("roleId") Integer roleId, @Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT rp FROM RolePermission rp JOIN FETCH rp.permission WHERE rp.roleId = :roleId " +
            "AND rp.isActive = true AND (rp.expiresAt IS NULL OR rp.expiresAt > CURRENT_TIMESTAMP)")
     List<RolePermission> getRolePermissions(@Param("roleId") Integer roleId);
}
