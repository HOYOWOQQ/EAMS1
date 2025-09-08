package com.eams.common.Support.Services;

import com.eams.common.Support.Repository.NotificationRepository;
import com.eams.common.Support.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationDeliveryService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationDeliveryService.class);
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private WebSocketNotificationService webSocketService;
    
    @Autowired
    private EmailNotificationService emailService;
    
    @Autowired
    private SmsNotificationService smsService;

    @Async
    public CompletableFuture<Void> deliverNotificationAsync(Notification notification) {
        return CompletableFuture.runAsync(() -> {
            try {
                // WebSocket 推送
                if (Boolean.TRUE.equals(notification.getPushWebsocket())) {
                    deliverWebSocket(notification);
                }
                
                // 電子郵件推送
                if (Boolean.TRUE.equals(notification.getPushEmail())) {
                    deliverEmail(notification);
                }
                
                // SMS 推送
                if (Boolean.TRUE.equals(notification.getPushSms())) {
                    deliverSms(notification);
                }
                
                // 瀏覽器推送
                if (Boolean.TRUE.equals(notification.getPushBrowser())) {
                    deliverBrowserPush(notification);
                }
                
                // 行動推送
                if (Boolean.TRUE.equals(notification.getPushMobile())) {
                    deliverMobilePush(notification);
                }
                
                // 更新發送時間
                notification.setSentAt(LocalDateTime.now());
                notification.setStatus("sent");
                notificationRepository.save(notification);
                
            } catch (Exception ex) {
                logger.error("Error delivering notification {}", notification.getId(), ex);
                handleDeliveryFailure(notification, ex.getMessage());
            }
        });
    }

    private void deliverWebSocket(Notification notification) {
        try {
            webSocketService.sendNotification(notification);
            updateDeliveryStatus(notification, "websocket", "sent", null);
        } catch (Exception ex) {
            updateDeliveryStatus(notification, "websocket", "failed", ex.getMessage());
        }
    }

    private void deliverEmail(Notification notification) {
        try {
            emailService.sendNotification(notification);
            updateDeliveryStatus(notification, "email", "sent", null);
        } catch (Exception ex) {
            updateDeliveryStatus(notification, "email", "failed", ex.getMessage());
        }
    }

    private void deliverSms(Notification notification) {
        try {
            smsService.sendNotification(notification);
            updateDeliveryStatus(notification, "sms", "sent", null);
        } catch (Exception ex) {
            updateDeliveryStatus(notification, "sms", "failed", ex.getMessage());
        }
    }

    private void deliverBrowserPush(Notification notification) {
        try {
            // 實作瀏覽器推送邏輯
            updateDeliveryStatus(notification, "browser_push", "sent", null);
        } catch (Exception ex) {
            updateDeliveryStatus(notification, "browser_push", "failed", ex.getMessage());
        }
    }

    private void deliverMobilePush(Notification notification) {
        try {
            // 實作行動推送邏輯
            updateDeliveryStatus(notification, "mobile_push", "sent", null);
        } catch (Exception ex) {
            updateDeliveryStatus(notification, "mobile_push", "failed", ex.getMessage());
        }
    }

    private void updateDeliveryStatus(Notification notification, String channel, String status, String error) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (channel) {
            case "websocket":
                notification.setWebsocketStatus(status);
                notification.setWebsocketSentAt(now);
                notification.setWebsocketError(error);
                break;
            case "email":
                notification.setEmailStatus(status);
                notification.setEmailSentAt(now);
                notification.setEmailError(error);
                break;
            case "sms":
                notification.setSmsStatus(status);
                notification.setSmsSentAt(now);
                notification.setSmsError(error);
                break;
            case "browser_push":
                notification.setBrowserPushStatus(status);
                notification.setBrowserPushSentAt(now);
                notification.setBrowserPushError(error);
                break;
            case "mobile_push":
                notification.setMobilePushStatus(status);
                notification.setMobilePushSentAt(now);
                notification.setMobilePushError(error);
                break;
        }
        
        notificationRepository.save(notification);
    }

    private void handleDeliveryFailure(Notification notification, String errorMessage) {
        notification.setStatus("failed");
        notification.setRetryCount(notification.getRetryCount() + 1);
        
        if (notification.getRetryCount() < notification.getMaxRetry()) {
            // 計算下次重試時間
            LocalDateTime nextRetry = LocalDateTime.now().plusMinutes((int) Math.pow(2, notification.getRetryCount()));
            notification.setNextRetryAt(nextRetry);
        }
        
        notificationRepository.save(notification);
    }
}