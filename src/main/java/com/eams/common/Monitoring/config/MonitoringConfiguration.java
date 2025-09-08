package com.eams.common.Monitoring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class MonitoringConfiguration {

    /**
     * 異步任務執行器配置
     * 用於處理效能指標記錄和事件發布的異步操作
     */
    @Bean(name = "monitoringTaskExecutor")
    public Executor monitoringTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);           // 核心執行緒數
        executor.setMaxPoolSize(15);           // 最大執行緒數
        executor.setQueueCapacity(100);        // 佇列容量
        executor.setThreadNamePrefix("Monitoring-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * JSON 序列化配置
     * 處理 LocalDateTime 等 Java 8 時間類型
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }

//    /**
//     * Swagger/OpenAPI 配置
//     * 提供 API 文檔
//     */
//    @Bean
//    public OpenAPI monitoringOpenAPI() {
//        return new OpenAPI()
//                .info(new Info()
//                        .title("EAMS 系統監控 API")
//                        .description("智慧校務系統監控與事件管理 API 文檔")
//                        .version("1.0.0")
//                        .contact(new Contact()
//                                .name("系統開發團隊")
//                                .email("dev@eams.edu.tw")));
//    }

    /**
     * 監控相關常數配置
     */
    public static class MonitoringConstants {
        
        // 效能指標相關常數
        public static final String METRIC_CATEGORY_SYSTEM = "SYSTEM";
        public static final String METRIC_CATEGORY_DATABASE = "DATABASE";
        public static final String METRIC_CATEGORY_APPLICATION = "APPLICATION";
        public static final String METRIC_CATEGORY_NETWORK = "NETWORK";
        
        // 事件分類常數
        public static final String EVENT_CATEGORY_SYSTEM = "SYSTEM";
        public static final String EVENT_CATEGORY_SECURITY = "SECURITY";
        public static final String EVENT_CATEGORY_PERFORMANCE = "PERFORMANCE";
        public static final String EVENT_CATEGORY_ERROR = "ERROR";
        public static final String EVENT_CATEGORY_NOTIFICATION = "NOTIFICATION";
        public static final String EVENT_CATEGORY_DATABASE = "DATABASE";
        public static final String EVENT_CATEGORY_FILE = "FILE";
        
        // 嚴重程度常數
        public static final String SEVERITY_LOW = "LOW";
        public static final String SEVERITY_NORMAL = "NORMAL";
        public static final String SEVERITY_MEDIUM = "MEDIUM";
        public static final String SEVERITY_HIGH = "HIGH";
        public static final String SEVERITY_CRITICAL = "CRITICAL";
        
        // 事件狀態常數
        public static final String EVENT_STATUS_NEW = "NEW";
        public static final String EVENT_STATUS_ACKNOWLEDGED = "ACKNOWLEDGED";
        public static final String EVENT_STATUS_IN_PROGRESS = "IN_PROGRESS";
        public static final String EVENT_STATUS_RESOLVED = "RESOLVED";
        public static final String EVENT_STATUS_CLOSED = "CLOSED";
        
        // 通知目標類型常數
        public static final String NOTIFICATION_TARGET_ALL = "ALL";
        public static final String NOTIFICATION_TARGET_ROLE = "ROLE";
        public static final String NOTIFICATION_TARGET_INDIVIDUAL = "INDIVIDUAL";
        
        // 閾值設定
        public static final double CPU_WARNING_THRESHOLD = 80.0;
        public static final double CPU_CRITICAL_THRESHOLD = 90.0;
        public static final double MEMORY_WARNING_THRESHOLD = 80.0;
        public static final double MEMORY_CRITICAL_THRESHOLD = 90.0;
        public static final double DISK_WARNING_THRESHOLD = 85.0;
        public static final double DISK_CRITICAL_THRESHOLD = 95.0;
        
        // 監控間隔設定（毫秒）
        public static final long METRIC_COLLECTION_INTERVAL = 300000; // 5分鐘
        public static final long HEALTH_CHECK_INTERVAL = 60000;       // 1分鐘
        public static final long EVENT_CLEANUP_INTERVAL = 86400000;   // 24小時
        
        // 資料保留設定（天數）
        public static final int METRIC_RETENTION_DAYS = 30;
        public static final int EVENT_RETENTION_DAYS = 90;
        public static final int LOW_SEVERITY_EVENT_RETENTION_DAYS = 7;
    }

    /**
     * 監控配置屬性
     */
    public static class MonitoringProperties {
        private boolean enableMetricCollection = true;
        private boolean enableEventLogging = true;
        private boolean enableNotifications = true;
        private boolean enableAutoCleanup = true;
        private int maxEventRetentionDays = 90;
        private int maxMetricRetentionDays = 30;
        private double cpuThresholdWarning = 80.0;
        private double cpuThresholdCritical = 90.0;
        private double memoryThresholdWarning = 80.0;
        private double memoryThresholdCritical = 90.0;
        private double diskThresholdWarning = 85.0;
        private double diskThresholdCritical = 95.0;

        // getters and setters
        public boolean isEnableMetricCollection() { return enableMetricCollection; }
        public void setEnableMetricCollection(boolean enableMetricCollection) { this.enableMetricCollection = enableMetricCollection; }
        
        public boolean isEnableEventLogging() { return enableEventLogging; }
        public void setEnableEventLogging(boolean enableEventLogging) { this.enableEventLogging = enableEventLogging; }
        
        public boolean isEnableNotifications() { return enableNotifications; }
        public void setEnableNotifications(boolean enableNotifications) { this.enableNotifications = enableNotifications; }
        
        public boolean isEnableAutoCleanup() { return enableAutoCleanup; }
        public void setEnableAutoCleanup(boolean enableAutoCleanup) { this.enableAutoCleanup = enableAutoCleanup; }
        
        public int getMaxEventRetentionDays() { return maxEventRetentionDays; }
        public void setMaxEventRetentionDays(int maxEventRetentionDays) { this.maxEventRetentionDays = maxEventRetentionDays; }
        
        public int getMaxMetricRetentionDays() { return maxMetricRetentionDays; }
        public void setMaxMetricRetentionDays(int maxMetricRetentionDays) { this.maxMetricRetentionDays = maxMetricRetentionDays; }
        
        public double getCpuThresholdWarning() { return cpuThresholdWarning; }
        public void setCpuThresholdWarning(double cpuThresholdWarning) { this.cpuThresholdWarning = cpuThresholdWarning; }
        
        public double getCpuThresholdCritical() { return cpuThresholdCritical; }
        public void setCpuThresholdCritical(double cpuThresholdCritical) { this.cpuThresholdCritical = cpuThresholdCritical; }
        
        public double getMemoryThresholdWarning() { return memoryThresholdWarning; }
        public void setMemoryThresholdWarning(double memoryThresholdWarning) { this.memoryThresholdWarning = memoryThresholdWarning; }
        
        public double getMemoryThresholdCritical() { return memoryThresholdCritical; }
        public void setMemoryThresholdCritical(double memoryThresholdCritical) { this.memoryThresholdCritical = memoryThresholdCritical; }
        
        public double getDiskThresholdWarning() { return diskThresholdWarning; }
        public void setDiskThresholdWarning(double diskThresholdWarning) { this.diskThresholdWarning = diskThresholdWarning; }
        
        public double getDiskThresholdCritical() { return diskThresholdCritical; }
        public void setDiskThresholdCritical(double diskThresholdCritical) { this.diskThresholdCritical = diskThresholdCritical; }
    }
}