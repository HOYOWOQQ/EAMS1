package com.eams.Repository.chat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.chat.ChatMessageReadStatus;

@Repository
public interface ChatMessageReadStatusRepository extends JpaRepository<ChatMessageReadStatus, Integer> {
    
    // 檢查消息是否已被用戶讀取
    boolean existsByMessageIdAndReaderId(Integer messageId, Integer readerId);
    
    // 獲取消息的讀取狀態列表
    @Query("SELECT cmrs FROM ChatMessageReadStatus cmrs " +
           "JOIN Member m ON cmrs.readerId = m.id " +
           "WHERE cmrs.messageId = :messageId " +
           "ORDER BY cmrs.readAt ASC")
    List<ChatMessageReadStatus> findByMessageIdOrderByReadAtAsc(@Param("messageId") Integer messageId);
    
    // 統計消息讀取人數
    long countByMessageId(Integer messageId);
    
    // 批量標記消息為已讀
    @Query("SELECT cm.id FROM ChatMessage cm " +
           "WHERE cm.roomId = :roomId AND cm.senderId != :userId " +
           "AND cm.createdAt <= :readTime AND cm.isDeleted = false")
    List<Integer> findUnreadMessageIds(@Param("roomId") Integer roomId, 
                                     @Param("userId") Integer userId, 
                                     @Param("readTime") LocalDateTime readTime);
    
    // 刪除讀取狀態（用於消息刪除時清理）
    @Modifying
    @Query("DELETE FROM ChatMessageReadStatus cmrs WHERE cmrs.messageId = :messageId")
    int deleteByMessageId(@Param("messageId") Integer messageId);
}