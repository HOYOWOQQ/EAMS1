package com.eams.common.Security.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Security.entity.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    // 根據角色代碼查找角色
    Optional<Role> findByRoleCode(String roleCode);
    
    // 根據角色名稱查找角色
    Optional<Role> findByRoleName(String roleName);
    
    // 查找所有啟用的角色
    List<Role> findByIsActiveTrueOrderByLevelPriorityAsc();
    
    // 查找系統角色
    List<Role> findByIsSystemRoleTrueOrderByLevelPriorityAsc();
    
    // 查找非系統角色
    List<Role> findByIsSystemRoleFalseAndIsActiveTrueOrderByLevelPriorityAsc();
    
    // 根據優先級範圍查找角色
    @Query("SELECT r FROM Role r WHERE r.levelPriority BETWEEN :minPriority AND :maxPriority AND r.isActive = true ORDER BY r.levelPriority")
    List<Role> findByPriorityRange(@Param("minPriority") Integer minPriority, @Param("maxPriority") Integer maxPriority);
    
    // 查找有用戶數限制的角色
    @Query("SELECT r FROM Role r WHERE r.maxUsers IS NOT NULL AND r.isActive = true")
    List<Role> findRolesWithUserLimit();
    
    // 檢查角色代碼是否存在
    boolean existsByRoleCode(String roleCode);
    
    // 檢查角色名稱是否存在
    boolean existsByRoleName(String roleName);
    
    // 根據創建者查找角色
    List<Role> findByCreatedByOrderByCreatedAtDesc(Integer createdBy);
    
    // 模糊查詢角色名稱
    @Query("SELECT r FROM Role r WHERE r.roleName LIKE %:keyword% AND r.isActive = true")
    List<Role> findByKeywordInRoleName(@Param("keyword") String keyword);
}