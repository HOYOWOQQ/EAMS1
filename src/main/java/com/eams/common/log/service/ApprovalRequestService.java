package com.eams.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.log.dto.CreateApprovalRequestDto;
import com.eams.common.log.entity.ApprovalRequest;
import com.eams.common.log.entity.ApprovalRequest.RequestStatus;
import com.eams.common.log.entity.ApprovalRequest.Priority;
import com.eams.common.log.repository.ApprovalRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 事前核准申請服務
 * 處理需要事先審查才能執行的操作申請
 */
@Service
@Transactional
@Slf4j
public class ApprovalRequestService {
    
    @Autowired
    private ApprovalRequestRepository approvalRequestRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private ApprovalExecutionService approvalExecutionService;
    
    // ===== 申請管理 =====
    
    /**
     * 創建新的核准申請
     */
    public ApprovalRequest createRequest(CreateApprovalRequestDto requestDto) {
        try {
            ApprovalRequest request = ApprovalRequest.builder()
                    .operationType(requestDto.getOperationType())
                    .operationName(requestDto.getOperationName())
                    .operationDesc(requestDto.getOperationDesc())
                    .userId(requestDto.getUserId())
                    .username(requestDto.getUsername())
                    .userRole(requestDto.getUserRole())
                    .targetType(requestDto.getTargetType())
                    .targetId(requestDto.getTargetId())
                    .targetName(requestDto.getTargetName())
                    .requestData(objectToJson(requestDto.getRequestData()))
                    .executionContext(objectToJson(requestDto.getExecutionContext()))
                    .requestStatus(RequestStatus.PENDING)
                    .priority(requestDto.getPriority())
                    .expiresAt(requestDto.getExpiresAt())
                    .build();
            
            ApprovalRequest savedRequest = approvalRequestRepository.save(request);
            
            log.info("創建事前核准申請 - ID: {}, 操作: {}, 申請人: {}", 
                    savedRequest.getRequestId(), savedRequest.getOperationType(), savedRequest.getUsername());
            
            return savedRequest;
            
        } catch (Exception e) {
            log.error("創建核准申請失敗", e);
            throw new RuntimeException("創建核准申請失敗: " + e.getMessage());
        }
    }
    
    /**
     * 根據ID獲取申請
     */
    public Optional<ApprovalRequest> getRequestById(Long requestId) {
        return approvalRequestRepository.findById(requestId);
    }
    
    /**
     * 獲取待審查的申請列表
     */
    public List<ApprovalRequest> getPendingRequests() {
        return approvalRequestRepository.findByRequestStatusOrderByPriorityDescCreatedAtAsc(RequestStatus.PENDING);
    }
    
    /**
     * 分頁獲取待審查申請
     */
    public Page<ApprovalRequest> getPendingRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return approvalRequestRepository.findByRequestStatusOrderByPriorityDescCreatedAtAsc(
                RequestStatus.PENDING, pageable);
    }
    
    /**
     * 獲取特定用戶的申請
     */
    public List<ApprovalRequest> getUserRequests(Long userId) {
        return approvalRequestRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    /**
     * 獲取特定用戶的待審查申請
     */
    public List<ApprovalRequest> getUserPendingRequests(Long userId) {
        return approvalRequestRepository.findByUserIdAndRequestStatusOrderByCreatedAtDesc(
                userId, RequestStatus.PENDING);
    }
    
    /**
     * 獲取高優先級待審查申請
     */
    public List<ApprovalRequest> getHighPriorityPendingRequests() {
        return approvalRequestRepository.findByRequestStatusAndPriorityOrderByCreatedAtAsc(
                RequestStatus.PENDING, Priority.HIGH);
    }
    
    /**
     * 獲取緊急待審查申請
     */
    public List<ApprovalRequest> getUrgentPendingRequests() {
        return approvalRequestRepository.findByRequestStatusAndPriorityOrderByCreatedAtAsc(
                RequestStatus.PENDING, Priority.URGENT);
    }
    
    // ===== 審查操作 =====
    
    /**
     * 批准申請
     */
    public boolean approveRequest(Long requestId, Long approverId, String approverName, String reason) {
        try {
            Optional<ApprovalRequest> optionalRequest = approvalRequestRepository.findById(requestId);
            if (optionalRequest.isEmpty()) {
                log.warn("核准申請不存在 - ID: {}", requestId);
                return false;
            }
            
            ApprovalRequest request = optionalRequest.get();
            request.approve(approverId, approverName, reason);
            approvalRequestRepository.save(request);
            
           
            
            log.info("核准申請已批准 - ID: {}, 審查人: {}", requestId, approverName);
            
            
            
            if (approvalExecutionService != null) {
                try {
                    Object result = approvalExecutionService.executeApprovedRequest(request);
                    log.info("✅ 申請自動執行成功 - ID: {}, 結果: {}", requestId, result != null ? "有返回值" : "無返回值");
                } catch (Exception e) {
                    log.error("❌ 申請自動執行失敗 - ID: {}", requestId, e);
                    // 這裡執行失敗會導致整個事務回滾，包括審核狀態
                    throw e; // 重新拋出異常讓事務回滾
                }
            } else {
                log.warn("ApprovalExecutionService 不可用，跳過自動執行");
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("批准核准申請失敗 - ID: {}", requestId, e);
            return false;
        }
    }
    
    /**
     * 拒絕申請
     */
    public boolean rejectRequest(Long requestId, Long approverId, String approverName, String reason) {
        try {
            Optional<ApprovalRequest> optionalRequest = approvalRequestRepository.findById(requestId);
            if (optionalRequest.isEmpty()) {
                log.warn("核准申請不存在 - ID: {}", requestId);
                return false;
            }
            
            ApprovalRequest request = optionalRequest.get();
            request.reject(approverId, approverName, reason);
            approvalRequestRepository.save(request);
            
            log.info("核准申請已拒絕 - ID: {}, 審查人: {}, 原因: {}", requestId, approverName, reason);
            
            return true;
            
        } catch (Exception e) {
            log.error("拒絕核准申請失敗 - ID: {}", requestId, e);
            return false;
        }
    }
    
    /**
     * 取消申請（申請人主動取消）
     */
    public boolean cancelRequest(Long requestId, Long userId, String reason) {
        try {
            Optional<ApprovalRequest> optionalRequest = approvalRequestRepository.findById(requestId);
            if (optionalRequest.isEmpty()) {
                return false;
            }
            
            ApprovalRequest request = optionalRequest.get();
            
            // 檢查是否為申請人本人
            if (!request.getUserId().equals(userId)) {
                log.warn("非申請人嘗試取消申請 - 申請ID: {}, 用戶ID: {}", requestId, userId);
                return false;
            }
            
            request.cancel(reason);
            approvalRequestRepository.save(request);
            
            log.info("申請已取消 - ID: {}, 申請人: {}, 原因: {}", requestId, request.getUsername(), reason);
            
            return true;
            
        } catch (Exception e) {
            log.error("取消申請失敗 - ID: {}", requestId, e);
            return false;
        }
    }
    
    // ===== 執行邏輯 =====
    
    /**
     * 執行已批准的申請
     */
    private void executeApprovedRequest(ApprovalRequest request) {
        try {
            if (!request.canExecute()) {
                log.warn("申請無法執行 - ID: {}, 狀態: {}", request.getRequestId(), request.getRequestStatus());
                return;
            }
            
            log.info("開始執行已批准的申請 - ID: {}, 操作: {}", 
                    request.getRequestId(), request.getOperationType());
            
            // TODO: 這裡需要根據操作類型執行具體的業務邏輯
            // 可以使用策略模式或工廠模式來處理不同類型的操作
            Object result = executeBusinessOperation(request);
            
            // 標記為已執行
            request.markAsExecuted(result);
            approvalRequestRepository.save(request);
            
            log.info("申請執行成功 - ID: {}", request.getRequestId());
            
        } catch (Exception e) {
            log.error("執行申請失敗 - ID: {}", request.getRequestId(), e);
            
            // 標記執行失敗
            request.markAsFailed(e);
            approvalRequestRepository.save(request);
        }
    }
    
    /**
     * 執行具體的業務操作
     * TODO: 這裡需要根據實際業務邏輯實現
     */
    private Object executeBusinessOperation(ApprovalRequest request) {
        // 根據操作類型執行相應的業務邏輯
        switch (request.getOperationType()) {
            case "STUDENT_HARD_DELETE":
                return executeStudentHardDelete(request);
            case "COURSE_HARD_DELETE":
                return executeCourseHardDelete(request);
            case "FINANCIAL_TRANSFER":
                return executeFinancialTransfer(request);
            default:
                throw new UnsupportedOperationException("不支持的操作類型: " + request.getOperationType());
        }
    }
    
    // TODO: 實現具體的業務執行邏輯
    private Object executeStudentHardDelete(ApprovalRequest request) {
        log.info("執行學生硬刪除 - 目標ID: {}", request.getTargetId());
        // 實際的刪除邏輯
        return Map.of("deleted", true, "studentId", request.getTargetId());
    }
    
    private Object executeCourseHardDelete(ApprovalRequest request) {
        log.info("執行課程硬刪除 - 目標ID: {}", request.getTargetId());
        // 實際的刪除邏輯
        return Map.of("deleted", true, "courseId", request.getTargetId());
    }
    
    private Object executeFinancialTransfer(ApprovalRequest request) {
        log.info("執行財務轉移 - 目標ID: {}", request.getTargetId());
        // 實際的轉移邏輯
        return Map.of("transferred", true, "transactionId", System.currentTimeMillis());
    }
    
    // ===== 過期處理 =====
    
    /**
     * 獲取即將過期的申請（24小時內）
     */
    public List<ApprovalRequest> getExpiringSoonRequests() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusHours(24);
        
        return approvalRequestRepository.findExpiringSoon(RequestStatus.PENDING, now, expiryTime);
    }
    
    /**
     * 處理過期申請
     */
    @Transactional
    public int processExpiredRequests() {
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = approvalRequestRepository.markExpiredRequests(now);
        
        if (expiredCount > 0) {
            log.info("已處理 {} 個過期的核准申請", expiredCount);
        }
        
        return expiredCount;
    }
    
    // ===== 統計功能 =====
    
    /**
     * 獲取申請統計信息
     */
    public Map<String, Object> getRequestStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 狀態分布
        List<Object[]> statusStats = approvalRequestRepository.countByRequestStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusMap.put(stat[0].toString(), (Long) stat[1]);
        }
        
        // 優先級分布
        List<Object[]> priorityStats = approvalRequestRepository.countByPriority();
        Map<String, Long> priorityMap = new HashMap<>();
        for (Object[] stat : priorityStats) {
            priorityMap.put(stat[0].toString(), (Long) stat[1]);
        }
        
        // 平均審查時間（過去30天）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime now = LocalDateTime.now();
        Double avgApprovalTime = approvalRequestRepository.getAverageApprovalTimeInHours(thirtyDaysAgo, now);
        Double avgExecutionTime = approvalRequestRepository.getAverageExecutionTimeInMinutes(thirtyDaysAgo, now);
        
        // 審查人工作量（過去7天）
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> workloadStats = approvalRequestRepository.getApproverWorkload(sevenDaysAgo, now);
        
        statistics.put("requestStatusDistribution", statusMap);
        statistics.put("priorityDistribution", priorityMap);
        statistics.put("averageApprovalTimeHours", avgApprovalTime);
        statistics.put("averageExecutionTimeMinutes", avgExecutionTime);
        statistics.put("approverWorkload", workloadStats);
        
        return statistics;
    }
    
    /**
     * 獲取用戶申請統計
     */
    public Map<String, Object> getUserRequestStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<Object[]> statusStats = approvalRequestRepository.countByUserIdAndRequestStatus(userId);
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusMap.put(stat[0].toString(), (Long) stat[1]);
        }
        
        stats.put("statusDistribution", statusMap);
        
        return stats;
    }
    
    /**
     * 清理舊的已完成申請
     */
    public long cleanupCompletedRequests(int days) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
        return approvalRequestRepository.deleteCompletedRequestsBefore(cutoffTime);
    }
    
    
    
    /**
     * 分頁獲取所有已處理的申請記錄
     */
    public Page<ApprovalRequest> getProcessedRequests(int page, int size, String priority, String username) {
        // 定義所有已處理的狀態
        List<RequestStatus> processedStatuses = Arrays.asList(
            RequestStatus.APPROVED,
            RequestStatus.REJECTED,
            RequestStatus.EXPIRED,
            RequestStatus.CANCELLED,
            RequestStatus.EXECUTED,
            RequestStatus.FAILED
        );
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // 根據參數構建查詢條件
        if (priority != null && !priority.isEmpty() && username != null && !username.isEmpty()) {
            try {
                Priority priorityEnum = Priority.valueOf(priority.toUpperCase());
                return approvalRequestRepository.findByRequestStatusInAndPriorityAndUsernameContaining(
                    processedStatuses, priorityEnum, username, pageable);
            } catch (IllegalArgumentException e) {
                return approvalRequestRepository.findByRequestStatusInAndUsernameContaining(
                    processedStatuses, username, pageable);
            }
        } else if (priority != null && !priority.isEmpty()) {
            try {
                Priority priorityEnum = Priority.valueOf(priority.toUpperCase());
                return approvalRequestRepository.findByRequestStatusInAndPriority(
                    processedStatuses, priorityEnum, pageable);
            } catch (IllegalArgumentException e) {
                return approvalRequestRepository.findByRequestStatusIn(processedStatuses, pageable);
            }
        } else if (username != null && !username.isEmpty()) {
            return approvalRequestRepository.findByRequestStatusInAndUsernameContaining(
                processedStatuses, username, pageable);
        } else {
            return approvalRequestRepository.findByRequestStatusIn(processedStatuses, pageable);
        }
    }

    
    // ===== 輔助方法 =====
    
    private String objectToJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("轉換JSON失敗", e);
            return obj.toString();
        }
    }
    
    private Object jsonToObject(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.warn("解析JSON失敗: {}", json, e);
            return json;
        }
    }
}

