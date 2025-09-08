package com.eams.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import com.eams.common.Configuration.Services.ConfigurationManager;
import com.eams.common.log.entity.OperationLog.Priority;

import java.time.LocalDateTime;

/**
 * 審計配置服務 - 簡化版
 * 專注於從資料庫獲取操作優先級對應的配置數據
 */
@Service
@Slf4j
public class ApprovalConfigService {
    
    @Autowired(required = false)
    private ConfigurationManager configurationManager;
    
    /**
     * 檢查操作是否需要審計
     * 簡單判斷：如果能獲取到配置就需要審計
     */
    public boolean requiresApproval(String operationType, String userRole, Object... params) {
        log.debug("檢查操作是否需要審計 - 操作: {}, 用戶角色: {}", operationType, userRole);
        
        // 簡單邏輯：預設需要審計
        // 後續可以根據需要擴展具體的判斷邏輯
        return true;
    }
    
    /**
     * 獲取操作的優先級
     * 預設為 NORMAL，可根據操作類型調整
     */
    public Priority getPriority(String operationType) {
        log.debug("獲取操作優先級 - 操作類型: {}", operationType);
        
        // 根據操作類型返回不同優先級
        if (operationType.contains("DELETE") || operationType.contains("HARD")) {
            return Priority.HIGH;
        } else if (operationType.contains("SYSTEM") || operationType.contains("URGENT")) {
            return Priority.URGENT;
        } else if (operationType.contains("EXPORT") || operationType.contains("BATCH")) {
            return Priority.NORMAL;
        } else {
            return Priority.NORMAL;
        }
    }
    
    /**
     * 獲取操作的過期時間
     * 根據優先級從資料庫獲取對應天數
     */
    public LocalDateTime getExpiryTime(String operationType) {
        Priority priority = getPriority(operationType);
        int expireDays = getExpireDaysFromDatabase(priority);
        
        LocalDateTime expiryTime = LocalDateTime.now().plusDays(expireDays);
        log.debug("操作 {} 的過期時間: {} ({}天後)", operationType, expiryTime, expireDays);
        
        return expiryTime;
    }
    
    /**
     * 獲取預估審批時間
     */
    public String getEstimatedApprovalTime(Priority priority) {
        int expireDays = getExpireDaysFromDatabase(priority);
        return formatApprovalTime(expireDays);
    }
    
    /**
     * 從資料庫獲取優先級對應的過期天數
     * 使用 default_expire_days * priority_ratio 的方式計算
     */
    private int getExpireDaysFromDatabase(Priority priority) {
        if (configurationManager == null) {
            log.warn("ConfigurationManager 不可用，使用預設天數");
            return getDefaultExpireDays(priority);
        }
        
        try {
            // 1. 獲取基準天數 (string 類型)
            String baseDaysStr = configurationManager.getConfigAsync(
                "approval", "default_expire_days", null, String.class).join();
            
            Integer baseDays = null;
            if (baseDaysStr != null && !baseDaysStr.trim().isEmpty()) {
                try {
                    baseDays = Integer.parseInt(baseDaysStr.trim());
                } catch (NumberFormatException e) {
                    log.warn("基準天數格式錯誤: {}", baseDaysStr);
                }
            }
            
            if (baseDays == null || baseDays <= 0) {
                log.debug("未獲取到有效基準天數，使用預設值 8");
                baseDays = 8;
            }
            
            // 2. 獲取優先級係數
            Double ratio = getPriorityRatioFromDatabase(priority);
            
            // 3. 計算實際天數
            double actualDays = baseDays * ratio;
            int result = (int) Math.ceil(actualDays); // 向上取整
            
            log.debug("優先級 {} 的過期天數計算: {} * {} = {} -> {} 天", 
                priority, baseDays, ratio, actualDays, result);
            
            return result;
            
        } catch (Exception e) {
            log.error("從資料庫計算優先級 {} 的過期天數失敗", priority, e);
            return getDefaultExpireDays(priority);
        }
    }
    
    /**
     * 從資料庫獲取優先級係數
     */
    private Double getPriorityRatioFromDatabase(Priority priority) {
        String ratioKey = getPriorityRatioKey(priority);
        
        try {
            // 獲取 number 類型的係數
            Double ratio = configurationManager.getConfigAsync(
                "approval", ratioKey, null, Double.class).join();
            
            if (ratio != null && ratio > 0) {
                log.debug("從資料庫獲取到 {} 的係數: {}", priority, ratio);
                return ratio;
            } else {
                log.debug("資料庫中沒有找到 {} 的係數配置，使用預設值", priority);
                return getDefaultPriorityRatio(priority);
            }
            
        } catch (Exception e) {
            log.error("從資料庫獲取優先級 {} 的係數失敗", priority, e);
            return getDefaultPriorityRatio(priority);
        }
    }
    
    /**
     * 獲取優先級係數的配置鍵名
     */
    private String getPriorityRatioKey(Priority priority) {
        switch (priority) {
            case URGENT: return "urgent_ratio";
            case HIGH: return "high_ratio";
            case NORMAL: return "normal_ratio";
            case LOW: return "low_ratio";
            default: return "normal_ratio";
        }
    }
    
    /**
     * 獲取預設優先級係數
     */
    private Double getDefaultPriorityRatio(Priority priority) {
        switch (priority) {
            case URGENT: return 0.14; // 約1天 (8 * 0.14 = 1.12)
            case HIGH: return 0.57;   // 約4天 (8 * 0.57 = 4.56)
            case NORMAL: return 1.0;  // 基準 (8 * 1.0 = 8)
            case LOW: return 1.43;    // 約11天 (8 * 1.43 = 11.44)
            default: return 1.0;
        }
    }
    
    /**
     * 獲取預設過期天數（當資料庫獲取失敗時使用）
     */
    private int getDefaultExpireDays(Priority priority) {
        switch (priority) {
            case URGENT: return 1;   // 緊急：1天
            case HIGH: return 3;     // 高優先級：3天
            case NORMAL: return 7;   // 普通：7天
            case LOW: return 14;     // 低優先級：14天
            default: return 7;
        }
    }
    
    /**
     * 格式化審批時間顯示
     */
    private String formatApprovalTime(int days) {
        if (days <= 0) {
            return "立即處理";
        } else if (days == 1) {
            return "1天內";
        } else if (days <= 3) {
            return days + "天內";
        } else if (days <= 7) {
            return "1週內";
        } else {
            return days + "天內";
        }
    }
    
    /**
     * 檢查 ConfigurationManager 是否可用
     */
    public boolean isConfigurationManagerAvailable() {
        boolean available = configurationManager != null;
        log.debug("ConfigurationManager 可用性: {}", available);
        return available;
    }
    
    /**
     * 測試資料庫連接和配置獲取
     */
    public void testDatabaseConnection() {
        log.info("=== 測試資料庫配置獲取 ===");
        log.info("ConfigurationManager 是否可用: {}", configurationManager != null);
        
        if (configurationManager != null) {
            try {
                // 測試基準天數獲取 (String 類型)
                String baseDaysStr = configurationManager.getConfigAsync(
                    "approval", "default_expire_days", null, String.class).join();
                log.info("✅ 基準天數字串 (default_expire_days): '{}'", baseDaysStr);
                
                Integer baseDays = null;
                if (baseDaysStr != null && !baseDaysStr.trim().isEmpty()) {
                    try {
                        baseDays = Integer.parseInt(baseDaysStr.trim());
                        log.info("✅ 轉換後的基準天數: {} 天", baseDays);
                    } catch (NumberFormatException e) {
                        log.error("❌ 基準天數轉換失敗: {}", baseDaysStr);
                    }
                }
                
                // 測試每個優先級的係數獲取
                for (Priority priority : Priority.values()) {
                    try {
                        String ratioKey = getPriorityRatioKey(priority);
                        Double ratio = configurationManager.getConfigAsync(
                            "approval", ratioKey, null, Double.class).join();
                        
                        // 計算實際天數
                        int actualDays = getExpireDaysFromDatabase(priority);
                        
                        log.info("✅ {} -> {}: {} (實際: {} 天)", 
                            priority, ratioKey, ratio, actualDays);
                            
                    } catch (Exception e) {
                        log.error("❌ {} 配置獲取失敗: {}", priority, e.getMessage());
                    }
                }
                
                // 測試完整流程
                log.info("--- 完整流程測試 ---");
                String testOperation = "STUDENT_DELETE";
                Priority priority = getPriority(testOperation);
                LocalDateTime expiry = getExpiryTime(testOperation);
                String timeDesc = getEstimatedApprovalTime(priority);
                
                log.info("操作: {} -> 優先級: {} -> 過期時間: {} -> 描述: {}", 
                    testOperation, priority, expiry, timeDesc);
                
            } catch (Exception e) {
                log.error("❌ 資料庫配置測試失敗", e);
            }
        }
        log.info("=== 測試完成 ===");
    }
}