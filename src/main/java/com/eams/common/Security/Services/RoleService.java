package com.eams.common.Security.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.Security.Repository.RoleRepository;
import com.eams.common.Security.Repository.MemberRoleRepository;
import com.eams.common.Security.Repository.RolePermissionRepository;
import com.eams.common.Security.entity.Role;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class RoleService {
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private MemberRoleRepository memberRoleRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    /**
     * 獲取所有活躍角色
     */
    public List<Role> getAllActiveRoles() {
        return roleRepository.findByIsActiveTrueOrderByLevelPriorityAsc();
    }
    
    /**
     * 根據ID獲取角色
     */
    public Optional<Role> getRoleById(Integer id) {
        return roleRepository.findById(id);
    }
    
    /**
     * 根據角色代碼獲取角色
     */
    public Optional<Role> getRoleByCode(String roleCode) {
        return roleRepository.findByRoleCode(roleCode);
    }
    
    /**
     * 根據角色名稱獲取角色
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }
    
    /**
     * 獲取系統角色
     */
    public List<Role> getSystemRoles() {
        return roleRepository.findByIsSystemRoleTrueOrderByLevelPriorityAsc();
    }
    
    /**
     * 獲取非系統角色
     */
    public List<Role> getNonSystemRoles() {
        return roleRepository.findByIsSystemRoleFalseAndIsActiveTrueOrderByLevelPriorityAsc();
    }
    
    /**
     * 搜尋角色
     */
    public List<Role> searchRoles(String keyword) {
        return roleRepository.findByKeywordInRoleName(keyword);
    }
    
    /**
     * 檢查角色代碼是否存在
     */
    public boolean isRoleCodeExists(String roleCode) {
        return roleRepository.existsByRoleCode(roleCode);
    }
    
    /**
     * 檢查角色名稱是否存在
     */
    public boolean isRoleNameExists(String roleName) {
        return roleRepository.existsByRoleName(roleName);
    }
    
    /**
     * 創建角色
     */
    public Role createRole(Role role) {
        role.setIsActive(true);
        role.setCreatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }
    
    /**
     * 更新角色
     */
    public Role updateRole(Role role) {
        role.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }
    
    /**
     * 軟刪除角色
     */
    public boolean deleteRole(Integer roleId) {
        try {
            Optional<Role> roleOpt = roleRepository.findById(roleId);
            if (roleOpt.isPresent()) {
                Role role = roleOpt.get();
                role.setIsActive(false);
                role.setUpdatedAt(LocalDateTime.now());
                roleRepository.save(role);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 獲取角色統計
     */
    public Map<String, Object> getRoleStatistics(Integer roleId) {
        Map<String, Object> stats = new HashMap<>();
        
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isPresent()) {
            Role role = roleOpt.get();
            stats.put("role", role);
            
            // 統計用戶數量
            Long userCount = memberRoleRepository.countActiveUsersByRoleId(roleId, LocalDateTime.now());
            stats.put("userCount", userCount);
            
            // 統計權限數量
            List<com.eams.common.Security.entity.RolePermission> permissions = 
                rolePermissionRepository.findByRoleId(roleId, LocalDateTime.now());
            stats.put("permissionCount", permissions.size());
        }
        
        return stats;
    }
}