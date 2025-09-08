package com.eams.common.Monitoring.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eams.common.Monitoring.Repository.SystemEventLogRepository;
import com.eams.common.Monitoring.entity.SystemEventLog;
import com.eams.common.Monitoring.enums.SeverityLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class EventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);
    
    @Autowired
    private SystemEventLogRepository eventRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // 如果您有 NotificationService，可以注入使用
    // @Autowired
    // private NotificationService notificationService;

    // ==================== 通用事件發布 ====================

    /**
     * 異步發布事件 - 通用方法
     */
    public <T> CompletableFuture<SystemEventLog> publishAsync(T eventData, String eventType, 
                                                             String category, SeverityLevel severity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return publishEvent(eventData, eventType, category, severity, null, null);
            } catch (Exception ex) {
                logger.error("Failed to publish event {} asynchronously", eventType, ex);
                return null;
            }
        });
    }

    /**
     * 同步發布事件 - 通用方法
     */
    public <T> SystemEventLog publishEvent(T eventData, String eventType, String category, 
                                          SeverityLevel severity, Integer memberId, String sourceIp) {
        try {
            SystemEventLog eventLog = SystemEventLog.builder()
                .eventCategory(category)
                .eventType(eventType)
                .severity(severity)
                .title(generateTitle(eventType, category))
                .message(generateMessage(eventType, eventData))
                .memberId(memberId)
                .sourceIp(sourceIp)
                .status("NEW")
                .occurrenceCount(1)
                .createdAt(LocalDateTime.now())
                .firstOccurredAt(LocalDateTime.now())
                .lastOccurredAt(LocalDateTime.now())
                .notificationSent(false)
                .build();

            // 序列化詳細資訊
            if (eventData != null) {
                try {
                    String detailsJson = objectMapper.writeValueAsString(eventData);
                    eventLog.setDetails(detailsJson);
                } catch (JsonProcessingException e) {
                    logger.warn("無法序列化事件資料", e);
                    eventLog.setDetails(eventData.toString());
                }
            }

            SystemEventLog savedEvent = eventRepository.save(eventLog);

            // 如果是高嚴重度事件，觸發通知 (如果有實作 NotificationService)
            if ("HIGH".equals(severity) || "CRITICAL".equals(severity)) {
                // sendUrgentNotification(savedEvent);
                logger.warn("High severity event published: {} [{}]", eventType, severity);
            }

            logger.info("Event published: {} with severity {}", eventType, severity);
            return savedEvent;
            
        } catch (Exception ex) {
            logger.error("Failed to publish event {}", eventType, ex);
            throw new RuntimeException("Failed to publish event", ex);
        }
    }

    // ==================== 安全事件發布 ====================

    /**
     * 異步發布安全事件
     */
    public CompletableFuture<SystemEventLog> publishSecurityEventAsync(String eventType, String message, 
                                                                      String sourceIp, Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return publishSecurityEvent(eventType, message, sourceIp, userId, null);
            } catch (Exception ex) {
                logger.error("Failed to publish security event {} asynchronously", eventType, ex);
                return null;
            }
        });
    }

    /**
     * 發布安全事件
     */
    public SystemEventLog publishSecurityEvent(String eventType, String message, String sourceIp, 
                                              Integer userId, Map<String, Object> additionalData) {
        try {
            
            SystemEventLog eventLog = SystemEventLog.builder()
                .eventCategory("SECURITY")
                .eventType(eventType)
                .severity(determineSecuritySeverity(eventType))
                .title("安全事件: " + getSecurityEventTitle(eventType))
                .message(message)
                .sourceIp(sourceIp)
                .memberId(userId)
                .status("NEW")
                .occurrenceCount(1)
                .createdAt(LocalDateTime.now())
                .firstOccurredAt(LocalDateTime.now())
                .lastOccurredAt(LocalDateTime.now())
                .notificationSent(false)
                .build();

            // 添加額外的安全相關資訊
            if (additionalData != null) {
                try {
                    String detailsJson = objectMapper.writeValueAsString(additionalData);
                    eventLog.setDetails(detailsJson);
                } catch (JsonProcessingException e) {
                    logger.warn("無法序列化安全事件詳情", e);
                }
            }

            SystemEventLog savedEvent = eventRepository.save(eventLog);
            
            // 安全事件通常需要立即關注
            if ("HIGH".equals(determineSecuritySeverity(eventType)) || "CRITICAL".equals(determineSecuritySeverity(eventType))) {
                logger.warn("SECURITY ALERT: {} from IP {} - {}", eventType, sourceIp, message);
                // sendSecurityAlert(savedEvent);
            }

            return savedEvent;
            
        } catch (Exception ex) {
            logger.error("Failed to publish security event {}", eventType, ex);
            throw new RuntimeException("Failed to publish security event", ex);
        }
    }

    // ==================== 系統通知發布 ====================

    /**
     * 異步發布系統通知
     */
    public CompletableFuture<SystemEventLog> publishNotificationAsync(String title, String content, 
                                                                     NotificationTarget target) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return publishNotification(title, content, target);
            } catch (Exception ex) {
                logger.error("Failed to publish notification asynchronously", ex);
                return null;
            }
        });
    }

    /**
     * 發布系統通知
     */
    public SystemEventLog publishNotification(String title, String content, NotificationTarget target) {
        try {
            SystemEventLog eventLog = SystemEventLog.builder()
                .eventCategory("NOTIFICATION")
                .eventType("SYSTEM_NOTIFICATION")
                .severity(SeverityLevel.LOW)
                .title(title)
                .message(content)
                .notificationTargetType(target.getType())
                .notificationTargetRoles(target.getRoles() != null ? String.join(",", target.getRoles()) : null)
                .status("NEW")
                .occurrenceCount(1)
                .createdAt(LocalDateTime.now())
                .firstOccurredAt(LocalDateTime.now())
                .lastOccurredAt(LocalDateTime.now())
                .notificationSent(false)
                .build();

            // 序列化目標用戶列表
            if (target.getUserIds() != null && !target.getUserIds().isEmpty()) {
                try {
                    String userIdsJson = objectMapper.writeValueAsString(target.getUserIds());
                    eventLog.setNotificationTargetUsers(userIdsJson);
                } catch (JsonProcessingException e) {
                    logger.warn("無法序列化通知目標用戶", e);
                }
            }

            SystemEventLog savedEvent = eventRepository.save(eventLog);
            logger.info("Notification published: {} to {}", title, target.getType());
            
            return savedEvent;
            
        } catch (Exception ex) {
            logger.error("Failed to publish notification", ex);
            throw new RuntimeException("Failed to publish notification", ex);
        }
    }

    // ==================== 效能事件發布 ====================

    /**
     * 發布效能警告事件
     */
    public SystemEventLog publishPerformanceAlert(String metricName, Double currentValue, 
                                                 Double threshold, String unit) {
    	SeverityLevel severity = currentValue > threshold * 1.2 ? SeverityLevel.HIGH : SeverityLevel.MEDIUM;
        String title = String.format("%s 效能警告", metricName);
        String message = String.format("%s 當前值 %.2f%s 超過閾值 %.2f%s", 
            metricName, currentValue, unit, threshold, unit);

        Map<String, Object> details = Map.of(
            "metric_name", metricName,
            "current_value", currentValue,
            "threshold", threshold,
            "unit", unit,
            "exceeded_by", currentValue - threshold
        );

        return publishEvent(details, "PERFORMANCE_ALERT", "PERFORMANCE", severity, null, "127.0.0.1");
    }

    /**
     * 發布系統錯誤事件
     */
    public SystemEventLog publishSystemError(String component, String errorMessage, 
                                            Exception exception, String sourceIp) {
        Map<String, Object> details = Map.of(
            "component", component,
            "error_message", errorMessage,
            "exception_class", exception != null ? exception.getClass().getSimpleName() : "Unknown",
            "stack_trace", exception != null ? getStackTraceString(exception) : "No stack trace",
            "timestamp", LocalDateTime.now()
        );

        return publishEvent(details, "SYSTEM_ERROR", "ERROR", SeverityLevel.HIGH, null, sourceIp);
    }

    // ==================== 批次事件處理 ====================

    /**
     * 檢查並合併重複事件
     */
    public SystemEventLog publishOrUpdateEvent(String eventType, String category, SeverityLevel severity,
                                              String message, Integer memberId, String sourceIp) {
        // 檢查最近5分鐘是否有相同類型的事件
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        
        // 這裡可以實作重複事件的檢查邏輯
        // 如果找到重複事件，更新 occurrence_count 和 last_occurred_at
        // 否則建立新事件
        
        return publishEvent(null, eventType, category, severity, memberId, sourceIp);
    }

    // ==================== 私有輔助方法 ====================

    private String generateTitle(String eventType, String category) {
        return String.format("[%s] %s", category, formatEventType(eventType));
    }

    private <T> String generateMessage(String eventType, T eventData) {
        if (eventData == null) {
            return String.format("系統事件 %s 已發生", eventType);
        }
        return String.format("事件 %s 已發生: %s", eventType, 
            eventData.toString().length() > 100 ? 
            eventData.toString().substring(0, 100) + "..." : 
            eventData.toString());
    }

    private SeverityLevel determineSecuritySeverity(String eventType) {
        switch (eventType.toLowerCase()) {
            case "login_failed":
            case "password_reset_requested":
                return SeverityLevel.MEDIUM;
            case "brute_force_detected":
            case "unauthorized_access_attempt":
            case "suspicious_activity":
                return SeverityLevel.HIGH;
            case "data_breach_suspected":
            case "admin_account_compromised":
                return SeverityLevel.CRITICAL;
            default:
                return SeverityLevel.LOW;
        }
    }


    private String getSecurityEventTitle(String eventType) {
        switch (eventType.toLowerCase()) {
            case "login_failed": return "登入失敗";
            case "brute_force_detected": return "暴力破解攻擊";
            case "unauthorized_access_attempt": return "未授權存取嘗試";
            case "suspicious_activity": return "可疑活動";
            case "data_breach_suspected": return "疑似資料外洩";
            default: return eventType;
        }
    }

    private String formatEventType(String eventType) {
        eventType = eventType.toLowerCase().replace("_", " ");
        
        Matcher matcher = Pattern.compile("\\b\\w").matcher(eventType);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().toUpperCase());
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }


    private String getStackTraceString(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    // 未來可以實作的通知方法
    /*
    private void sendUrgentNotification(SystemEventLog event) {
        if (notificationService != null) {
            notificationService.sendUrgentNotificationAsync(event);
        }
    }

    private void sendSecurityAlert(SystemEventLog event) {
        if (notificationService != null) {
            notificationService.sendSecurityAlertAsync(event);
        }
    }
    */
}