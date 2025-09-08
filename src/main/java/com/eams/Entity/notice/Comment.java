package com.eams.Entity.notice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;
    
    @Column(name = "notice_id", nullable = false)
    private Integer noticeId;
    
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    
    @Column(name = "content", nullable = false, columnDefinition = "NTEXT")
    private String content;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "parent_comment_id")
    private Integer parentCommentId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 關聯映射
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", insertable = false, updatable = false)
    @JsonIgnore
    private Notice notice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private Member user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", insertable = false, updatable = false)
    @JsonIgnore
    private Comment parentComment;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    @JsonIgnore
    private List<Comment> replies = new ArrayList<>();
    
    // 建構子
    public Comment() {}
    
    public Comment(Integer noticeId, Integer userId, String content, Integer parentCommentId) {
        this.noticeId = noticeId;
        this.userId = userId;
        this.content = content;
        this.parentCommentId = parentCommentId;
        this.isActive = true;
    }
    
    // Getter 和 Setter 方法
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
    
    public Notice getNotice() { return notice; }
    public void setNotice(Notice notice) { this.notice = notice; }
    
    public Member getUser() { return user; }
    public void setUser(Member user) { this.user = user; }
    
    public Comment getParentComment() { return parentComment; }
    public void setParentComment(Comment parentComment) { this.parentComment = parentComment; }
    
    public List<Comment> getReplies() { return replies; }
    public void setReplies(List<Comment> replies) { this.replies = replies; }
}