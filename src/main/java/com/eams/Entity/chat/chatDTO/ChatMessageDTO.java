package com.eams.Entity.chat.chatDTO;
import java.time.LocalDateTime;
import java.util.List;

public class ChatMessageDTO {
    private Integer id;
    private Integer roomId;
    private Integer senderId;
    private String senderName;
    private String senderRole;
    private String content;
    private String messageType;
    private Integer replyToId;
    private String replyToContent;
    private String replyToSenderName;
    private Boolean isEdited;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChatAttachmentDTO> attachments;
    private Integer readCount;
    private Boolean isRead;
    
    // 建構子
    public ChatMessageDTO() {}
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    
    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public Integer getReplyToId() { return replyToId; }
    public void setReplyToId(Integer replyToId) { this.replyToId = replyToId; }
    
    public String getReplyToContent() { return replyToContent; }
    public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }
    
    public String getReplyToSenderName() { return replyToSenderName; }
    public void setReplyToSenderName(String replyToSenderName) { this.replyToSenderName = replyToSenderName; }
    
    public Boolean getIsEdited() { return isEdited; }
    public void setIsEdited(Boolean isEdited) { this.isEdited = isEdited; }
    
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<ChatAttachmentDTO> getAttachments() { return attachments; }
    public void setAttachments(List<ChatAttachmentDTO> attachments) { this.attachments = attachments; }
    
    public Integer getReadCount() { return readCount; }
    public void setReadCount(Integer readCount) { this.readCount = readCount; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}

