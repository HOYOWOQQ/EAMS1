package com.eams.Entity.chat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eams.Entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
//聊天消息實體
@Entity
@Table(name = "chat_message")
public class ChatMessage {
 
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer id;
 
 @Column(name = "room_id", nullable = false)
 private Integer roomId;
 
 @Column(name = "sender_id", nullable = false)
 private Integer senderId;
 
 @Column(name = "content", nullable = false, columnDefinition = "NVARCHAR(MAX)")
 private String content;
 
 @Column(name = "message_type", length = 20)
 private String messageType = "text"; // text, image, file, system
 
 @Column(name = "reply_to_id")
 private Integer replyToId;
 
 @Column(name = "is_edited")
 private Boolean isEdited = false;
 
 @Column(name = "is_deleted")
 private Boolean isDeleted = false;
 
 @CreationTimestamp
 @Column(name = "created_at", nullable = false, updatable = false)
 private LocalDateTime createdAt;
 
 @UpdateTimestamp
 @Column(name = "updated_at", nullable = false)
 private LocalDateTime updatedAt;
 
 // 關聯映射
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "room_id", insertable = false, updatable = false)
 @JsonIgnore
 private ChatRoom chatRoom;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "sender_id", insertable = false, updatable = false)
 @JsonIgnore
 private Member sender;
 
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "reply_to_id", insertable = false, updatable = false)
 @JsonIgnore
 private ChatMessage replyToMessage;
 
 @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 @JsonIgnore
 private List<ChatAttachment> attachments = new ArrayList<>();
 
 @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 @JsonIgnore
 private List<ChatMessageReadStatus> readStatuses = new ArrayList<>();
 
 // 建構子
 public ChatMessage() {}
 
 public ChatMessage(Integer roomId, Integer senderId, String content) {
     this.roomId = roomId;
     this.senderId = senderId;
     this.content = content;
 }
 
 // Getter 和 Setter
 public Integer getId() { return id; }
 public void setId(Integer id) { this.id = id; }
 
 public Integer getRoomId() { return roomId; }
 public void setRoomId(Integer roomId) { this.roomId = roomId; }
 
 public Integer getSenderId() { return senderId; }
 public void setSenderId(Integer senderId) { this.senderId = senderId; }
 
 public String getContent() { return content; }
 public void setContent(String content) { this.content = content; }
 
 public String getMessageType() { return messageType; }
 public void setMessageType(String messageType) { this.messageType = messageType; }
 
 public Integer getReplyToId() { return replyToId; }
 public void setReplyToId(Integer replyToId) { this.replyToId = replyToId; }
 
 public Boolean getIsEdited() { return isEdited; }
 public void setIsEdited(Boolean isEdited) { this.isEdited = isEdited; }
 
 public Boolean getIsDeleted() { return isDeleted; }
 public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
 
 public LocalDateTime getCreatedAt() { return createdAt; }
 public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
 
 public LocalDateTime getUpdatedAt() { return updatedAt; }
 public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
 
 public ChatRoom getChatRoom() { return chatRoom; }
 public void setChatRoom(ChatRoom chatRoom) { this.chatRoom = chatRoom; }
 
 public Member getSender() { return sender; }
 public void setSender(Member sender) { this.sender = sender; }
 
 public ChatMessage getReplyToMessage() { return replyToMessage; }
 public void setReplyToMessage(ChatMessage replyToMessage) { this.replyToMessage = replyToMessage; }
 
 public List<ChatAttachment> getAttachments() { return attachments; }
 public void setAttachments(List<ChatAttachment> attachments) { this.attachments = attachments; }
 
 public List<ChatMessageReadStatus> getReadStatuses() { return readStatuses; }
 public void setReadStatuses(List<ChatMessageReadStatus> readStatuses) { this.readStatuses = readStatuses; }
}

