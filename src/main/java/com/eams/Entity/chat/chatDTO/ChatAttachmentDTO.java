package com.eams.Entity.chat.chatDTO;
import java.time.LocalDateTime;

public class ChatAttachmentDTO {
    private Integer id;
    private Integer messageId;
    private String fileName;
    private String filePath;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private String downloadUrl; // 下載連結
    
    // 建構子
    public ChatAttachmentDTO() {}
    
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
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}

