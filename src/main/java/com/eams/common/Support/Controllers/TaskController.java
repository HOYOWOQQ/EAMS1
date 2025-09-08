//package com.eams.common.Support.Controllers;
//
//import com.eams.common.Support.Services.CustomTaskScheduler;
//import com.eams.common.Support.Repository.TaskExecutionRecordRepository;
//import com.eams.common.Support.entity.TaskExecutionRecord;
//import com.eams.common.ApiResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import jakarta.validation.Valid;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.concurrent.CompletableFuture;
//
//@RestController
//@RequestMapping("/api/tasks")
//@CrossOrigin(origins = "*")
//public class TaskController {
//    
//    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
//    
//    @Autowired
//    private CustomTaskScheduler taskScheduler;
//    
//    @Autowired
//    private TaskExecutionRecordRepository taskRepo;
//
//    /**
//     * 手動執行任務
//     * POST /api/tasks/execute
//     */
//    @PostMapping("/execute")
//    public CompletableFuture<ResponseEntity<ApiResponse<String>>> executeTask(
//            @Valid @RequestBody TaskExecutionRequest request) {
//        
//        logger.info("Manual task execution requested: {}", request.getTaskName());
//        
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                Runnable task = createTaskRunnable(request);
//                
//                taskScheduler.scheduleTaskAsync(
//                    request.getTaskCategory(),
//                    request.getTaskType(),
//                    request.getTaskName(),
//                    task
//                );
//                
//                return ResponseEntity.ok(ApiResponse.success("任務已提交執行"));
//                
//            } catch (Exception ex) {
//                logger.error("Error executing task", ex);
//                return ResponseEntity.internalServerError()
//                    .body(ApiResponse.error("任務執行失敗：" + ex.getMessage()));
//            }
//        });
//    }
//
//    /**
//     * 獲取任務執行記錄
//     * GET /api/tasks/records
//     */
//    @GetMapping("/records")
//    public ResponseEntity<ApiResponse<Page<TaskExecutionRecord>>> getTaskRecords(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "startedAt") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDir,
//            @RequestParam(required = false) String status,
//            @RequestParam(required = false) String taskCategory,
//            @RequestParam(required = false) String taskType) {
//        
//        try {
//            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
//                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
//            Pageable pageable = PageRequest.of(page, size, sort);
//            
//            Page<TaskExecutionRecord> records;
//            
//            if (status != null && taskCategory != null) {
//                records = taskRepo.findByStatusAndTaskCategory(status, taskCategory, pageable);
//            } else if (status != null) {
//                records = taskRepo.findByStatus(status, pageable);
//            } else if (taskCategory != null) {
//                records = taskRepo.findByTaskCategory(taskCategory, pageable);
//            } else {
//                records = taskRepo.findAll(pageable);
//            }
//            
//            return ResponseEntity.ok(ApiResponse.success("查詢成功", records));
//            
//        } catch (Exception ex) {
//            logger.error("Error getting task records", ex);
//            return ResponseEntity.internalServerError()
//                .body(ApiResponse.error("查詢失敗：" + ex.getMessage()));
//        }
//    }
//
//    /**
//     * 獲取任務統計信息
//     * GET /api/tasks/statistics
//     */
//    @GetMapping("/statistics")
//    public ResponseEntity<ApiResponse<TaskStatistics>> getTaskStatistics(
//            @RequestParam(required = false) String startDate,
//            @RequestParam(required = false) String endDate) {
//        
//        try {
//            TaskStatistics stats = new TaskStatistics();
//            
//            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
//            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
//            
//            stats.setTodayTotal(taskRepo.countByStartedAtBetween(todayStart, todayEnd));
//            stats.setTodayCompleted(taskRepo.countByStatusAndStartedAtBetween("COMPLETED", todayStart, todayEnd));
//            stats.setTodayFailed(taskRepo.countByStatusAndStartedAtBetween("FAILED", todayStart, todayEnd));
//            stats.setTodayRunning(taskRepo.countByStatus("RUNNING"));
//            
//            LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
//            stats.setWeekTotal(taskRepo.countByStartedAtAfter(weekStart));
//            stats.setWeekCompleted(taskRepo.countByStatusAndStartedAtAfter("COMPLETED", weekStart));
//            stats.setWeekFailed(taskRepo.countByStatusAndStartedAtAfter("FAILED", weekStart));
//            
//            LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
//            stats.setMonthTotal(taskRepo.countByStartedAtAfter(monthStart));
//            stats.setMonthCompleted(taskRepo.countByStatusAndStartedAtAfter("COMPLETED", monthStart));
//            stats.setMonthFailed(taskRepo.countByStatusAndStartedAtAfter("FAILED", monthStart));
//            
//            stats.setTotalTasks(taskRepo.count());
//            stats.setTotalCompleted(taskRepo.countByStatus("COMPLETED"));
//            stats.setTotalFailed(taskRepo.countByStatus("FAILED"));
//            
//            if (stats.getTotalTasks() > 0) {
//                stats.setSuccessRate((double) stats.getTotalCompleted() / stats.getTotalTasks() * 100);
//            }
//            
//            stats.setCategoryStats(getTaskCategoryStats());
//            
//            return ResponseEntity.ok(ApiResponse.success("統計查詢成功", stats));
//            
//        } catch (Exception ex) {
//            logger.error("Error getting task statistics", ex);
//            return ResponseEntity.internalServerError()
//                .body(ApiResponse.error("統計查詢失敗：" + ex.getMessage()));
//        }
//    }
//
//    /**
//     * 取消正在運行的任務
//     * POST /api/tasks/{taskId}/cancel
//     */
//    @PostMapping("/{taskId}/cancel")
//    public ResponseEntity<ApiResponse<String>> cancelTask(@PathVariable Long taskId) {
//        try {
//            TaskExecutionRecord task = taskRepo.findById(taskId).orElse(null);
//            
//            if (task == null) {
//                return ResponseEntity.notFound()
//                    .body(ApiResponse.error("任務不存在"));
//            }
//            
//            if (!"RUNNING".equals(task.getStatus())) {
//                return ResponseEntity.badRequest()
//                    .body(ApiResponse.error("只能取消正在運行的任務"));
//            }
//            
//            task.setStatus("CANCELLED");
//            task.setCompletedAt(LocalDateTime.now());
//            task.setResultSummary("任務已被手動取消");
//            taskRepo.save(task);
//            
//            logger.info("Task {} cancelled manually", taskId);
//            return ResponseEntity.ok(ApiResponse.success("任務已取消"));
//            
//        } catch (Exception ex) {
//            logger.error("Error cancelling task", ex);
//            return ResponseEntity.internalServerError()
//                .body(ApiResponse.error("取消任務失敗：" + ex.getMessage()));
//        }
//    }
//
//    /**
//     * 獲取可用的任務類型
//     * GET /api/tasks/types
//     */
//    @GetMapping("/types")
//    public ResponseEntity<ApiResponse<List<TaskTypeInfo>>> getAvailableTaskTypes() {
//        try {
//            List<TaskTypeInfo> taskTypes = List.of(
//                new TaskTypeInfo("backup", "database_backup", "每日資料庫備份", "每天凌晨2點執行的資料庫備份任務"),
//                new TaskTypeInfo("cleanup", "system_cleanup", "系統清理", "清理臨時檔案、過期記錄等"),
//                new TaskTypeInfo("report", "monthly_performance", "月度效能報告", "生成月度系統效能分析報告"),
//                new TaskTypeInfo("maintenance", "log_rotation", "日誌輪轉", "清理和歸檔系統日誌檔案"),
//                new TaskTypeInfo("sync", "data_sync", "數據同步", "同步外部系統數據"),
//                new TaskTypeInfo("notification", "cleanup_expired", "清理過期通知", "清理超過保留期限的通知記錄")
//            );
//            
//            return ResponseEntity.ok(ApiResponse.success("查詢成功", taskTypes));
//            
//        } catch (Exception ex) {
//            logger.error("Error getting task types", ex);
//            return ResponseEntity.internalServerError()
//                .body(ApiResponse.error("查詢失敗：" + ex.getMessage()));
//        }
//    }
//
//    /**
//     * 重新執行失敗的任務
//     * POST /api/tasks/{taskId}/retry
//     */
//    @PostMapping("/{taskId}/retry")
//    public CompletableFuture<ResponseEntity<ApiResponse<String>>> retryTask(@PathVariable Long taskId) {
//        return CompletableFuture.supplyAsync(() -> {
//            try {
//                TaskExecutionRecord failedTask = taskRepo.findById(taskId).orElse(null);
//                
//                if (failedTask == null) {
//                    return ResponseEntity.notFound()
//                        .body(ApiResponse.error("任務不存在"));
//                }
//                
//                if (!"FAILED".equals(failedTask.getStatus())) {
//                    return ResponseEntity.badRequest()
//                        .body(ApiResponse.error("只能重試失敗的任務"));
//                }
//                
//                Runnable retryTask = createTaskRunnableFromRecord(failedTask);
//                
//                taskScheduler.scheduleTaskAsync(
//                    failedTask.getTaskCategory(),
//                    failedTask.getTaskType(),
//                    failedTask.getTaskName() + " (重試)",
//                    retryTask
//                );
//                
//                return ResponseEntity.ok(ApiResponse.success("任務重試已提交"));
//                
//            } catch (Exception ex) {
//                logger.error("Error retrying task", ex);
//                return ResponseEntity.internalServerError()
//                    .body(ApiResponse.error("任務重試失敗：" + ex.getMessage()));
//            }
//        });
//    }
//
//    // 私有輔助方法
//    private Runnable createTaskRunnable(TaskExecutionRequest request) {
//        return switch (request.getTaskType()) {
//            case "database_backup" -> () -> {
//                logger.info("Executing database backup...");
//                try {
//                    Thread.sleep(5000);
//                    logger.info("Database backup completed");
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException("Backup interrupted", e);
//                }
//            };
//            case "system_cleanup" -> () -> {
//                logger.info("Executing system cleanup...");
//                try {
//                    Thread.sleep(3000);
//                    logger.info("System cleanup completed");
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException("Cleanup interrupted", e);
//                }
//            };
//            case "monthly_performance" -> () -> {
//                logger.info("Generating monthly performance report...");
//                try {
//                    Thread.sleep(10000);
//                    logger.info("Monthly report generated");
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException("Report generation interrupted", e);
//                }
//            };
//            default -> () -> {
//                logger.info("Executing custom task: {}", request.getTaskName());
//                try {
//                    Thread.sleep(2000);
//                    logger.info("Custom task completed: {}", request.getTaskName());
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    throw new RuntimeException("Task interrupted", e);
//                }
//            };
//        };
//    }
//
//    private Runnable createTaskRunnableFromRecord(TaskExecutionRecord record) {
//        return createTaskRunnable(new TaskExecutionRequest(
//            record.getTaskCategory(),
//            record.getTaskType(),
//            record.getTaskName()
//        ));
//    }
//
//    private Map<String, Long> getTaskCategoryStats() {
//        return Map.of(
//            "backup", taskRepo.countByTaskCategory("backup"),
//            "cleanup", taskRepo.countByTaskCategory("cleanup"),
//            "report", taskRepo.countByTaskCategory("report"),
//            "maintenance", taskRepo.countByTaskCategory("maintenance"),
//            "sync", taskRepo.countByTaskCategory("sync"),
//            "notification", taskRepo.countByTaskCategory("notification")
//        );
//    }
//
//    // DTO 類別
//    public static class TaskExecutionRequest {
//        private String taskCategory;
//        private String taskType;
//        private String taskName;
//
//        public TaskExecutionRequest() {}
//
//        public TaskExecutionRequest(String taskCategory, String taskType, String taskName) {
//            this.taskCategory = taskCategory;
//            this.taskType = taskType;
//            this.taskName = taskName;
//        }
//
//        public String getTaskCategory() { return taskCategory; }
//        public void setTaskCategory(String taskCategory) { this.taskCategory = taskCategory; }
//        
//        public String getTaskType() { return taskType; }
//        public void setTaskType(String taskType) { this.taskType = taskType; }
//        
//        public String getTaskName() { return taskName; }
//        public void setTaskName(String taskName) { this.taskName = taskName; }
//    }
//
//    public static class TaskStatistics {
//        private Long todayTotal;
//        private Long todayCompleted;
//        private Long todayFailed;
//        private Long todayRunning;
//        private Long weekTotal;
//        private Long weekCompleted;
//        private Long weekFailed;
//        private Long monthTotal;
//        private Long monthCompleted;
//        private Long monthFailed;
//        private Long totalTasks;
//        private Long totalCompleted;
//        private Long totalFailed;
//        private Double successRate;
//        private Map<String, Long> categoryStats;
//
//        public Long getTodayTotal() { return todayTotal; }
//        public void setTodayTotal(Long todayTotal) { this.todayTotal = todayTotal; }
//        
//        public Long getTodayCompleted() { return todayCompleted; }
//        public void setTodayCompleted(Long todayCompleted) { this.todayCompleted = todayCompleted; }
//        
//        public Long getTodayFailed() { return todayFailed; }
//        public void setTodayFailed(Long todayFailed) { this.todayFailed = todayFailed; }
//        
//        public Long getTodayRunning() { return todayRunning; }
//        public void setTodayRunning(Long todayRunning) { this.todayRunning = todayRunning; }
//        
//        public Long getWeekTotal() { return weekTotal; }
//        public void setWeekTotal(Long weekTotal) { this.weekTotal = weekTotal; }
//        
//        public Long getWeekCompleted() { return weekCompleted; }
//        public void setWeekCompleted(Long weekCompleted) { this.weekCompleted = weekCompleted; }
//        
//        public Long getWeekFailed() { return weekFailed; }
//        public void setWeekFailed(Long weekFailed) { this.weekFailed = weekFailed; }
//        
//        public Long getMonthTotal() { return monthTotal; }
//        public void setMonthTotal(Long monthTotal) { this.monthTotal = monthTotal; }
//        
//        public Long getMonthCompleted() { return monthCompleted; }
//        public void setMonthCompleted(Long monthCompleted) { this.monthCompleted = monthCompleted; }
//        
//        public Long getMonthFailed() { return monthFailed; }
//        public void setMonthFailed(Long monthFailed) { this.monthFailed = monthFailed; }
//        
//        public Long getTotalTasks() { return totalTasks; }
//        public void setTotalTasks(Long totalTasks) { this.totalTasks = totalTasks; }
//        
//        public Long getTotalCompleted() { return totalCompleted; }
//        public void setTotalCompleted(Long totalCompleted) { this.totalCompleted = totalCompleted; }
//        
//        public Long getTotalFailed() { return totalFailed; }
//        public void setTotalFailed(Long totalFailed) { this.totalFailed = totalFailed; }
//        
//        public Double getSuccessRate() { return successRate; }
//        public void setSuccessRate(Double successRate) { this.successRate = successRate; }
//        
//        public Map<String, Long> getCategoryStats() { return categoryStats; }
//        public void setCategoryStats(Map<String, Long> categoryStats) { this.categoryStats = categoryStats; }
//    }
//
//    public static class TaskTypeInfo {
//        private String category;
//        private String type;
//        private String name;
//        private String description;
//
//        public TaskTypeInfo(String category, String type, String name, String description) {
//            this.category = category;
//            this.type = type;
//            this.name = name;
//            this.description = description;
//        }
//
//        public String getCategory() { return category; }
//        public String getType() { return type; }
//        public String getName() { return name; }
//        public String getDescription() { return description; }
//    }
//}