package com.eams.Entity.chat;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
@Entity
@Table(name = "chat_room_member")
public class ChatRoomMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "room_id", nullable = false)
    private Integer roomId;
    
    @Column(name = "member_id", nullable = false)
    private Integer memberId;
    
    @Column(name = "role", length = 20)
    private String role = "member"; // admin, member
    
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;
    
    @Column(name = "is_muted")
    private Boolean isMuted = false;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // 關聯映射
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @JsonIgnore
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    @JsonIgnore
    private Member member;
    
    // 建構子
    public ChatRoomMember() {}
    
    public ChatRoomMember(Integer roomId, Integer memberId) {
        this.roomId = roomId;
        this.memberId = memberId;
    }
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }
    
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    
    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }
    
    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public ChatRoom getChatRoom() { return chatRoom; }
    public void setChatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
}
