package com.eams.Entity.chat;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "member_online_status")
public class MemberOnlineStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "member_id", nullable = false, unique = true)
    private Integer memberId;
    
    @Column(name = "status", length = 20)
    private String status = "offline"; // online, away, busy, offline
    
    @Column(name = "last_seen")
    private LocalDateTime lastSeen;
    
    @Column(name = "device_info", length = 255)
    private String deviceInfo;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 關聯映射
    @OneToOne
    @JoinColumn(name = "id")
    @JsonBackReference
    private Member member;
    
    // 建構子
    public MemberOnlineStatus() {}
    
    public MemberOnlineStatus(Integer memberId, String status) {
        this.memberId = memberId;
        this.status = status;
        this.lastSeen = LocalDateTime.now();
    }
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
}