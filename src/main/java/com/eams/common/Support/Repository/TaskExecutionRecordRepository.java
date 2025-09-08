package com.eams.common.Support.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Support.entity.TaskExecutionRecord;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskExecutionRecordRepository extends JpaRepository<TaskExecutionRecord, Long> {
    
    // 根據任務分類查找任務
    List<TaskExecutionRecord> findByTaskCategoryOrderByStartedAtDesc(String taskCategory);
    
    // 根據任務類型查找任務
    List<TaskExecutionRecord> findByTaskTypeOrderByStartedAtDesc(String taskType);
    
    // 根據狀態查找任務
    List<TaskExecutionRecord> findByStatusOrderByStartedAtDesc(String status);
    
    // 查找正在執行的任務
    @Query("SELECT ter FROM TaskExecutionRecord ter WHERE ter.status IN ('PENDING', 'RUNNING') ORDER BY ter.startedAt")
    List<TaskExecutionRecord> findRunningTasks();
    
    // 查找失敗的任務
//    List<TaskExecutionRecord> findByStatusOrderByStartedAtDesc(String status);
    
    // 根據創建者查找任務
    List<TaskExecutionRecord> findByCreatedByOrderByStartedAtDesc(Integer createdBy);
    
    // 根據時間範圍查找任務
    @Query("SELECT ter FROM TaskExecutionRecord ter WHERE ter.startedAt BETWEEN :startTime AND :endTime ORDER BY ter.startedAt DESC")
    List<TaskExecutionRecord> findByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 分頁查詢任務
    Page<TaskExecutionRecord> findByTaskCategoryOrderByStartedAtDesc(String taskCategory, Pageable pageable);
    
    // 查找子任務
    List<TaskExecutionRecord> findByParentTaskIdOrderByStartedAtAsc(Long parentTaskId);
    
    // 查找需要重試的任務
    @Query("SELECT ter FROM TaskExecutionRecord ter WHERE ter.status = 'FAILED' AND ter.retryCount < ter.maxRetryCount AND ter.nextRetryAt <= :currentTime")
    List<TaskExecutionRecord> findTasksForRetry(@Param("currentTime") LocalDateTime currentTime);
    
    // 查找即將過期的任務結果
    @Query("SELECT ter FROM TaskExecutionRecord ter WHERE ter.expiresAt <= :expirationTime AND ter.expiresAt IS NOT NULL")
    List<TaskExecutionRecord> findExpiringTasks(@Param("expirationTime") LocalDateTime expirationTime);
    
    // 統計任務執行統計
    @Query("SELECT ter.status, COUNT(ter) FROM TaskExecutionRecord ter GROUP BY ter.status")
    List<Object[]> countTasksByStatus();
    
    // 統計任務分類統計
    @Query("SELECT ter.taskCategory, COUNT(ter) FROM TaskExecutionRecord ter GROUP BY ter.taskCategory")
    List<Object[]> countTasksByCategory();
    
    // 計算平均執行時間
    @Query("SELECT AVG(ter.durationSeconds) FROM TaskExecutionRecord ter WHERE ter.taskType = :taskType AND ter.status = 'COMPLETED'")
    Double calculateAverageExecutionTime(@Param("taskType") String taskType);
    
 // 按執行模式查詢
    List<TaskExecutionRecord> findByExecutionModeOrderByStartedAtDesc(String executionMode);
    
    // 按進度查詢
    List<TaskExecutionRecord> findByProgressPercentGreaterThanEqualOrderByStartedAtDesc(Integer progressPercent);
    
    // 查找已完成但未發送通知的任務
    @Query("SELECT ter FROM TaskExecutionRecord ter WHERE ter.status = 'COMPLETED' AND ter.notificationSent = false ORDER BY ter.completedAt DESC")
    List<TaskExecutionRecord> findCompletedTasksWithoutNotification();
    
    // 查找包含警告的任務
    List<TaskExecutionRecord> findByWarningCountGreaterThanOrderByStartedAtDesc(Integer warningCount);
    
    // 查找加密的任務結果
    List<TaskExecutionRecord> findByEncryptionAppliedOrderByStartedAtDesc(Boolean encryptionApplied);
}