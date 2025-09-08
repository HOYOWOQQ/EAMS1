package com.eams.common.log.dto;

import java.time.LocalDateTime;

import com.eams.common.log.entity.OperationLog.ApprovalStatus;
import com.eams.common.log.entity.OperationLog.Priority;

import lombok.Data;

@Data
public class OperationLogQueryDto {
    private Long userId;
    private String operationType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int page = 0;
    private int size = 20;
    private String operationStatus;
    private String username;
    
    // ===== 新增：審計相關查詢條件 =====
    
    /**
     * 是否只查詢需要審查的記錄
     */
    private Boolean requiresApproval;
    
    /**
     * 審查狀態過濾
     */
    private ApprovalStatus approvalStatus;
    
    /**
     * 優先級過濾
     */
    private Priority priority;
    
    /**
     * 審查人ID
     */
    private Long approverId;
    
    /**
     * 審查人姓名
     */
    private String approverName;
    
    /**
     * 是否查詢即將過期的記錄（24小時內過期）
     */
    private Boolean expiringSoon;
    
    /**
     * 是否查詢已過期的記錄
     */
    private Boolean expired;
    
    /**
     * 審查開始時間
     */
    private LocalDateTime approvalStartTime;
    
    /**
     * 審查結束時間
     */
    private LocalDateTime approvalEndTime;
    
    // ===== 便利方法 =====
    
    /**
     * 設置查詢待審查的記錄
     */
    public OperationLogQueryDto pendingApproval() {
        this.requiresApproval = true;
        this.approvalStatus = ApprovalStatus.PENDING;
        return this;
    }
    
    /**
     * 設置查詢高優先級記錄
     */
    public OperationLogQueryDto highPriority() {
        this.priority = Priority.HIGH;
        return this;
    }
    
    /**
     * 設置查詢緊急記錄
     */
    public OperationLogQueryDto urgent() {
        this.priority = Priority.URGENT;
        return this;
    }
    
    /**
     * 設置查詢即將過期的記錄
     */
    public OperationLogQueryDto expiringSoon() {
        this.expiringSoon = true;
        return this;
    }
    
    /**
     * 設置查詢已過期的記錄
     */
    public OperationLogQueryDto expired() {
        this.expired = true;
        return this;
    }
    
    /**
     * 設置查詢特定審查人的記錄
     */
    public OperationLogQueryDto approvedBy(Long approverId) {
        this.approverId = approverId;
        return this;
    }
    
    /**
     * 設置查詢特定用戶的待審查記錄
     */
    public OperationLogQueryDto userPendingApproval(Long userId) {
        this.userId = userId;
        this.requiresApproval = true;
        this.approvalStatus = ApprovalStatus.PENDING;
        return this;
    }
}