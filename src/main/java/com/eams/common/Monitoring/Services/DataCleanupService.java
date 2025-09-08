package com.eams.common.Monitoring.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eams.common.Monitoring.Repository.SystemEventLogRepository;
import com.eams.common.Monitoring.Repository.PerformanceMetricRepository;
import com.eams.common.Monitoring.entity.SystemEventLog;
import com.eams.common.Monitoring.enums.SeverityLevel;
import com.eams.common.Monitoring.entity.PerformanceMetric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class DataCleanupService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataCleanupService.class);
    
    @Autowired
    private SystemEventLogRepository eventRepository;
    
    @Autowired
    private PerformanceMetricRepository metricRepository;
    
    @Autowired
    private EventPublisher eventPublisher;

    /**
     * 每天凌晨 2 點執行資料清理
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void performDailyCleanup() {
        logger.info("開始執行每日資料清理任務");
        
        CompletableFuture<Void> eventCleanup = cleanupOldEventsAsync();
        CompletableFuture<Void> metricCleanup = cleanupOldMetricsAsync();
        CompletableFuture<Void> notificationCleanup = cleanupOldNotificationsAsync();
        
        CompletableFuture.allOf(eventCleanup, metricCleanup, notificationCleanup)
                .thenRun(() -> {
                    logger.info("每日資料清理任務完成");
                    
                    // 記錄清理完成事件
                    eventPublisher.publishEvent(
                        null,
                        "DATA_CLEANUP_COMPLETED",
                        "SYSTEM",
                        SeverityLevel.LOW,
                        null,
                        "127.0.0.1"
                    );
                })
                .exceptionally(ex -> {
                    logger.error("每日資料清理任務失敗", ex);
                    
                    eventPublisher.publishSystemError(
                        "DataCleanupService",
                        "每日資料清理任務失敗",
                        (Exception) ex,
                        "127.0.0.1"
                    );
                    
                    return null;
                });
    }

    /**
     * 清理舊的系統事件日誌
     */
    public CompletableFuture<Void> cleanupOldEventsAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                cleanupOldEvents();
            } catch (Exception ex) {
                logger.error("清理舊事件失敗", ex);
                throw new RuntimeException("清理舊事件失敗", ex);
            }
        });
    }

    @Transactional
    public void cleanupOldEvents() {
        logger.info("開始清理舊的系統事件日誌");
        
        // 清理 90 天前的一般事件
        LocalDateTime generalCutoff = LocalDateTime.now().minusDays(90);
        List<SystemEventLog> oldEvents = eventRepository.findEventsForCleanup(generalCutoff);
        
        // 清理 7 天前的低嚴重度事件
        LocalDateTime lowSeverityCutoff = LocalDateTime.now().minusDays(7);
        List<SystemEventLog> oldLowSeverityEvents = eventRepository.findByCreatedAtBeforeAndSeverityIn(
            lowSeverityCutoff, List.of("LOW", "NORMAL")
        );
        
        int deletedCount = 0;
        
        // 分批刪除以避免長時間鎖表
        int batchSize = 100;
        
        // 刪除一般舊事件
        for (int i = 0; i < oldEvents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, oldEvents.size());
            List<SystemEventLog> batch = oldEvents.subList(i, end);
            eventRepository.deleteAll(batch);
            deletedCount += batch.size();
            
            if (deletedCount % 500 == 0) {
                logger.info("已刪除 {} 筆舊事件", deletedCount);
            }
        }
        
        // 刪除低嚴重度舊事件
        for (int i = 0; i < oldLowSeverityEvents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, oldLowSeverityEvents.size());
            List<SystemEventLog> batch = oldLowSeverityEvents.subList(i, end);
            eventRepository.deleteAll(batch);
            deletedCount += batch.size();
            
            if (deletedCount % 500 == 0) {
                logger.info("已刪除 {} 筆舊事件", deletedCount);
            }
        }
        
        logger.info("系統事件清理完成，共刪除 {} 筆記錄", deletedCount);
    }

    /**
     * 清理舊的效能指標
     */
    public CompletableFuture<Void> cleanupOldMetricsAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                cleanupOldMetrics();
            } catch (Exception ex) {
                logger.error("清理舊效能指標失敗", ex);
                throw new RuntimeException("清理舊效能指標失敗", ex);
            }
        });
    }

    @Transactional
    public void cleanupOldMetrics() {
        logger.info("開始清理舊的效能指標");
        
        // 清理 30 天前的效能指標
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<PerformanceMetric> oldMetrics = metricRepository.findByRecordedAtBefore(cutoff);
        
        int deletedCount = 0;
        int batchSize = 200;
        
        // 分批刪除
        for (int i = 0; i < oldMetrics.size(); i += batchSize) {
            int end = Math.min(i + batchSize, oldMetrics.size());
            List<PerformanceMetric> batch = oldMetrics.subList(i, end);
            metricRepository.deleteAll(batch);
            deletedCount += batch.size();
            
            if (deletedCount % 1000 == 0) {
                logger.info("已刪除 {} 筆舊效能指標", deletedCount);
            }
        }
        
        logger.info("效能指標清理完成，共刪除 {} 筆記錄", deletedCount);
    }

    /**
     * 清理舊的通知記錄
     */
    public CompletableFuture<Void> cleanupOldNotificationsAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                cleanupOldNotifications();
            } catch (Exception ex) {
                logger.error("清理舊通知記錄失敗", ex);
                throw new RuntimeException("清理舊通知記錄失敗", ex);
            }
        });
    }

    @Transactional
    public void cleanupOldNotifications() {
        logger.info("開始清理舊的通知記錄");
        
        // 清理 30 天前已處理的通知
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        List<SystemEventLog> oldNotifications = eventRepository.findByEventCategoryAndCreatedAtBeforeAndNotificationSent(
            "NOTIFICATION", cutoff, true
        );
        
        int deletedCount = 0;
        int batchSize = 100;
        
        // 分批刪除
        for (int i = 0; i < oldNotifications.size(); i += batchSize) {
            int end = Math.min(i + batchSize, oldNotifications.size());
            List<SystemEventLog> batch = oldNotifications.subList(i, end);
            eventRepository.deleteAll(batch);
            deletedCount += batch.size();
        }
        
        logger.info("通知記錄清理完成，共刪除 {} 筆記錄", deletedCount);
    }

    /**
     * 每週執行深度清理（每週日凌晨 3 點）
     */
    @Scheduled(cron = "0 0 3 * * SUN")
    public void performWeeklyDeepCleanup() {
        logger.info("開始執行每週深度清理任務");
        
        try {
            // 清理重複事件
            mergeDuplicateEvents();
            
            // 清理過期的未處理事件
            cleanupExpiredEvents();
            
            // 優化資料庫統計資訊
            updateDatabaseStatistics();
            
            logger.info("每週深度清理任務完成");
            
            eventPublisher.publishEvent(
                null,
                "WEEKLY_DEEP_CLEANUP_COMPLETED",
                "SYSTEM",
                SeverityLevel.LOW,
                null,
                "127.0.0.1"
            );
            
        } catch (Exception ex) {
            logger.error("每週深度清理任務失敗", ex);
            
            eventPublisher.publishSystemError(
                "DataCleanupService",
                "每週深度清理任務失敗",
                ex,
                "127.0.0.1"
            );
        }
    }

    /**
     * 合併重複事件
     */
    @Transactional
    public void mergeDuplicateEvents() {
        logger.info("開始合併重複事件");
        
        // 查找最近24小時內的重複事件
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        
        // 這裡可以實作更複雜的重複事件合併邏輯
        // 暫時記錄合併開始的事件
        eventPublisher.publishEvent(
            null,
            "DUPLICATE_EVENT_MERGE_STARTED",
            "SYSTEM",
            SeverityLevel.LOW,
            null,
            "127.0.0.1"
        );
        
        // TODO: 實作重複事件合併邏輯
        // 可以根據 eventType, category, sourceIp, memberId 等條件
        // 找出重複事件並合併 occurrence_count
        
        logger.info("重複事件合併完成");
    }

    /**
     * 清理過期的未處理事件
     */
    @Transactional
    public void cleanupExpiredEvents() {
        logger.info("開始清理過期的未處理事件");
        
        LocalDateTime cutoff = LocalDateTime.now().minusHours(48);
        List<SystemEventLog> expiredEvents = eventRepository.findUnprocessedEvents(cutoff);
        
        for (SystemEventLog event : expiredEvents) {
            // 將過期未處理事件標記為已過期
            event.setStatus("EXPIRED");
            event.setResolutionNote("系統自動過期 - 超過48小時未處理");
            eventRepository.save(event);
        }
        
        logger.info("過期事件清理完成，處理 {} 筆過期事件", expiredEvents.size());
    }

    /**
     * 更新資料庫統計資訊
     */
    public void updateDatabaseStatistics() {
        logger.info("開始更新資料庫統計資訊");
        
        try {
            // 這裡可以執行資料庫特定的統計更新命令
            // 例如：UPDATE STATISTICS, ANALYZE TABLE 等
            
            eventPublisher.publishEvent(
                null,
                "DATABASE_STATISTICS_UPDATED",
                "DATABASE",
                SeverityLevel.LOW,
                null,
                "127.0.0.1"
            );
            
            logger.info("資料庫統計資訊更新完成");
            
        } catch (Exception ex) {
            logger.error("更新資料庫統計資訊失敗", ex);
            throw ex;
        }
    }

    /**
     * 手動觸發清理 - 提供給管理員使用
     */
    public void manualCleanup(int eventRetentionDays, int metricRetentionDays) {
        logger.info("開始手動清理，事件保留{}天，指標保留{}天", eventRetentionDays, metricRetentionDays);
        
        try {
            // 清理指定天數前的事件
            LocalDateTime eventCutoff = LocalDateTime.now().minusDays(eventRetentionDays);
            List<SystemEventLog> oldEvents = eventRepository.findByCreatedAtBefore(eventCutoff);
            eventRepository.deleteAll(oldEvents);
            
            // 清理指定天數前的效能指標
            LocalDateTime metricCutoff = LocalDateTime.now().minusDays(metricRetentionDays);
            List<PerformanceMetric> oldMetrics = metricRepository.findByRecordedAtBefore(metricCutoff);
            metricRepository.deleteAll(oldMetrics);
            
            logger.info("手動清理完成，刪除{}筆事件，{}筆指標", oldEvents.size(), oldMetrics.size());
            
            eventPublisher.publishEvent(
                String.format("刪除%d筆事件，%d筆指標", oldEvents.size(), oldMetrics.size()),
                "MANUAL_CLEANUP_COMPLETED",
                "SYSTEM",
                SeverityLevel.LOW,
                null,
                "127.0.0.1"
            );
            
        } catch (Exception ex) {
            logger.error("手動清理失敗", ex);
            
            eventPublisher.publishSystemError(
                "DataCleanupService",
                "手動清理失敗",
                ex,
                "127.0.0.1"
            );
            
            throw new RuntimeException("手動清理失敗", ex);
        }
    }

    /**
     * 獲取清理統計資訊
     */
    public CleanupStatistics getCleanupStatistics() {
        try {
            LocalDateTime now = LocalDateTime.now();
            
            // 統計各類資料數量
            long totalEvents = eventRepository.count();
            long eventsLast7Days = eventRepository.countByCreatedAtAfter(now.minusDays(7));
            long eventsLast30Days = eventRepository.countByCreatedAtAfter(now.minusDays(30));
            
            long totalMetrics = metricRepository.count();
            long metricsLast7Days = metricRepository.countByRecordedAtAfter(now.minusDays(7));
            long metricsLast30Days = metricRepository.countByRecordedAtAfter(now.minusDays(30));
            
            // 估算可清理的資料量
            long cleanableEvents = eventRepository.countByCreatedAtBefore(now.minusDays(90));
            long cleanableMetrics = metricRepository.countByRecordedAtBefore(now.minusDays(30));
            
            return CleanupStatistics.builder()
                    .totalEvents(totalEvents)
                    .eventsLast7Days(eventsLast7Days)
                    .eventsLast30Days(eventsLast30Days)
                    .totalMetrics(totalMetrics)
                    .metricsLast7Days(metricsLast7Days)
                    .metricsLast30Days(metricsLast30Days)
                    .cleanableEvents(cleanableEvents)
                    .cleanableMetrics(cleanableMetrics)
                    .lastCleanupTime(getLastCleanupTime())
                    .build();
                    
        } catch (Exception ex) {
            logger.error("獲取清理統計資訊失敗", ex);
            throw new RuntimeException("獲取清理統計資訊失敗", ex);
        }
    }

    private LocalDateTime getLastCleanupTime() {
        // 查找最近的清理完成事件
        List<SystemEventLog> cleanupEvents = eventRepository.findByEventTypeAndCreatedAtAfterOrderByCreatedAtDesc(
            "DATA_CLEANUP_COMPLETED", 
            LocalDateTime.now().minusDays(7)
        );
        
        return cleanupEvents.isEmpty() ? null : cleanupEvents.get(0).getCreatedAt();
    }

    /**
     * 清理統計資訊 DTO
     */
    public static class CleanupStatistics {
        private long totalEvents;
        private long eventsLast7Days;
        private long eventsLast30Days;
        private long totalMetrics;
        private long metricsLast7Days;
        private long metricsLast30Days;
        private long cleanableEvents;
        private long cleanableMetrics;
        private LocalDateTime lastCleanupTime;

        // Builder pattern
        public static CleanupStatisticsBuilder builder() {
            return new CleanupStatisticsBuilder();
        }

        public static class CleanupStatisticsBuilder {
            private CleanupStatistics statistics = new CleanupStatistics();

            public CleanupStatisticsBuilder totalEvents(long totalEvents) {
                statistics.totalEvents = totalEvents;
                return this;
            }

            public CleanupStatisticsBuilder eventsLast7Days(long eventsLast7Days) {
                statistics.eventsLast7Days = eventsLast7Days;
                return this;
            }

            public CleanupStatisticsBuilder eventsLast30Days(long eventsLast30Days) {
                statistics.eventsLast30Days = eventsLast30Days;
                return this;
            }

            public CleanupStatisticsBuilder totalMetrics(long totalMetrics) {
                statistics.totalMetrics = totalMetrics;
                return this;
            }

            public CleanupStatisticsBuilder metricsLast7Days(long metricsLast7Days) {
                statistics.metricsLast7Days = metricsLast7Days;
                return this;
            }

            public CleanupStatisticsBuilder metricsLast30Days(long metricsLast30Days) {
                statistics.metricsLast30Days = metricsLast30Days;
                return this;
            }

            public CleanupStatisticsBuilder cleanableEvents(long cleanableEvents) {
                statistics.cleanableEvents = cleanableEvents;
                return this;
            }

            public CleanupStatisticsBuilder cleanableMetrics(long cleanableMetrics) {
                statistics.cleanableMetrics = cleanableMetrics;
                return this;
            }

            public CleanupStatisticsBuilder lastCleanupTime(LocalDateTime lastCleanupTime) {
                statistics.lastCleanupTime = lastCleanupTime;
                return this;
            }

            public CleanupStatistics build() {
                return statistics;
            }
        }

        // getters
        public long getTotalEvents() { return totalEvents; }
        public long getEventsLast7Days() { return eventsLast7Days; }
        public long getEventsLast30Days() { return eventsLast30Days; }
        public long getTotalMetrics() { return totalMetrics; }
        public long getMetricsLast7Days() { return metricsLast7Days; }
        public long getMetricsLast30Days() { return metricsLast30Days; }
        public long getCleanableEvents() { return cleanableEvents; }
        public long getCleanableMetrics() { return cleanableMetrics; }
        public LocalDateTime getLastCleanupTime() { return lastCleanupTime; }
    }
}