package com.eams.Entity.notice.DTO;


import java.time.LocalDateTime;
import java.util.List;

public class NoticeDTO {
    private Integer noticeId;
    private Integer courseId;
    private Integer teacherId;
    private String title;
    private String content;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String courseName;
    private String teacherName;
    private Boolean isRead;
    private Integer commentCount;
    private List<CommentDTO> comments;
    private Long readCount;
    private Long unreadCount;      // 新增：未讀人數
    private Long totalStudents;    // 新增：總學生數
    private Double readRate;       // 新增：已讀率
    
    // 建構子
    public NoticeDTO() {}
    
    // Getter 和 Setter
    public Integer getNoticeId() { return noticeId; }
    public void setNoticeId(Integer noticeId) { this.noticeId = noticeId; }
    
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    
    public Integer getTeacherId() { return teacherId; }
    public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    
    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }
    
    public Long getReadCount() { 
        return readCount; 
    }

    public void setReadCount(Long readCount) { 
        this.readCount = readCount; 
    }
 // 新增：未讀人數的 getter 和 setter
    public Long getUnreadCount() { 
        return unreadCount; 
    }
    
    public void setUnreadCount(Long unreadCount) { 
        this.unreadCount = unreadCount; 
    }
    
    // 新增：總學生數的 getter 和 setter
    public Long getTotalStudents() { 
        return totalStudents; 
    }
    
    public void setTotalStudents(Long totalStudents) { 
        this.totalStudents = totalStudents; 
    }
    
    // 新增：已讀率的 getter 和 setter
    public Double getReadRate() { 
        return readRate; 
    }
    
    public void setReadRate(Double readRate) { 
        this.readRate = readRate; 
    }
}
