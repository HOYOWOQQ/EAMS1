package com.eams.common.log.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.service.OperationLogService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 審計相關定時任務
 * 處理過期審查請求等定時操作
 */
@Component
@Slf4j
public class ApprovalScheduledTask {
    
    @Autowired
    private OperationLogService operationLogService;
    
    /**
     * 每小時檢查並處理過期的審查請求
     * 每小時的第0分執行
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void processExpiredApprovals() {
        log.info("=== 開始處理過期審查請求 ===");
        
        try {
            int expiredCount = operationLogService.processExpiredApprovals();
            
            if (expiredCount > 0) {
                log.info("✅ 已處理 {} 個過期的審查請求", expiredCount);
            } else {
                log.debug("🔍 沒有發現過期的審查請求");
            }
            
        } catch (Exception e) {
            log.error("❌ 處理過期審查請求時發生錯誤", e);
        }
        
        log.info("=== 完成處理過期審查請求 ===");
    }
    
    /**
     * 每天早上9點發送即將過期的審查請求通知
     * 每天09:00:00執行
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendExpiringApprovalNotifications() {
        log.info("=== 開始發送即將過期的審查通知 ===");
        
        try {
            List<OperationLogDto> expiringSoonLogs = operationLogService.getExpiringSoonApprovals();
            
            if (!expiringSoonLogs.isEmpty()) {
                log.info("🔔 發現 {} 個即將過期的審查請求", expiringSoonLogs.size());
                
                // TODO: 實現郵件或其他通知機制
                for (OperationLogDto log : expiringSoonLogs) {
                    sendExpiringNotification(log);
                }
                
                log.info("✅ 已發送 {} 個即將過期通知", expiringSoonLogs.size());
            } else {
                log.debug("🔍 沒有即將過期的審查請求");
            }
            
        } catch (Exception e) {
            log.error("❌ 發送即將過期通知時發生錯誤", e);
        }
        
        log.info("=== 完成發送即將過期通知 ===");
    }
    
    /**
     * 每天凌晨2點清理舊的已完成審查記錄（可選）
     * 每天02:00:00執行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldCompletedApprovals() {
        log.info("=== 開始清理舊的已完成審查記錄 ===");
        
        try {
            // 清理90天前的已完成審查記錄
            long deletedCount = operationLogService.cleanupOldLogs(90);
            
            if (deletedCount > 0) {
                log.info("✅ 已清理 {} 條舊的審查記錄", deletedCount);
            } else {
                log.debug("🔍 沒有需要清理的舊審查記錄");
            }
            
        } catch (Exception e) {
            log.error("❌ 清理舊審查記錄時發生錯誤", e);
        }
        
        log.info("=== 完成清理舊審查記錄 ===");
    }
    
    /**
     * 每週一早上8點生成審查統計報告
     * 每週一08:00:00執行
     */
    @Scheduled(cron = "0 0 8 * * MON")
    public void generateWeeklyApprovalReport() {
        log.info("=== 開始生成週審查統計報告 ===");
        
        try {
            var statistics = operationLogService.getApprovalStatistics();
            
            log.info("📊 週審查統計報告：");
            log.info("   - 審查狀態分布: {}", statistics.get("approvalStatusDistribution"));
            log.info("   - 優先級分布: {}", statistics.get("priorityDistribution"));
            log.info("   - 平均審查時間: {} 小時", statistics.get("averageApprovalTimeHours"));
            log.info("   - 審查人工作量: {}", statistics.get("approverWorkload"));
            
            // TODO: 可以將報告發送給管理員或保存到文件
            
            log.info("✅ 週審查統計報告生成完成");
            
        } catch (Exception e) {
            log.error("❌ 生成週審查統計報告時發生錯誤", e);
        }
        
        log.info("=== 完成生成週審查統計報告 ===");
    }
    
    /**
     * 發送即將過期的通知
     * TODO: 實現實際的通知機制（郵件、簡訊、系統通知等）
     */
    private void sendExpiringNotification(OperationLogDto operationLog) {
        try {
            // 這裡應該實現實際的通知發送邏輯
            // 例如：發送郵件、簡訊、或系統內通知
            
            String message = String.format(
                "您的操作「%s」(ID: %d) 將於 %s 過期，請及時處理。優先級：%s",
                operationLog.getOperationName(),
                operationLog.getLogId(), 
                operationLog.getExpiresAt(),
                operationLog.getPriorityText()
            );
            
            log.info("📧 準備發送過期通知 - 用戶: {}, 消息: {}", operationLog.getUsername(), message);
            
            // TODO: 整合郵件服務、簡訊服務或其他通知服務
            // emailService.sendNotification(log.getUserId(), "審查即將過期", message);
            // smsService.sendNotification(log.getPhoneNumber(), message);
            // systemNotificationService.sendNotification(log.getUserId(), message);
            
        } catch (Exception e) {
            log.error("發送過期通知失敗 - 日誌ID: {}, 用戶: {}", operationLog.getLogId(), operationLog.getUsername(), e);
        }
    }
    
    /**
     * 手動觸發處理過期審查請求（用於測試或緊急情況）
     */
    public void manualProcessExpiredApprovals() {
        log.info("手動觸發處理過期審查請求");
        processExpiredApprovals();
    }
    
    /**
     * 手動觸發發送即將過期通知（用於測試）
     */
    public void manualSendExpiringNotifications() {
        log.info("手動觸發發送即將過期通知");
        sendExpiringApprovalNotifications();
    }
}