package com.eams.Entity.chat.chatDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class ChatMessageUpdateDTO {
    
    @NotBlank(message = "消息內容不能為空")
    @Size(max = 2000, message = "消息內容不能超過2000個字符")
    private String content;
    
    // 建構子
    public ChatMessageUpdateDTO() {}
    
    // Getter 和 Setter
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
