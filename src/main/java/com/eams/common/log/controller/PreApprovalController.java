package com.eams.common.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.common.log.entity.ApprovalRequest;
import com.eams.common.log.service.ApprovalRequestService;
import com.eams.common.log.util.UserContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事前審查控制器
 * 處理 approval_requests 表的審查申請相關API
 */
@RestController
@RequestMapping("/api/admin/pre-approval")
public class PreApprovalController {
    
    @Autowired
    private ApprovalRequestService approvalRequestService;
    
    @Autowired
  	private UserContextUtil userContextUtil;
    
    // ===== 查詢審查申請 =====
    
    /**
     * 獲取所有待審查的申請
     * GET /api/admin/pre-approval/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ApprovalRequest>> getPendingRequests() {
        List<ApprovalRequest> pendingRequests = approvalRequestService.getPendingRequests();
        return ResponseEntity.ok(pendingRequests);
    }
    
    /**
     * 分頁獲取待審查申請
     * GET /api/admin/pre-approval/pending-page?page=0&size=20
     */
    @GetMapping("/pending-page")
    public ResponseEntity<Page<ApprovalRequest>> getPendingRequestsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<ApprovalRequest> pendingRequests = approvalRequestService.getPendingRequests(page, size);
        return ResponseEntity.ok(pendingRequests);
    }
    
    /**
     * 獲取高優先級待審查申請
     * GET /api/admin/pre-approval/high-priority
     */
    @GetMapping("/high-priority")
    public ResponseEntity<List<ApprovalRequest>> getHighPriorityPending() {
        List<ApprovalRequest> highPriorityRequests = approvalRequestService.getHighPriorityPendingRequests();
        return ResponseEntity.ok(highPriorityRequests);
    }
    
    /**
     * 獲取緊急待審查申請
     * GET /api/admin/pre-approval/urgent
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<ApprovalRequest>> getUrgentPending() {
        List<ApprovalRequest> urgentRequests = approvalRequestService.getUrgentPendingRequests();
        return ResponseEntity.ok(urgentRequests);
    }
    
    /**
     * 獲取即將過期的審查申請
     * GET /api/admin/pre-approval/expiring-soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<ApprovalRequest>> getExpiringSoon() {
        List<ApprovalRequest> expiringSoon = approvalRequestService.getExpiringSoonRequests();
        return ResponseEntity.ok(expiringSoon);
    }
    
    /**
     * 獲取特定用戶的待審查申請
     * GET /api/admin/pre-approval/user/{userId}/pending
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<ApprovalRequest>> getUserPendingRequests(@PathVariable Long userId) {
        List<ApprovalRequest> userPending = approvalRequestService.getUserPendingRequests(userId);
        return ResponseEntity.ok(userPending);
    }
    
    /**
     * 根據ID獲取申請詳情
     * GET /api/admin/pre-approval/{requestId}/detail
     */
    @GetMapping("/{requestId}/detail")
    public ResponseEntity<ApprovalRequest> getRequestDetail(@PathVariable Long requestId) {
        return approvalRequestService.getRequestById(requestId)
                .map(request -> ResponseEntity.ok(request))
                .orElse(ResponseEntity.notFound().build());
    }
    
    // ===== 審查操作 =====
    
    /**
     * 批准申請（會自動執行原操作）
     * POST /api/admin/pre-approval/{requestId}/approve
     */
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<Map<String, Object>> approveRequest(
            @PathVariable Long requestId,
            @RequestBody ApprovalRequestDto request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long approverId = getCurrentUserId();
            String approverName = getCurrentUsername();
            
            boolean success = approvalRequestService.approveRequest(
                    requestId, approverId, approverName, request.getReason());
            
            if (success) {
                response.put("success", true);
                response.put("message", "申請已批准，操作將自動執行");
                response.put("requestId", requestId);
                response.put("approver", approverName);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "批准申請失敗");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批准申請時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 拒絕申請
     * POST /api/admin/pre-approval/{requestId}/reject
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Map<String, Object>> rejectRequest(
            @PathVariable Long requestId,
            @RequestBody ApprovalRequestDto request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (request.getReason() == null || request.getReason().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "拒絕申請必須提供原因");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long approverId = getCurrentUserId();
            String approverName = getCurrentUsername();
            
            boolean success = approvalRequestService.rejectRequest(
                    requestId, approverId, approverName, request.getReason());
            
            if (success) {
                response.put("success", true);
                response.put("message", "申請已拒絕");
                response.put("requestId", requestId);
                response.put("approver", approverName);
                response.put("reason", request.getReason());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "拒絕申請失敗");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "拒絕申請時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量批准申請
     * POST /api/admin/pre-approval/batch-approve
     */
    @PostMapping("/batch-approve")
    public ResponseEntity<Map<String, Object>> batchApprove(@RequestBody BatchApprovalRequestDto request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long approverId = getCurrentUserId();
            String approverName = getCurrentUsername();
            
            int successCount = 0;
            int failedCount = 0;
            
            for (Long requestId : request.getRequestIds()) {
                try {
                    boolean success = approvalRequestService.approveRequest(
                            requestId, approverId, approverName, request.getReason());
                    if (success) {
                        successCount++;
                    } else {
                        failedCount++;
                    }
                } catch (Exception e) {
                    failedCount++;
                }
            }
            
            response.put("success", true);
            response.put("message", String.format("批量批准完成：成功 %d 個，失敗 %d 個", successCount, failedCount));
            response.put("successCount", successCount);
            response.put("failedCount", failedCount);
            response.put("totalCount", request.getRequestIds().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量批准時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 取消申請（申請人主動取消）
     * POST /api/admin/pre-approval/{requestId}/cancel
     */
    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelRequest(
            @PathVariable Long requestId,
            @RequestBody ApprovalRequestDto request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = getCurrentUserId();
            
            boolean success = approvalRequestService.cancelRequest(
                    requestId, userId, request.getReason());
            
            if (success) {
                response.put("success", true);
                response.put("message", "申請已取消");
                response.put("requestId", requestId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "取消申請失敗（可能不是您的申請或申請已處理）");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "取消申請時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ===== 統計和管理 =====
    
    /**
     * 獲取申請統計信息
     * GET /api/admin/pre-approval/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getRequestStatistics() {
        Map<String, Object> statistics = approvalRequestService.getRequestStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 處理過期的申請
     * POST /api/admin/pre-approval/process-expired
     */
    @PostMapping("/process-expired")
    public ResponseEntity<Map<String, Object>> processExpiredRequests() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int expiredCount = approvalRequestService.processExpiredRequests();
            
            response.put("success", true);
            response.put("message", String.format("已處理 %d 個過期的申請", expiredCount));
            response.put("expiredCount", expiredCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "處理過期申請時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 獲取用戶申請統計
     * GET /api/admin/pre-approval/user/{userId}/statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getUserRequestStatistics(@PathVariable Long userId) {
        Map<String, Object> statistics = approvalRequestService.getUserRequestStatistics(userId);
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 獲取所有用戶的申請（管理員查看）
     * GET /api/admin/pre-approval/user/{userId}/requests
     */
    @GetMapping("/user/{userId}/requests")
    public ResponseEntity<List<ApprovalRequest>> getUserRequests(@PathVariable Long userId) {
        List<ApprovalRequest> userRequests = approvalRequestService.getUserRequests(userId);
        return ResponseEntity.ok(userRequests);
    }
    
    // ===== 執行狀態查詢 =====
    
    /**
     * 獲取執行成功的申請
     * GET /api/admin/pre-approval/executed
     */
    @GetMapping("/executed")
    public ResponseEntity<List<ApprovalRequest>> getExecutedRequests() {
        // 這需要在 ApprovalRequestService 中添加對應方法
        // 暫時返回空列表
        return ResponseEntity.ok(java.util.Collections.emptyList());
    }
    
    /**
     * 獲取執行失敗的申請
     * GET /api/admin/pre-approval/failed
     */
    @GetMapping("/failed")
    public ResponseEntity<List<ApprovalRequest>> getFailedRequests() {
        // 這需要在 ApprovalRequestService 中添加對應方法
        // 暫時返回空列表
        return ResponseEntity.ok(java.util.Collections.emptyList());
    }
    
    @GetMapping("/pending/count")
    public ResponseEntity<Map<String, Object>> getPendingCount() {
        int count = approvalRequestService.getPendingRequests().size();
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    
    
    
    /**
     * 分頁獲取所有已處理的申請記錄
     * GET /api/admin/pre-approval/processed-page?page=0&size=20&priority=HIGH&username=user
     */
    @GetMapping("/processed-page")
    public ResponseEntity<Page<ApprovalRequest>> getProcessedRequestsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String username) {
        
        Page<ApprovalRequest> processedRequests = approvalRequestService.getProcessedRequests(page, size, priority, username);
        return ResponseEntity.ok(processedRequests);
    }
    
    // ===== 輔助方法 =====
    
    /**
     * 獲取當前用戶ID
     * 需要根據實際的認證機制實現
     */
    private Long getCurrentUserId() {
	    return userContextUtil.getCurrentUserId();
	}
    
    /**
     * 獲取當前用戶名
     * 需要根據實際的認證機制實現
     */
    private String getCurrentUsername() {
		return userContextUtil.getCurrentUsername();
	}
}

/**
 * 審查申請DTO
 */
class ApprovalRequestDto {
    private String reason;
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}

/**
 * 批量審查申請DTO
 */
class BatchApprovalRequestDto {
    private List<Long> requestIds;
    private String reason;
    
    public List<Long> getRequestIds() {
        return requestIds;
    }
    
    public void setRequestIds(List<Long> requestIds) {
        this.requestIds = requestIds;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}