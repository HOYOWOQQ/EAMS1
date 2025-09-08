package com.eams.Repository.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.chat.MemberOnlineStatus;

@Repository
public interface MemberOnlineStatusRepository extends JpaRepository<MemberOnlineStatus, Integer> {
    
    // 根據用戶ID查找在線狀態
    Optional<MemberOnlineStatus> findByMemberId(Integer memberId);
    
    // 獲取在線用戶列表
    List<MemberOnlineStatus> findByStatusOrderByUpdatedAtDesc(String status);
    
    // 獲取所有在線狀態（排除離線）
    @Query("SELECT mos FROM MemberOnlineStatus mos " +
           "WHERE mos.status != 'offline' " +
           "ORDER BY mos.updatedAt DESC")
    List<MemberOnlineStatus> findAllOnlineMembers();
    
    // 獲取特定聊天室成員的在線狀態
    @Query("SELECT mos FROM MemberOnlineStatus mos " +
           "JOIN ChatRoomMember crm ON mos.memberId = crm.memberId " +
           "WHERE crm.roomId = :roomId AND crm.isActive = true " +
           "ORDER BY mos.status ASC, mos.updatedAt DESC")
    List<MemberOnlineStatus> findOnlineStatusByRoomId(@Param("roomId") Integer roomId);
    
    // 更新在線狀態
    @Modifying
    @Query("UPDATE MemberOnlineStatus mos SET mos.status = :status, " +
           "mos.lastSeen = CURRENT_TIMESTAMP, mos.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE mos.memberId = :memberId")
    int updateMemberOnlineStatus(@Param("memberId") Integer memberId, @Param("status") String status);
    
    // 批量設置離線狀態（用於系統維護）
    @Modifying
    @Query("UPDATE MemberOnlineStatus mos SET mos.status = 'offline', " +
           "mos.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE mos.updatedAt < :thresholdTime")
    int batchSetOfflineStatus(@Param("thresholdTime") LocalDateTime thresholdTime);
    
    // 統計在線用戶數量
    @Query("SELECT COUNT(mos) FROM MemberOnlineStatus mos WHERE mos.status != 'offline'")
    long countOnlineMembers();
}
