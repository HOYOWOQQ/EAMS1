package com.eams.Entity.notice.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    private Integer commentId;
    private Integer noticeId;
    private Integer userId;
    private String content;
    private Boolean isActive;
    private Integer parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName;
    private String userRole;
    private List<CommentDTO> replies;
    
    // 建構子
    public CommentDTO() {}
    
    // Getter 和 Setter
    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }
    
    public Integer getNoticeId() { return noticeId; }
    public void setNoticeId(Integer noticeId) { this.noticeId = noticeId; }
    
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }
}
