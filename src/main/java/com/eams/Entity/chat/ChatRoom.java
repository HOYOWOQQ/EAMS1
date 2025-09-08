package com.eams.Entity.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eams.Entity.course.Course;
import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

// 聊天室實體
@Entity
@Table(name = "chat_room")
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "type", nullable = false, length = 20)
    private String type; // private, group, course
    
    @Column(name = "course_id")
    private Integer courseId;
    
    @Column(name = "description", length = 255)
    private String description;
    
    @Column(name = "created_by", nullable = false)
    private Integer createdBy;
    
    @Column(name = "max_members")
    private Integer maxMembers = 50;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;
    
    // 關聯映射
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonIgnore
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    @JsonIgnore
    private Member creator;
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChatRoomMember> roomMembers = new ArrayList<>();
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ChatMessage> messages = new ArrayList<>();
    
    // 建構子
    public ChatRoom() {}
    
    public ChatRoom(String name, String type, Integer createdBy) {
        this.name = name;
        this.type = type;
        this.createdBy = createdBy;
    }
    
    // Getter 和 Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
    
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    
    public Member getCreator() { return creator; }
    public void setCreator(Member creator) { this.creator = creator; }
    
    public List<ChatRoomMember> getRoomMembers() { return roomMembers; }
    public void setRoomMembers(List<ChatRoomMember> roomMembers) { this.roomMembers = roomMembers; }
    
    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }
}
