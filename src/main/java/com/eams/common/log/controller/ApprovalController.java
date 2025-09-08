package com.eams.common.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.dto.OperationLogQueryDto;
import com.eams.common.log.service.OperationLogService;
import com.eams.common.log.util.UserContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 審計功能控制器
 * 處理審查相關的API請求
 */
@RestController
@RequestMapping("/api/admin/approval")
public class ApprovalController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
	private UserContextUtil userContextUtil;
	
    
    // ===== 查詢審查記錄 =====
    
    /**
     * 獲取所有待審查的記錄
     * GET /api/admin/approval/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<OperationLogDto>> getPendingApprovals() {
        List<OperationLogDto> pendingApprovals = operationLogService.getPendingApprovals();
        return ResponseEntity.ok(pendingApprovals);
    }
    
    /**
     * 分頁獲取待審查記錄
     * GET /api/admin/approval/pending-page?page=0&size=20
     */
    @GetMapping("/pending-page")
    public ResponseEntity<Page<OperationLogDto>> getPendingApprovalsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<OperationLogDto> pendingApprovals = operationLogService.getPendingApprovals(page, size);
        return ResponseEntity.ok(pendingApprovals);
    }
    
    /**
     * 獲取高優先級待審查記錄
     * GET /api/admin/approval/high-priority
     */
    @GetMapping("/high-priority")
    public ResponseEntity<List<OperationLogDto>> getHighPriorityPending() {
        List<OperationLogDto> highPriorityApprovals = operationLogService.getHighPriorityPendingApprovals();
        return ResponseEntity.ok(highPriorityApprovals);
    }
    
    /**
     * 獲取緊急待審查記錄
     * GET /api/admin/approval/urgent
     */
    @GetMapping("/urgent")
    public ResponseEntity<List<OperationLogDto>> getUrgentPending() {
        List<OperationLogDto> urgentApprovals = operationLogService.getUrgentPendingApprovals();
        return ResponseEntity.ok(urgentApprovals);
    }
    
    /**
     * 獲取即將過期的審查請求
     * GET /api/admin/approval/expiring-soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<OperationLogDto>> getExpiringSoon() {
        List<OperationLogDto> expiringSoon = operationLogService.getExpiringSoonApprovals();
        return ResponseEntity.ok(expiringSoon);
    }
    
    /**
     * 獲取特定用戶的待審查記錄
     * GET /api/admin/approval/user/{userId}/pending
     */
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<OperationLogDto>> getUserPendingApprovals(@PathVariable Long userId) {
        List<OperationLogDto> userPending = operationLogService.getUserPendingApprovals(userId);
        return ResponseEntity.ok(userPending);
    }
    
    /**
     * 獲取審查人的處理記錄
     * GET /api/admin/approval/approver/{approverId}/history
     */
    @GetMapping("/approver/{approverId}/history")
    public ResponseEntity<List<OperationLogDto>> getApproverHistory(@PathVariable Long approverId) {
        List<OperationLogDto> approverHistory = operationLogService.getApproverHistory(approverId);
        return ResponseEntity.ok(approverHistory);
    }
    
    // ===== 審查操作 =====
    
    /**
     * 批准操作
     * POST /api/admin/approval/{logId}/approve
     */
    @PostMapping("/{logId}/approve")
    public ResponseEntity<Map<String, Object>> approveOperation(
            @PathVariable Long logId,
            @RequestBody ApprovalRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 這裡需要從當前登入用戶獲取審查人信息
            Long approverId = getCurrentUserId(); // 需要實現獲取當前用戶ID的方法
            String approverName = getCurrentUsername(); // 需要實現獲取當前用戶名的方法
            
            boolean success = operationLogService.approveOperation(
                    logId, approverId, approverName, request.getReason());
            
            if (success) {
                response.put("success", true);
                response.put("message", "操作已成功批准");
                response.put("logId", logId);
                response.put("approver", approverName);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "批准操作失敗");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批准操作時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 拒絕操作
     * POST /api/admin/approval/{logId}/reject
     */
    @PostMapping("/{logId}/reject")
    public ResponseEntity<Map<String, Object>> rejectOperation(
            @PathVariable Long logId,
            @RequestBody ApprovalRequest request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 驗證拒絕原因是否提供
            if (request.getReason() == null || request.getReason().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "拒絕操作必須提供原因");
                return ResponseEntity.badRequest().body(response);
            }
            
            Long approverId = getCurrentUserId();
            String approverName = getCurrentUsername();
            
            boolean success = operationLogService.rejectOperation(
                    logId, approverId, approverName, request.getReason());
            
            if (success) {
                response.put("success", true);
                response.put("message", "操作已成功拒絕");
                response.put("logId", logId);
                response.put("approver", approverName);
                response.put("reason", request.getReason());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "拒絕操作失敗");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "拒絕操作時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 批量批准操作
     * POST /api/admin/approval/batch-approve
     */
    @PostMapping("/batch-approve")
    public ResponseEntity<Map<String, Object>> batchApprove(@RequestBody BatchApprovalRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long approverId = getCurrentUserId();
            String approverName = getCurrentUsername();
            
            int successCount = 0;
            int failedCount = 0;
            
            for (Long logId : request.getLogIds()) {
                boolean success = operationLogService.approveOperation(
                        logId, approverId, approverName, request.getReason());
                if (success) {
                    successCount++;
                } else {
                    failedCount++;
                }
            }
            
            response.put("success", true);
            response.put("message", String.format("批量批准完成：成功 %d 個，失敗 %d 個", successCount, failedCount));
            response.put("successCount", successCount);
            response.put("failedCount", failedCount);
            response.put("totalCount", request.getLogIds().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量批准時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // ===== 統計和報告 =====
    
    /**
     * 獲取審查統計信息
     * GET /api/admin/approval/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getApprovalStatistics() {
        Map<String, Object> statistics = operationLogService.getApprovalStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 處理過期的審查請求
     * POST /api/admin/approval/process-expired
     */
    @PostMapping("/process-expired")
    public ResponseEntity<Map<String, Object>> processExpiredApprovals() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int expiredCount = operationLogService.processExpiredApprovals();
            
            response.put("success", true);
            response.put("message", String.format("已處理 %d 個過期的審查請求", expiredCount));
            response.put("expiredCount", expiredCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "處理過期審查請求時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 獲取審查操作的詳細信息
     * GET /api/admin/approval/{logId}/detail
     */
    @GetMapping("/{logId}/detail")
    public ResponseEntity<OperationLogDto> getApprovalDetail(@PathVariable Long logId) {
        OperationLogDto logDetail = operationLogService.getLogById(logId);
        
        if (logDetail != null) {
            return ResponseEntity.ok(logDetail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    
    
    /**
     * 分頁獲取所有已處理的審計記錄
     * GET /api/admin/approval/processed-page?page=0&size=20&priority=HIGH&username=user
     */
    @GetMapping("/processed-page")
    public ResponseEntity<Page<OperationLogDto>> getProcessedLogsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String username) {
        
        Page<OperationLogDto> processedLogs = operationLogService.getProcessedOperationLogs(page, size, priority, username);
        return ResponseEntity.ok(processedLogs);
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
 * 審查請求DTO
 */
class ApprovalRequest {
    private String reason;
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}

/**
 * 批量審查請求DTO
 */
class BatchApprovalRequest {
    private List<Long> logIds;
    private String reason;
    
    public List<Long> getLogIds() {
        return logIds;
    }
    
    public void setLogIds(List<Long> logIds) {
        this.logIds = logIds;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}