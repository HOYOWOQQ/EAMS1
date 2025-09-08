package com.eams.common.log.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.log.entity.ApprovalRequest;
import com.eams.common.log.entity.ApprovalRequest.RequestStatus;
import com.eams.common.log.entity.OperationLog;
import com.eams.common.log.entity.ApprovalRequest.Priority;

@Repository
public interface ApprovalRequestRepository 
        extends JpaRepository<ApprovalRequest, Long>, JpaSpecificationExecutor<ApprovalRequest> {

    // ===== 基本查詢 =====
    
    /**
     * 根據用戶查詢申請
     */
    List<ApprovalRequest> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根據操作類型查詢
     */
    List<ApprovalRequest> findByOperationTypeOrderByCreatedAtDesc(String operationType);
    
    /**
     * 根據狀態查詢
     */
    List<ApprovalRequest> findByRequestStatusOrderByPriorityDescCreatedAtAsc(RequestStatus status);
    
    /**
     * 分頁查詢指定狀態的申請
     */
    Page<ApprovalRequest> findByRequestStatusOrderByPriorityDescCreatedAtAsc(
            RequestStatus status, Pageable pageable);
    
    // ===== 待審查查詢 =====
    
//    /**
//     * 查詢所有待審查的申請
//     */
//    List<ApprovalRequest> findByRequestStatusOrderByPriorityDescCreatedAtAsc(RequestStatus status);
    
    /**
     * 查詢特定用戶的待審查申請
     */
    List<ApprovalRequest> findByUserIdAndRequestStatusOrderByCreatedAtDesc(
            Long userId, RequestStatus status);
    
    /**
     * 查詢特定優先級的待審查申請
     */
    List<ApprovalRequest> findByRequestStatusAndPriorityOrderByCreatedAtAsc(
            RequestStatus status, Priority priority);
    
    // ===== 審查人相關查詢 =====
    
    /**
     * 查詢特定審查人處理的申請
     */
    List<ApprovalRequest> findByApproverIdOrderByApprovedAtDesc(Long approverId);
    
    /**
     * 查詢特定審查人在指定時間範圍內處理的申請數量
     */
    @Query("SELECT COUNT(ar) FROM ApprovalRequest ar " +
           "WHERE ar.approverId = :approverId " +
           "AND ar.approvedAt BETWEEN :startTime AND :endTime")
    long countByApproverIdAndApprovedAtBetween(
            @Param("approverId") Long approverId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // ===== 過期相關查詢 =====
    
    /**
     * 查詢即將過期的申請（指定時間內過期）
     */
    @Query("SELECT ar FROM ApprovalRequest ar " +
           "WHERE ar.requestStatus = :status " +
           "AND ar.expiresAt BETWEEN :now AND :expiryTime " +
           "ORDER BY ar.expiresAt ASC")
    List<ApprovalRequest> findExpiringSoon(
            @Param("status") RequestStatus status,
            @Param("now") LocalDateTime now,
            @Param("expiryTime") LocalDateTime expiryTime);
    
    /**
     * 查詢已過期但狀態仍為PENDING的申請
     */
    @Query("SELECT ar FROM ApprovalRequest ar " +
           "WHERE ar.requestStatus = 'PENDING' " +
           "AND ar.expiresAt < :now")
    List<ApprovalRequest> findExpiredPendingRequests(@Param("now") LocalDateTime now);
    
    /**
     * 批量更新過期申請狀態
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApprovalRequest ar SET ar.requestStatus = 'EXPIRED', ar.updatedAt = :now " +
           "WHERE ar.requestStatus = 'PENDING' " +
           "AND ar.expiresAt < :now")
    int markExpiredRequests(@Param("now") LocalDateTime now);
    
    // ===== 執行相關查詢 =====
    
    /**
     * 查詢已批准但尚未執行的申請
     */
    @Query("SELECT ar FROM ApprovalRequest ar " +
           "WHERE ar.requestStatus = 'APPROVED' " +
           "AND ar.executedAt IS NULL " +
           "ORDER BY ar.priority DESC, ar.approvedAt ASC")
    List<ApprovalRequest> findApprovedButNotExecuted();
    
    /**
     * 查詢執行失敗的申請
     */
    List<ApprovalRequest> findByRequestStatusOrderByUpdatedAtDesc(RequestStatus status);
    
    // ===== 統計查詢 =====
    
    /**
     * 統計申請狀態分布
     */
    @Query("SELECT ar.requestStatus, COUNT(ar) FROM ApprovalRequest ar " +
           "GROUP BY ar.requestStatus")
    List<Object[]> countByRequestStatus();
    
    /**
     * 統計優先級分布
     */
    @Query("SELECT ar.priority, COUNT(ar) FROM ApprovalRequest ar " +
           "WHERE ar.requestStatus != 'CANCELLED' " +
           "GROUP BY ar.priority")
    List<Object[]> countByPriority();
    
    /**
     * 統計操作類型分布
     */
    @Query("SELECT ar.operationType, ar.requestStatus, COUNT(ar) " +
           "FROM ApprovalRequest ar " +
           "GROUP BY ar.operationType, ar.requestStatus " +
           "ORDER BY ar.operationType, ar.requestStatus")
    List<Object[]> countByOperationTypeAndStatus();
    
    /**
     * 計算平均審查時間（小時）
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, ar.createdAt, ar.approvedAt)) " +
           "FROM ApprovalRequest ar " +
           "WHERE ar.requestStatus IN ('APPROVED', 'REJECTED') " +
           "AND ar.approvedAt BETWEEN :startTime AND :endTime")
    Double getAverageApprovalTimeInHours(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 計算平均執行時間（從批准到執行完成）
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, ar.approvedAt, ar.executedAt)) " +
           "FROM ApprovalRequest ar " +
           "WHERE ar.requestStatus = 'EXECUTED' " +
           "AND ar.executedAt BETWEEN :startTime AND :endTime")
    Double getAverageExecutionTimeInMinutes(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // ===== 清理相關 =====
    
    /**
     * 刪除指定時間之前的已完成申請
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ApprovalRequest ar " +
           "WHERE ar.requestStatus IN ('EXECUTED', 'REJECTED', 'EXPIRED', 'CANCELLED') " +
           "AND ar.updatedAt < :cutoffTime")
    long deleteCompletedRequestsBefore(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * 查詢時間範圍內的申請數量
     */
    long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // ===== 用戶相關統計 =====
    
    /**
     * 查詢用戶的申請統計
     */
    @Query("SELECT ar.requestStatus, COUNT(ar) FROM ApprovalRequest ar " +
           "WHERE ar.userId = :userId " +
           "GROUP BY ar.requestStatus")
    List<Object[]> countByUserIdAndRequestStatus(@Param("userId") Long userId);
    
    /**
     * 查詢用戶在指定時間範圍內的申請數量
     */
    @Query("SELECT COUNT(ar) FROM ApprovalRequest ar " +
           "WHERE ar.userId = :userId " +
           "AND ar.createdAt BETWEEN :startTime AND :endTime")
    long countByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    // ===== 審查人工作量統計 =====
    
    /**
     * 查詢審查人工作量統計
     */
    @Query("SELECT ar.approverId, ar.approverName, COUNT(ar) " +
           "FROM ApprovalRequest ar " +
           "WHERE ar.approverId IS NOT NULL " +
           "AND ar.approvedAt BETWEEN :startTime AND :endTime " +
           "GROUP BY ar.approverId, ar.approverName " +
           "ORDER BY COUNT(ar) DESC")
    List<Object[]> getApproverWorkload(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查詢特定操作類型的審查統計
     */
    @Query("SELECT ar.operationType, ar.requestStatus, COUNT(ar) " +
           "FROM ApprovalRequest ar " +
           "WHERE ar.operationType = :operationType " +
           "GROUP BY ar.operationType, ar.requestStatus")
    List<Object[]> getRequestStatsByOperationType(@Param("operationType") String operationType);
    
 // 基本查詢方法
    Page<ApprovalRequest> findByRequestStatusIn(List<RequestStatus> statuses, Pageable pageable);

    // 按優先級過濾
    Page<ApprovalRequest> findByRequestStatusInAndPriority(List<RequestStatus> statuses, Priority priority, Pageable pageable);

    // 按用戶名過濾
    Page<ApprovalRequest> findByRequestStatusInAndUsernameContaining(List<RequestStatus> statuses, String username, Pageable pageable);

    // 同時按優先級和用戶名過濾
    Page<ApprovalRequest> findByRequestStatusInAndPriorityAndUsernameContaining(List<RequestStatus> statuses, Priority priority, String username, Pageable pageable);

    // ==================== 6. 如果使用JpaSpecificationExecutor，也可以用這種方式 ====================

    // 在 OperationLogRepository 中確保繼承了 JpaSpecificationExecutor
    public interface OperationLogRepository extends JpaRepository<OperationLog, Long>, JpaSpecificationExecutor<OperationLog> {
        // 現有方法保持不變
    }
    
}