package com.eams.common.Security.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Security.entity.MemberRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRoleRepository extends JpaRepository<MemberRole, Integer> {
    
    // 根據用戶ID查找有效角色
    @Query("SELECT mr FROM MemberRole mr JOIN FETCH mr.role WHERE mr.memberId = :memberId AND mr.isActive = true AND (mr.expiresAt IS NULL OR mr.expiresAt > :currentTime)")
    List<MemberRole> findByMemberId(@Param("memberId") Integer memberId, @Param("currentTime") LocalDateTime currentTime);
    
    // 根據角色ID查找用戶
    @Query("SELECT mr FROM MemberRole mr WHERE mr.roleId = :roleId AND mr.isActive = true AND (mr.expiresAt IS NULL OR mr.expiresAt > :currentTime)")
    List<MemberRole> findByRoleId(@Param("roleId") Integer roleId, @Param("currentTime") LocalDateTime currentTime);
    
    // 查找用戶的主要角色
    @Query("SELECT mr FROM MemberRole mr JOIN FETCH mr.role WHERE mr.memberId = :memberId AND mr.isPrimary = true AND mr.isActive = true AND (mr.expiresAt IS NULL OR mr.expiresAt > :currentTime)")
    Optional<MemberRole> findPrimaryRoleByMemberId(@Param("memberId") Integer memberId, @Param("currentTime") LocalDateTime currentTime);
    
    // 查找用戶的所有角色（包含過期的）
    List<MemberRole> findByMemberIdOrderByAssignedAtDesc(Integer memberId);
    
    // 檢查用戶是否有特定角色
    @Query("SELECT COUNT(mr) > 0 FROM MemberRole mr WHERE mr.memberId = :memberId AND mr.roleId = :roleId AND mr.isActive = true AND (mr.expiresAt IS NULL OR mr.expiresAt > :currentTime)")
    boolean existsByMemberIdAndRoleId(@Param("memberId") Integer memberId, @Param("roleId") Integer roleId, @Param("currentTime") LocalDateTime currentTime);
    
    // 查找即將過期的用戶角色
    @Query("SELECT mr FROM MemberRole mr WHERE mr.expiresAt BETWEEN :startTime AND :endTime AND mr.isActive = true")
    List<MemberRole> findExpiringMemberRoles(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 根據指派者查找角色分配記錄
    List<MemberRole> findByAssignedByOrderByAssignedAtDesc(Integer assignedBy);
    
    // 統計角色的用戶數量
    @Query("SELECT COUNT(mr) FROM MemberRole mr WHERE mr.roleId = :roleId AND mr.isActive = true AND (mr.expiresAt IS NULL OR mr.expiresAt > :currentTime)")
    Long countActiveUsersByRoleId(@Param("roleId") Integer roleId, @Param("currentTime") LocalDateTime currentTime);
    
    // 查找用戶的角色代碼
    @Query("SELECT r.roleCode FROM MemberRole mr JOIN mr.role r WHERE mr.memberId = :memberId AND mr.isActive = true AND (mr.expiresAt IS NULL OR mr.expiresAt > :currentTime)")
    List<String> findRoleCodesByMemberId(@Param("memberId") Integer memberId, @Param("currentTime") LocalDateTime currentTime);
    
    // 刪除用戶的特定角色
    void deleteByMemberIdAndRoleId(Integer memberId, Integer roleId);
    
    @Query("SELECT mr FROM MemberRole mr JOIN FETCH mr.role WHERE mr.memberId = :memberId " +
            "AND mr.isActive = true AND (mr.expiresAt IS NULL OR mr.expiresAt > CURRENT_TIMESTAMP)")
     List<MemberRole> getUserActiveRoles(@Param("memberId") Integer memberId);
    
    Optional<MemberRole> findByMemberIdAndRoleId(Integer memberId, Integer roleId);
}