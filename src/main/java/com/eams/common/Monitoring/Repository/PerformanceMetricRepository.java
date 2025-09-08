package com.eams.common.Monitoring.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.Monitoring.entity.PerformanceMetric;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PerformanceMetricRepository extends JpaRepository<PerformanceMetric, Long> {
    
    // 根據分類查找指標
    List<PerformanceMetric> findByMetricCategoryOrderByRecordedAtDesc(String metricCategory);
    
    // 根據分類和名稱查找指標
    List<PerformanceMetric> findByMetricCategoryAndMetricNameOrderByRecordedAtDesc(String metricCategory, String metricName);
    
    // 根據時間範圍查找指標
    @Query("SELECT pm FROM PerformanceMetric pm WHERE pm.metricCategory = :category AND pm.recordedAt BETWEEN :startTime AND :endTime ORDER BY pm.recordedAt")
    List<PerformanceMetric> findByCategoryAndDateRange(@Param("category") String category, 
                                                       @Param("startTime") LocalDateTime startTime, 
                                                       @Param("endTime") LocalDateTime endTime);
    
    // 查找最近的指標
    @Query("SELECT pm FROM PerformanceMetric pm WHERE pm.recordedAt >= :fromTime ORDER BY pm.recordedAt DESC")
    List<PerformanceMetric> findRecentMetrics(@Param("fromTime") LocalDateTime fromTime);
    
    // 根據狀態查找指標
    List<PerformanceMetric> findByStatusOrderByRecordedAtDesc(String status);
    
    // 查找異常指標（WARNING 或 CRITICAL）
    @Query("SELECT pm FROM PerformanceMetric pm WHERE pm.status IN ('WARNING', 'CRITICAL') ORDER BY pm.recordedAt DESC")
    List<PerformanceMetric> findAbnormalMetrics();
    
    // 根據聚合類型查找指標
    List<PerformanceMetric> findByAggregationTypeOrderByRecordedAtDesc(String aggregationType);
    
    // 統計指標平均值
    @Query("SELECT AVG(pm.metricValue) FROM PerformanceMetric pm WHERE pm.metricCategory = :category AND pm.metricName = :name AND pm.recordedAt BETWEEN :startTime AND :endTime")
    Double calculateAverageValue(@Param("category") String category, 
                                @Param("name") String name, 
                                @Param("startTime") LocalDateTime startTime, 
                                @Param("endTime") LocalDateTime endTime);
    
    // 統計指標最大值
    @Query("SELECT MAX(pm.metricValue) FROM PerformanceMetric pm WHERE pm.metricCategory = :category AND pm.metricName = :name AND pm.recordedAt BETWEEN :startTime AND :endTime")
    Double calculateMaxValue(@Param("category") String category, 
                            @Param("name") String name, 
                            @Param("startTime") LocalDateTime startTime, 
                            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 新增：查詢最近 N 分鐘的指標 (如果您還需要按分鐘查詢)
     */
    @Query("SELECT pm FROM PerformanceMetric pm WHERE pm.recordedAt >= :fromTime ORDER BY pm.recordedAt DESC")
    List<PerformanceMetric> findMetricsFromMinutesAgo(@Param("fromTime") LocalDateTime fromTime);
    
    
    /**
     * 新增：清理舊資料的方法修正
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM PerformanceMetric pm WHERE pm.recordedAt < :cutoffTime")
    void deleteOldMetrics(@Param("cutoffTime") LocalDateTime cutoffTime);
    
 // ===== DataCleanupService 需要的額外方法 =====

 // 根據記錄時間查找舊指標
 List<PerformanceMetric> findByRecordedAtBefore(LocalDateTime before);

 // 統計最近創建的指標數量
 @Query("SELECT COUNT(pm) FROM PerformanceMetric pm WHERE pm.recordedAt > :after")
 Long countByRecordedAtAfter(@Param("after") LocalDateTime after);

 // 根據記錄時間範圍查找指標（用於統計）
 @Query("SELECT pm FROM PerformanceMetric pm WHERE pm.recordedAt BETWEEN :startTime AND :endTime")
 List<PerformanceMetric> findByRecordedAtBetween(
     @Param("startTime") LocalDateTime startTime, 
     @Param("endTime") LocalDateTime endTime);
 
 
//統計指定時間前的指標數量
@Query("SELECT COUNT(pm) FROM PerformanceMetric pm WHERE pm.recordedAt < :before")
Long countByRecordedAtBefore(@Param("before") LocalDateTime before);
}