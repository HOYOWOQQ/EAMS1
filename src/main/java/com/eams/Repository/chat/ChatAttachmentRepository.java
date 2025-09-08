package com.eams.Repository.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.Entity.chat.ChatAttachment;
@Repository
public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment, Integer> {
    
    // 根據消息ID獲取附件列表
    List<ChatAttachment> findByMessageIdOrderByUploadedAtAsc(Integer messageId);
    
    // 根據文件類型獲取附件
    @Query("SELECT ca FROM ChatAttachment ca " +
           "JOIN ChatMessage cm ON ca.messageId = cm.id " +
           "JOIN ChatRoomMember crm ON cm.roomId = crm.roomId " +
           "WHERE crm.memberId = :userId AND crm.isActive = true " +
           "AND ca.fileType LIKE %:fileType% " +
           "ORDER BY ca.uploadedAt DESC")
    List<ChatAttachment> findAttachmentsByFileType(@Param("fileType") String fileType, 
                                                  @Param("userId") Integer userId);
    
    // 統計用戶上傳的文件總大小
    @Query("SELECT COALESCE(SUM(ca.fileSize), 0) FROM ChatAttachment ca " +
           "JOIN ChatMessage cm ON ca.messageId = cm.id " +
           "WHERE cm.senderId = :userId")
    Long getTotalFileSizeByUserId(@Param("userId") Integer userId);
    
    // 獲取聊天室的所有附件
    @Query("SELECT ca FROM ChatAttachment ca " +
           "JOIN ChatMessage cm ON ca.messageId = cm.id " +
           "WHERE cm.roomId = :roomId " +
           "ORDER BY ca.uploadedAt DESC")
    List<ChatAttachment> findAttachmentsByRoomId(@Param("roomId") Integer roomId);
}
