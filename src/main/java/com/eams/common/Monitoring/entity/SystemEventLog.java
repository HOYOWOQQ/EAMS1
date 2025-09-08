package com.eams.common.Monitoring.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.eams.common.Monitoring.enums.SeverityLevel;

@Entity
@Table(name = "system_event_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 事件分類與類型
    @Column(name = "event_category", length = 30, nullable = false)
    private String eventCategory;

    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 10, nullable = false)
    private SeverityLevel  severity; 

    // 基本資訊
    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "message", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String message;

    @Column(name = "details", columnDefinition = "NVARCHAR(MAX)")
    private String details;

    // 來源資訊
    @Column(name = "source_ip", length = 45)
    private String sourceIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_method", length = 10)
    private String requestMethod;

    // 目標資訊
    @Column(name = "target_type", length = 50)
    private String targetType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "target_name", length = 200)
    private String targetName;

    // 狀態處理
    @Column(name = "status", length = 20)
    private String status = "NEW"; // 可搭配 Enum

    @Column(name = "assigned_to")
    private Integer assignedTo;

    @Column(name = "resolved_by")
    private Integer resolvedBy;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolution_note", columnDefinition = "NVARCHAR(MAX)")
    private String resolutionNote;

    // 統計
    @Column(name = "occurrence_count")
    private Integer occurrenceCount = 1;

    @Column(name = "first_occurred_at")
    private LocalDateTime firstOccurredAt;

    @Column(name = "last_occurred_at")
    private LocalDateTime lastOccurredAt;

    // 通知相關
    @Column(name = "notification_priority", length = 10)
    private String notificationPriority = "NORMAL"; // Enum

    @Column(name = "notification_target_type", length = 20)
    private String notificationTargetType; // Enum

    @Column(name = "notification_target_roles", length = 200)
    private String notificationTargetRoles;

    @Column(name = "notification_target_users", columnDefinition = "NVARCHAR(MAX)")
    private String notificationTargetUsers;

    @Column(name = "notification_delivery_methods", length = 100)
    private String notificationDeliveryMethods;

    @Column(name = "notification_sent")
    private Boolean notificationSent = false;

    @Column(name = "notification_sent_at")
    private LocalDateTime notificationSentAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // 外鍵關聯（如要用 @ManyToOne 自行補 Member entity）
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "assigned_to", insertable = false, updatable = false)
    // private Member assignedToMember;
    //
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "resolved_by", insertable = false, updatable = false)
    // private Member resolvedByMember;
}
