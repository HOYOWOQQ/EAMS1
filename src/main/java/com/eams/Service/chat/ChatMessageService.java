package com.eams.Service.chat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.chat.ChatMessage;
import com.eams.Entity.chat.chatDTO.ChatMessageCreateDTO;
import com.eams.Entity.chat.chatDTO.ChatMessageDTO;
import com.eams.Entity.member.Member;
import com.eams.Repository.chat.ChatMessageReadStatusRepository;
import com.eams.Repository.chat.ChatMessageRepository;
import com.eams.Repository.chat.ChatRoomMemberRepository;
import com.eams.Repository.chat.ChatRoomRepository;
import com.eams.Repository.member.MemberRepository;

@Service
@Transactional
public class ChatMessageService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Autowired
    private ChatRoomMemberRepository chatRoomMemberRepository;
    
    @Autowired
    private ChatMessageReadStatusRepository chatMessageReadStatusRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    // 發送消息
    public ChatMessageDTO sendMessage(ChatMessageCreateDTO createDTO, Integer senderId) {
        // 檢查是否有權限在此聊天室發送消息
        if (!chatRoomRepository.canUserAccessRoom(createDTO.getRoomId(), senderId)) {
            throw new RuntimeException("您沒有權限在此聊天室發送消息");
        }
        
        // 創建消息
        ChatMessage message = new ChatMessage(createDTO.getRoomId(), senderId, createDTO.getContent());
        message.setMessageType(createDTO.getMessageType());
        message.setReplyToId(createDTO.getReplyToId());
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // 更新聊天室最後消息時間
        chatRoomRepository.updateLastMessageTime(createDTO.getRoomId(), savedMessage.getCreatedAt());
        
        return convertToDTO(savedMessage, senderId);
    }
    
    // 獲取聊天室消息（分頁）
    public Map<String, Object> getChatRoomMessages(Integer roomId, Integer page, Integer size, Integer userId) {
        // 檢查權限
        if (!chatRoomRepository.canUserAccessRoom(roomId, userId)) {
            throw new RuntimeException("您沒有權限查看此聊天室的消息");
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagePage = chatMessageRepository.findByRoomIdAndIsDeletedFalseOrderByCreatedAtDesc(roomId, pageable);
        
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();
        for (ChatMessage message : messagePage.getContent()) {
            ChatMessageDTO dto = convertToDTO(message, userId);
            messageDTOs.add(dto);
        }
        
        // 標記消息為已讀
        markRoomMessagesAsRead(roomId, userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("messages", messageDTOs);
        result.put("totalPages", messagePage.getTotalPages());
        result.put("totalElements", messagePage.getTotalElements());
        result.put("currentPage", page);
        result.put("size", size);
        
        return result;
    }
    
    // 編輯消息
    public ChatMessageDTO editMessage(Integer messageId, String content, Integer userId) {
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            throw new RuntimeException("消息不存在");
        }
        
        ChatMessage message = messageOpt.get();
        
        // 檢查權限（只有發送者可以編輯）
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("您只能編輯自己發送的消息");
        }
        
        // 檢查消息是否已刪除
        if (message.getIsDeleted()) {
            throw new RuntimeException("無法編輯已刪除的消息");
        }
        
        // 更新消息內容
        int result = chatMessageRepository.updateMessageContent(messageId, content);
        if (result > 0) {
            // 重新獲取更新後的消息
            Optional<ChatMessage> updatedMessageOpt = chatMessageRepository.findById(messageId);
            if (updatedMessageOpt.isPresent()) {
                return convertToDTO(updatedMessageOpt.get(), userId);
            }
        }
        
        throw new RuntimeException("消息更新失敗");
    }
    
    // 刪除消息
    public boolean deleteMessage(Integer messageId, Integer userId) {
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            return false;
        }
        
        ChatMessage message = messageOpt.get();
        
        // 檢查權限（發送者或聊天室管理員可以刪除）
        boolean canDelete = message.getSenderId().equals(userId) || 
                           chatRoomRepository.isUserRoomAdmin(message.getRoomId(), userId);
        
        if (!canDelete) {
            throw new RuntimeException("您沒有權限刪除此消息");
        }
        
        // 軟刪除消息
        int result = chatMessageRepository.softDeleteMessage(messageId);
        return result > 0;
    }
    
    // 標記聊天室消息為已讀
    private void markRoomMessagesAsRead(Integer roomId, Integer userId) {
        try {
            // 更新聊天室成員的最後讀取時間
            chatRoomMemberRepository.updateLastReadTime(roomId, userId, LocalDateTime.now());
        } catch (Exception e) {
            System.err.println("標記消息為已讀時發生錯誤: " + e.getMessage());
        }
    }
    
    // 獲取未讀消息數量
    public long getUnreadMessageCount(Integer userId) {
        return chatMessageRepository.countUnreadMessagesByUserId(userId);
    }
    
    // 搜索消息
    public List<ChatMessageDTO> searchMessages(String keyword, Integer userId) {
        List<ChatMessage> messages = chatMessageRepository.searchMessagesByKeyword(keyword, userId);
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();
        
        for (ChatMessage message : messages) {
            ChatMessageDTO dto = convertToDTO(message, userId);
            messageDTOs.add(dto);
        }
        
        return messageDTOs;
    }
    
    /**
     * 🔥 修復：根據消息ID獲取消息詳情，包含完整的發送者信息
     * @param messageId 消息ID
     * @return ChatMessageDTO 消息DTO，如果不存在則返回null
     */
    public ChatMessageDTO getMessageById(Integer messageId) {
        try {
            // 使用 Repository 的 findById 方法
            Optional<ChatMessage> messageOptional = chatMessageRepository.findById(messageId);
            
            if (messageOptional.isPresent()) {
                ChatMessage message = messageOptional.get();
                
                // 檢查消息是否已被刪除
                if (message.getIsDeleted()) {
                    return null;
                }
                
                // 🔥 關鍵修復：使用 convertToDTO 方法確保獲取完整的發送者信息
                // 使用發送者ID作為currentUserId參數，這樣可以正確設置所有信息
                ChatMessageDTO dto = convertToDTO(message, message.getSenderId());
                
                return dto;
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("獲取消息詳情失敗: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("獲取消息詳情失敗", e);
        }
    }
    
    /**
     * 🔥 增強：簡化版的getMessageById，專門用於WebSocket廣播
     * 確保返回的消息包含正確的發送者名稱
     */
    public ChatMessageDTO getMessageByIdForBroadcast(Integer messageId) {
        try {
            Optional<ChatMessage> messageOptional = chatMessageRepository.findById(messageId);
            
            if (messageOptional.isPresent()) {
                ChatMessage message = messageOptional.get();
                
                if (message.getIsDeleted()) {
                    return null;
                }
                
                // 創建基本DTO
                ChatMessageDTO dto = new ChatMessageDTO();
                dto.setId(message.getId());
                dto.setRoomId(message.getRoomId());
                dto.setSenderId(message.getSenderId());
                dto.setContent(message.getContent());
                dto.setMessageType(message.getMessageType());
                dto.setReplyToId(message.getReplyToId());
                dto.setCreatedAt(message.getCreatedAt());
                dto.setUpdatedAt(message.getUpdatedAt());
                dto.setIsEdited(message.getIsEdited());
                dto.setIsDeleted(message.getIsDeleted());
                
                // 🔥 關鍵：確保設置發送者姓名
                Optional<Member> senderOpt = memberRepository.findById(message.getSenderId());
                if (senderOpt.isPresent()) {
                    Member sender = senderOpt.get();
                    dto.setSenderName(sender.getName()); // 設置真實姓名
                    dto.setSenderRole(sender.getRole());
                    
                    System.out.println("✅ 設置發送者信息 - ID: " + sender.getId() + ", 姓名: " + sender.getName());
                } else {
                    // 如果找不到發送者，設置默認值
                    dto.setSenderName("未知用戶");
                    System.err.println("❌ 找不到發送者信息，用戶ID: " + message.getSenderId());
                }
                
                return dto;
            }
            
            return null;
            
        } catch (Exception e) {
            System.err.println("獲取消息用於廣播失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // 轉換為DTO
    private ChatMessageDTO convertToDTO(ChatMessage message, Integer currentUserId) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setRoomId(message.getRoomId());
        dto.setSenderId(message.getSenderId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setReplyToId(message.getReplyToId());
        dto.setIsEdited(message.getIsEdited());
        dto.setIsDeleted(message.getIsDeleted());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setUpdatedAt(message.getUpdatedAt());
        
        // 🔥 設置發送者信息 - 確保正確獲取發送者姓名
        Optional<Member> senderOpt = memberRepository.findById(message.getSenderId());
        if (senderOpt.isPresent()) {
            Member sender = senderOpt.get();
            dto.setSenderName(sender.getName()); // 真實姓名
            dto.setSenderRole(sender.getRole());
        } else {
            // 找不到發送者時的後備處理
            dto.setSenderName("未知用戶");
            dto.setSenderRole("unknown");
            System.err.println("⚠️ 警告：找不到用戶 " + message.getSenderId() + " 的信息");
        }
        
        // 設置回復消息信息
        if (message.getReplyToId() != null) {
            Optional<ChatMessage> replyToOpt = chatMessageRepository.findById(message.getReplyToId());
            if (replyToOpt.isPresent()) {
                ChatMessage replyTo = replyToOpt.get();
                dto.setReplyToContent(replyTo.getContent());
                
                Optional<Member> replyToSenderOpt = memberRepository.findById(replyTo.getSenderId());
                if (replyToSenderOpt.isPresent()) {
                    dto.setReplyToSenderName(replyToSenderOpt.get().getName());
                }
            }
        }
        
        // 設置已讀狀態
        boolean isRead = chatMessageReadStatusRepository.existsByMessageIdAndReaderId(message.getId(), currentUserId);
        dto.setIsRead(isRead);
        
        // 設置讀取人數
        long readCount = chatMessageReadStatusRepository.countByMessageId(message.getId());
        dto.setReadCount((int) readCount);
        
        return dto;
    }
}