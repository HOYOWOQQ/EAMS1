package com.eams.common.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.dto.OperationLogQueryDto;
import com.eams.common.log.service.OperationLogService;

import java.util.List;
import java.util.Map;

/**
 * 操作日誌 API 控制器 - RESTful API
 */
@RestController
@RequestMapping("/api/admin/logs")
public class LogApiController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    // ===== 原有API =====
    
    /**
     * 獲取操作日誌列表
     * GET /api/admin/logs?page=0&size=20&operationType=COURSE_CREATE&userId=1
     */
    @GetMapping
    public ResponseEntity<Page<OperationLogDto>> getLogs(OperationLogQueryDto queryDto) {
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 獲取日誌統計信息
     * GET /api/admin/logs/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = operationLogService.getOperationStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 根據ID獲取單個日誌
     * GET /api/admin/logs/{logId}
     */
    @GetMapping("/{logId}")
    public ResponseEntity<OperationLogDto> getLogById(@PathVariable Long logId) {
        OperationLogDto log = operationLogService.getLogById(logId);
        if (log != null) {
            return ResponseEntity.ok(log);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 獲取用戶的操作日誌
     * GET /api/admin/logs/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<OperationLogDto>> getUserLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setUserId(userId);
        queryDto.setPage(page);
        queryDto.setSize(size);
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 獲取操作類型列表
     * GET /api/admin/logs/operation-types
     */
    @GetMapping("/operation-types")
    public ResponseEntity<String[]> getOperationTypes() {
        String[] operationTypes = {
            "COURSE_CREATE", "COURSE_UPDATE", "COURSE_DELETE", "COURSE_SCHEDULE",
            "STUDENT_CREATE", "STUDENT_UPDATE", "STUDENT_DELETE", "STUDENT_ENROLL",
            "USER_LOGIN", "USER_LOGOUT", "USER_CREATE", "USER_UPDATE",
            "DATA_EXPORT", "DATA_IMPORT", "SYSTEM_CONFIG"
        };
        return ResponseEntity.ok(operationTypes);
    }
    
    /**
     * 清理舊日誌 (僅超級管理員可用)
     * DELETE /api/admin/logs/cleanup?days=90
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldLogs(
            @RequestParam(defaultValue = "90") int days) {
        
        long deletedCount = operationLogService.cleanupOldLogs(days);
        
        Map<String, Object> result = Map.of(
            "success", true,
            "deletedCount", deletedCount,
            "message", String.format("已清理 %d 天前的日誌，共刪除 %d 條記錄", days, deletedCount)
        );
        
        return ResponseEntity.ok(result);
    }
    
    // ===== 新增：審計相關API =====
    
    /**
     * 獲取需要審查的操作日誌
     * GET /api/admin/logs/pending-approval?page=0&size=20
     */
    @GetMapping("/pending-approval")
    public ResponseEntity<Page<OperationLogDto>> getPendingApprovalLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String priority) {
        
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setRequiresApproval(true);
        queryDto.setApprovalStatus(com.eams.common.log.entity.OperationLog.ApprovalStatus.PENDING);
        queryDto.setPage(page);
        queryDto.setSize(size);
        
        if (priority != null && !priority.isEmpty()) {
            try {
                queryDto.setPriority(com.eams.common.log.entity.OperationLog.Priority.valueOf(priority.toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // 忽略無效的優先級參數
            }
        }
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 獲取高優先級待審查日誌
     * GET /api/admin/logs/high-priority-pending
     */
    @GetMapping("/high-priority-pending")
    public ResponseEntity<List<OperationLogDto>> getHighPriorityPendingLogs() {
        List<OperationLogDto> logs = operationLogService.getHighPriorityPendingApprovals();
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 獲取緊急待審查日誌
     * GET /api/admin/logs/urgent-pending
     */
    @GetMapping("/urgent-pending")
    public ResponseEntity<List<OperationLogDto>> getUrgentPendingLogs() {
        List<OperationLogDto> logs = operationLogService.getUrgentPendingApprovals();
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 獲取即將過期的審查請求
     * GET /api/admin/logs/expiring-soon
     */
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<OperationLogDto>> getExpiringSoonLogs() {
        List<OperationLogDto> logs = operationLogService.getExpiringSoonApprovals();
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 根據審查狀態查詢日誌
     * GET /api/admin/logs/by-approval-status/{status}
     */
    @GetMapping("/by-approval-status/{status}")
    public ResponseEntity<Page<OperationLogDto>> getLogsByApprovalStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            OperationLogQueryDto queryDto = new OperationLogQueryDto();
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
     * 獲取審查人的處理記錄
     * GET /api/admin/logs/approver/{approverId}/history
     */
    @GetMapping("/approver/{approverId}/history")
    public ResponseEntity<List<OperationLogDto>> getApproverHistory(@PathVariable Long approverId) {
        List<OperationLogDto> logs = operationLogService.getApproverHistory(approverId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 獲取用戶的待審查記錄
     * GET /api/admin/logs/user/{userId}/pending-approval
     */
    @GetMapping("/user/{userId}/pending-approval")
    public ResponseEntity<List<OperationLogDto>> getUserPendingApprovalLogs(@PathVariable Long userId) {
        List<OperationLogDto> logs = operationLogService.getUserPendingApprovals(userId);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 獲取審查統計報告
     * GET /api/admin/logs/approval-statistics
     */
    @GetMapping("/approval-statistics")
    public ResponseEntity<Map<String, Object>> getApprovalStatistics() {
        Map<String, Object> statistics = operationLogService.getApprovalStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 獲取審查狀態選項（用於前端下拉選單）
     * GET /api/admin/logs/approval-status-options
     */
    @GetMapping("/approval-status-options")
    public ResponseEntity<Map<String, String>> getApprovalStatusOptions() {
        Map<String, String> statusOptions = Map.of(
            "NONE", "無需審查",
            "PENDING", "待審查", 
            "APPROVED", "已批准",
            "REJECTED", "已拒絕",
            "EXPIRED", "已過期"
        );
        return ResponseEntity.ok(statusOptions);
    }
    
    /**
     * 獲取優先級選項（用於前端下拉選單）
     * GET /api/admin/logs/priority-options
     */
    @GetMapping("/priority-options")
    public ResponseEntity<Map<String, String>> getPriorityOptions() {
        Map<String, String> priorityOptions = Map.of(
            "LOW", "低",
            "NORMAL", "一般",
            "HIGH", "高",
            "URGENT", "緊急"
        );
        return ResponseEntity.ok(priorityOptions);
    }
    
    /**
     * 高級查詢：支援複雜的審計查詢條件
     * POST /api/admin/logs/advanced-search
     */
    @PostMapping("/advanced-search")
    public ResponseEntity<Page<OperationLogDto>> advancedSearch(@RequestBody OperationLogQueryDto queryDto) {
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 匯出審計報告
     * GET /api/admin/logs/export-audit-report?format=excel&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/export-audit-report")
    public ResponseEntity<Map<String, Object>> exportAuditReport(
            @RequestParam(defaultValue = "excel") String format,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        // TODO: 實現匯出功能
        Map<String, Object> result = Map.of(
            "message", "審計報告匯出功能開發中",
            "format", format,
            "startDate", startDate != null ? startDate : "未指定",
            "endDate", endDate != null ? endDate : "未指定"
        );
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 獲取審計儀表板數據
     * GET /api/admin/logs/audit-dashboard
     */
    @GetMapping("/audit-dashboard")
    public ResponseEntity<Map<String, Object>> getAuditDashboardData() {
        Map<String, Object> dashboardData = new java.util.HashMap<>();
        
        // 基本統計
        dashboardData.put("statistics", operationLogService.getApprovalStatistics());
        
        // 待審查數量
        dashboardData.put("pendingCount", operationLogService.getPendingApprovals().size());
        
        // 高優先級待審查數量  
        dashboardData.put("highPriorityCount", operationLogService.getHighPriorityPendingApprovals().size());
        
        // 緊急待審查數量
        dashboardData.put("urgentCount", operationLogService.getUrgentPendingApprovals().size());
        
        // 即將過期數量
        dashboardData.put("expiringSoonCount", operationLogService.getExpiringSoonApprovals().size());
        
        return ResponseEntity.ok(dashboardData);
    }
}