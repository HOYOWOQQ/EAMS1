package com.eams.common.log.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;
    
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;
    
    @Column(name = "operation_name", nullable = false, length = 100)
    private String operationName;
    
    @Column(name = "operation_desc", columnDefinition = "TEXT")
    private String operationDesc;
    
    // 用戶資訊
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", length = 50)
    private String username;
    
    @Column(name = "user_role", length = 20)
    private String userRole;
    
    // 操作目標
    @Column(name = "target_type", length = 50)
    private String targetType;
    
    @Column(name = "target_id")
    private Long targetId;
    
    @Column(name = "target_name", length = 100)
    private String targetName;
    
    // 操作內容 (JSON 格式)
    @Column(name = "old_value", columnDefinition = "JSON")
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "JSON")
    private String newValue;
    
    // 請求資訊
    @Column(name = "request_ip", length = 45)
    private String requestIp;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "request_url", length = 500)
    private String requestUrl;
    
    @Column(name = "request_method", length = 10)
    private String requestMethod;
    
    // 結果資訊
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_status")
    private OperationStatus operationStatus = OperationStatus.SUCCESS;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "execution_time")
    private Integer executionTime;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // ===== 新增：審計功能欄位 =====
    
    @Column(name = "requires_approval", nullable = false)
    @Builder.Default
    private Boolean requiresApproval = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    @Builder.Default
    private ApprovalStatus approvalStatus = ApprovalStatus.NONE;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    @Builder.Default
    private Priority priority = Priority.NORMAL;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "approver_id")
    private Long approverId;
    
    @Column(name = "approver_name", length = 50)
    private String approverName;
    
    @Column(name = "approval_reason", columnDefinition = "TEXT")
    private String approvalReason;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    // 操作狀態枚舉
    public enum OperationStatus {
        SUCCESS, FAILED
    }
    
    // 審查狀態枚舉
    public enum ApprovalStatus {
        NONE,       // 無需審查
        PENDING,    // 待審查
        APPROVED,   // 已批准
        REJECTED,   // 已拒絕
        EXPIRED     // 已過期
    }
    
    // 優先級枚舉
    public enum Priority {
        LOW,        // 低優先級
        NORMAL,     // 一般優先級
        HIGH,       // 高優先級
        URGENT      // 緊急優先級
    }
    
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
     * 批准操作
     */
    public void approve(Long approverId, String approverName, String reason) {
        if (!canApprove()) {
            throw new IllegalStateException("此操作無法被審查");
        }
        
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approverId = approverId;
        this.approverName = approverName;
        this.approvalReason = reason;
        this.approvedAt = LocalDateTime.now();
    }
    
    /**
     * 拒絕操作
     */
    public void reject(Long approverId, String approverName, String reason) {
        if (!canApprove()) {
            throw new IllegalStateException("此操作無法被審查");
        }
        
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.approverId = approverId;
        this.approverName = approverName;
        this.approvalReason = reason;
        this.approvedAt = LocalDateTime.now();
    }
    
    /**
     * 標記為過期
     */
    public void markAsExpired() {
        if (requiresApproval && approvalStatus == ApprovalStatus.PENDING) {
            this.approvalStatus = ApprovalStatus.EXPIRED;
        }
    }
}