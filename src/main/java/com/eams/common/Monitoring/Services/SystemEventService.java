package com.eams.common.Monitoring.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eams.common.Monitoring.Repository.SystemEventLogRepository;
import com.eams.common.Monitoring.entity.SystemEventLog;
import com.eams.common.Monitoring.enums.SeverityLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemEventService {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemEventService.class);
    
    @Autowired
    private SystemEventLogRepository eventRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private EventPublisher eventPublisher;

    /**
     * 記錄系統事件 - 基本方法
     */
    public SystemEventLog logEvent(String category, String type, SeverityLevel severity, 
                                  String title, String message) {
        return logEvent(category, type, severity, title, message, null, null, null);
    }

    /**
     * 記錄系統事件 - 完整方法
     */
    public SystemEventLog logEvent(String category, String type, SeverityLevel severity, 
                                  String title, String message, Object details, 
                                  Integer memberId, String sourceIp) {
        try {
            SystemEventLog event = SystemEventLog.builder()
                .eventCategory(category)
                .eventType(type)
                .severity(severity)
                .title(title)
                .message(message)
                .memberId(memberId)
                .sourceIp(sourceIp)
                .status("NEW")
                .occurrenceCount(1)
                .createdAt(LocalDateTime.now())
                .firstOccurredAt(LocalDateTime.now())
                .lastOccurredAt(LocalDateTime.now())
                .notificationSent(false)
                .build();
            
            // 處理詳細資訊
            if (details != null) {
                try {
                    String detailsJson = objectMapper.writeValueAsString(details);
                    event.setDetails(detailsJson);
                } catch (JsonProcessingException e) {
                    logger.warn("無法序列化事件詳情", e);
                    event.setDetails(details.toString());
                }
            }
            
            SystemEventLog savedEvent = eventRepository.save(event);
            logger.info("系統事件已記錄: {} - {} [{}]", category, type, severity);
            
            return savedEvent;
            
        } catch (Exception e) {
            logger.error("記錄系統事件失敗: {} - {}", category, type, e);
            throw new RuntimeException("Failed to log system event", e);
        }
    }

    /**
     * 記錄應用啟動事件
     */
    public SystemEventLog logApplicationStartup(long startupTimeMs, String[] profiles, int port) {
        Map<String, Object> details = new HashMap<>();
        details.put("startup_time", startupTimeMs + "ms");
        details.put("active_profiles", profiles);
        details.put("server_port", port);
        details.put("java_version", System.getProperty("java.version"));
        details.put("database_url", "jdbc:mysql://localhost:3306/test");
        
        return logEvent("SYSTEM", "APPLICATION_STARTED", SeverityLevel.LOW, 
                       "應用程式啟動", 
                       "智慧校務系統已啟動完成", 
                       details, null, "127.0.0.1");
    }

    /**
     * 記錄應用關閉事件
     */
    public SystemEventLog logApplicationShutdown(String reason, long uptimeMs) {
        Map<String, Object> details = new HashMap<>();
        details.put("shutdown_reason", reason);
        details.put("uptime", formatUptime(uptimeMs));
        
        return logEvent("SYSTEM", "APPLICATION_SHUTDOWN", SeverityLevel.LOW, 
                       "應用程式關閉", 
                       "系統正常關閉", 
                       details, null, "127.0.0.1");
    }

    /**
     * 記錄資料庫慢查詢
     */
    public SystemEventLog logSlowQuery(String tableName, long executionTimeMs, String queryType) {
        if (executionTimeMs < 1000) return null; // 只記錄超過1秒的查詢
        
        Map<String, Object> details = new HashMap<>();
        details.put("execution_time", executionTimeMs + "ms");
        details.put("threshold", "1000ms");
        details.put("query_type", queryType);
        details.put("affected_table", tableName);
        
        SeverityLevel severity = executionTimeMs > 5000 ? SeverityLevel.HIGH : SeverityLevel.MEDIUM;
        
        return logEvent("DATABASE", "SLOW_QUERY", severity, 
                       "偵測到慢查詢", 
                       "查詢執行時間超過閾值", 
                       details, null, "127.0.0.1");
    }

    /**
     * 記錄登入失敗事件
     */
    public SystemEventLog logLoginFailure(String username, String sourceIp, String reason) {
        Map<String, Object> details = new HashMap<>();
        details.put("attempted_username", username);
        details.put("failure_reason", reason);
        details.put("timestamp", LocalDateTime.now());
        
        return logEvent("SECURITY", "LOGIN_FAILED", SeverityLevel.MEDIUM, 
                       "登入失敗", 
                       "用戶登入嘗試失敗", 
                       details, null, sourceIp);
    }

    /**
     * 記錄權限不足事件
     */
    public SystemEventLog logUnauthorizedAccess(Integer memberId, String requestUrl, 
                                               String userRole, String requiredRole, String sourceIp) {
        Map<String, Object> details = new HashMap<>();
        details.put("requested_url", requestUrl);
        details.put("user_role", userRole);
        details.put("required_role", requiredRole);
        details.put("timestamp", LocalDateTime.now());
        
        return logEvent("SECURITY", "UNAUTHORIZED_ACCESS", SeverityLevel.LOW, 
                       "權限不足", 
                       "嘗試存取未授權資源", 
                       details, memberId, sourceIp);
    }

    /**
     * 記錄大檔案上傳事件
     */
    public SystemEventLog logLargeFileUpload(Integer memberId, String fileName, 
                                           long fileSizeBytes, long uploadTimeMs) {
        if (fileSizeBytes < 10 * 1024 * 1024) return null; // 只記錄超過10MB的檔案
        
        Map<String, Object> details = new HashMap<>();
        details.put("file_name", fileName);
        details.put("file_size", formatFileSize(fileSizeBytes));
        details.put("upload_time", uploadTimeMs + "ms");
        details.put("size_bytes", fileSizeBytes);
        
        return logEvent("FILE", "LARGE_FILE_UPLOAD", SeverityLevel.LOW, 
                       "大檔案上傳", 
                       "檔案大小超過10MB", 
                       details, memberId, "127.0.0.1");
    }

    // ==== 查詢方法 ====

    /**
     * 查詢事件 - 分頁
     */
    public Page<SystemEventLog> getEvents(String category, String severity, Pageable pageable) {
        if (category != null && severity != null) {
            // 這個方法需要在 Repository 中添加
            return eventRepository.findByEventCategoryAndSeverityOrderByCreatedAtDesc(category, severity, pageable);
        } else if (category != null) {
            return eventRepository.findByEventCategoryOrderByCreatedAtDesc(category, pageable);
        } else {
            return eventRepository.findAll(pageable);
        }
    }

    /**
     * 查詢最近的高嚴重度事件
     */
    public List<SystemEventLog> getRecentHighSeverityEvents() {
        return eventRepository.findHighSeverityEvents();
    }

    /**
     * 查詢用戶相關事件
     */
    public List<SystemEventLog> getUserEvents(Integer memberId) {
        return eventRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    /**
     * 統計事件數量
     */
    public Map<String, Long> getEventStatistics() {
        Map<String, Long> stats = new HashMap<>();
        
        List<Object[]> categoryCounts = eventRepository.countEventsByCategory();
        for (Object[] row : categoryCounts) {
            stats.put("category_" + row[0], (Long) row[1]);
        }
        
        List<Object[]> severityCounts = eventRepository.countEventsBySeverity();
        for (Object[] row : severityCounts) {
            stats.put("severity_" + row[0], (Long) row[1]);
        }
        
        return stats;
    }

    // ==== 私有輔助方法 ====

    private String formatUptime(long uptimeMs) {
        long hours = uptimeMs / (1000 * 60 * 60);
        long minutes = (uptimeMs % (1000 * 60 * 60)) / (1000 * 60);
        return hours + "h " + minutes + "m";
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)) + " MB";
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }
    
    
    
 
    // ==================== 使用 EventPublisher 的便捷方法 ====================

    /**
     * 使用 EventPublisher 記錄登入失敗（推薦方式）
     */
    public SystemEventLog logLoginFailureViaPublisher(String username, String sourceIp, String reason) {
        Map<String, Object> eventData = Map.of(
            "attempted_username", username,
            "failure_reason", reason,
            "timestamp", LocalDateTime.now(),
            "user_agent", "Unknown" // 可從 HttpServletRequest 獲取
        );
        
        return eventPublisher.publishSecurityEvent("LOGIN_FAILED", 
            "用戶登入嘗試失敗: " + reason, sourceIp, null, eventData);
    }

    /**
     * 使用 EventPublisher 記錄暴力破解攻擊
     */
    public SystemEventLog logBruteForceAttack(String sourceIp, int attemptCount, String timeWindow) {
        Map<String, Object> eventData = Map.of(
            "source_ip", sourceIp,
            "attempt_count", attemptCount,
            "time_window", timeWindow,
            "detection_time", LocalDateTime.now()
        );
        
        return eventPublisher.publishSecurityEvent("BRUTE_FORCE_DETECTED", 
            String.format("偵測到暴力破解攻擊: %d 次嘗試在 %s 內", attemptCount, timeWindow), 
            sourceIp, null, eventData);
    }

    /**
     * 使用 EventPublisher 記錄系統錯誤
     */
    public SystemEventLog logSystemErrorViaPublisher(String component, String errorMessage, Exception ex) {
        return eventPublisher.publishSystemError(component, errorMessage, ex, "127.0.0.1");
    }

    /**
     * 發送系統維護通知
     */
    public SystemEventLog sendMaintenanceNotification(String title, String message, LocalDateTime scheduledTime) {
        NotificationTarget target = new NotificationTarget();
        target.setType("ALL"); // 通知所有用戶
        
        String fullMessage = String.format("%s\n預定時間: %s", message, scheduledTime);
        
        return eventPublisher.publishNotification(title, fullMessage, target);
    }

    /**
     * 發送角色特定通知
     */
    public SystemEventLog sendRoleNotification(String title, String message, String... roles) {
        NotificationTarget target = new NotificationTarget();
        target.setType("ROLE");
        target.setRoles(List.of(roles));
        
        return eventPublisher.publishNotification(title, message, target);
    }
}