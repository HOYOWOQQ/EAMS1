package com.eams.common.Security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Security.entity.Permission;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    
    // 根據權限代碼查找權限
    Optional<Permission> findByPermissionCode(String permissionCode);
    
    // 根據分類查找權限
    List<Permission> findByCategoryAndIsActiveTrueOrderByPermissionNameAsc(String category);
    
    // 查找所有啟用的權限
    List<Permission> findByIsActiveTrueOrderByCategoryAscPermissionNameAsc();
    
    // 根據資源類型查找權限
    List<Permission> findByResourceTypeAndIsActiveTrueOrderByPermissionNameAsc(String resourceType);
    
    // 根據操作類型查找權限
    List<Permission> findByActionTypeAndIsActiveTrueOrderByPermissionNameAsc(String actionType);
    
    // 根據資源類型和操作類型查找權限
    List<Permission> findByResourceTypeAndActionTypeAndIsActiveTrueOrderByPermissionNameAsc(String resourceType, String actionType);
    
    // 查找危險權限
    List<Permission> findByIsDangerousTrueAndIsActiveTrueOrderByPermissionNameAsc();
    
    // 檢查權限代碼是否存在
    boolean existsByPermissionCode(String permissionCode);
    
    // 根據創建者查找權限
    List<Permission> findByCreatedByOrderByCreatedAtDesc(Integer createdBy);
    
    // 模糊查詢權限名稱
    @Query("SELECT p FROM Permission p WHERE p.permissionName LIKE %:keyword% AND p.isActive = true")
    List<Permission> findByKeywordInPermissionName(@Param("keyword") String keyword);
    
    // 根據分類統計權限數量
    @Query("SELECT p.category, COUNT(p) FROM Permission p WHERE p.isActive = true GROUP BY p.category")
    List<Object[]> countPermissionsByCategory();
}
