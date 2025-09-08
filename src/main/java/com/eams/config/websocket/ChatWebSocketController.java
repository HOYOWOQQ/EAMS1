package com.eams.config.websocket;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.eams.Entity.chat.chatDTO.ChatAttachmentDTO;
import com.eams.Entity.chat.chatDTO.ChatMessageCreateDTO;
import com.eams.Entity.chat.chatDTO.ChatMessageDTO;
import com.eams.Service.chat.ChatMessageService;
import com.eams.Service.chat.ChatFileUploadService;
import com.eams.Service.chat.OnlineStatusService;

@Controller
public class ChatWebSocketController {
    
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private OnlineStatusService onlineStatusService;
    
    // 🔥 新增：注入文件服務
    @Autowired
    private ChatFileUploadService chatFileUploadService;
    
    // 用戶加入聊天室
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                              SimpMessageHeaderAccessor headerAccessor) {
        // 在WebSocket會話中添加用戶名
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("userId", chatMessage.getSenderId());
        
        // 更新用戶在線狀態
        if (chatMessage.getSenderId() != null) {
            onlineStatusService.updateOnlineStatus(chatMessage.getSenderId(), "online");
        }
        
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessage;
    }
    
    // 🔥 更新：支持文件消息的發送方法
    @MessageMapping("/chat.sendMessage/{roomId}")
    public void sendMessage(@DestinationVariable String roomId,
                           @Payload ChatMessageWebSocketDTO messageDTO,
                           SimpMessageHeaderAccessor headerAccessor,
                           Principal principal) {
        try {
            // 從會話中獲取用戶ID
            Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
            if (userId == null) {
                System.err.println("用戶未登入，無法發送消息");
                return;
            }
            
            // 創建消息DTO
            ChatMessageCreateDTO createDTO = new ChatMessageCreateDTO();
            createDTO.setRoomId(Integer.parseInt(roomId));
            createDTO.setContent(messageDTO.getContent());
            createDTO.setMessageType(messageDTO.getMessageType());
            createDTO.setReplyToId(messageDTO.getReplyToId());
            
            // 保存消息到數據庫
            ChatMessageDTO savedMessage = chatMessageService.sendMessage(createDTO, userId);
            
            // 創建WebSocket消息
            ChatMessage wsMessage = new ChatMessage();
            wsMessage.setId(savedMessage.getId());
            wsMessage.setRoomId(savedMessage.getRoomId());
            wsMessage.setSenderId(savedMessage.getSenderId());
            wsMessage.setSender(savedMessage.getSenderName());
            wsMessage.setContent(savedMessage.getContent());
            wsMessage.setMessageType(savedMessage.getMessageType());
            wsMessage.setReplyToId(savedMessage.getReplyToId());
            wsMessage.setTimestamp(savedMessage.getCreatedAt());
            wsMessage.setType(ChatMessage.MessageType.CHAT);
            
            // 🔥 新增：如果是文件消息，獲取並設置附件信息
            if ("file".equals(savedMessage.getMessageType()) && savedMessage.getId() != null) {
                try {
                    List<ChatAttachmentDTO> attachments = chatFileUploadService.getMessageAttachments(
                        savedMessage.getId(), userId);
                    if (!attachments.isEmpty()) {
                        wsMessage.setAttachment(attachments.get(0)); // 設置第一個附件
                    }
                } catch (Exception e) {
                    System.err.println("獲取文件附件失敗: " + e.getMessage());
                    // 即使附件獲取失敗，也要發送基本消息
                }
            }
            
            // 向聊天室廣播消息
            messagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);
            
        } catch (Exception e) {
            System.err.println("發送消息時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 🔥 新增：專門處理文件消息廣播的方法
    @MessageMapping("/chat.broadcastFileMessage/{roomId}")
    public void broadcastFileMessage(@DestinationVariable String roomId,
                                   @Payload FileMessageBroadcastDTO fileMessageDTO,
                                   SimpMessageHeaderAccessor headerAccessor) {
        try {
            Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
            if (userId == null) {
                System.err.println("用戶未登入，無法廣播文件消息");
                return;
            }
            
            // 獲取完整的消息信息
            ChatMessageDTO message = chatMessageService.getMessageById(fileMessageDTO.getMessageId());
            if (message == null) {
                System.err.println("消息不存在: " + fileMessageDTO.getMessageId());
                return;
            }
            
            // 獲取附件信息
            List<ChatAttachmentDTO> attachments = chatFileUploadService.getMessageAttachments(
                fileMessageDTO.getMessageId(), userId);
            
            // 創建完整的WebSocket文件消息
            ChatMessage wsMessage = new ChatMessage();
            wsMessage.setId(message.getId());
            wsMessage.setRoomId(message.getRoomId());
            wsMessage.setSenderId(message.getSenderId());
            wsMessage.setSender(message.getSenderName());
            wsMessage.setContent(message.getContent());
            wsMessage.setMessageType("file");
            wsMessage.setTimestamp(message.getCreatedAt());
            wsMessage.setType(ChatMessage.MessageType.CHAT);
            
            if (!attachments.isEmpty()) {
                wsMessage.setAttachment(attachments.get(0));
            }
            
            // 廣播完整的文件消息
            messagingTemplate.convertAndSend("/topic/room/" + roomId, wsMessage);
            
        } catch (Exception e) {
            System.err.println("廣播文件消息時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    
    // 用戶開始打字
    @MessageMapping("/chat.typing/{roomId}")
    public void handleTyping(@DestinationVariable String roomId,
                            @Payload TypingMessage typingMessage,
                            SimpMessageHeaderAccessor headerAccessor) {
        Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        
        if (userId != null && username != null) {
            typingMessage.setSenderId(userId);
            typingMessage.setSender(username);
            typingMessage.setTimestamp(LocalDateTime.now());
            
            // 向聊天室其他成員廣播打字狀態
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/typing", typingMessage);
        }
    }
    
    // 更新用戶在線狀態
    @MessageMapping("/user.updateStatus")
    public void updateUserStatus(@Payload UserStatusUpdateMessage statusMessage,
                                SimpMessageHeaderAccessor headerAccessor) {
        Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        
        if (userId != null) {
            // 更新數據庫中的在線狀態
            onlineStatusService.updateOnlineStatus(userId, statusMessage.getStatus());
            
            // 廣播狀態更新
            UserStatusMessage statusUpdate = new UserStatusMessage();
            statusUpdate.setUserId(userId);
            statusUpdate.setUsername(username);
            statusUpdate.setStatus(statusMessage.getStatus());
            statusUpdate.setTimestamp(LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/user.statusUpdate", statusUpdate);
        }
    }
    
    // 🔥 更新：WebSocket消息類，添加附件支持
    public static class ChatMessage {
        private Integer id;
        private Integer roomId;
        private Integer senderId;
        private String sender;
        private String content;
        private String messageType = "text";
        private Integer replyToId;
        private LocalDateTime timestamp;
        private MessageType type;
        private ChatAttachmentDTO attachment; // 🔥 新增：附件信息
        
        public enum MessageType {
            CHAT, JOIN, LEAVE
        }
        
        // 建構子
        public ChatMessage() {}
        
        // Getter 和 Setter
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public Integer getRoomId() { return roomId; }
        public void setRoomId(Integer roomId) { this.roomId = roomId; }
        
        public Integer getSenderId() { return senderId; }
        public void setSenderId(Integer senderId) { this.senderId = senderId; }
        
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public Integer getReplyToId() { return replyToId; }
        public void setReplyToId(Integer replyToId) { this.replyToId = replyToId; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public MessageType getType() { return type; }
        public void setType(MessageType type) { this.type = type; }
        
        // 🔥 新增：附件的 getter 和 setter
        public ChatAttachmentDTO getAttachment() { return attachment; }
        public void setAttachment(ChatAttachmentDTO attachment) { this.attachment = attachment; }
    }
    
    // WebSocket消息DTO
    public static class ChatMessageWebSocketDTO {
        private String content;
        private String messageType = "text";
        private Integer replyToId;
        
        // Getter 和 Setter
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public Integer getReplyToId() { return replyToId; }
        public void setReplyToId(Integer replyToId) { this.replyToId = replyToId; }
    }
    
    // 🔥 新增：文件消息廣播DTO
    public static class FileMessageBroadcastDTO {
        private Integer messageId;
        private Integer roomId;
        
        public Integer getMessageId() { return messageId; }
        public void setMessageId(Integer messageId) { this.messageId = messageId; }
        
        public Integer getRoomId() { return roomId; }
        public void setRoomId(Integer roomId) { this.roomId = roomId; }
    }
    
    // 打字消息類
    public static class TypingMessage {
        private Integer senderId;
        private String sender;
        private boolean isTyping;
        private LocalDateTime timestamp;
        
        // Getter 和 Setter
        public Integer getSenderId() { return senderId; }
        public void setSenderId(Integer senderId) { this.senderId = senderId; }
        
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        
        public boolean isTyping() { return isTyping; }
        public void setTyping(boolean typing) { isTyping = typing; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    // 用戶狀態更新消息
    public static class UserStatusUpdateMessage {
        private String status;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    // 用戶狀態消息
    public static class UserStatusMessage {
        private Integer userId;
        private String username;
        private String status;
        private LocalDateTime timestamp;
        
        // Getter 和 Setter
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}