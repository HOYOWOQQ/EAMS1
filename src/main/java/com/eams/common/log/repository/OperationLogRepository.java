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

import com.eams.common.log.entity.OperationLog;
import com.eams.common.log.entity.OperationLog.OperationStatus;
import com.eams.common.log.entity.OperationLog.ApprovalStatus;
import com.eams.common.log.entity.OperationLog.Priority;

@Repository
public interface OperationLogRepository
		extends JpaRepository<OperationLog, Long>, JpaSpecificationExecutor<OperationLog> {

	// ===== 原有查詢方法 =====
	
	// 根據用戶查詢操作日誌
	List<OperationLog> findByUserIdOrderByCreatedAtDesc(Long userId);

	// 根據操作類型查詢
	List<OperationLog> findByOperationTypeOrderByCreatedAtDesc(String operationType);

	// 根據時間範圍查詢
	@Query("SELECT ol FROM OperationLog ol WHERE ol.createdAt BETWEEN :startTime AND :endTime ORDER BY ol.createdAt DESC")
	List<OperationLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
			@Param("endTime") LocalDateTime endTime);

	// 查詢失敗的操作
	List<OperationLog> findByOperationStatusOrderByCreatedAtDesc(OperationStatus status);

	// 分頁查詢
	Page<OperationLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

	// 統計查詢
	@Query("SELECT ol.operationType, COUNT(ol) FROM OperationLog ol GROUP BY ol.operationType")
	List<Object[]> countByOperationType();

	// 新增：統計時間範圍內的操作數量
	long countByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
	
	/**
	 * 刪除指定時間之前的日誌
	 */
	@Modifying
	@Transactional
	@Query("DELETE FROM OperationLog ol WHERE ol.createdAt < :cutoffTime")
	long deleteByCreatedAtBefore(@Param("cutoffTime") LocalDateTime cutoffTime);

	// ===== 新增：審計相關查詢方法 =====
	
	/**
	 * 查詢所有待審查的記錄
	 */
	List<OperationLog> findByRequiresApprovalTrueAndApprovalStatusOrderByPriorityDescCreatedAtAsc(ApprovalStatus approvalStatus);
	
	/**
	 * 分頁查詢待審查記錄
	 */
	Page<OperationLog> findByRequiresApprovalTrueAndApprovalStatusOrderByPriorityDescCreatedAtAsc(
			ApprovalStatus approvalStatus, Pageable pageable);
	
	/**
	 * 查詢特定用戶的待審查記錄
	 */
	List<OperationLog> findByUserIdAndRequiresApprovalTrueAndApprovalStatusOrderByCreatedAtDesc(
			Long userId, ApprovalStatus approvalStatus);
	
	/**
	 * 查詢特定審查人處理的記錄
	 */
	List<OperationLog> findByApproverIdOrderByApprovedAtDesc(Long approverId);
	
	/**
	 * 查詢即將過期的審查請求（指定時間內過期）
	 */
	@Query("SELECT ol FROM OperationLog ol WHERE ol.requiresApproval = true " +
		   "AND ol.approvalStatus = :status " +
		   "AND ol.expiresAt BETWEEN :now AND :expiryTime " +
		   "ORDER BY ol.expiresAt ASC")
	List<OperationLog> findExpiringSoon(@Param("status") ApprovalStatus status,
										@Param("now") LocalDateTime now,
										@Param("expiryTime") LocalDateTime expiryTime);
	
	/**
	 * 查詢已過期但狀態仍為PENDING的記錄
	 */
	@Query("SELECT ol FROM OperationLog ol WHERE ol.requiresApproval = true " +
		   "AND ol.approvalStatus = 'PENDING' " +
		   "AND ol.expiresAt < :now")
	List<OperationLog> findExpiredPendingApprovals(@Param("now") LocalDateTime now);
	
	/**
	 * 批量更新過期記錄狀態
	 */
	@Modifying
	@Transactional
	@Query("UPDATE OperationLog ol SET ol.approvalStatus = 'EXPIRED' " +
		   "WHERE ol.requiresApproval = true " +
		   "AND ol.approvalStatus = 'PENDING' " +
		   "AND ol.expiresAt < :now")
	int markExpiredApprovals(@Param("now") LocalDateTime now);
	
	/**
	 * 統計審查狀態分布
	 */
	@Query("SELECT ol.approvalStatus, COUNT(ol) FROM OperationLog ol " +
		   "WHERE ol.requiresApproval = true " +
		   "GROUP BY ol.approvalStatus")
	List<Object[]> countByApprovalStatus();
	
	/**
	 * 統計優先級分布
	 */
	@Query("SELECT ol.priority, COUNT(ol) FROM OperationLog ol " +
		   "WHERE ol.requiresApproval = true " +
		   "GROUP BY ol.priority")
	List<Object[]> countByPriority();
	
	/**
	 * 查詢特定優先級的待審查記錄
	 */
	List<OperationLog> findByRequiresApprovalTrueAndApprovalStatusAndPriorityOrderByCreatedAtAsc(
			ApprovalStatus approvalStatus, Priority priority);
	
	/**
	 * 統計特定時間範圍內的審查效率
	 */
	@Query("SELECT AVG(TIMESTAMPDIFF(HOUR, ol.createdAt, ol.approvedAt)) " +
		   "FROM OperationLog ol " +
		   "WHERE ol.requiresApproval = true " +
		   "AND ol.approvalStatus IN ('APPROVED', 'REJECTED') " +
		   "AND ol.approvedAt BETWEEN :startTime AND :endTime")
	Double getAverageApprovalTimeInHours(@Param("startTime") LocalDateTime startTime,
										 @Param("endTime") LocalDateTime endTime);
	
	/**
	 * 查詢特定操作類型的審查統計
	 */
	@Query("SELECT ol.operationType, ol.approvalStatus, COUNT(ol) " +
		   "FROM OperationLog ol " +
		   "WHERE ol.requiresApproval = true " +
		   "AND ol.operationType = :operationType " +
		   "GROUP BY ol.operationType, ol.approvalStatus")
	List<Object[]> getApprovalStatsByOperationType(@Param("operationType") String operationType);
	
	/**
	 * 查詢審查人的工作量統計
	 */
	@Query("SELECT ol.approverId, ol.approverName, COUNT(ol) " +
		   "FROM OperationLog ol " +
		   "WHERE ol.approverId IS NOT NULL " +
		   "AND ol.approvedAt BETWEEN :startTime AND :endTime " +
		   "GROUP BY ol.approverId, ol.approverName " +
		   "ORDER BY COUNT(ol) DESC")
	List<Object[]> getApproverWorkload(@Param("startTime") LocalDateTime startTime,
									   @Param("endTime") LocalDateTime endTime);
}