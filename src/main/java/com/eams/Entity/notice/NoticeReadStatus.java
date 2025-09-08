package com.eams.Entity.notice;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "notice_read_status")
public class NoticeReadStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "notice_id", nullable = false)
    private int noticeId;
    
    @Column(name = "user_id", nullable = false)
    private int userId;
    
    @CreationTimestamp
    @Column(name = "read_time", nullable = false)
    private LocalDateTime readTime;
    
    // 關聯映射
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notice_id", insertable = false, updatable = false)
    private Notice notice;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private Member user;
    
    // 建構子
    public NoticeReadStatus() {}
    
    public NoticeReadStatus(int noticeId, int userId) {
        this.noticeId = noticeId;
        this.userId = userId;
    }
    
    // Getter 和 Setter 方法
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getNoticeId() { return noticeId; }
    public void setNoticeId(int noticeId) { this.noticeId = noticeId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }
    
    public Notice getNotice() { return notice; }
    public void setNotice(Notice notice) { this.notice = notice; }
    
    public Member getUser() { return user; }
    public void setUser(Member user) { this.user = user; }
    
    @Override
    public String toString() {
        return "NoticeReadStatus{" +
                "id=" + id +
                ", noticeId=" + noticeId +
                ", userId=" + userId +
                ", readTime=" + readTime +
                '}';
    }
}