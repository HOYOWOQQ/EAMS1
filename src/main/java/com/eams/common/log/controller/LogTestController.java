package com.eams.common.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.common.log.annotation.LogOperation;
import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.dto.OperationLogQueryDto;
import com.eams.common.log.service.OperationLogService;

import java.util.HashMap;
import java.util.Map;

/**
 * 日誌系統測試控制器
 * 用於驗證日誌功能是否正常工作
 */
@RestController
@RequestMapping("/api/test/log")
public class LogTestController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    /**
     * 測試成功操作日誌記錄
     */
    @GetMapping("/success")
    @LogOperation(
        type = "TEST_SUCCESS",
        name = "測試成功操作",
        description = "測試日誌系統是否能正確記錄成功操作",
        targetType = "TEST"
    )
    public ResponseEntity<Map<String, Object>> testSuccess() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "測試成功操作完成");
        result.put("timestamp", System.currentTimeMillis());
        result.put("testId", 12345L);
        result.put("testName", "成功測試");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 測試失敗操作日誌記錄 - 直接拋異常版本
     */
    @GetMapping("/error")
    @LogOperation(
        type = "TEST_ERROR",
        name = "測試錯誤操作",
        description = "測試日誌系統是否能正確記錄失敗操作",
        targetType = "TEST"
    )
    public ResponseEntity<Map<String, Object>> testError() {
        // 故意拋出異常來測試錯誤日誌記錄
        throw new RuntimeException("這是一個測試異常，用於驗證錯誤日誌記錄功能");
    }
    
    /**
     * 測試業務失敗情況 - 不拋異常版本
     */
    @GetMapping("/business-error")
    @LogOperation(
        type = "TEST_BUSINESS_ERROR",
        name = "測試業務錯誤",
        description = "測試業務失敗但不拋異常的情況",
        targetType = "TEST"
    )
    public ResponseEntity<Map<String, Object>> testBusinessError() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "error");
        result.put("message", "業務處理失敗");
        result.put("errorCode", "BUSINESS_ERROR");
        result.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 檢查日誌記錄情況
     */
    @GetMapping("/check-logs")
    public ResponseEntity<Map<String, Object>> checkLogs() {
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setPage(0);
        queryDto.setSize(20);
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalLogs", logs.getTotalElements());
        result.put("recentLogs", logs.getContent());
        result.put("message", "最近的日誌記錄");
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 測試帶參數的操作
     */
    @PostMapping("/with-params")
    @LogOperation(
        type = "TEST_PARAMS",
        name = "測試參數操作",
        description = "測試帶有請求參數的操作日誌記錄",
        targetType = "TEST"
    )
    public ResponseEntity<Map<String, Object>> testWithParams(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "帶參數測試完成");
        result.put("receivedParams", params);
        result.put("processedAt", System.currentTimeMillis());
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 測試複雜對象操作
     */
    @PostMapping("/complex")
    @LogOperation(
        type = "TEST_COMPLEX",
        name = "測試複雜對象",
        description = "測試複雜對象的日誌記錄",
        targetType = "TEST"
    )
    public ResponseEntity<TestDto> testComplexObject(@RequestBody TestDto testDto) {
        // 模擬處理邏輯
        testDto.setId(System.currentTimeMillis());
        testDto.setStatus("已處理");
        testDto.setProcessedAt(new java.util.Date());
        
        return ResponseEntity.ok(testDto);
    }
    
    /**
     * 查看最近的測試日誌
     */
    @GetMapping("/recent-logs")
    public ResponseEntity<Page<OperationLogDto>> getRecentTestLogs() {
        OperationLogQueryDto queryDto = new OperationLogQueryDto();
        queryDto.setPage(0);
        queryDto.setSize(10);
        
        // 只查看測試相關的日誌
        // 這裡可以添加過濾條件，比如 operationType 以 "TEST_" 開頭的
        
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        return ResponseEntity.ok(logs);
    }
    
    /**
     * 手動觸發日誌清理（僅用於測試）
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> testCleanup(@RequestParam(defaultValue = "30") int days) {
        long deletedCount = operationLogService.cleanupOldLogs(days);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("deletedCount", deletedCount);
        result.put("message", String.format("清理了 %d 天前的日誌，共刪除 %d 條記錄", days, deletedCount));
        
        return ResponseEntity.ok(result);
    }
}

/**
 * 測試用的 DTO 類
 */
class TestDto {
    private Long id;
    private String name;
    private String description;
    private String status;
    private java.util.Date processedAt;
    
    // 構造函數
    public TestDto() {}
    
    public TestDto(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public java.util.Date getProcessedAt() { return processedAt; }
    public void setProcessedAt(java.util.Date processedAt) { this.processedAt = processedAt; }
    
    @Override
    public String toString() {
        return String.format("TestDto{id=%d, name='%s', description='%s', status='%s', processedAt=%s}",
                id, name, description, status, processedAt);
    }
}