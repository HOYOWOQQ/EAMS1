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
 * 用戶個人日誌查詢控制器
 */
@RestController
@RequestMapping("/api/user/logs")
public class UserLogController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    // ===== 原有功能 =====
    
    /**
     * 獲取當前用戶的操作日誌
     * GET /api/user/logs
     */
    @GetMapping
    public ResponseEntity<Page<OperationLogDto>> getMyLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 獲取當前登入用戶ID（需要根據您的認證機制調整）
        Long currentUserId = getCurrentUserId();
        
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setUserId(currentUserId);
        queryDto.setPage(page);
        queryDto.setSize(size);
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs);
    }
    
    // ===== 新增：審計相關功能 =====
    
    /**
     * 獲取當前用戶的待審查記錄
     * GET /api/user/logs/pending-approval
     */
    @GetMapping("/pending-approval")
    public ResponseEntity<List<OperationLogDto>> getMyPendingApprovals() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<OperationLogDto> pendingApprovals = operationLogService.getUserPendingApprovals(currentUserId);
        return ResponseEntity.ok(pendingApprovals);
    }
    
    /**
     * 獲取當前用戶需要審查的操作記錄（按狀態分類）
     * GET /api/user/logs/approval-summary
     */
    @GetMapping("/approval-summary")
    public ResponseEntity<Map<String, Object>> getMyApprovalSummary() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, Object> summary = new HashMap<>();
        
        // 待審查記錄
        OperationLogQueryDto pendingQuery = new OperationLogQueryDto();
        pendingQuery.setUserId(currentUserId);
        pendingQuery.setRequiresApproval(true);
        pendingQuery.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.PENDING);
        pendingQuery.setSize(100); // 獲取足夠多的記錄用於統計
        
        Page<OperationLogDto> pendingLogs = operationLogService.getOperationLogs(pendingQuery);
        
        // 已批准記錄
        OperationLogQueryDto approvedQuery = new OperationLogQueryDto();
        approvedQuery.setUserId(currentUserId);
        approvedQuery.setRequiresApproval(true);
        approvedQuery.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.APPROVED);
        approvedQuery.setSize(100);
        
        Page<OperationLogDto> approvedLogs = operationLogService.getOperationLogs(approvedQuery);
        
        // 已拒絕記錄
        OperationLogQueryDto rejectedQuery = new OperationLogQueryDto();
        rejectedQuery.setUserId(currentUserId);
        rejectedQuery.setRequiresApproval(true);
        rejectedQuery.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.REJECTED);
        rejectedQuery.setSize(100);
        
        Page<OperationLogDto> rejectedLogs = operationLogService.getOperationLogs(rejectedQuery);
        
        // 已過期記錄
        OperationLogQueryDto expiredQuery = new OperationLogQueryDto();
        expiredQuery.setUserId(currentUserId);
        expiredQuery.setRequiresApproval(true);
        expiredQuery.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.EXPIRED);
        expiredQuery.setSize(100);
        
        Page<OperationLogDto> expiredLogs = operationLogService.getOperationLogs(expiredQuery);
        
        summary.put("pending", Map.of(
            "count", pendingLogs.getTotalElements(),
            "logs", pendingLogs.getContent()
        ));
        
        summary.put("approved", Map.of(
            "count", approvedLogs.getTotalElements(),
            "logs", approvedLogs.getContent()
        ));
        
        summary.put("rejected", Map.of(
            "count", rejectedLogs.getTotalElements(),
            "logs", rejectedLogs.getContent()
        ));
        
        summary.put("expired", Map.of(
            "count", expiredLogs.getTotalElements(),
            "logs", expiredLogs.getContent()
        ));
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * 獲取當前用戶的審計統計
     * GET /api/user/logs/my-audit-stats
     */
    @GetMapping("/my-audit-stats")
    public ResponseEntity<Map<String, Object>> getMyAuditStatistics() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, Object> stats = new HashMap<>();
        
        // 總的需要審查的操作數
        OperationLogQueryDto totalQuery = new OperationLogQueryDto();
        totalQuery.setUserId(currentUserId);
        totalQuery.setRequiresApproval(true);
        totalQuery.setSize(1);
        
        Page<OperationLogDto> totalRequiringApproval = operationLogService.getOperationLogs(totalQuery);
        
        // 待審查數量
        OperationLogQueryDto pendingQuery = new OperationLogQueryDto();
        pendingQuery.setUserId(currentUserId);
        pendingQuery.setRequiresApproval(true);
        pendingQuery.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.PENDING);
        pendingQuery.setSize(1);
        
        Page<OperationLogDto> pendingApprovals = operationLogService.getOperationLogs(pendingQuery);
        
        // 即將過期的數量
        OperationLogQueryDto expiringSoonQuery = new OperationLogQueryDto();
        expiringSoonQuery.setUserId(currentUserId);
        expiringSoonQuery.setExpiringSoon(true);
        expiringSoonQuery.setSize(1);
        
        Page<OperationLogDto> expiringSoon = operationLogService.getOperationLogs(expiringSoonQuery);
        
        stats.put("totalRequiringApproval", totalRequiringApproval.getTotalElements());
        stats.put("pendingApproval", pendingApprovals.getTotalElements());
        stats.put("expiringSoon", expiringSoon.getTotalElements());
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 獲取當前用戶特定狀態的審計記錄
     * GET /api/user/logs/by-status/{status}
     */
    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<OperationLogDto>> getMyLogsByApprovalStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            OperationLogQueryDto queryDto = new OperationLogQueryDto();
            queryDto.setUserId(currentUserId);
            queryDto.setRequiresApproval(true);
            queryDto.setApprovalStatus(
                com.eams.common.log.entity.OperationLog.ApprovalStatus.valueOf(status.toUpperCase()));
            queryDto.setPage(page);
            queryDto.setSize(size);
            
            Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
            return ResponseEntity.ok(logs);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 獲取當前用戶高優先級的待審查記錄
     * GET /api/user/logs/high-priority-pending
     */
    @GetMapping("/high-priority-pending")
    public ResponseEntity<List<OperationLogDto>> getMyHighPriorityPending() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setUserId(currentUserId);
        queryDto.setRequiresApproval(true);
        queryDto.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.PENDING);
        queryDto.setPriority(com.eams.common.log.entity.OperationLog.Priority.HIGH);
        queryDto.setSize(50);
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs.getContent());
    }
    
    /**
     * 獲取當前用戶緊急待審查記錄
     * GET /api/user/logs/urgent-pending
     */
    @GetMapping("/urgent-pending")
    public ResponseEntity<List<OperationLogDto>> getMyUrgentPending() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setUserId(currentUserId);
        queryDto.setRequiresApproval(true);
        queryDto.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.PENDING);
        queryDto.setPriority(com.eams.common.log.entity.OperationLog.Priority.URGENT);
        queryDto.setSize(50);
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs.getContent());
    }
    
    /**
     * 獲取當前用戶即將過期的審查記錄
     * GET /api/user/logs/expiring-soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<OperationLogDto>> getMyExpiringSoon() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setUserId(currentUserId);
        queryDto.setExpiringSoon(true);
        queryDto.setSize(50);
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs.getContent());
    }
    
    /**
     * 獲取當前用戶的審計記錄詳情
     * GET /api/user/logs/audit/{logId}
     */
    @GetMapping("/audit/{logId}")
    public ResponseEntity<OperationLogDto> getMyAuditLogDetail(@PathVariable Long logId) {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        OperationLogDto log = operationLogService.getLogById(logId);
        
        // 確保用戶只能查看自己的記錄
        if (log != null && currentUserId.equals(log.getUserId())) {
            return ResponseEntity.ok(log);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 獲取當前用戶的操作需要審查的原因說明
     * GET /api/user/logs/approval-help
     */
    @GetMapping("/approval-help")
    public ResponseEntity<Map<String, Object>> getApprovalHelp() {
        Map<String, Object> helpInfo = new HashMap<>();
        
        helpInfo.put("title", "為什麼我的操作需要審查？");
        helpInfo.put("description", "某些操作基於系統安全和合規要求需要經過審查批准");
        
        Map<String, String> reasons = new HashMap<>();
        reasons.put("高風險操作", "刪除重要數據、批量修改等操作");
        reasons.put("權限限制", "您的角色權限不足以直接執行此操作");
        reasons.put("金額門檻", "涉及金額超過系統設定的門檻值");
        reasons.put("數量門檻", "批量操作的數據量超過系統設定的門檻值");
        reasons.put("系統配置", "修改系統重要配置需要更高權限審查");
        
        helpInfo.put("commonReasons", reasons);
        
        Map<String, String> statusExplanation = new HashMap<>();
        statusExplanation.put("PENDING", "您的操作正在等待管理員審查");
        statusExplanation.put("APPROVED", "您的操作已被批准並執行");
        statusExplanation.put("REJECTED", "您的操作被拒絕，請查看拒絕原因");
        statusExplanation.put("EXPIRED", "審查請求已過期，您可能需要重新提交操作");
        
        helpInfo.put("statusExplanation", statusExplanation);
        
        helpInfo.put("tips", new String[]{
            "高優先級和緊急操作會被優先處理",
            "請確保操作的必要性，避免不必要的審查請求",
            "如有疑問，請聯繫系統管理員",
            "過期的審查請求需要重新提交操作"
        });
        
        return ResponseEntity.ok(helpInfo);
    }
    
    /**
     * 獲取當前用戶ID
     * 這裡需要根據您的實際認證機制實現
     */
    private Long getCurrentUserId() {
        return userContextUtil.getCurrentUserId();
    }
}