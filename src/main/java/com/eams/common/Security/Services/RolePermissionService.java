package com.eams.common.Security.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.Security.Repository.RolePermissionRepository;
import com.eams.common.Security.Repository.MemberRoleRepository;
import com.eams.common.Security.entity.RolePermission;
import com.eams.common.Security.entity.MemberRole;
import com.eams.common.Security.Services.PermissionChecker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RolePermissionService {
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    @Autowired
    private MemberRoleRepository memberRoleRepository;
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    /**
     * 獲取角色的活躍權限
     */
    public List<RolePermission> getRolePermissions(Integer roleId) {
        return rolePermissionRepository.getRolePermissions(roleId);
    }
    
    /**
     * 獲取角色的所有權限（包含過期的）
     */
    public List<RolePermission> getRoleAllPermissions(Integer roleId) {
        return rolePermissionRepository.findByRoleIdOrderByGrantedAtDesc(roleId);
    }
    
    /**
     * 獲取權限被分配給哪些角色
     */
    public List<RolePermission> getPermissionRoles(Integer permissionId) {
        return rolePermissionRepository.findByPermissionId(permissionId, LocalDateTime.now());
    }
    
    /**
     * 檢查角色是否有某個權限
     */
    public boolean hasPermission(Integer roleId, Integer permissionId) {
        return rolePermissionRepository.existsByRoleIdAndPermissionId(roleId, permissionId, LocalDateTime.now());
    }
    
    /**
     * 為角色分配權限
     */
    public RolePermission assignPermission(Integer roleId, Integer permissionId, Integer grantedBy, 
                                         LocalDateTime expiresAt, String conditions) {
        
        RolePermission rolePermission = RolePermission.builder()
            .roleId(roleId)
            .permissionId(permissionId)
            .grantedAt(LocalDateTime.now())
            .grantedBy(grantedBy)
            .expiresAt(expiresAt)
            .conditions(conditions)
            .isActive(true)
            .build();
        
        RolePermission saved = rolePermissionRepository.save(rolePermission);
        
        // 清除所有擁有此角色的用戶的權限快取
        List<MemberRole> memberRoles = memberRoleRepository.findByRoleId(roleId, LocalDateTime.now());
        for (MemberRole mr : memberRoles) {
            permissionChecker.invalidateUserPermissionsCache(mr.getMemberId());
        }
        
        return saved;
    }
    
    /**
     * 移除角色權限
     */
    public boolean removePermission(Integer rolePermissionId) {
        try {
            Optional<RolePermission> rolePermissionOpt = rolePermissionRepository.findById(rolePermissionId);
            if (rolePermissionOpt.isPresent()) {
                RolePermission rolePermission = rolePermissionOpt.get();
                rolePermission.setIsActive(false);
                rolePermissionRepository.save(rolePermission);
                
                // 清除所有擁有此角色的用戶的權限快取
                List<MemberRole> memberRoles = memberRoleRepository.findByRoleId(
                    rolePermission.getRoleId(), LocalDateTime.now());
                for (MemberRole mr : memberRoles) {
                    permissionChecker.invalidateUserPermissionsCache(mr.getMemberId());
                }
                
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 批量為角色分配權限
     */
    public List<RolePermission> batchAssignPermissions(Integer roleId, List<Integer> permissionIds, 
                                                     Integer grantedBy, LocalDateTime expiresAt, String conditions) {
        
        List<RolePermission> assignments = new java.util.ArrayList<>();
        
        for (Integer permissionId : permissionIds) {
            // 檢查是否已存在
            if (!hasPermission(roleId, permissionId)) {
                RolePermission rolePermission = assignPermission(roleId, permissionId, grantedBy, expiresAt, conditions);
                assignments.add(rolePermission);
            }
        }
        
        return assignments;
    }
    
    /**
     * 獲取角色的權限代碼列表
     */
    public List<String> getRolePermissionCodes(Integer roleId) {
        return rolePermissionRepository.findPermissionCodesByRoleId(roleId, LocalDateTime.now());
    }
    
    /**
     * 獲取即將過期的權限分配
     */
    public List<RolePermission> getExpiringPermissions(int days) {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(days);
        return rolePermissionRepository.findExpiringPermissions(startTime, endTime);
    }
}