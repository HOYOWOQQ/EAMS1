package com.eams.Entity.chat;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
@Entity
@Table(name = "chat_attachment")
public class ChatAttachment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "message_id", nullable = false)
    private Integer messageId;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;
    
    @Column(name = "file_type", length = 100)
    private String fileType;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
    
    // 關聯映射
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", insertable = false, updatable = false)
    @JsonIgnore
    private ChatMessage message;
    
    // 建構子
    public ChatAttachment() {}
    
    public ChatAttachment(Integer messageId, String fileName, String filePath) {
        this.messageId = messageId;
        this.fileName = fileName;
        this.filePath = filePath;
    }
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getMessageId() { return messageId; }
    public void setMessageId(Integer messageId) { this.messageId = messageId; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public ChatMessage getMessage() { return message; }
    public void setMessage(ChatMessage message) { this.message = message; }
}
