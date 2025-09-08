package com.eams.Repository.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.chat.ChatRoomMember;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Integer> {
    
    // 根據聊天室ID獲取成員列表
    @Query("SELECT crm FROM ChatRoomMember crm " +
           "JOIN Member m ON crm.memberId = m.id " +
           "WHERE crm.roomId = :roomId AND crm.isActive = true " +
           "ORDER BY crm.role DESC, crm.joinedAt ASC")
    List<ChatRoomMember> findByRoomIdAndIsActiveTrueOrderByRole(@Param("roomId") Integer roomId);
    
    // 根據用戶ID獲取聊天室成員記錄
    List<ChatRoomMember> findByMemberIdAndIsActiveTrueOrderByJoinedAtDesc(Integer memberId);
    
    // 檢查用戶是否為聊天室成員
    boolean existsByRoomIdAndMemberIdAndIsActiveTrue(Integer roomId, Integer memberId);
    
    // 獲取聊天室成員數量
    long countByRoomIdAndIsActiveTrue(Integer roomId);
    
    // 根據聊天室ID和成員ID查找
    Optional<ChatRoomMember> findByRoomIdAndMemberIdAndIsActiveTrue(Integer roomId, Integer memberId);
    
    // 軟刪除聊天室成員
    @Modifying
    @Query("UPDATE ChatRoomMember crm SET crm.isActive = false " +
           "WHERE crm.roomId = :roomId AND crm.memberId = :memberId")
    int removeMemberFromRoom(@Param("roomId") Integer roomId, @Param("memberId") Integer memberId);
    
    // 更新最後讀取時間
    @Modifying
    @Query("UPDATE ChatRoomMember crm SET crm.lastReadAt = :readTime " +
           "WHERE crm.roomId = :roomId AND crm.memberId = :memberId")
    int updateLastReadTime(@Param("roomId") Integer roomId, 
                          @Param("memberId") Integer memberId, 
                          @Param("readTime") LocalDateTime readTime);
    
    // 獲取聊天室管理員
    @Query("SELECT crm FROM ChatRoomMember crm " +
           "WHERE crm.roomId = :roomId AND crm.role = 'admin' AND crm.isActive = true")
    List<ChatRoomMember> findRoomAdmins(@Param("roomId") Integer roomId);
}
