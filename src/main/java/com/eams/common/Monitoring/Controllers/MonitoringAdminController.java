package com.eams.common.Monitoring.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.common.Monitoring.Services.*;
import com.eams.common.Monitoring.config.MonitoringConfiguration;
import com.eams.common.Monitoring.enums.SeverityLevel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/admin")
@Tag(name = "監控系統管理", description = "系統監控管理功能 API")
@CrossOrigin(origins = "*")
public class MonitoringAdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoringAdminController.class);
    
    @Autowired
    private DataCleanupService dataCleanupService;
    
    @Autowired
    private PerformanceMonitorService performanceMonitorService;
    
    @Autowired
    private SystemEventService systemEventService;
    
    @Autowired
    private EventPublisher eventPublisher;

    // ==================== 系統狀態管理 ====================

    @GetMapping("/system/status")
    @Operation(summary = "獲取監控系統狀態", description = "返回監控系統的整體運行狀態")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 基本系統資訊
            status.put("system_time", LocalDateTime.now());
            status.put("uptime", getSystemUptime());
            status.put("java_version", System.getProperty("java.version"));
            status.put("monitoring_version", "1.0.0");
            
            // 監控服務狀態
            status.put("performance_monitoring", "ACTIVE");
            status.put("event_logging", "ACTIVE");
            status.put("notification_service", "ACTIVE");
            status.put("cleanup_service", "ACTIVE");
            
            // 資源使用狀況
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> memory = new HashMap<>();
            memory.put("total_mb", runtime.totalMemory() / 1024 / 1024);
            memory.put("free_mb", runtime.freeMemory() / 1024 / 1024);
            memory.put("used_mb", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
            memory.put("max_mb", runtime.maxMemory() / 1024 / 1024);
            status.put("jvm_memory", memory);
            
            // 執行緒資訊
            status.put("active_threads", Thread.activeCount());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", status);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("獲取系統狀態失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "獲取系統狀態失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/system/config")
    @Operation(summary = "獲取監控配置", description = "返回當前監控系統的配置資訊")
    public ResponseEntity<Map<String, Object>> getMonitoringConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            
            // 閾值配置
            Map<String, Object> thresholds = new HashMap<>();
            thresholds.put("cpu_warning", MonitoringConfiguration.MonitoringConstants.CPU_WARNING_THRESHOLD);
            thresholds.put("cpu_critical", MonitoringConfiguration.MonitoringConstants.CPU_CRITICAL_THRESHOLD);
            thresholds.put("memory_warning", MonitoringConfiguration.MonitoringConstants.MEMORY_WARNING_THRESHOLD);
            thresholds.put("memory_critical", MonitoringConfiguration.MonitoringConstants.MEMORY_CRITICAL_THRESHOLD);
            thresholds.put("disk_warning", MonitoringConfiguration.MonitoringConstants.DISK_WARNING_THRESHOLD);
            thresholds.put("disk_critical", MonitoringConfiguration.MonitoringConstants.DISK_CRITICAL_THRESHOLD);
            config.put("thresholds", thresholds);
            
            // 間隔配置
            Map<String, Object> intervals = new HashMap<>();
            intervals.put("metric_collection_ms", MonitoringConfiguration.MonitoringConstants.METRIC_COLLECTION_INTERVAL);
            intervals.put("health_check_ms", MonitoringConfiguration.MonitoringConstants.HEALTH_CHECK_INTERVAL);
            intervals.put("cleanup_ms", MonitoringConfiguration.MonitoringConstants.EVENT_CLEANUP_INTERVAL);
            config.put("intervals", intervals);
            
            // 保留期配置
            Map<String, Object> retention = new HashMap<>();
            retention.put("metric_retention_days", MonitoringConfiguration.MonitoringConstants.METRIC_RETENTION_DAYS);
            retention.put("event_retention_days", MonitoringConfiguration.MonitoringConstants.EVENT_RETENTION_DAYS);
            retention.put("low_severity_event_retention_days", MonitoringConfiguration.MonitoringConstants.LOW_SEVERITY_EVENT_RETENTION_DAYS);
            config.put("retention", retention);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", config);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("獲取監控配置失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "獲取監控配置失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ==================== 資料清理管理 ====================

    @GetMapping("/cleanup/statistics")
    @Operation(summary = "獲取清理統計", description = "返回資料清理相關的統計資訊")
    public ResponseEntity<Map<String, Object>> getCleanupStatistics() {
        try {
            DataCleanupService.CleanupStatistics stats = dataCleanupService.getCleanupStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("獲取清理統計失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "獲取清理統計失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/cleanup/manual")
    @Operation(summary = "手動執行清理", description = "手動觸發資料清理作業")
    public ResponseEntity<Map<String, Object>> manualCleanup(
            @Parameter(description = "事件保留天數") @RequestParam(defaultValue = "90") int eventRetentionDays,
            @Parameter(description = "指標保留天數") @RequestParam(defaultValue = "30") int metricRetentionDays) {
        
        try {
            dataCleanupService.manualCleanup(eventRetentionDays, metricRetentionDays);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "手動清理已完成");
            response.put("event_retention_days", eventRetentionDays);
            response.put("metric_retention_days", metricRetentionDays);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("手動清理失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "手動清理失敗: " + ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/cleanup/old-events")
    @Operation(summary = "清理舊事件", description = "清理舊的系統事件日誌")
    public ResponseEntity<Map<String, Object>> cleanupOldEvents() {
        try {
            dataCleanupService.cleanupOldEventsAsync()
                    .thenRun(() -> logger.info("異步清理舊事件已啟動"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "舊事件清理已啟動");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("啟動舊事件清理失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "啟動清理失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/cleanup/old-metrics")
    @Operation(summary = "清理舊指標", description = "清理舊的效能指標")
    public ResponseEntity<Map<String, Object>> cleanupOldMetrics() {
        try {
            dataCleanupService.cleanupOldMetricsAsync()
                    .thenRun(() -> logger.info("異步清理舊指標已啟動"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "舊指標清理已啟動");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("啟動舊指標清理失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "啟動清理失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ==================== 監控控制 ====================

    @PostMapping("/monitoring/restart")
    @Operation(summary = "重啟監控服務", description = "重新啟動效能監控服務")
    public ResponseEntity<Map<String, Object>> restartMonitoring() {
        try {
            // 記錄重啟事件
            eventPublisher.publishEvent(
                null,
                "MONITORING_RESTART_REQUESTED",
                "SYSTEM",
                SeverityLevel.LOW,
                null,
                "127.0.0.1"
            );
            
            // 重新啟動監控
            performanceMonitorService.startContinuousMonitoring();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "監控服務已重啟");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("重啟監控服務失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "重啟監控服務失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/monitoring/force-collect")
    @Operation(summary = "強制收集指標", description = "立即強制收集系統效能指標")
    public ResponseEntity<Map<String, Object>> forceCollectMetrics() {
        try {
            performanceMonitorService.collectSystemMetrics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "強制指標收集已完成");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("強制收集指標失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "強制收集指標失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @PostMapping("/monitoring/health-check")
    @Operation(summary = "執行健康檢查", description = "立即執行系統健康檢查並發送通知")
    public ResponseEntity<Map<String, Object>> executeHealthCheck() {
        try {
            performanceMonitorService.checkAndNotifyHealthStatusChange();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "健康檢查已執行");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("執行健康檢查失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "執行健康檢查失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ==================== 測試功能 ====================

    @PostMapping("/test/create-sample-events")
    @Operation(summary = "創建測試事件", description = "創建一些測試用的系統事件")
    public ResponseEntity<Map<String, Object>> createSampleEvents(
            @Parameter(description = "事件數量") @RequestParam(defaultValue = "10") int count) {
        
        try {
            String[] eventTypes = {"LOGIN_FAILED", "PERFORMANCE_ALERT", "SYSTEM_ERROR", "SECURITY_WARNING"};
            SeverityLevel[] severities = {
            	    SeverityLevel.LOW,
            	    SeverityLevel.MEDIUM,
            	    SeverityLevel.HIGH,
            	    SeverityLevel.CRITICAL
            	};

            
            for (int i = 0; i < count; i++) {
                String eventType = eventTypes[i % eventTypes.length];
                SeverityLevel severity = severities[i % severities.length];
                
                systemEventService.logEvent(
                    "TEST",
                    eventType,
                    severity,
                    "測試事件 #" + (i + 1),
                    "這是一個測試用的系統事件",
                    Map.of("test_id", i + 1, "batch", "admin_test"),
                    null,
                    "127.0.0.1"
                );
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "已創建 " + count + " 個測試事件");
            response.put("count", count);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("創建測試事件失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "創建測試事件失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/test/trigger-alert")
    @Operation(summary = "觸發測試警報", description = "觸發一個測試警報事件")
    public ResponseEntity<Map<String, Object>> triggerTestAlert(
            @Parameter(description = "警報類型") @RequestParam(defaultValue = "PERFORMANCE") String alertType) {
        
        try {
            switch (alertType.toUpperCase()) {
                case "PERFORMANCE":
                    eventPublisher.publishPerformanceAlert("test_metric", 95.0, 80.0, "%");
                    break;
                case "SECURITY":
                    eventPublisher.publishSecurityEvent(
                        "TEST_SECURITY_ALERT",
                        "測試安全警報",
                        "127.0.0.1",
                        null,
                        Map.of("test", true)
                    );
                    break;
                case "SYSTEM":
                    eventPublisher.publishSystemError(
                        "TestComponent",
                        "測試系統錯誤",
                        new RuntimeException("這是一個測試異常"),
                        "127.0.0.1"
                    );
                    break;
                default:
                    throw new IllegalArgumentException("不支援的警報類型: " + alertType);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "測試警報已觸發");
            response.put("alert_type", alertType);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("觸發測試警報失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "觸發測試警報失敗: " + ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/test/send-notification")
    @Operation(summary = "發送測試通知", description = "發送一個測試通知")
    public ResponseEntity<Map<String, Object>> sendTestNotification() {
        try {
            NotificationTarget target = new NotificationTarget();
            target.setType("ALL");
            
            eventPublisher.publishNotification(
                "系統測試通知",
                "這是一個由管理員觸發的測試通知，用於驗證通知系統是否正常運作。",
                target
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "測試通知已發送");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("發送測試通知失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "發送測試通知失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // ==================== 資料匯出功能 ====================

    @GetMapping("/export/events")
    @Operation(summary = "匯出事件資料", description = "匯出指定時間範圍的事件資料")
    public ResponseEntity<Map<String, Object>> exportEvents(
            @Parameter(description = "開始日期 (YYYY-MM-DD)") @RequestParam(required = false) String startDate,
            @Parameter(description = "結束日期 (YYYY-MM-DD)") @RequestParam(required = false) String endDate,
            @Parameter(description = "事件分類") @RequestParam(required = false) String category,
            @Parameter(description = "嚴重程度") @RequestParam(required = false) String severity) {
        
        try {
            // 這裡可以實作事件資料匯出邏輯
            // 例如生成 CSV 或 Excel 檔案
            
            Map<String, Object> exportInfo = new HashMap<>();
            exportInfo.put("start_date", startDate);
            exportInfo.put("end_date", endDate);
            exportInfo.put("category", category);
            exportInfo.put("severity", severity);
            exportInfo.put("export_time", LocalDateTime.now());
            exportInfo.put("format", "CSV");
            
            // 記錄匯出事件
            eventPublisher.publishEvent(
                exportInfo,
                "DATA_EXPORT_REQUESTED",
                "SYSTEM",
                SeverityLevel.LOW,
                null,
                "127.0.0.1"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "事件資料匯出已啟動");
            response.put("export_info", exportInfo);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("匯出事件資料失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "匯出事件資料失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/export/metrics")
    @Operation(summary = "匯出效能指標", description = "匯出指定時間範圍的效能指標")
    public ResponseEntity<Map<String, Object>> exportMetrics(
            @Parameter(description = "開始日期 (YYYY-MM-DD)") @RequestParam(required = false) String startDate,
            @Parameter(description = "結束日期 (YYYY-MM-DD)") @RequestParam(required = false) String endDate,
            @Parameter(description = "指標分類") @RequestParam(required = false) String category) {
        
        try {
            Map<String, Object> exportInfo = new HashMap<>();
            exportInfo.put("start_date", startDate);
            exportInfo.put("end_date", endDate);
            exportInfo.put("category", category);
            exportInfo.put("export_time", LocalDateTime.now());
            exportInfo.put("format", "CSV");
            
            // 記錄匯出事件
            eventPublisher.publishEvent(
                exportInfo,
                "METRIC_EXPORT_REQUESTED",
                "SYSTEM",
                SeverityLevel.LOW,
                null,
                "127.0.0.1"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "效能指標匯出已啟動");
            response.put("export_info", exportInfo);
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("匯出效能指標失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "匯出效能指標失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ==================== 維護模式管理 ====================

    @PostMapping("/maintenance/enable")
    @Operation(summary = "啟用維護模式", description = "啟用系統維護模式")
    public ResponseEntity<Map<String, Object>> enableMaintenanceMode(
            @RequestBody MaintenanceModeRequest request) {
        
        try {
            // 發送維護通知
            systemEventService.sendMaintenanceNotification(
                "系統維護通知",
                request.getMessage(),
                request.getScheduledTime()
            );
            
            // 記錄維護模式啟用事件
            eventPublisher.publishEvent(
                Map.of(
                    "scheduled_time", request.getScheduledTime(),
                    "duration_minutes", request.getDurationMinutes(),
                    "message", request.getMessage()
                ),
                "MAINTENANCE_MODE_ENABLED",
                "SYSTEM",
                SeverityLevel.HIGH,
                null,
                "127.0.0.1"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "維護模式已啟用");
            response.put("scheduled_time", request.getScheduledTime());
            response.put("duration_minutes", request.getDurationMinutes());
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("啟用維護模式失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "啟用維護模式失敗");
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/maintenance/disable")
    @Operation(summary = "停用維護模式", description = "停用系統維護模式")
    public ResponseEntity<Map<String, Object>> disableMaintenanceMode() {
        try {
            // 記錄維護模式停用事件
            eventPublisher.publishEvent(
                null,
                "MAINTENANCE_MODE_DISABLED",
                "SYSTEM",
                SeverityLevel.LOW,
                null,
                "127.0.0.1"
            );
            
            // 發送維護完成通知
            NotificationTarget target = new NotificationTarget();
            target.setType("ALL");
            
            eventPublisher.publishNotification(
                "系統維護完成",
                "系統維護已完成，所有服務已恢復正常運作。感謝您的耐心等候。",
                target
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "維護模式已停用");
            response.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("停用維護模式失敗", ex);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "停用維護模式失敗");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // ==================== 輔助方法 ====================

    private String getSystemUptime() {
        long uptimeMs = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
        long hours = uptimeMs / (1000 * 60 * 60);
        long minutes = (uptimeMs % (1000 * 60 * 60)) / (1000 * 60);
        return hours + "h " + minutes + "m";
    }

    // ==================== 請求 DTO 類別 ====================

    public static class MaintenanceModeRequest {
        private String message;
        private LocalDateTime scheduledTime;
        private Integer durationMinutes;

        // getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public LocalDateTime getScheduledTime() { return scheduledTime; }
        public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }
        
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    }
}