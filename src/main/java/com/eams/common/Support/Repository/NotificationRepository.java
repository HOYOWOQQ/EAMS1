package com.eams.common.Support.Repository;

import com.eams.common.Support.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    // 基本查詢方法
    List<Notification> findByStatus(String status);
    
    List<Notification> findByNotificationType(String notificationType);
    
    List<Notification> findByCategory(String category);
    
    List<Notification> findByPriority(String priority);
    
    List<Notification> findBySenderId(Integer senderId);
    
    List<Notification> findByTargetType(String targetType);
    
    // 時間範圍查詢
    List<Notification> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Notification> findByScheduledAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Notification> findByScheduledAtBeforeAndStatus(LocalDateTime time, String status);
    
    // 狀態相關查詢
    List<Notification> findByStatusAndCreatedAtAfter(String status, LocalDateTime createdAfter);
    
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.scheduledAt <= :now")
    List<Notification> findPendingNotifications(@Param("status") String status, @Param("now") LocalDateTime now);
    
    // 推送狀態查詢
    List<Notification> findByWebsocketStatus(String websocketStatus);
    
    List<Notification> findByEmailStatus(String emailStatus);
    
    List<Notification> findBySmsStatus(String smsStatus);
    
    List<Notification> findByBrowserPushStatus(String browserPushStatus);
    
    List<Notification> findByMobilePushStatus(String mobilePushStatus);
    
    // 重試機制查詢
    @Query("SELECT n FROM Notification n WHERE n.retryCount < n.maxRetry AND n.nextRetryAt <= :now")
    List<Notification> findNotificationsForRetry(@Param("now") LocalDateTime now);
    
    // 過期通知查詢
    @Query("SELECT n FROM Notification n WHERE n.expiresAt <= :now AND n.status != 'expired'")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now);
    
    // 關聯查詢
    List<Notification> findByRelatedTableAndRelatedId(String relatedTable, Long relatedId);
    
    List<Notification> findByTemplateId(Integer templateId);
    
    List<Notification> findByCreatedBy(Integer createdBy);
    
    // 統計查詢
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.status = :status")
    Long countByStatus(@Param("status") String status);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.notificationType = :type AND n.createdAt >= :startDate")
    Long countByTypeAndDateAfter(@Param("type") String notificationType, @Param("startDate") LocalDateTime startDate);
    
    // 分頁查詢方法
    Page<Notification> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
    
    Page<Notification> findByNotificationTypeOrderByCreatedAtDesc(String notificationType, Pageable pageable);
    
    Page<Notification> findByCreatedByOrderByCreatedAtDesc(Integer createdBy, Pageable pageable);
    
    // 目標用戶查詢
    @Query("SELECT n FROM Notification n WHERE n.targetType = 'user' AND n.targetUsers LIKE %:userId%")
    List<Notification> findByTargetUser(@Param("userId") String userId);
    
    @Query("SELECT n FROM Notification n WHERE n.targetType = 'role' AND n.targetRoles LIKE %:role%")
    List<Notification> findByTargetRole(@Param("role") String role);
    
    // 複合條件查詢
    @Query("SELECT n FROM Notification n WHERE n.status = :status AND n.priority = :priority ORDER BY n.createdAt DESC")
    List<Notification> findByStatusAndPriority(@Param("status") String status, @Param("priority") String priority);
    
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :startDate AND n.createdAt <= :endDate AND n.status = :status")
    List<Notification> findByDateRangeAndStatus(@Param("startDate") LocalDateTime startDate, 
                                               @Param("endDate") LocalDateTime endDate, 
                                               @Param("status") String status);
}