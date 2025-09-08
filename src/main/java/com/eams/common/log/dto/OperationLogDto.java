package com.eams.common.log.dto;

import java.time.LocalDateTime;

import com.eams.common.log.entity.OperationLog;
import com.eams.common.log.entity.OperationLog.OperationStatus;
import com.eams.common.log.entity.OperationLog.ApprovalStatus;
import com.eams.common.log.entity.OperationLog.Priority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogDto {
    private Long logId;
    private String operationType;
    private String operationName;
    private String operationDesc;
    private Long userId;
    private String username;
    private String userRole;
    private String targetType;
    private Long targetId;
    private String targetName;
    private Object oldValue;
    private Object newValue;
    private String requestIp;
    private String userAgent;
    private String requestUrl;
    private String requestMethod;
    private OperationStatus operationStatus;
    private String errorMessage;
    private Integer executionTime;
    private LocalDateTime createdAt;
    
    // ===== 新增：審計功能欄位 =====
    @Builder.Default
    private Boolean requiresApproval = false;
    
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.NONE;
    
    @Builder.Default
    private Priority priority = Priority.NORMAL;
    
    private LocalDateTime expiresAt;
    private Long approverId;
    private String approverName;
    private String approvalReason;
    private LocalDateTime approvedAt;
    
    // ===== 審計相關的便利方法 =====
    
    /**
     * 檢查審查請求是否已過期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * 檢查是否可以審查
     */
    public boolean canApprove() {
        return requiresApproval && 
               approvalStatus == ApprovalStatus.PENDING && 
               !isExpired();
    }
    
    /**
     * 獲取剩餘時間（小時）
     */
    public Long getRemainingHours() {
        if (expiresAt == null) return null;
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(expiresAt)) return 0L;
        
        return java.time.Duration.between(now, expiresAt).toHours();
    }
    
    /**
     * 獲取審查狀態的中文描述
     */
    public String getApprovalStatusText() {
        if (approvalStatus == null) return "無";
        
        switch (approvalStatus) {
            case NONE: return "無需審查";
            case PENDING: return "待審查";
            case APPROVED: return "已批准";
            case REJECTED: return "已拒絕";
            case EXPIRED: return "已過期";
            default: return approvalStatus.name();
        }
    }
    
    /**
     * 獲取優先級的中文描述
     */
    public String getPriorityText() {
        if (priority == null) return "一般";
        
        switch (priority) {
            case LOW: return "低";
            case NORMAL: return "一般";
            case HIGH: return "高";
            case URGENT: return "緊急";
            default: return priority.name();
        }
    }
    
    /**
     * 獲取優先級的CSS類名（用於前端樣式）
     */
    public String getPriorityCssClass() {
        if (priority == null) return "priority-normal";
        
        switch (priority) {
            case LOW: return "priority-low";
            case NORMAL: return "priority-normal";
            case HIGH: return "priority-high";
            case URGENT: return "priority-urgent";
            default: return "priority-normal";
        }
    }
    
    /**
     * 獲取審查狀態的CSS類名（用於前端樣式）
     */
    public String getApprovalStatusCssClass() {
        if (approvalStatus == null) return "status-none";
        
        switch (approvalStatus) {
            case NONE: return "status-none";
            case PENDING: return "status-pending";
            case APPROVED: return "status-approved";
            case REJECTED: return "status-rejected";
            case EXPIRED: return "status-expired";
            default: return "status-none";
        }
    }
    
    /**
     * 是否顯示倒計時
     */
    public boolean shouldShowCountdown() {
        return requiresApproval && 
               approvalStatus == ApprovalStatus.PENDING && 
               expiresAt != null && 
               !isExpired();
    }
}