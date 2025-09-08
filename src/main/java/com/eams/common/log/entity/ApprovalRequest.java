package com.eams.common.log.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 事前核准申請實體
 * 用於存儲需要事前核准的操作申請
 */
@Entity
@Table(name = "approval_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;
    
    // ===== 申請基本信息 =====
    @Column(name = "operation_type", nullable = false, length = 50)
    private String operationType;
    
    @Column(name = "operation_name", nullable = false, length = 100)
    private String operationName;
    
    @Column(name = "operation_desc", columnDefinition = "TEXT")
    private String operationDesc;
    
    // ===== 申請人信息 =====
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "username", length = 50)
    private String username;
    
    @Column(name = "user_role", length = 20)
    private String userRole;
    
    // ===== 操作目標 =====
    @Column(name = "target_type", length = 50)
    private String targetType;
    
    @Column(name = "target_id")
    private Long targetId;
    
    @Column(name = "target_name", length = 100)
    private String targetName;
    
    // ===== 申請內容 =====
    @Column(name = "request_data", columnDefinition = "JSON")
    private String requestData; // 序列化的申請參數
    
    @Column(name = "execution_context", columnDefinition = "JSON")
    private String executionContext; // 執行上下文（方法信息、參數等）
    
    // ===== 申請狀態 =====
    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 20)
    @Builder.Default
    private RequestStatus requestStatus = RequestStatus.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    @Builder.Default
    private Priority priority = Priority.NORMAL;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    // ===== 審查信息 =====
    @Column(name = "approver_id")
    private Long approverId;
    
    @Column(name = "approver_name", length = 50)
    private String approverName;
    
    @Column(name = "approval_reason", columnDefinition = "TEXT")
    private String approvalReason;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    // ===== 執行信息 =====
    @Column(name = "executed_at")
    private LocalDateTime executedAt;
    
    @Column(name = "execution_result", columnDefinition = "JSON")
    private String executionResult;
    
    @Column(name = "execution_error", columnDefinition = "TEXT")
    private String executionError;
    
    // ===== 時間戳 =====
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ===== 枚舉定義 =====
    
    /**
     * 申請狀態枚舉
     */
    public enum RequestStatus {
        PENDING,        // 待審查
        APPROVED,       // 已批准
        REJECTED,       // 已拒絕
        EXPIRED,        // 已過期
        EXECUTED,       // 已執行
        FAILED,         // 執行失敗
        CANCELLED       // 已取消
    }
    
    /**
     * 優先級枚舉
     */
    public enum Priority {
        LOW,            // 低優先級
        NORMAL,         // 一般優先級
        HIGH,           // 高優先級
        URGENT          // 緊急優先級
    }
    
    // ===== 業務方法 =====
    
    /**
     * 檢查申請是否已過期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * 檢查是否可以審查
     */
    public boolean canApprove() {
        return requestStatus == RequestStatus.PENDING && !isExpired();
    }
    
    /**
     * 檢查是否可以執行
     */
    public boolean canExecute() {
        return requestStatus == RequestStatus.APPROVED && executedAt == null;
    }
    
    /**
     * 批准申請
     */
    public void approve(Long approverId, String approverName, String reason) {
        if (!canApprove()) {
            throw new IllegalStateException("此申請無法被審查");
        }
        
        this.requestStatus = RequestStatus.APPROVED;
        this.approverId = approverId;
        this.approverName = approverName;
        this.approvalReason = reason;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 拒絕申請
     */
    public void reject(Long approverId, String approverName, String reason) {
        if (!canApprove()) {
            throw new IllegalStateException("此申請無法被審查");
        }
        
        this.requestStatus = RequestStatus.REJECTED;
        this.approverId = approverId;
        this.approverName = approverName;
        this.approvalReason = reason;
        this.approvedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 標記為已執行
     */
    public void markAsExecuted(Object result) {
        if (!canExecute()) {
            throw new IllegalStateException("此申請無法執行");
        }
        
        this.requestStatus = RequestStatus.EXECUTED;
        this.executedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // 保存執行結果
        if (result != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                this.executionResult = mapper.writeValueAsString(result);
            } catch (Exception e) {
                this.executionResult = result.toString();
            }
        }
    }
    
    /**
     * 標記執行失敗
     */
    public void markAsFailed(Exception error) {
        this.requestStatus = RequestStatus.FAILED;
        this.executedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.executionError = error.getMessage();
    }
    
    /**
     * 標記為過期
     */
    public void markAsExpired() {
        if (requestStatus == RequestStatus.PENDING) {
            this.requestStatus = RequestStatus.EXPIRED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    /**
     * 取消申請
     */
    public void cancel(String reason) {
        if (requestStatus == RequestStatus.PENDING) {
            this.requestStatus = RequestStatus.CANCELLED;
            this.approvalReason = reason;
            this.updatedAt = LocalDateTime.now();
        }
    }
}