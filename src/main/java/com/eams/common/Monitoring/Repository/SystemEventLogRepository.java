package com.eams.common.Monitoring.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Monitoring.entity.SystemEventLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemEventLogRepository extends JpaRepository<SystemEventLog, Long> {
    
    // 根據事件分類查找事件
    List<SystemEventLog> findByEventCategoryOrderByCreatedAtDesc(String eventCategory);
    
    // 根據嚴重程度查找事件
    List<SystemEventLog> findBySeverityOrderByCreatedAtDesc(String severity);
    
    // 根據事件分類和類型查找
    List<SystemEventLog> findByEventCategoryAndEventTypeOrderByCreatedAtDesc(String eventCategory, String eventType);
    
    // 根據時間範圍查找事件
    @Query("SELECT sel FROM SystemEventLog sel WHERE sel.createdAt BETWEEN :startTime AND :endTime ORDER BY sel.createdAt DESC")
    List<SystemEventLog> findByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 查找用戶相關事件
    List<SystemEventLog> findByMemberIdOrderByCreatedAtDesc(Integer memberId);
    
    // 查找高嚴重度事件
    @Query("SELECT sel FROM SystemEventLog sel WHERE sel.severity IN ('HIGH', 'CRITICAL') ORDER BY sel.createdAt DESC")
    List<SystemEventLog> findHighSeverityEvents();
    
    // 查找未處理的事件
    List<SystemEventLog> findByStatusOrderByCreatedAtDesc(String status);
    
    // 根據來源IP查找安全事件
    @Query("SELECT sel FROM SystemEventLog sel WHERE sel.eventCategory = 'security' AND sel.sourceIp = :sourceIp ORDER BY sel.createdAt DESC")
    List<SystemEventLog> findSecurityEventsBySourceIp(@Param("sourceIp") String sourceIp);
    
    // 分頁查詢事件
    Page<SystemEventLog> findByEventCategoryOrderByCreatedAtDesc(String eventCategory, Pageable pageable);
    
    // 查找用戶通知
    @Query("SELECT sel FROM SystemEventLog sel WHERE sel.eventCategory = 'notification' AND " +
           "(sel.notificationTargetType = 'ALL' OR " +
           "(sel.notificationTargetType = 'ROLE' AND sel.notificationTargetRoles LIKE %:userRole%) OR " +
           "(sel.notificationTargetType = 'INDIVIDUAL' AND sel.notificationTargetUsers LIKE %:userId%)) " +
           "ORDER BY sel.createdAt DESC")
    List<SystemEventLog> findUserNotifications(@Param("userId") String userId, @Param("userRole") String userRole);
    
    // 分頁查詢用戶通知
    @Query("SELECT sel FROM SystemEventLog sel WHERE sel.eventCategory = 'notification' AND " +
           "(sel.notificationTargetType = 'ALL' OR " +
           "(sel.notificationTargetType = 'ROLE' AND sel.notificationTargetRoles LIKE %:userRole%) OR " +
           "(sel.notificationTargetType = 'INDIVIDUAL' AND sel.notificationTargetUsers LIKE %:userId%)) " +
           "ORDER BY sel.createdAt DESC")
    Page<SystemEventLog> findUserNotifications(@Param("userId") String userId, @Param("userRole") String userRole, Pageable pageable);
    
//    // 統計未讀通知數量
//    @Query("SELECT COUNT(sel) FROM SystemEventLog sel WHERE sel.eventCategory = 'notification' AND " +
//           "(sel.notificationTargetType = 'ALL' OR " +
//           "(sel.notificationTargetType = 'ROLE' AND sel.notificationTargetRoles LIKE %:userRole%) OR " +
//           "(sel.notificationTargetType = 'INDIVIDUAL' AND sel.notificationTargetUsers LIKE %:userId%)) " +
//           "AND sel.id NOT IN (SELECT nrs.eventLogId FROM NotificationReadStatus nrs WHERE nrs.memberId = :memberIdInt)")
//    Integer countUnreadNotifications(@Param("userId") String userId, @Param("userRole") String userRole, @Param("memberIdInt") Integer memberIdInt);
    
    // 根據目標類型和ID查找事件
    List<SystemEventLog> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(String targetType, Long targetId);
    
    // 統計事件數量（按分類）
    @Query("SELECT sel.eventCategory, COUNT(sel) FROM SystemEventLog sel GROUP BY sel.eventCategory")
    List<Object[]> countEventsByCategory();
    
    // 統計事件數量（按嚴重程度）
    @Query("SELECT sel.severity, COUNT(sel) FROM SystemEventLog sel GROUP BY sel.severity")
    List<Object[]> countEventsBySeverity();
    
    //根據事情分類和嚴重程度查詢
    @Query("SELECT sel FROM SystemEventLog sel WHERE sel.eventCategory = :category AND sel.severity = :severity ORDER BY sel.createdAt DESC")
    Page<SystemEventLog> findByEventCategoryAndSeverityOrderByCreatedAtDesc(
        @Param("category") String category, 
        @Param("severity") String severity, 
        Pageable pageable);   
    
    //查詢所有事件 
    Page<SystemEventLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    
    
 // ===== DataCleanupService 需要的額外方法 =====

 // 查找需要清理的舊事件（已解決的低嚴重度事件）
 @Query("SELECT sel FROM SystemEventLog sel WHERE sel.createdAt < :before " +
        "AND sel.severity IN ('LOW', 'NORMAL') AND sel.status = 'RESOLVED'")
 List<SystemEventLog> findEventsForCleanup(@Param("before") LocalDateTime before);

 // 根據創建時間和嚴重程度查找事件
 @Query("SELECT sel FROM SystemEventLog sel WHERE sel.createdAt < :before " +
        "AND sel.severity IN :severities")
 List<SystemEventLog> findByCreatedAtBeforeAndSeverityIn(
     @Param("before") LocalDateTime before, 
     @Param("severities") List<String> severities);

 // 根據創建時間查找事件
 List<SystemEventLog> findByCreatedAtBefore(LocalDateTime before);

 // 根據事件分類、創建時間和通知狀態查找
 List<SystemEventLog> findByEventCategoryAndCreatedAtBeforeAndNotificationSent(
     String eventCategory, LocalDateTime before, Boolean notificationSent);

 // 查找未處理的過期事件
 @Query("SELECT sel FROM SystemEventLog sel WHERE sel.status = 'NEW' " +
        "AND sel.createdAt <= :before ORDER BY sel.createdAt ASC")
 List<SystemEventLog> findUnprocessedEvents(@Param("before") LocalDateTime before);

 // 根據事件類型和創建時間查找（用於查找清理完成事件）
 List<SystemEventLog> findByEventTypeAndCreatedAtAfterOrderByCreatedAtDesc(
     String eventType, LocalDateTime after);

 // 統計最近創建的事件數量
 @Query("SELECT COUNT(sel) FROM SystemEventLog sel WHERE sel.createdAt > :after")
 Long countByCreatedAtAfter(@Param("after") LocalDateTime after);
    
//統計指定時間前的事件數量
@Query("SELECT COUNT(sel) FROM SystemEventLog sel WHERE sel.createdAt < :before")
Long countByCreatedAtBefore(@Param("before") LocalDateTime before);
 
}