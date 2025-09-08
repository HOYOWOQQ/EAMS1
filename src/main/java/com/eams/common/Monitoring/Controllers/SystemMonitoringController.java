package com.eams.common.Monitoring.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.common.Monitoring.Services.*;
import com.eams.common.Monitoring.entity.PerformanceMetric;
import com.eams.common.Monitoring.entity.SystemEventLog;
import com.eams.common.Monitoring.enums.SeverityLevel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;



@RestController
@RequestMapping("/api/monitoring")
@Tag(name = "系統監控", description = "系統監控與事件管理 API")
public class SystemMonitoringController {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemMonitoringController.class);
    
    @Autowired
    private PerformanceMonitorService performanceMonitorService;
    
    @Autowired
    private SystemEventService systemEventService;
    
    @Autowired
    private EventPublisher eventPublisher;

    // ==================== 系統健康狀態 ====================

    @GetMapping("/health")
    @Operation(summary = "獲取系統健康狀態", description = "返回系統 CPU、記憶體、磁碟使用率和整體健康分數")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getSystemHealth() {
        return performanceMonitorService.getSystemHealthAsync()
                .thenApply(health -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", health);
                    response.put("timestamp", LocalDateTime.now());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    logger.error("獲取系統健康狀態失敗", ex);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("error", "無法獲取系統健康狀態");
                    errorResponse.put("timestamp", LocalDateTime.now());
                    return ResponseEntity.internalServerError().body(errorResponse);
                });
    }

    @GetMapping("/health/check")
    @Operation(summary = "快速健康檢查", description = "簡化的健康狀態檢查，用於負載均衡器")
    public ResponseEntity<Map<String, Object>> quickHealthCheck() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now());
            response.put("service", "EAMS Monitoring Service");
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "DOWN");
            response.put("error", ex.getMessage());
            response.put("timestamp", LocalDateTime.now());
            return ResponseEntity.status(503).body(response);
        }
    }

    // ==================== 效能指標管理 ====================

    @PostMapping("/metrics")
    @Operation(summary = "記錄效能指標", description = "記錄新的效能指標數據")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> recordMetric(
            @RequestBody MetricRequest request) {
        
        return performanceMonitorService.recordMetricAsync(
                request.getCategory(), 
                request.getName(), 
                request.getValue(), 
                request.getUnit())
                .thenApply(result -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "效能指標記錄成功");
                    response.put("timestamp", LocalDateTime.now());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    logger.error("記錄效能指標失敗", ex);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("error", "記錄效能指標失敗");
                    return ResponseEntity.badRequest().body(errorResponse);
                });
    }

    @GetMapping("/metrics")
    @Operation(summary = "查詢效能指標", description = "根據分類和時間範圍查詢效能指標")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getMetrics(
            @Parameter(description = "指標分類") @RequestParam(required = false) String category,
            @Parameter(description = "開始時間") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @Parameter(description = "結束時間") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        
        return performanceMonitorService.getMetricsAsync(category, from, to)
                .thenApply(metrics -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", metrics);
                    response.put("count", metrics.size());
                    response.put("timestamp", LocalDateTime.now());
                    return ResponseEntity.ok(response);
                })
                .exceptionally(ex -> {
                    logger.error("查詢效能指標失敗", ex);
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("error", "查詢效能指標失敗");
                    return ResponseEntity.internalServerError().body(errorResponse);
                });
    }

    @PostMapping("/metrics/batch")
    @Operation(summary = "批次記錄效能指標", description = "一次記錄多個效能指標")
    public ResponseEntity<Map<String, Object>> recordMetricsBatch(
            @RequestBody List<MetricRequest> requests) {
        
        try {
            List<CompletableFuture<Void>> futures = requests.stream()
                    .map(request -> performanceMonitorService.recordMetricAsync(
                            request.getCategory(), 
                            request.getName(), 
                            request.getValue(), 
                            request.getUnit()))
                    .toList();
            
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批次記錄成功");
            response.put("processed_count", requests.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("批次記錄效能指標失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "批次記錄失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== 系統事件管理 ====================

    @GetMapping("/events")
    @Operation(summary = "查詢系統事件", description = "分頁查詢系統事件日誌")
    public ResponseEntity<Map<String, Object>> getEvents(
            @Parameter(description = "事件分類") @RequestParam(required = false) String category,
            @Parameter(description = "嚴重程度") @RequestParam(required = false) String severity,
            @Parameter(description = "頁碼，從0開始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁數量") @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SystemEventLog> events = systemEventService.getEvents(category, severity, pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", events.getContent());
            response.put("pagination", Map.of(
                "current_page", events.getNumber(),
                "total_pages", events.getTotalPages(),
                "total_elements", events.getTotalElements(),
                "size", events.getSize(),
                "has_next", events.hasNext(),
                "has_previous", events.hasPrevious()
            ));
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("查詢系統事件失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "查詢系統事件失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/events/recent-alerts")
    @Operation(summary = "獲取最近的高嚴重度事件", description = "返回最近的高嚴重度事件列表")
    public ResponseEntity<Map<String, Object>> getRecentAlerts() {
        try {
            List<SystemEventLog> alerts = systemEventService.getRecentHighSeverityEvents();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", alerts);
            response.put("count", alerts.size());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("獲取高嚴重度事件失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "獲取警告事件失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/events/user/{memberId}")
    @Operation(summary = "獲取用戶相關事件", description = "查詢特定用戶的相關事件")
    public ResponseEntity<Map<String, Object>> getUserEvents(
            @Parameter(description = "會員ID") @PathVariable Integer memberId) {
        
        try {
            List<SystemEventLog> userEvents = systemEventService.getUserEvents(memberId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userEvents);
            response.put("count", userEvents.size());
            response.put("member_id", memberId);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("獲取用戶事件失敗: memberId={}", memberId, ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "獲取用戶事件失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/events/statistics")
    @Operation(summary = "獲取事件統計", description = "返回事件分類和嚴重程度的統計資訊")
    public ResponseEntity<Map<String, Object>> getEventStatistics() {
        try {
            Map<String, Long> statistics = systemEventService.getEventStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", statistics);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("獲取事件統計失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "獲取統計資料失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ==================== 事件記錄 ====================

    @PostMapping("/events/security")
    @Operation(summary = "記錄安全事件", description = "記錄安全相關事件")
    public ResponseEntity<Map<String, Object>> logSecurityEvent(
            @RequestBody SecurityEventRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            String sourceIp = getClientIpAddress(httpRequest);
            
            SystemEventLog event = eventPublisher.publishSecurityEvent(
                request.getEventType(),
                request.getMessage(),
                sourceIp,
                request.getMemberId(),
                request.getAdditionalData()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "安全事件記錄成功");
            response.put("event_id", event.getId());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("記錄安全事件失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "記錄安全事件失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/events/system")
    @Operation(summary = "記錄系統事件", description = "記錄一般系統事件")
    public ResponseEntity<Map<String, Object>> logSystemEvent(
            @RequestBody SystemEventRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            String sourceIp = getClientIpAddress(httpRequest);
            
            SystemEventLog event = systemEventService.logEvent(
                request.getCategory(),
                request.getType(),
                request.getSeverity(),
                request.getTitle(),
                request.getMessage(),
                request.getDetails(),
                request.getMemberId(),
                sourceIp
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "系統事件記錄成功");
            response.put("event_id", event.getId());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("記錄系統事件失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "記錄系統事件失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== 通知管理 ====================

    @PostMapping("/notifications")
    @Operation(summary = "發送系統通知", description = "發送系統通知給指定目標")
    public ResponseEntity<Map<String, Object>> sendNotification(
            @RequestBody NotificationRequest request) {
        
        try {
            SystemEventLog notification = eventPublisher.publishNotification(
                request.getTitle(),
                request.getContent(),
                request.getTarget()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "通知發送成功");
            response.put("notification_id", notification.getId());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("發送通知失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "發送通知失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/notifications/maintenance")
    @Operation(summary = "發送維護通知", description = "發送系統維護通知")
    public ResponseEntity<Map<String, Object>> sendMaintenanceNotification(
            @RequestBody MaintenanceNotificationRequest request) {
        
        try {
            SystemEventLog notification = systemEventService.sendMaintenanceNotification(
                request.getTitle(),
                request.getMessage(),
                request.getScheduledTime()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "維護通知發送成功");
            response.put("notification_id", notification.getId());
            response.put("scheduled_time", request.getScheduledTime());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("發送維護通知失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "發送維護通知失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== 系統控制 ====================

    @PostMapping("/monitoring/start")
    @Operation(summary = "啟動監控", description = "手動啟動系統監控")
    public ResponseEntity<Map<String, Object>> startMonitoring() {
        try {
            performanceMonitorService.startContinuousMonitoring();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "監控已啟動");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("啟動監控失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "啟動監控失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/monitoring/collect")
    @Operation(summary = "收集系統指標", description = "手動觸發系統指標收集")
    public ResponseEntity<Map<String, Object>> collectMetrics() {
        try {
            performanceMonitorService.collectSystemMetrics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "系統指標收集完成");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("收集系統指標失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "收集指標失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/health/check-and-notify")
    @Operation(summary = "檢查健康狀態並通知", description = "檢查系統健康狀態，如有問題則發送通知")
    public ResponseEntity<Map<String, Object>> checkHealthAndNotify() {
        try {
            performanceMonitorService.checkAndNotifyHealthStatusChange();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "健康狀態檢查完成");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("健康狀態檢查失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "健康狀態檢查失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/metrics/history")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getMetricsHistory(
            @RequestParam(defaultValue = "24") int hours) {
        
        LocalDateTime from = LocalDateTime.now().minusHours(hours);
        return performanceMonitorService.getMetricsAsync("SYSTEM", from, LocalDateTime.now())
            .thenApply(metrics -> {
                List<Map<String, Object>> historyData = formatHistoryData(metrics);
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", historyData);
                return ResponseEntity.ok(response);
            });
    }

    private List<Map<String, Object>> formatHistoryData(List<PerformanceMetric> metrics) {
        // 1. 分組：依據小時分組
        Map<LocalDateTime, List<PerformanceMetric>> groupedMetrics = new HashMap<>();
        for (PerformanceMetric m : metrics) {
            LocalDateTime hour = m.getRecordedAt().truncatedTo(ChronoUnit.HOURS);
            groupedMetrics.computeIfAbsent(hour, k -> new ArrayList<>()).add(m);
        }

        // 2. 建立結果 List
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<LocalDateTime, List<PerformanceMetric>> entry : groupedMetrics.entrySet()) {
            Map<String, Object> point = new HashMap<>();
            point.put("time", entry.getKey().format(DateTimeFormatter.ofPattern("HH:mm")));
            point.put("cpu", getAverageValue(entry.getValue(), "cpu_usage"));
            point.put("memory", getAverageValue(entry.getValue(), "memory_usage"));
            point.put("disk", getAverageValue(entry.getValue(), "disk_usage"));
            result.add(point);
        }

        // 3. 依時間排序
        result.sort(Comparator.comparing(m -> (String)m.get("time")));
        return result;
    }


    private Double getAverageValue(List<PerformanceMetric> metrics, String metricName) {
        return metrics.stream()
            .filter(m -> metricName.equals(m.getMetricName()))
            .mapToDouble(m -> m.getMetricValue().doubleValue())
            .average()
            .orElse(0.0);
    }


    // ==================== 輔助方法 ====================

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    // ==================== 請求/回應 DTO 類別 ====================

    public static class MetricRequest {
        private String category;
        private String name;
        private BigDecimal value;
        private String unit;

        // getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
    }

    public static class SecurityEventRequest {
        private String eventType;
        private String message;
        private Integer memberId;
        private Map<String, Object> additionalData;

        // getters and setters
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Integer getMemberId() { return memberId; }
        public void setMemberId(Integer memberId) { this.memberId = memberId; }
        
        public Map<String, Object> getAdditionalData() { return additionalData; }
        public void setAdditionalData(Map<String, Object> additionalData) { this.additionalData = additionalData; }
    }

    public static class SystemEventRequest {
        private String category;
        private String type;
        private SeverityLevel  severity;
        private String title;
        private String message;
        private Object details;
        private Integer memberId;

        // getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public SeverityLevel  getSeverity() { return severity; }
        public void setSeverity(SeverityLevel  severity) { this.severity = severity; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public Object getDetails() { return details; }
        public void setDetails(Object details) { this.details = details; }
        
        public Integer getMemberId() { return memberId; }
        public void setMemberId(Integer memberId) { this.memberId = memberId; }
    }

    public static class NotificationRequest {
        private String title;
        private String content;
        private NotificationTarget target;

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public NotificationTarget getTarget() { return target; }
        public void setTarget(NotificationTarget target) { this.target = target; }
    }

    public static class MaintenanceNotificationRequest {
        private String title;
        private String message;
        private LocalDateTime scheduledTime;

        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
    }
}