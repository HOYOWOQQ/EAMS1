package com.eams.common.Support.DTO;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String title;
    private String content;
    private String notificationType;
    private String category;
    private String priority;
    private String senderName;
    private String actionUrl;
    private Boolean actionRequired;
    private LocalDateTime createdAt;
    private Boolean isRead;
    private LocalDateTime readAt;
    private Boolean isArchived;
    private Long imageFileId;
}