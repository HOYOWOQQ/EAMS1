package com.eams.Repository.chat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.chat.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    
    // 根據用戶ID獲取聊天室列表（通過成員關係）
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "JOIN ChatRoomMember crm ON cr.id = crm.roomId " +
           "WHERE crm.memberId = :userId AND crm.isActive = true " +
           "AND cr.isActive = true " +
           "ORDER BY cr.lastMessageAt DESC NULLS LAST, cr.createdAt DESC")
    List<ChatRoom> findChatRoomsByUserId(@Param("userId") Integer userId);
    
    // 根據課程ID獲取聊天室
    List<ChatRoom> findByCourseIdAndIsActiveTrueOrderByCreatedAtDesc(Integer courseId);
    
    // 根據類型獲取聊天室
    List<ChatRoom> findByTypeAndIsActiveTrueOrderByCreatedAtDesc(String type);
    
    // 根據創建者獲取聊天室
    List<ChatRoom> findByCreatedByAndIsActiveTrueOrderByCreatedAtDesc(Integer createdBy);
    
    // 檢查用戶是否可以訪問聊天室
    @Query("SELECT COUNT(crm) > 0 FROM ChatRoomMember crm " +
           "WHERE crm.roomId = :roomId AND crm.memberId = :userId AND crm.isActive = true")
    boolean canUserAccessRoom(@Param("roomId") Integer roomId, @Param("userId") Integer userId);
    
    // 檢查用戶是否為聊天室管理員
    @Query("SELECT COUNT(crm) > 0 FROM ChatRoomMember crm " +
           "WHERE crm.roomId = :roomId AND crm.memberId = :userId " +
           "AND crm.role = 'admin' AND crm.isActive = true")
    boolean isUserRoomAdmin(@Param("roomId") Integer roomId, @Param("userId") Integer userId);
    
    // 更新最後消息時間
    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.lastMessageAt = :lastMessageAt " +
           "WHERE cr.id = :roomId")
    int updateLastMessageTime(@Param("roomId") Integer roomId, 
                             @Param("lastMessageAt") LocalDateTime lastMessageAt);
    
    // 搜索聊天室
    @Query("SELECT DISTINCT cr FROM ChatRoom cr " +
           "LEFT JOIN ChatRoomMember crm ON cr.id = crm.roomId " +
           "WHERE cr.isActive = true " +
           "AND (cr.name LIKE %:keyword% OR cr.description LIKE %:keyword%) " +
           "AND (crm.memberId = :userId OR cr.type != 'private') " +
           "ORDER BY cr.lastMessageAt DESC NULLS LAST")
    List<ChatRoom> searchChatRooms(@Param("keyword") String keyword, @Param("userId") Integer userId);
    
    // 獲取用戶創建的聊天室數量
    long countByCreatedByAndIsActiveTrue(Integer createdBy);
}
