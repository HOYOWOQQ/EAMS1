package com.eams.common.Support.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.eams.common.Security.entity.Role;
import com.eams.common.Configuration.entity.ContentTemplate;

// 1. Notification Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Column(name = "notification_type", length = 50)
    private String notificationType;

    @Column(length = 50)
    private String category;

    @Column(length = 20)
    private String priority;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "sender_name", length = 100)
    private String senderName;

    @Column(name = "target_type", nullable = false, length = 20)
    private String targetType;

    @Column(name = "target_users", columnDefinition = "NVARCHAR(MAX)")
    private String targetUsers;

    @Column(name = "target_roles", length = 500)
    private String targetRoles;

    // 推送方式設定
    @Column(name = "push_websocket")
    private Boolean pushWebsocket;

    @Column(name = "push_email")
    private Boolean pushEmail;

    @Column(name = "push_sms")
    private Boolean pushSms;

    @Column(name = "push_browser")
    private Boolean pushBrowser;

    @Column(name = "push_mobile")
    private Boolean pushMobile;

    // 推送條件
    @Column(name = "send_if_online")
    private Boolean sendIfOnline;

    @Column(name = "send_if_offline")
    private Boolean sendIfOffline;

    @Column(name = "offline_delay_minutes")
    private Integer offlineDelayMinutes;

    @Column(name = "work_hours_only")
    private Boolean workHoursOnly;

    // 各渠道推送狀態
    @Column(name = "websocket_status", length = 20)
    private String websocketStatus;

    @Column(name = "websocket_sent_at")
    private LocalDateTime websocketSentAt;

    @Column(name = "websocket_error", columnDefinition = "NVARCHAR(MAX)")
    private String websocketError;

    @Column(name = "email_status", length = 20)
    private String emailStatus;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @Column(name = "email_error", columnDefinition = "NVARCHAR(MAX)")
    private String emailError;

    @Column(name = "sms_status", length = 20)
    private String smsStatus;

    @Column(name = "sms_sent_at")
    private LocalDateTime smsSentAt;

    @Column(name = "sms_error", columnDefinition = "NVARCHAR(MAX)")
    private String smsError;

    @Column(name = "browser_push_status", length = 20)
    private String browserPushStatus;

    @Column(name = "browser_push_sent_at")
    private LocalDateTime browserPushSentAt;

    @Column(name = "browser_push_error", columnDefinition = "NVARCHAR(MAX)")
    private String browserPushError;

    @Column(name = "mobile_push_status", length = 20)
    private String mobilePushStatus;

    @Column(name = "mobile_push_sent_at")
    private LocalDateTime mobilePushSentAt;

    @Column(name = "mobile_push_error", columnDefinition = "NVARCHAR(MAX)")
    private String mobilePushError;

    // 關聯資訊
    @Column(name = "related_table", length = 100)
    private String relatedTable;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "action_required")
    private Boolean actionRequired;

    // 重試機制
    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "max_retry")
    private Integer maxRetry;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    // 狀態控制
    @Column(length = 20)
    private String status;

    // 時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // 建立者
    @Column(name = "created_by")
    private Integer createdBy;

    // 模板關聯
    @Column(name = "template_id")
    private Integer templateId;

    // 統計資訊
    @Column(name = "total_recipients")
    private Integer totalRecipients;

    @Column(name = "read_count")
    private Integer readCount;

    // 額外資料
    @Column(name = "extra_data", columnDefinition = "NVARCHAR(MAX)")
    private String extraData;

    @Column(name = "device_info", length = 200)
    private String deviceInfo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "image_file_id")
    private Long imageFileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_file_id", insertable = false, updatable = false)
    private FileAttachment imageFile;

    // ================================
    // JPA 關聯屬性
    // ================================

    // 關聯 ContentTemplate 表
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", insertable = false, updatable = false)
    private ContentTemplate template;

    // 關聯多個角色 (多對多關係)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "notification_target_roles",
        joinColumns = @JoinColumn(name = "notification_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> targetRoleList;

    // 關聯已讀狀態 (一對多關係)
    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<NotificationReadStatus> readStatusList;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        // 設定預設值
        if (notificationType == null) notificationType = "system";
        if (priority == null) priority = "normal";
        if (status == null) status = "active";
        if (pushWebsocket == null) pushWebsocket = true;
        if (pushEmail == null) pushEmail = false;
        if (pushSms == null) pushSms = false;
        if (pushBrowser == null) pushBrowser = false;
        if (pushMobile == null) pushMobile = false;
        if (sendIfOnline == null) sendIfOnline = true;
        if (sendIfOffline == null) sendIfOffline = true;
        if (offlineDelayMinutes == null) offlineDelayMinutes = 0;
        if (workHoursOnly == null) workHoursOnly = false;
        if (actionRequired == null) actionRequired = false;
        if (retryCount == null) retryCount = 0;
        if (maxRetry == null) maxRetry = 3;
        if (totalRecipients == null) totalRecipients = 0;
        if (readCount == null) readCount = 0;
        
        // 初始化推送狀態
        if (websocketStatus == null) websocketStatus = pushWebsocket ? "pending" : "skipped";
        if (emailStatus == null) emailStatus = pushEmail ? "pending" : "skipped";
        if (smsStatus == null) smsStatus = pushSms ? "pending" : "skipped";
        if (browserPushStatus == null) browserPushStatus = pushBrowser ? "pending" : "skipped";
        if (mobilePushStatus == null) mobilePushStatus = pushMobile ? "pending" : "skipped";
    }
}