package com.eams.common.log.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

/**
 * 日誌系統配置類
 */
@Configuration
@EnableAspectJAutoProxy  // 啟用 AOP
@EnableAsync            // 啟用異步處理
@EnableScheduling       // 啟用定時任務
public class LogConfig {
    
    /**
     * 異步執行器 - 專門用於日誌記錄
     * 使用不同的名稱避免與默認配置衝突
     */
    @Bean("logTaskExecutor")
    public Executor logTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);        // 核心線程數
        executor.setMaxPoolSize(5);         // 最大線程數
        executor.setQueueCapacity(100);     // 隊列容量
        executor.setThreadNamePrefix("LogTask-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    /**
     * 定時任務執行器 - 專門用於審計相關的定時任務
     */
    @Bean("approvalTaskScheduler")
    public ThreadPoolTaskScheduler approvalTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(3);           // 線程池大小
        scheduler.setThreadNamePrefix("ApprovalTask-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();
        return scheduler;
    }
    
    /**
     * 審計通知執行器 - 專門用於發送審計通知
     */
    @Bean("notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);        // 通知不需要太多並發
        executor.setMaxPoolSize(3);         
        executor.setQueueCapacity(50);      
        executor.setThreadNamePrefix("NotificationTask-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "taskExecutor")
    @Primary
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("DefaultTask-");
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}