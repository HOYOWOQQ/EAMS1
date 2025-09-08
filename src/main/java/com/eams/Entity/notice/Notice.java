package com.eams.Entity.notice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eams.Entity.course.Course;
import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "notice")
public class Notice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Integer noticeId;
    
    @Column(name = "course_id", nullable = false)
    private Integer courseId;
    
    @Column(name = "teacher_id", nullable = false)
    private Integer teacherId;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String content;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 關聯映射
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonIgnore
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    @JsonIgnore
    private Member teacher;
    
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();
    
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<NoticeReadStatus> readStatuses = new ArrayList<>();
    
    // 建構子
    public Notice() {}
    
    public Notice(Integer courseId, Integer teacherId, String title, String content) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.title = title;
        this.content = content;
        this.isActive = true;
    }
    
    // Getter 和 Setter 方法
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
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public Member getTeacher() { return teacher; }
    public void setTeacher(Member teacher) { this.teacher = teacher; }
    
    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
    
    public List<NoticeReadStatus> getReadStatuses() { return readStatuses; }
    public void setReadStatuses(List<NoticeReadStatus> readStatuses) { this.readStatuses = readStatuses; }
}