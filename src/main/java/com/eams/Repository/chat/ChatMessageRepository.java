package com.eams.Repository.chat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.eams.Entity.chat.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    // 根據聊天室ID獲取消息（分頁）
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.roomId = :roomId AND cm.isDeleted = false " +
           "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(@Param("roomId") Integer roomId, 
                                                                        Pageable pageable);
    
    // 根據聊天室ID獲取消息列表（不分頁，用於實時更新）
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.roomId = :roomId AND cm.isDeleted = false " +
           "AND cm.createdAt > :afterTime " +
           "ORDER BY cm.createdAt ASC")
    List<ChatMessage> findMessagesByRoomIdAfterTime(@Param("roomId") Integer roomId, 
                                                   @Param("afterTime") LocalDateTime afterTime);
    
    // 獲取聊天室最後一條消息
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.roomId = :roomId AND cm.isDeleted = false " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> findLastMessageByRoomId(@Param("roomId") Integer roomId, Pageable pageable);
    
    // 獲取用戶未讀消息數量
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "LEFT JOIN ChatMessageReadStatus cmrs ON cm.id = cmrs.messageId AND cmrs.readerId = :userId " +
           "JOIN ChatRoomMember crm ON cm.roomId = crm.roomId " +
           "WHERE crm.memberId = :userId AND crm.isActive = true " +
           "AND cm.senderId != :userId AND cm.isDeleted = false " +
           "AND cmrs.id IS NULL " +
           "AND (crm.lastReadAt IS NULL OR cm.createdAt > crm.lastReadAt)")
    long countUnreadMessagesByUserId(@Param("userId") Integer userId);
    
    // 獲取特定聊天室的未讀消息數量
    @Query("SELECT COUNT(cm) FROM ChatMessage cm " +
           "LEFT JOIN ChatMessageReadStatus cmrs ON cm.id = cmrs.messageId AND cmrs.readerId = :userId " +
           "JOIN ChatRoomMember crm ON cm.roomId = crm.roomId " +
           "WHERE cm.roomId = :roomId AND crm.memberId = :userId AND crm.isActive = true " +
           "AND cm.senderId != :userId AND cm.isDeleted = false " +
           "AND cmrs.id IS NULL " +
           "AND (crm.lastReadAt IS NULL OR cm.createdAt > crm.lastReadAt)")
    long countUnreadMessagesByRoomIdAndUserId(@Param("roomId") Integer roomId, @Param("userId") Integer userId);
    
    // 搜索消息
    @Query("SELECT cm FROM ChatMessage cm " +
           "JOIN ChatRoomMember crm ON cm.roomId = crm.roomId " +
           "WHERE crm.memberId = :userId AND crm.isActive = true " +
           "AND cm.isDeleted = false " +
           "AND cm.content LIKE %:keyword% " +
           "ORDER BY cm.createdAt DESC")
    List<ChatMessage> searchMessagesByKeyword(@Param("keyword") String keyword, @Param("userId") Integer userId);
    
    // 軟刪除消息
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isDeleted = true, cm.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE cm.id = :messageId")
    int softDeleteMessage(@Param("messageId") Integer messageId);
    
    // 更新消息內容（編輯）
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.content = :content, cm.isEdited = true, " +
           "cm.updatedAt = CURRENT_TIMESTAMP WHERE cm.id = :messageId")
    int updateMessageContent(@Param("messageId") Integer messageId, @Param("content") String content);
    
    // 獲取回復消息
    List<ChatMessage> findByReplyToIdAndIsDeletedFalseOrderByCreatedAtAsc(Integer replyToId);
    
    // 統計聊天室消息總數
    long countByRoomIdAndIsDeletedFalse(Integer roomId);
}
