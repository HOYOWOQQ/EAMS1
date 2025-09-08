package com.eams.Entity.chat.chatDTO;

import java.time.LocalDateTime;

// 聊天室DTO
public class ChatRoomDTO {
    private Integer id;
    private String name;
    private String type;
    private Integer courseId;
    private String courseName;
    private String description;
    private Integer createdBy;
    private String creatorName;
    private Integer maxMembers;
    private Integer currentMembers;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastMessageAt;
    private String lastMessageContent;
    private String lastMessageSenderName;
    private Integer unreadCount;
    private Boolean isMember;
    private String memberRole;
    private Boolean isMuted;
    
    // 新增字段
    private Boolean canManage = false; // 是否可以管理聊天室（邀請其他人等）
    private Integer onlineMembers = 0; // 在線成員數量
    
    // 建構子
    public ChatRoomDTO() {}
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    
    public Integer getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(Integer currentMembers) { this.currentMembers = currentMembers; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public String getLastMessageContent() { return lastMessageContent; }
    public void setLastMessageContent(String lastMessageContent) { this.lastMessageContent = lastMessageContent; }
    
    public String getLastMessageSenderName() { return lastMessageSenderName; }
    public void setLastMessageSenderName(String lastMessageSenderName) { this.lastMessageSenderName = lastMessageSenderName; }
    
    public Integer getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; }
    
    public Boolean getIsMember() { return isMember; }
    public void setIsMember(Boolean isMember) { this.isMember = isMember; }
    
    public String getMemberRole() { return memberRole; }
    public void setMemberRole(String memberRole) { this.memberRole = memberRole; }
    
    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }
    
    // 新增的 Getter 和 Setter
    public Boolean getCanManage() { return canManage; }
    public void setCanManage(Boolean canManage) { this.canManage = canManage; }
    
    public Integer getOnlineMembers() { return onlineMembers; }
    public void setOnlineMembers(Integer onlineMembers) { this.onlineMembers = onlineMembers; }
    
    // 便利方法
    public boolean isPrivateRoom() {
        return "private".equals(this.type);
    }
    
    public boolean isGroupRoom() {
        return "group".equals(this.type);
    }
    
    public boolean isCourseRoom() {
        return "course".equals(this.type);
    }
    
    public boolean isCreator(Integer userId) {
        return this.createdBy != null && this.createdBy.equals(userId);
    }
    
    public boolean isAdmin() {
        return "admin".equals(this.memberRole);
    }
    
    public boolean hasUnreadMessages() {
        return this.unreadCount != null && this.unreadCount > 0;
    }
    
    public boolean isFull() {
        return this.currentMembers != null && this.maxMembers != null && 
               this.currentMembers >= this.maxMembers;
    }
    
    @Override
    public String toString() {
        return "ChatRoomDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", currentMembers=" + currentMembers +
                ", maxMembers=" + maxMembers +
                ", unreadCount=" + unreadCount +
                ", canManage=" + canManage +
                ", onlineMembers=" + onlineMembers +
                '}';
    }
}