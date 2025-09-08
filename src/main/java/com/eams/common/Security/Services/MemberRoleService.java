package com.eams.common.Security.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.Security.Repository.MemberRoleRepository;
import com.eams.common.Security.entity.MemberRole;
import com.eams.common.Security.Services.PermissionChecker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MemberRoleService {
    
    @Autowired
    private MemberRoleRepository memberRoleRepository;
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    /**
     * 獲取用戶的活躍角色
     */
    public List<MemberRole> getUserActiveRoles(Integer memberId) {
        return memberRoleRepository.getUserActiveRoles(memberId);
    }
    
    /**
     * 獲取用戶的所有角色（包含過期的）
     */
    public List<MemberRole> getUserAllRoles(Integer memberId) {
        return memberRoleRepository.findByMemberIdOrderByAssignedAtDesc(memberId);
    }
    
    /**
     * 獲取用戶的主要角色
     */
    public Optional<MemberRole> getUserPrimaryRole(Integer memberId) {
        return memberRoleRepository.findPrimaryRoleByMemberId(memberId, LocalDateTime.now());
    }
    
    /**
     * 獲取角色的所有用戶
     */
    public List<MemberRole> getRoleUsers(Integer roleId) {
        return memberRoleRepository.findByRoleId(roleId, LocalDateTime.now());
    }
    
    /**
     * 檢查用戶是否有某個角色
     */
    public boolean hasRole(Integer memberId, Integer roleId) {
        return memberRoleRepository.existsByMemberIdAndRoleId(memberId, roleId, LocalDateTime.now());
    }
    
    /**
     * 分配角色給用戶
     */
    public MemberRole assignRole(Integer memberId, Integer roleId, Integer assignedBy,
                               LocalDateTime expiresAt, String conditions, Boolean isPrimary, String notes) {

        Optional<MemberRole> existingRole = memberRoleRepository.findByMemberIdAndRoleId(memberId, roleId);
        
        MemberRole memberRole;
        
        if (existingRole.isPresent()) {
            memberRole = existingRole.get();
            memberRole.setAssignedAt(LocalDateTime.now());
            memberRole.setAssignedBy(assignedBy);
            memberRole.setExpiresAt(expiresAt);
            memberRole.setConditions(conditions);
            memberRole.setIsActive(true);  
            memberRole.setIsPrimary(isPrimary);
            memberRole.setNotes(notes);
        } else {
            memberRole = MemberRole.builder()
                    .memberId(memberId)
                    .roleId(roleId)
                    .assignedAt(LocalDateTime.now())
                    .assignedBy(assignedBy)
                    .expiresAt(expiresAt)
                    .conditions(conditions)
                    .isActive(true)
                    .isPrimary(isPrimary)
                    .notes(notes)
                    .build();
        }

        // 如果設為主要角色，先取消其他主要角色
        if (Boolean.TRUE.equals(isPrimary)) {
            Optional<MemberRole> existingPrimary = memberRoleRepository.findPrimaryRoleByMemberId(memberId, LocalDateTime.now());
            if (existingPrimary.isPresent() && !existingPrimary.get().getId().equals(memberRole.getId())) {
                MemberRole primary = existingPrimary.get();
                primary.setIsPrimary(false);
                memberRoleRepository.save(primary);
            }
        }

        MemberRole saved = memberRoleRepository.save(memberRole);

        // 清除權限快取
        permissionChecker.invalidateUserPermissionsCache(memberId);

        return saved;
    }
    
    /**
     * 移除用戶角色
     */
    public boolean removeRole(Integer memberRoleId) {
        try {
            Optional<MemberRole> memberRoleOpt = memberRoleRepository.findById(memberRoleId);
            if (memberRoleOpt.isPresent()) {
                MemberRole memberRole = memberRoleOpt.get();
                memberRole.setIsActive(false);
                memberRoleRepository.save(memberRole);
                
                // 清除權限快取
                permissionChecker.invalidateUserPermissionsCache(memberRole.getMemberId());
                
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 更新用戶角色設定
     */
    public MemberRole updateMemberRole(Integer memberRoleId, LocalDateTime expiresAt, 
                                     String conditions, Boolean isPrimary, String notes) {
        
        Optional<MemberRole> memberRoleOpt = memberRoleRepository.findById(memberRoleId);
        if (memberRoleOpt.isPresent()) {
            MemberRole memberRole = memberRoleOpt.get();
            
            // 如果要設為主要角色，先取消其他主要角色
            if (Boolean.TRUE.equals(isPrimary) && !memberRole.getIsPrimary()) {
                Optional<MemberRole> existingPrimary = memberRoleRepository.findPrimaryRoleByMemberId(
                    memberRole.getMemberId(), LocalDateTime.now());
                if (existingPrimary.isPresent()) {
                    MemberRole primary = existingPrimary.get();
                    primary.setIsPrimary(false);
                    memberRoleRepository.save(primary);
                }
            }
            
            memberRole.setExpiresAt(expiresAt);
            memberRole.setConditions(conditions);
            memberRole.setIsPrimary(isPrimary);
            memberRole.setNotes(notes);
            
            MemberRole saved = memberRoleRepository.save(memberRole);
            
            // 清除權限快取
            permissionChecker.invalidateUserPermissionsCache(memberRole.getMemberId());
            
            return saved;
        }
        return null;
    }
    
    /**
     * 獲取即將過期的角色分配
     */
    public List<MemberRole> getExpiringRoles(int days) {
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusDays(days);
        return memberRoleRepository.findExpiringMemberRoles(startTime, endTime);
    }
    
    /**
     * 獲取用戶的角色代碼列表
     */
    public List<String> getUserRoleCodes(Integer memberId) {
        return memberRoleRepository.findRoleCodesByMemberId(memberId, LocalDateTime.now());
    }
    
    /**
     * 統計角色的用戶數量
     */
    public Long countRoleUsers(Integer roleId) {
        return memberRoleRepository.countActiveUsersByRoleId(roleId, LocalDateTime.now());
    }
}