package com.eams.common.Support.Services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.eams.common.Support.Repository.TaskExecutionRecordRepository;
import com.eams.common.Support.entity.TaskExecutionRecord;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class CustomTaskScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomTaskScheduler.class);
    
    private final TaskExecutionRecordRepository taskRepo;
    private final ScheduledExecutorService executorService;

    public CustomTaskScheduler(TaskExecutionRecordRepository taskRepo) {
        this.taskRepo = taskRepo;
        this.executorService = Executors.newScheduledThreadPool(5);
    }

    @Async
    public CompletableFuture<Void> scheduleTaskAsync(String taskCategory, String taskType, String taskName, Runnable task) {
        return CompletableFuture.runAsync(() -> {
            TaskExecutionRecord taskRecord = new TaskExecutionRecord();
            taskRecord.setTaskCategory(taskCategory);
            taskRecord.setTaskType(taskType);
            taskRecord.setTaskName(taskName);
            taskRecord.setExecutionMode("SCHEDULED");
            taskRecord.setStatus("RUNNING");
            taskRecord.setStartedAt(LocalDateTime.now());
            taskRecord.setProgressPercent(0);

            try {
                taskRepo.save(taskRecord);
                
                long startTime = System.currentTimeMillis();
                task.run();
                long duration = (System.currentTimeMillis() - startTime) / 1000;
                
                taskRecord.setStatus("COMPLETED");
                taskRecord.setCompletedAt(LocalDateTime.now());
                taskRecord.setDurationSeconds((int) duration);
                taskRecord.setProgressPercent(100);
                taskRecord.setResultSummary("任務執行成功");
                
                taskRepo.save(taskRecord);
                logger.info("Task {} completed successfully in {} seconds", taskName, duration);
                
            } catch (Exception ex) {
                taskRecord.setStatus("FAILED");
                taskRecord.setCompletedAt(LocalDateTime.now());
                taskRecord.setErrorMessage(ex.getMessage());
                taskRecord.setDurationSeconds((int) ((System.currentTimeMillis() - taskRecord.getStartedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()) / 1000));
                
                taskRepo.save(taskRecord);
                logger.error("Task {} failed", taskName, ex);
            }
        });
    }

    // 每日備份任務 - 每天凌晨2點執行
    @Scheduled(cron = "0 0 2 * * ?")
    public void performDailyBackup() {
        scheduleTaskAsync("backup", "database_backup", "每日資料庫備份", () -> {
            logger.info("Starting database backup...");
            // 實際的備份邏輯
            try {
                Thread.sleep(5000); // 模擬備份過程
                logger.info("Database backup completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Backup interrupted", e);
            }
        });
    }

    // 系統清理任務 - 每週日凌晨3點執行
    @Scheduled(cron = "0 0 3 * * SUN")
    public void performSystemCleanup() {
        scheduleTaskAsync("cleanup", "system_cleanup", "系統清理", () -> {
            logger.info("Starting system cleanup...");
            // 清理臨時檔案、過期記錄等
            try {
                Thread.sleep(3000); // 模擬清理過程
                logger.info("System cleanup completed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Cleanup interrupted", e);
            }
        });
    }

    // 效能報告生成 - 每月1號執行
    @Scheduled(cron = "0 0 1 1 * ?")
    public void generateMonthlyReport() {
        scheduleTaskAsync("report", "monthly_performance", "月度效能報告", () -> {
            logger.info("Generating monthly performance report...");
            // 生成報告邏輯
            try {
                Thread.sleep(10000); // 模擬報告生成
                logger.info("Monthly report generated");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Report generation interrupted", e);
            }
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Task Scheduler initialized and ready");
    }
}