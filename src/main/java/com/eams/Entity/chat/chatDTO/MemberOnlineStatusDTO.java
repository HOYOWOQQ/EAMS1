package com.eams.Entity.chat.chatDTO;
import java.time.LocalDateTime;

public class MemberOnlineStatusDTO {
    private Integer memberId;
    private String memberName;
    private String status;
    private LocalDateTime lastSeen;
    private String deviceInfo;
    private LocalDateTime updatedAt;
    
    // 建構子
    public MemberOnlineStatusDTO() {}
    
    // Getter 和 Setter
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}