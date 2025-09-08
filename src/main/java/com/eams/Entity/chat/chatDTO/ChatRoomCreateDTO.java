package com.eams.Entity.chat.chatDTO;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class ChatRoomCreateDTO {
    
    @NotBlank(message = "聊天室名稱不能為空")
    @Size(max = 100, message = "聊天室名稱不能超過100個字符")
    private String name;
    
    @NotBlank(message = "聊天室類型不能為空")
    private String type; // private, group, course
    
    private Integer courseId;
    
    @Size(max = 255, message = "描述不能超過255個字符")
    private String description;
    
    private Integer maxMembers = 50;
    
    private List<Integer> memberIds; // 初始成員列表
    
    // 建構子
    public ChatRoomCreateDTO() {}
    
    // Getter 和 Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    
    public List<Integer> getMemberIds() { return memberIds; }
    public void setMemberIds(List<Integer> memberIds) { this.memberIds = memberIds; }
}
