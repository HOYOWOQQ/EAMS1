package com.eams.Entity.chat;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
@Entity
@Table(name = "chat_message_read_status")
public class ChatMessageReadStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "message_id", nullable = false)
    private Integer messageId;
    
    @Column(name = "reader_id", nullable = false)
    private Integer readerId;
    
    @CreationTimestamp
    @Column(name = "read_at", nullable = false, updatable = false)
    private LocalDateTime readAt;
    
    // 關聯映射
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    @JsonIgnore
    private ChatMessage message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", insertable = false, updatable = false)
    @JsonIgnore
    private Member reader;
    
    // 建構子
    public ChatMessageReadStatus() {}
    
    public ChatMessageReadStatus(Integer messageId, Integer readerId) {
        this.messageId = messageId;
        this.readerId = readerId;
    }
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }
    
    public Integer getReaderId() { return readerId; }
    public void setReaderId(Integer readerId) { this.readerId = readerId; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public ChatMessage getMessage() { return message; }
    public void setMessage(ChatMessage message) { this.message = message; }
    
    public Member getReader() { return reader; }
    public void setReader(Member reader) { this.reader = reader; }
}
