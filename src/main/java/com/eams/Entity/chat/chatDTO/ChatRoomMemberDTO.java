package com.eams.Entity.chat.chatDTO;
import java.time.LocalDateTime;

public class ChatRoomMemberDTO {
    private Integer id;
    private Integer roomId;
    private Integer memberId;
    private String memberName;
    private String memberRole; // 系統角色：student, teacher
    private String roomRole;   // 聊天室角色：admin, member
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;
    private Boolean isMuted;
    private Boolean isActive;
    private String onlineStatus;
    private LocalDateTime lastSeen;
    
    // 建構子
    public ChatRoomMemberDTO() {}
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    
    public String getMemberRole() { return memberRole; }
    public void setMemberRole(String memberRole) { this.memberRole = memberRole; }
    
    public String getRoomRole() { return roomRole; }
    public void setRoomRole(String roomRole) { this.roomRole = roomRole; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    
    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }
    
    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public String getOnlineStatus() { return onlineStatus; }
    public void setOnlineStatus(String onlineStatus) { this.onlineStatus = onlineStatus; }
    
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
}