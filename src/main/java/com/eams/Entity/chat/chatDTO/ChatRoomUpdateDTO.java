package com.eams.Entity.chat.chatDTO;

import jakarta.validation.constraints.Size;
public class ChatRoomUpdateDTO {
    
    @Size(max = 100, message = "聊天室名稱不能超過100個字符")
    private String name;
    
    @Size(max = 255, message = "描述不能超過255個字符")
    private String description;
    
    private Integer maxMembers;
    
    // 建構子
    public ChatRoomUpdateDTO() {}
    
    // Getter 和 Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
}
