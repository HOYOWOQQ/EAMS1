package com.eams.Entity.chat.chatDTO;

import java.time.LocalDateTime;

public class InvitableMemberDTO {
    
    private Integer id;
    private String name;
    private String role; // 系統角色：teacher, admin, student 等
    private String email;
    private String avatar; // 頭像URL
    
    // 在線狀態
    private String onlineStatus; // online, away, busy, offline
    private LocalDateTime lastSeen;
    private String deviceInfo;
    
    // 額外信息
    private String department; // 系所
    private String position; // 職位
    private Boolean isRecommended = false; // 是否為推薦邀請（如同課程學生）
    
    // 建構子
    public InvitableMemberDTO() {}
    
    public InvitableMemberDTO(Integer id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public String getOnlineStatus() { return onlineStatus; }
    public void setOnlineStatus(String onlineStatus) { this.onlineStatus = onlineStatus; }
    
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public Boolean getIsRecommended() { return isRecommended; }
    public void setIsRecommended(Boolean isRecommended) { this.isRecommended = isRecommended; }
    
    // 便利方法
    public boolean isOnline() {
        return "online".equals(this.onlineStatus);
    }
    
    public boolean isTeacher() {
        return "teacher".equalsIgnoreCase(this.role);
    }
    
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(this.role);
    }
    
    public boolean isStudent() {
        return "student".equalsIgnoreCase(this.role);
    }
    
    public String getDisplayName() {
        if (this.position != null && !this.position.trim().isEmpty()) {
            return this.name + " (" + this.position + ")";
        }
        return this.name;
    }
    
    public String getRoleDisplayName() {
        switch (this.role.toLowerCase()) {
            case "teacher": return "教師";
            case "admin": return "管理員";
            case "student": return "學生";
            case "assistant": return "助教";
            default: return this.role;
        }
    }
    
    public String getOnlineStatusDisplayName() {
        switch (this.onlineStatus) {
            case "online": return "在線";
            case "away": return "離開";
            case "busy": return "忙碌";
            case "offline": return "離線";
            default: return "未知";
        }
    }
    
    @Override
    public String toString() {
        return "InvitableMemberDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", onlineStatus='" + onlineStatus + '\'' +
                ", isRecommended=" + isRecommended +
                '}';
    }
}
