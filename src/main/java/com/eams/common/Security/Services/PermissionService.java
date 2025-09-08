package com.eams.common.Security.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.Security.Repository.PermissionRepository;
import com.eams.common.Security.entity.Permission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    /**
     * 獲取所有活躍權限
     */
    public List<Permission> getAllActivePermissions() {
        return permissionRepository.findByIsActiveTrueOrderByCategoryAscPermissionNameAsc();
    }
    
    /**
     * 根據ID獲取權限
     */
    public Optional<Permission> getPermissionById(Integer id) {
        return permissionRepository.findById(id);
    }
    
    /**
     * 根據權限代碼獲取權限
     */
    public Optional<Permission> getPermissionByCode(String permissionCode) {
        return permissionRepository.findByPermissionCode(permissionCode);
    }
    
    /**
     * 根據分類獲取權限
     */
    public List<Permission> getPermissionsByCategory(String category) {
        return permissionRepository.findByCategoryAndIsActiveTrueOrderByPermissionNameAsc(category);
    }
    
    /**
     * 根據資源類型獲取權限
     */
    public List<Permission> getPermissionsByResourceType(String resourceType) {
        return permissionRepository.findByResourceTypeAndIsActiveTrueOrderByPermissionNameAsc(resourceType);
    }
    
    /**
     * 根據操作類型獲取權限
     */
    public List<Permission> getPermissionsByActionType(String actionType) {
        return permissionRepository.findByActionTypeAndIsActiveTrueOrderByPermissionNameAsc(actionType);
    }
    
    /**
     * 獲取危險權限
     */
    public List<Permission> getDangerousPermissions() {
        return permissionRepository.findByIsDangerousTrueAndIsActiveTrueOrderByPermissionNameAsc();
    }
    
    /**
     * 搜尋權限
     */
    public List<Permission> searchPermissions(String keyword) {
        return permissionRepository.findByKeywordInPermissionName(keyword);
    }
    
    /**
     * 檢查權限代碼是否存在
     */
    public boolean isPermissionCodeExists(String permissionCode) {
        return permissionRepository.existsByPermissionCode(permissionCode);
    }
    
    /**
     * 創建權限
     */
    public Permission createPermission(Permission permission) {
        permission.setIsActive(true);
        permission.setCreatedAt(LocalDateTime.now());
        return permissionRepository.save(permission);
    }
    
    /**
     * 更新權限
     */
    public Permission updatePermission(Permission permission) {
        return permissionRepository.save(permission);
    }
    
    /**
     * 軟刪除權限
     */
    public boolean deletePermission(Integer permissionId) {
        try {
            Optional<Permission> permissionOpt = permissionRepository.findById(permissionId);
            if (permissionOpt.isPresent()) {
                Permission permission = permissionOpt.get();
                permission.setIsActive(false);
                permissionRepository.save(permission);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 獲取權限分類統計
     */
    public List<Object[]> getPermissionCategoryStatistics() {
        return permissionRepository.countPermissionsByCategory();
    }
}