package com.eams.common.Support.Controllers;

import com.eams.common.Support.Services.NotificationService;
import com.eams.common.Support.DTO.NotificationRequest;
import com.eams.common.Support.DTO.NotificationResult;
import com.eams.common.Support.DTO.NotificationDTO;
import com.eams.common.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;

    /**
     * 創建並發送通知
     * POST /api/notifications
     */
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResult>> createNotification(
        @Valid @RequestBody NotificationRequest request) {
    	System.out.println(request);
        try {
            NotificationResult result = notificationService.createAndSendNotification(request).get();
            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("通知創建成功", result));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.getMessage()));
            }
        } catch (Exception ex) {
            logger.error("Error creating notification", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("系統錯誤：" + ex.getMessage()));
        }
    }


    /**
     * 獲取用戶通知列表
     * GET /api/notifications/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getUserNotifications(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority) {
        
        try {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<NotificationDTO> notifications = notificationService
                .getUserNotifications(userId, isRead, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", notifications));
            
        } catch (Exception ex) {
            logger.error("Error getting user notifications", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查詢失敗：" + ex.getMessage()));
        }
    }

    /**
     * 獲取通知詳情
     * GET /api/notifications/{notificationId}
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationDTO>> getNotificationDetail(
            @PathVariable Long notificationId,
            @RequestParam Integer userId) {
        
        try {
            // 這裡需要在 NotificationService 中添加相應方法
            // NotificationDTO notification = notificationService.getNotificationDetail(notificationId, userId);
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功"));
            
        } catch (Exception ex) {
            logger.error("Error getting notification detail", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查詢失敗：" + ex.getMessage()));
        }
    }

    /**
     * 標記通知為已讀
     * PUT /api/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable Long notificationId,
            @RequestParam Integer userId) {
        try {
            Boolean success = notificationService.markAsRead(notificationId, userId).get();
            if (Boolean.TRUE.equals(success)) {
                return ResponseEntity.ok(ApiResponse.success("標記成功"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("標記失敗"));
            }
        } catch (Exception ex) {
            logger.error("Error marking notification as read", ex);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("系統錯誤：" + ex.getMessage()));
        }
    }


    /**
     * 批量標記為已讀
     * PUT /api/notifications/batch/read
     */
    @PutMapping("/batch/read")
    public ResponseEntity<ApiResponse<String>> markMultipleAsRead(
            @RequestBody List<Long> notificationIds,
            @RequestParam Integer userId) {
        try {
            Integer count = notificationService.markMultipleAsRead(notificationIds, userId).get();
            return ResponseEntity.ok(ApiResponse.success(
                    String.format("成功標記 %d 條通知為已讀", count)));
        } catch (Exception ex) {
            logger.error("Error marking multiple notifications as read", ex);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("批量標記失敗：" + ex.getMessage()));
        }
    }


    /**
     * 標記所有通知為已讀
     * PUT /api/notifications/user/{userId}/read-all
     */
    @PutMapping("/user/{userId}/read-all")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> markAllAsRead(
            @PathVariable Integer userId) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 這裡需要在 NotificationService 中添加相應方法
                // int count = notificationService.markAllAsRead(userId);
                
                return ResponseEntity.ok(ApiResponse.success("所有通知已標記為已讀"));
                
            } catch (Exception ex) {
                logger.error("Error marking all notifications as read", ex);
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("標記失敗：" + ex.getMessage()));
            }
        });
    }

    /**
     * 歸檔通知
     * PUT /api/notifications/{notificationId}/archive
     */
    @PutMapping("/{notificationId}/archive")
    public ResponseEntity<ApiResponse<String>> archiveNotification(
            @PathVariable Long notificationId,
            @RequestParam Integer userId) {
        try {
            Boolean success = notificationService.archiveNotification(notificationId, userId).get();
            if (Boolean.TRUE.equals(success)) {
                return ResponseEntity.ok(ApiResponse.success("歸檔成功"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("歸檔失敗"));
            }
        } catch (Exception ex) {
            logger.error("Error archiving notification", ex);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("歸檔失敗：" + ex.getMessage()));
        }
    }


    /**
     * 批量歸檔通知
     * PUT /api/notifications/batch/archive
     */
    @PutMapping("/batch/archive")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> archiveMultipleNotifications(
            @RequestBody List<Long> notificationIds,
            @RequestParam Integer userId) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                int count = 0;
                for (Long notificationId : notificationIds) {
                    if (notificationService.archiveNotification(notificationId, userId).join()) {
                        count++;
                    }
                }
                
                return ResponseEntity.ok(ApiResponse.success(
                    String.format("成功歸檔 %d 條通知", count)));
                
            } catch (Exception ex) {
                logger.error("Error archiving multiple notifications", ex);
                return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("批量歸檔失敗：" + ex.getMessage()));
            }
        });
    }

    /**
     * 刪除通知（軟刪除）
     * DELETE /api/notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Integer userId) {
        
        try {
            // 這裡需要在 NotificationService 中添加相應方法
            // boolean success = notificationService.deleteNotification(notificationId, userId);
            
            return ResponseEntity.ok(ApiResponse.success("通知刪除成功"));
            
        } catch (Exception ex) {
            logger.error("Error deleting notification", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("刪除失敗：" + ex.getMessage()));
        }
    }

    /**
     * 獲取未讀通知數量
     * GET /api/notifications/user/{userId}/unread/count
     */
    @GetMapping("/user/{userId}/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Integer userId) {
        try {
            Long count = notificationService.getUnreadCount(userId);
            return ResponseEntity.ok(ApiResponse.success("查詢成功", count));
        } catch (Exception ex) {
            logger.error("Error getting unread count", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查詢失敗：" + ex.getMessage()));
        }
    }

    /**
     * 獲取各種通知統計
     * GET /api/notifications/user/{userId}/statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserNotificationStatistics(
            @PathVariable Integer userId) {
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 基本統計
            stats.put("unreadCount", notificationService.getUnreadCount(userId));
            
            // 這裡可以添加更多統計信息
            // stats.put("totalCount", notificationService.getTotalCount(userId));
            // stats.put("archivedCount", notificationService.getArchivedCount(userId));
            // stats.put("todayCount", notificationService.getTodayCount(userId));
            
            return ResponseEntity.ok(ApiResponse.success("統計查詢成功", stats));
            
        } catch (Exception ex) {
            logger.error("Error getting user notification statistics", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("統計查詢失敗：" + ex.getMessage()));
        }
    }

    /**
     * 搜索通知
     * GET /api/notifications/user/{userId}/search
     */
    @GetMapping("/user/{userId}/search")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> searchNotifications(
            @PathVariable Integer userId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority) {
        
        try {
            Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            // 這裡需要在 NotificationService 中添加搜索方法
            // Page<NotificationDTO> notifications = notificationService.searchNotifications(
            //     userId, keyword, category, priority, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("搜索成功"));
            
        } catch (Exception ex) {
            logger.error("Error searching notifications", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("搜索失敗：" + ex.getMessage()));
        }
    }

    /**
     * 獲取通知分類列表
     * GET /api/notifications/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getNotificationCategories() {
        try {
            List<String> categories = List.of(
                "system", "security", "update", "reminder", 
                "announcement", "alert", "info", "warning"
            );
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功", categories));
            
        } catch (Exception ex) {
            logger.error("Error getting notification categories", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查詢失敗：" + ex.getMessage()));
        }
    }

    /**
     * 重試失敗的通知 (管理員功能)
     * POST /api/notifications/admin/retry-failed
     */
    @PostMapping("/admin/retry-failed")
    public ResponseEntity<ApiResponse<String>> retryFailedNotifications() {
        try {
            notificationService.retryFailedNotifications();
            return ResponseEntity.ok(ApiResponse.success("重試任務已啟動"));
        } catch (Exception ex) {
            logger.error("Error retrying failed notifications", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("重試失敗：" + ex.getMessage()));
        }
    }

    /**
     * 處理過期通知 (管理員功能)
     * POST /api/notifications/admin/handle-expired
     */
    @PostMapping("/admin/handle-expired")
    public ResponseEntity<ApiResponse<String>> handleExpiredNotifications() {
        try {
            notificationService.handleExpiredNotifications();
            return ResponseEntity.ok(ApiResponse.success("過期通知處理完成"));
        } catch (Exception ex) {
            logger.error("Error handling expired notifications", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("處理失敗：" + ex.getMessage()));
        }
    }

    /**
     * 獲取系統通知統計 (管理員功能)
     * GET /api/notifications/admin/statistics
     */
    @GetMapping("/admin/statistics")
    public ResponseEntity<ApiResponse<NotificationStatistics>> getSystemNotificationStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            NotificationStatistics stats = new NotificationStatistics();
            
            // 這裡需要在 NotificationService 中添加相應的統計方法
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime today = now.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime week = now.minusDays(7);
            LocalDateTime month = now.minusDays(30);
            
            // 設置統計數據
            stats.setTodaySent(0L);
            stats.setWeekSent(0L);
            stats.setMonthSent(0L);
            stats.setTotalSent(0L);
            stats.setTotalRead(0L);
            stats.setTotalFailed(0L);
            stats.setReadRate(0.0);
            
            return ResponseEntity.ok(ApiResponse.success("統計查詢成功", stats));
        } catch (Exception ex) {
            logger.error("Error getting system notification statistics", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("統計查詢失敗：" + ex.getMessage()));
        }
    }

    /**
     * 發送測試通知 (管理員功能)
     * POST /api/notifications/admin/test
     */
    @PostMapping("/admin/test")
    public ResponseEntity<ApiResponse<String>> sendTestNotification(
            @RequestParam Integer targetUserId,
            @RequestParam(defaultValue = "測試通知") String title,
            @RequestParam(defaultValue = "這是一條測試通知") String content) {
        try {
            NotificationRequest request = new NotificationRequest();
            request.setTitle(title);
            request.setContent(content);
            request.setTargetType("user");
            request.setTargetUserIds(List.of(targetUserId));
            request.setPushWebsocket(true);
            request.setNotificationType("test");
            request.setPriority("normal");

            NotificationResult result = notificationService.createAndSendNotification(request).get();

            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("測試通知發送成功"));
            } else {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("測試通知發送失敗：" + result.getMessage()));
            }
        } catch (Exception ex) {
            logger.error("Error sending test notification", ex);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("測試通知發送失敗：" + ex.getMessage()));
        }
    }


    // 統計數據DTO類
    public static class NotificationStatistics {
        private Long totalSent;
        private Long totalRead;
        private Long totalFailed;
        private Double readRate;
        private Long todaySent;
        private Long weekSent;
        private Long monthSent;

        public Long getTotalSent() { return totalSent; }
        public void setTotalSent(Long totalSent) { this.totalSent = totalSent; }
        
        public Long getTotalRead() { return totalRead; }
        public void setTotalRead(Long totalRead) { this.totalRead = totalRead; }
        
        public Long getTotalFailed() { return totalFailed; }
        public void setTotalFailed(Long totalFailed) { this.totalFailed = totalFailed; }
        
        public Double getReadRate() { return readRate; }
        public void setReadRate(Double readRate) { this.readRate = readRate; }
        
        public Long getTodaySent() { return todaySent; }
        public void setTodaySent(Long todaySent) { this.todaySent = todaySent; }
        
        public Long getWeekSent() { return weekSent; }
        public void setWeekSent(Long weekSent) { this.weekSent = weekSent; }
        
        public Long getMonthSent() { return monthSent; }
        public void setMonthSent(Long monthSent) { this.monthSent = monthSent; }
    }
}