package com.eams.common.Support.Repository;

import com.eams.common.Support.entity.NotificationReadStatus;
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
public interface NotificationReadStatusRepository extends JpaRepository<NotificationReadStatus, Long>, JpaSpecificationExecutor<NotificationReadStatus> {

    // 基本查詢方法
    List<NotificationReadStatus> findByNotificationId(Long notificationId);
    
    List<NotificationReadStatus> findByUserId(Integer userId);
    
    Optional<NotificationReadStatus> findByNotificationIdAndUserId(Long notificationId, Integer userId);
    
    // 讀取狀態查詢
    List<NotificationReadStatus> findByIsRead(Boolean isRead);
    
    List<NotificationReadStatus> findByUserIdAndIsRead(Integer userId, Boolean isRead);
    
    List<NotificationReadStatus> findByNotificationIdAndIsRead(Long notificationId, Boolean isRead);
    
    // 歸檔狀態查詢
    List<NotificationReadStatus> findByIsArchived(Boolean isArchived);
    
    List<NotificationReadStatus> findByUserIdAndIsArchived(Integer userId, Boolean isArchived);
    
    // 行動狀態查詢
    List<NotificationReadStatus> findByActionTaken(Boolean actionTaken);
    
    List<NotificationReadStatus> findByUserIdAndActionTaken(Integer userId, Boolean actionTaken);
    
    List<NotificationReadStatus> findByActionResult(String actionResult);
    
    // 時間範圍查詢
    List<NotificationReadStatus> findByReadAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<NotificationReadStatus> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    List<NotificationReadStatus> findByUserIdAndReadAtBetween(Integer userId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 統計查詢
    @Query("SELECT COUNT(nrs) FROM NotificationReadStatus nrs WHERE nrs.notificationId = :notificationId AND nrs.isRead = true")
    Long countReadByNotificationId(@Param("notificationId") Long notificationId);
    
    @Query("SELECT COUNT(nrs) FROM NotificationReadStatus nrs WHERE nrs.userId = :userId AND nrs.isRead = false")
    Long countUnreadByUserId(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(nrs) FROM NotificationReadStatus nrs WHERE nrs.userId = :userId AND nrs.actionTaken = true")
    Long countActionTakenByUserId(@Param("userId") Integer userId);
    
    // 複合查詢
    @Query("SELECT nrs FROM NotificationReadStatus nrs WHERE nrs.userId = :userId AND nrs.isRead = :isRead AND nrs.createdAt >= :startDate ORDER BY nrs.createdAt DESC")
    List<NotificationReadStatus> findUserReadStatusAfterDate(@Param("userId") Integer userId, 
                                                            @Param("isRead") Boolean isRead, 
                                                            @Param("startDate") LocalDateTime startDate);
    
    // 分頁查詢
    Page<NotificationReadStatus> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);
    
    Page<NotificationReadStatus> findByNotificationIdOrderByCreatedAtDesc(Long notificationId, Pageable pageable);
    
    Page<NotificationReadStatus> findByUserIdAndIsReadOrderByCreatedAtDesc(Integer userId, Boolean isRead, Pageable pageable);
    
    // 設備信息查詢
    List<NotificationReadStatus> findByDeviceInfo(String deviceInfo);
    
    List<NotificationReadStatus> findByReadMethod(String readMethod);
    
    // 刪除相關
    void deleteByNotificationId(Long notificationId);
    
    void deleteByUserId(Integer userId);
}