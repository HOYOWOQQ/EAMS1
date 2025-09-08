package com.eams.common.log.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eams.common.log.annotation.LogOperation;
import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.dto.OperationLogQueryDto;
import com.eams.common.log.entity.OperationLog.OperationStatus;
import com.eams.common.log.service.OperationLogService;

import java.util.HashMap;
import java.util.Map;

/**
 * AOP 調試控制器
 * 用於驗證 AOP 是否正常工作
 */
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    /**
     * 測試 AOP 是否工作 - 簡單測試
     */
    @GetMapping("/aop-test")
    @LogOperation(
        type = "DEBUG_AOP_TEST",
        name = "AOP調試測試",
        description = "驗證AOP切面是否正常工作",
        targetType = "DEBUG"
    )
    public ResponseEntity<Map<String, Object>> testAOP() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "AOP測試成功");
        result.put("timestamp", System.currentTimeMillis());
        result.put("aopWorking", true);
        
        System.out.println("=== AOP 測試方法執行 ===");
        System.out.println("如果您看到這條消息，說明方法確實被執行了");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 不使用 @LogOperation 註解的對照組
     */
    @GetMapping("/no-aop")
    public ResponseEntity<Map<String, Object>> testNoAOP() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "無AOP註解的方法");
        result.put("timestamp", System.currentTimeMillis());
        result.put("hasLogAnnotation", false);
        
        System.out.println("=== 無AOP註解的方法執行 ===");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 手動記錄日誌測試
     */
    @GetMapping("/manual-log")
    public ResponseEntity<Map<String, Object>> testManualLog() {
        System.out.println("=== 手動記錄日誌測試 ===");
        
        try {
            // 手動創建並記錄日誌
            OperationLogDto logDto = OperationLogDto.builder()
                    .operationType("MANUAL_TEST")
                    .operationName("手動日誌測試")
                    .operationDesc("直接調用Service記錄日誌")
                    .userId(1L)
                    .username("testUser")
                    .userRole("USER")
                    .targetType("TEST")
                    .targetId(999L)
                    .targetName("手動測試目標")
                    .operationStatus(OperationStatus.SUCCESS)
                    .executionTime(50)
                    .build();
            
            operationLogService.logOperation(logDto);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "手動記錄日誌成功");
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            System.err.println("手動記錄日誌失敗: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "手動記錄日誌失敗: " + e.getMessage());
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(result);
        }
    }
    
    /**
     * 檢查資料庫連接和表結構
     */
    @GetMapping("/db-check")
    public ResponseEntity<Map<String, Object>> checkDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 嘗試查詢日誌總數
            OperationLogQueryDto queryDto = new OperationLogQueryDto();
            queryDto.setPage(0);
            queryDto.setSize(1);
            
            org.springframework.data.domain.Page<OperationLogDto> logs = 
                operationLogService.getOperationLogs(queryDto);
            
            result.put("status", "success");
            result.put("message", "資料庫連接正常");
            result.put("totalLogs", logs.getTotalElements());
            result.put("timestamp", System.currentTimeMillis());
            
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", "資料庫連接失敗: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(500).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 全面的系統狀態檢查
     */
    @GetMapping("/system-status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        Map<String, Object> details = new HashMap<>();
        boolean allGood = true;
        
        // 檢查資料庫
        try {
            OperationLogQueryDto queryDto = new OperationLogQueryDto();
            queryDto.setSize(1);
            operationLogService.getOperationLogs(queryDto);
            details.put("database", "OK");
        } catch (Exception e) {
            details.put("database", "ERROR: " + e.getMessage());
            allGood = false;
        }
        
        // 檢查日誌服務
        try {
            operationLogService.getOperationStatistics();
            details.put("logService", "OK");
        } catch (Exception e) {
            details.put("logService", "ERROR: " + e.getMessage());
            allGood = false;
        }
        
        status.put("overall", allGood ? "HEALTHY" : "ISSUES_DETECTED");
        status.put("details", details);
        status.put("timestamp", System.currentTimeMillis());
        status.put("checkTime", new java.util.Date());
        
        return ResponseEntity.ok(status);
    }
}