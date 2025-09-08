package com.eams.common.Support.Services;

import com.eams.common.Support.Repository.NotificationRepository;
import com.eams.common.Support.DTO.NotificationRequest;
import com.eams.common.Support.DTO.NotificationResult;
import com.eams.common.Support.DTO.ValidationResult;
import com.eams.common.Support.DTO.NotificationDTO;
import com.eams.common.Support.Repository.NotificationReadStatusRepository;
import com.eams.common.Support.entity.Notification;
import com.eams.common.Support.entity.NotificationReadStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private NotificationReadStatusRepository readStatusRepository;
    
    @Autowired
    private NotificationDeliveryService deliveryService;

    /**
     * 創建並發送通知
     */
    public CompletableFuture<NotificationResult> createAndSendNotification(NotificationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 驗證請求
                ValidationResult validation = validateNotificationRequest(request);
                if (!validation.isValid()) {
                    return new NotificationResult(false, validation.getErrorMessage());
                }

                // 創建通知
                Notification notification = buildNotification(request);
                notification = notificationRepository.save(notification);
                
                // 創建讀取狀態記錄
                createReadStatusRecords(notification, request.getTargetUserIds());
                
                // 異步發送通知
                if (notification.getScheduledAt() == null || notification.getScheduledAt().isBefore(LocalDateTime.now())) {
                    deliveryService.deliverNotificationAsync(notification);
                }
                
                logger.info("Notification created and queued for delivery: {}", notification.getId());
                return new NotificationResult(true, notification.getId(), "通知創建成功");
                
            } catch (Exception ex) {
                logger.error("Error creating notification", ex);
                return new NotificationResult(false, "創建通知失敗: " + ex.getMessage());
            }
        });
    }

    /**
     * 標記通知為已讀
     */
    public CompletableFuture<Boolean> markAsRead(Long notificationId, Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<NotificationReadStatus> statusOpt = 
                    readStatusRepository.findByNotificationIdAndUserId(notificationId, userId);
                
                if (statusOpt.isPresent()) {
                    NotificationReadStatus status = statusOpt.get();
                    if (!status.getIsRead()) {
                        status.setIsRead(true);
                        status.setReadAt(LocalDateTime.now());
                        status.setReadMethod("manual");
                        readStatusRepository.save(status);
                        
                        // 更新通知的已讀計數
                        updateNotificationReadCount(notificationId);
                        
                        logger.debug("Notification {} marked as read by user {}", notificationId, userId);
                    }
                    return true;
                }
                return false;
                
            } catch (Exception ex) {
                logger.error("Error marking notification as read", ex);
                return false;
            }
        });
    }

    /**
     * 獲取用戶的通知列表
     */
    public Page<NotificationDTO> getUserNotifications(Integer userId, Boolean isRead, Pageable pageable) {
        Page<NotificationReadStatus> readStatuses;
        
        if (isRead != null) {
            readStatuses = readStatusRepository.findByUserIdAndIsReadOrderByCreatedAtDesc(userId, isRead, pageable);
        } else {
            readStatuses = readStatusRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }
        
        return readStatuses.map(this::convertToNotificationDTO);
    }

    /**
     * 獲取用戶未讀通知數量
     */
    public Long getUnreadCount(Integer userId) {
        return readStatusRepository.countUnreadByUserId(userId);
    }

    /**
     * 批量標記為已讀
     */
    @Transactional
    public CompletableFuture<Integer> markMultipleAsRead(List<Long> notificationIds, Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            int count = 0;
            for (Long notificationId : notificationIds) {
                if (markAsRead(notificationId, userId).join()) {
                    count++;
                }
            }
            return count;
        });
    }

    /**
     * 歸檔通知
     */
    public CompletableFuture<Boolean> archiveNotification(Long notificationId, Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<NotificationReadStatus> statusOpt = 
                    readStatusRepository.findByNotificationIdAndUserId(notificationId, userId);
                
                if (statusOpt.isPresent()) {
                    NotificationReadStatus status = statusOpt.get();
                    status.setIsArchived(true);
                    status.setArchivedAt(LocalDateTime.now());
                    readStatusRepository.save(status);
                    return true;
                }
                return false;
                
            } catch (Exception ex) {
                logger.error("Error archiving notification", ex);
                return false;
            }
        });
    }

    /**
     * 重試失敗的通知
     */
    @Transactional
    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findNotificationsForRetry(LocalDateTime.now());
        
        for (Notification notification : failedNotifications) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notification.setNextRetryAt(calculateNextRetryTime(notification.getRetryCount()));
            notificationRepository.save(notification);
            
            deliveryService.deliverNotificationAsync(notification);
            logger.info("Retrying notification delivery: {}", notification.getId());
        }
    }

    /**
     * 處理過期通知
     */
    @Transactional
    public void handleExpiredNotifications() {
        List<Notification> expiredNotifications = notificationRepository.findExpiredNotifications(LocalDateTime.now());
        
        for (Notification notification : expiredNotifications) {
            notification.setStatus("expired");
            notificationRepository.save(notification);
        }
        
        logger.info("Processed {} expired notifications", expiredNotifications.size());
    }

    // 私有輔助方法

    private ValidationResult validateNotificationRequest(NotificationRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            return new ValidationResult(false, "通知標題不能為空");
        }
        
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            return new ValidationResult(false, "通知內容不能為空");
        }
        
        if (request.getTargetType() == null) {
            return new ValidationResult(false, "目標類型不能為空");
        }
        
        if ("user".equals(request.getTargetType()) && 
            (request.getTargetUserIds() == null || request.getTargetUserIds().isEmpty())) {
            return new ValidationResult(false, "用戶目標通知必須指定目標用戶");
        }
        
        return new ValidationResult(true, null);
    }

    private Notification buildNotification(NotificationRequest request) {
        return Notification.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .notificationType(request.getNotificationType() != null ? request.getNotificationType() : "system")
            .category(request.getCategory())
            .priority(request.getPriority() != null ? request.getPriority() : "normal")
            .senderId(request.getSenderId())
            .senderName(request.getSenderName())
            .targetType(request.getTargetType())
            .targetUsers(request.getTargetType().equals("user") ? String.join(",", request.getTargetUserIds().stream().map(String::valueOf).toArray(String[]::new)) : null)
            .targetRoles(request.getTargetType().equals("role") ? String.join(",", request.getTargetRoles()) : null)
            .pushWebsocket(request.getPushWebsocket() != null ? request.getPushWebsocket() : true)
            .pushEmail(request.getPushEmail() != null ? request.getPushEmail() : false)
            .pushSms(request.getPushSms() != null ? request.getPushSms() : false)
            .pushBrowser(request.getPushBrowser() != null ? request.getPushBrowser() : false)
            .pushMobile(request.getPushMobile() != null ? request.getPushMobile() : false)
            .relatedTable(request.getRelatedTable())
            .relatedId(request.getRelatedId())
            .actionUrl(request.getActionUrl())
            .actionRequired(request.getActionRequired() != null ? request.getActionRequired() : false)
            .scheduledAt(request.getScheduledAt())
            .expiresAt(request.getExpiresAt())
            .createdBy(request.getCreatedBy())
            .templateId(request.getTemplateId())
            .extraData(request.getExtraData())
            .imageFileId(request.getImageFileId())
            .build();
    }

    private void createReadStatusRecords(Notification notification, List<Integer> targetUserIds) {
        if (targetUserIds != null && !targetUserIds.isEmpty()) {
            for (Integer userId : targetUserIds) {
                NotificationReadStatus readStatus = NotificationReadStatus.builder()
                    .notificationId(notification.getId())
                    .userId(userId)
                    .build();
                readStatusRepository.save(readStatus);
            }
            
            // 更新通知的總接收者數量
            notification.setTotalRecipients(targetUserIds.size());
            notificationRepository.save(notification);
        }
    }

    private void updateNotificationReadCount(Long notificationId) {
        Long readCount = readStatusRepository.countReadByNotificationId(notificationId);
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            notification.setReadCount(readCount.intValue());
            notificationRepository.save(notification);
        }
    }

    private LocalDateTime calculateNextRetryTime(Integer retryCount) {
        // 指數退避策略：1分鐘、2分鐘、4分鐘
        int delayMinutes = (int) Math.pow(2, retryCount - 1);
        return LocalDateTime.now().plusMinutes(delayMinutes);
    }

    private NotificationDTO convertToNotificationDTO(NotificationReadStatus readStatus) {
        Notification notification = readStatus.getNotification();
        return NotificationDTO.builder()
            .id(notification.getId())
            .title(notification.getTitle())
            .content(notification.getContent())
            .notificationType(notification.getNotificationType())
            .category(notification.getCategory())
            .priority(notification.getPriority())
            .senderName(notification.getSenderName())
            .actionUrl(notification.getActionUrl())
            .actionRequired(notification.getActionRequired())
            .createdAt(notification.getCreatedAt())
            .isRead(readStatus.getIsRead())
            .readAt(readStatus.getReadAt())
            .isArchived(readStatus.getIsArchived())
            .imageFileId(notification.getImageFileId())
            .build();
    }
}