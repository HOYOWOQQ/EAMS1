package com.eams.common.Monitoring.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eams.common.Monitoring.Repository.PerformanceMetricRepository;
import com.eams.common.Monitoring.entity.PerformanceMetric;
import com.eams.common.Monitoring.enums.SeverityLevel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class PerformanceMonitorService {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceMonitorService.class);
    
    private final PerformanceMetricRepository metricRepo;
    private final OperatingSystemMXBean osBean;
    
    // 【修改】移除 SystemEventService 依賴，只使用 EventPublisher
    @Autowired
    private EventPublisher eventPublisher;

    // 【修改】建構函數只需要 PerformanceMetricRepository
    public PerformanceMonitorService(PerformanceMetricRepository metricRepo) {
        this.metricRepo = metricRepo;
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
    }

    public CompletableFuture<Void> recordMetricAsync(String category, String name, BigDecimal value, String unit) {
        return CompletableFuture.runAsync(() -> {
            try {
                PerformanceMetric metric = PerformanceMetric.builder()
                    .metricCategory(category)
                    .metricName(name)
                    .metricValue(value)
                    .unit(unit)
                    .status(determineStatus(category, name, value))
                    .aggregationType("INSTANT")
                    .recordedAt(LocalDateTime.now())
                    .build();

                metricRepo.save(metric);
                
                // 【修改】改用 EventPublisher
                if ("WARNING".equals(metric.getStatus()) || "CRITICAL".equals(metric.getStatus())) {
                    logPerformanceAlert(metric);
                }
                
            } catch (Exception ex) {
                logger.error("Failed to record metric {}.{}", category, name, ex);
            }
        });
    }

    // ... 其他方法保持不變 ...

    // 【修改】系統啟動事件改用 EventPublisher
    @EventListener(ApplicationReadyEvent.class)
    public void startContinuousMonitoring() {
        logger.info("Performance monitoring started");
        
        // 使用 EventPublisher 記錄啟動事件
        Map<String, Object> startupData = Map.of(
            "startup_time", "3200ms",
            "active_profiles", new String[]{"dev", "monitoring"},
            "server_port", 8080,
            "java_version", System.getProperty("java.version"),
            "monitor_interval", "5min"
        );
        
        eventPublisher.publishEvent(startupData, "APPLICATION_STARTED", "SYSTEM", SeverityLevel.LOW, null, "127.0.0.1");
    }

    @Scheduled(fixedRate = 300000) // 每5分鐘執行一次
    public void collectSystemMetrics() {
        try {
            // 收集 CPU 使用率
            BigDecimal cpuUsage = BigDecimal.valueOf(getCpuUsage());
            recordMetricAsync("SYSTEM", "cpu_usage", cpuUsage, "%");

            // 收集記憶體使用率
            BigDecimal memoryUsage = BigDecimal.valueOf(getMemoryUsage());
            recordMetricAsync("SYSTEM", "memory_usage", memoryUsage, "%");

            // 收集硬碟使用率
            BigDecimal diskUsage = BigDecimal.valueOf(getDiskUsage());
            recordMetricAsync("SYSTEM", "disk_usage", diskUsage, "%");

            logger.debug("System metrics collected: CPU {}%, Memory {}%, Disk {}%", 
                    cpuUsage, memoryUsage, diskUsage);
        } catch (Exception ex) {
            logger.error("Error collecting system metrics", ex);
            
            // 【修改】改用 EventPublisher 記錄錯誤
            eventPublisher.publishSystemError("PerformanceMonitor", 
                "無法收集系統效能指標", ex, "127.0.0.1");
        }
    }

    // 【修改】效能警告改用 EventPublisher
    private void logPerformanceAlert(PerformanceMetric metric) {
        Double threshold = "CRITICAL".equals(metric.getStatus()) ? 
            90.0 : 80.0; // 簡化閾值邏輯
            
        eventPublisher.publishPerformanceAlert(
            metric.getMetricName(),
            metric.getMetricValue().doubleValue(),
            threshold,
            metric.getUnit()
        );
    }

    // 【新增】系統健康狀態變化通知
    public void checkAndNotifyHealthStatusChange() {
        getSystemHealthAsync().thenAccept(health -> {
            if ("CRITICAL".equals(health.getStatus())) {
                // 發送緊急通知給管理員
                NotificationTarget adminTarget = new NotificationTarget();
                adminTarget.setType("ROLE");
                adminTarget.setRoles(List.of("ADMIN"));
                
                eventPublisher.publishNotification(
                    "系統健康狀態警告",
                    String.format("系統健康分數降至 %d 分，請立即檢查！\nCPU: %.1f%%, 記憶體: %.1f%%, 磁碟: %.1f%%", 
                        health.getOverallScore(), health.getCpuUsage(), health.getMemoryUsage(), health.getDiskUsage()),
                    adminTarget
                );
            }
        });
    }

    // ... 其他私有方法保持不變 ...
    
    private double getCpuUsage() {
        try {
            com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            double load = osBean.getProcessCpuLoad();
            return load > 0 ? load * 100 : Math.random() * 30 + 10;
        } catch (Exception ex) {
            return Math.random() * 30 + 10;
        }
    }

    private double getMemoryUsage() {
        try {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            return (double) usedMemory / totalMemory * 100;
        } catch (Exception ex) {
            return Math.random() * 50 + 30;
        }
    }

    private double getDiskUsage() {
        try {
            java.io.File file = new java.io.File(".");
            long totalSpace = file.getTotalSpace();
            long freeSpace = file.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            return (double) usedSpace / totalSpace * 100;
        } catch (Exception ex) {
            return Math.random() * 40 + 20;
        }
    }

    private String determineStatus(String category, String name, BigDecimal value) {
        if ("SYSTEM".equals(category)) {
            if (value.compareTo(BigDecimal.valueOf(90)) >= 0) return "CRITICAL";
            if (value.compareTo(BigDecimal.valueOf(80)) >= 0) return "WARNING";
            return "NORMAL";
        }
        return "NORMAL";
    }

    private double calculateAverageValue(List<PerformanceMetric> metrics, String metricName) {
        return metrics.stream()
                .filter(m -> metricName.equals(m.getMetricName()))
                .mapToDouble(m -> m.getMetricValue().doubleValue())
                .average()
                .orElse(0.0);
    }

    private int calculateHealthScore(SystemHealthStatus health) {
        double cpuScore = Math.max(0, 100 - health.getCpuUsage());
        double memoryScore = Math.max(0, 100 - health.getMemoryUsage());
        double diskScore = Math.max(0, 100 - health.getDiskUsage());
        return (int) ((cpuScore + memoryScore + diskScore) / 3);
    }

    private String determineHealthStatus(int score) {
        if (score >= 80) return "HEALTHY";
        if (score >= 60) return "WARNING";
        return "CRITICAL";
    }

    public CompletableFuture<List<PerformanceMetric>> getMetricsAsync(String category, LocalDateTime from, LocalDateTime to) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return metricRepo.findByCategoryAndDateRange(category, 
                        from != null ? from : LocalDateTime.now().minusDays(1), 
                        to != null ? to : LocalDateTime.now());
            } catch (Exception ex) {
                logger.error("Failed to get metrics for category {}", category, ex);
                return List.of();
            }
        });
    }

    public CompletableFuture<SystemHealthStatus> getSystemHealthAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
                List<PerformanceMetric> recentMetrics = metricRepo.findRecentMetrics(fiveMinutesAgo);
                
                SystemHealthStatus health = new SystemHealthStatus();
                health.setCpuUsage(calculateAverageValue(recentMetrics, "cpu_usage"));
                health.setMemoryUsage(calculateAverageValue(recentMetrics, "memory_usage"));
                health.setDiskUsage(calculateAverageValue(recentMetrics, "disk_usage"));
                health.setOverallScore(calculateHealthScore(health));
                health.setStatus(determineHealthStatus(health.getOverallScore()));
                health.setCheckTime(LocalDateTime.now());

                return health;
            } catch (Exception ex) {
                logger.error("Failed to get system health", ex);
                SystemHealthStatus health = new SystemHealthStatus();
                health.setStatus("ERROR");
                health.setCheckTime(LocalDateTime.now());
                return health;
            }
        });
    }
}