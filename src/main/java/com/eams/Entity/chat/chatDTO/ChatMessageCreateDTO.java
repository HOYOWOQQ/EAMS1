package com.eams.Entity.chat.chatDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class ChatMessageCreateDTO {
    
    @NotNull(message = "聊天室ID不能為空")
    private Integer roomId;
    
    @NotBlank(message = "消息內容不能為空")
    @Size(max = 2000, message = "消息內容不能超過2000個字符")
    private String content;
    
    private String messageType = "text"; // text, image, file, system
    
    private Integer replyToId;
    
    // 建構子
    public ChatMessageCreateDTO() {}
    
    // Getter 和 Setter
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public Integer getReplyToId() { return replyToId; }
    public void setReplyToId(Integer replyToId) { this.replyToId = replyToId; }
}
