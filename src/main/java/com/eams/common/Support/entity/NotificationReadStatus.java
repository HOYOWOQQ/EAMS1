package com.eams.common.Support.entity;

import lombok.*;

import java.time.LocalDateTime;

import jakarta.persistence.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notification_read_status")
public class NotificationReadStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // 閱讀狀態
    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "read_method", length = 20)
    private String readMethod;

    // 用戶操作
    @Column(name = "action_taken")
    private Boolean actionTaken;

    @Column(name = "action_result", length = 50)
    private String actionResult;

    @Column(name = "action_at")
    private LocalDateTime actionAt;

    @Column(name = "action_notes", columnDefinition = "NVARCHAR(MAX)")
    private String actionNotes;

    // 歸檔狀態
    @Column(name = "is_archived")
    private Boolean isArchived;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    // 設備資訊
    @Column(name = "device_info", length = 200)
    private String deviceInfo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "NVARCHAR(MAX)")
    private String userAgent;

    // 時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================================
    // JPA 關聯屬性
    // ================================

    // 關聯 Notification 表
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", insertable = false, updatable = false)
    private Notification notification;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (isRead == null) isRead = false;
        if (actionTaken == null) actionTaken = false;
        if (isArchived == null) isArchived = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}