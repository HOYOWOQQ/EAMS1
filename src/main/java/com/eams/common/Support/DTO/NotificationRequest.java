package com.eams.common.Support.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String title;
    private String content;
    private String notificationType;
    private String category;
    private String priority;
    private Integer senderId;
    private String senderName;
    private String targetType; // "user", "role", "all"
    private List<Integer> targetUserIds;
    private List<String> targetRoles;
    
    // 推送方式設定
    private Boolean pushWebsocket;
    private Boolean pushEmail;
    private Boolean pushSms;
    private Boolean pushBrowser;
    private Boolean pushMobile;
    
    // 關聯資訊
    private String relatedTable;
    private Long relatedId;
    private String actionUrl;
    private Boolean actionRequired;
    
    // 時間設定
    private LocalDateTime scheduledAt;
    private LocalDateTime expiresAt;
    
    // 其他
    private Integer createdBy;
    private Integer templateId;
    private String extraData;
    private Long imageFileId;
}