package com.eams.common.Support.Services;

import com.eams.common.Support.entity.Notification;
import com.eams.Repository.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Arrays;

@Service
public class SmsNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(SmsNotificationService.class);
    
    @Autowired
    private MemberRepository memberRepository;
    
    public void sendNotification(Notification notification) {
        try {
            // 獲取目標用戶的手機號碼
            List<String> phoneNumbers = getRecipientPhoneNumbers(notification);
            
            if (phoneNumbers.isEmpty()) {
                logger.warn("No phone numbers found for SMS notification: {}", notification.getId());
                return;
            }
            
            // 發送 SMS 到每個手機號碼
            for (String phoneNumber : phoneNumbers) {
                sendSmsToNumber(notification, phoneNumber);
            }
            
            logger.info("SMS notification sent to {} recipients: {}", phoneNumbers.size(), notification.getId());
            
        } catch (Exception ex) {
            logger.error("Failed to send SMS notification: {}", notification.getId(), ex);
            throw ex;
        }
    }
    
    private List<String> getRecipientPhoneNumbers(Notification notification) {
        switch (notification.getTargetType()) {
            case "user":
                return getPhoneNumbersByUserIds(notification.getTargetUsers());
            case "role":
                return getPhoneNumbersByRoles(notification.getTargetRoles());
            case "all":
                return memberRepository.findAllActivePhoneNumbers();
            default:
                return List.of();
        }
    }
    
    private List<String> getPhoneNumbersByUserIds(String targetUsers) {
        if (targetUsers == null || targetUsers.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            List<Integer> userIds = Arrays.stream(targetUsers.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
            
            return memberRepository.findPhoneNumbersByIds(userIds);
            
        } catch (NumberFormatException ex) {
            logger.error("Invalid user IDs format: {}", targetUsers, ex);
            return List.of();
        }
    }
    
    private List<String> getPhoneNumbersByRoles(String targetRoles) {
        if (targetRoles == null || targetRoles.trim().isEmpty()) {
            return List.of();
        }
        
        List<String> roleCodes = Arrays.stream(targetRoles.split(","))
            .map(String::trim)
            .toList();
        
        return memberRepository.findPhoneNumbersByRoleCodes(roleCodes);
    }
    
    private void sendSmsToNumber(Notification notification, String phoneNumber) {
        try {
            // 這裡實作實際的 SMS 發送邏輯
            // 可以整合第三方 SMS 服務提供商如：
            // - Twilio
            // - 阿里雲短信服務
            // - 騰訊雲短信
            // - 中華電信簡訊平台
            
            String message = buildSmsMessage(notification);
            
            // 驗證手機號碼格式
            if (!isValidPhoneNumber(phoneNumber)) {
                logger.warn("Invalid phone number format: {}", phoneNumber);
                return;
            }
            
            // 模擬發送（實際實作時替換為真實的 SMS API 調用）
            logger.debug("Sending SMS to {}: {}", phoneNumber, message);
            
            // 示例：使用第三方 SMS API
            // boolean success = smsProvider.sendMessage(phoneNumber, message);
            // if (!success) {
            //     throw new RuntimeException("SMS sending failed");
            // }
            
        } catch (Exception ex) {
            logger.error("Failed to send SMS to {}: {}", phoneNumber, ex.getMessage());
            throw ex;
        }
    }
    
    private String buildSmsMessage(Notification notification) {
        StringBuilder message = new StringBuilder();
        
        // SMS 通常有字數限制，需要精簡內容
        message.append("【系統通知】");
        
        // 優先級標示
        if (notification.getPriority() != null) {
            switch (notification.getPriority().toLowerCase()) {
                case "urgent":
                    message.append("【緊急】");
                    break;
                case "high":
                    message.append("【重要】");
                    break;
            }
        }
        
        // 標題
        String title = notification.getTitle();
        if (title.length() > 20) {
            title = title.substring(0, 17) + "...";
        }
        message.append(title);
        
        // 內容（限制長度避免超過 SMS 限制）
        String content = notification.getContent();
        if (content != null && !content.trim().isEmpty()) {
            // 移除 HTML 標籤
            content = content.replaceAll("<[^>]*>", "");
            
            // 限制內容長度（考慮中文字符，一般 SMS 限制 70 字符）
            int maxContentLength = 40 - message.length();
            if (content.length() > maxContentLength) {
                content = content.substring(0, maxContentLength - 3) + "...";
            }
            message.append(" ").append(content);
        }
        
        // 如果有操作連結，添加查看詳情提示
        if (notification.getActionRequired() != null && notification.getActionRequired()) {
            message.append(" 請登入系統查看詳情。");
        }
        
        // 發送者資訊
        if (notification.getSenderName() != null && !notification.getSenderName().trim().isEmpty()) {
            message.append(" -").append(notification.getSenderName());
        }
        
        return message.toString();
    }
    
    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        
        // 基本的台灣手機號碼格式驗證
        // 支援 09XXXXXXXX 或 +886-9XXXXXXXX 格式
        String cleanNumber = phoneNumber.replaceAll("[\\s-]", "");
        
        // 台灣手機號碼格式
        return cleanNumber.matches("^09\\d{8}$") || 
               cleanNumber.matches("^\\+8869\\d{8}$") ||
               cleanNumber.matches("^8869\\d{8}$");
    }
    
    /**
     * 檢查 SMS 發送頻率限制
     * 可以整合 Redis 或資料庫來追蹤發送頻率
     */
    private boolean checkRateLimit(String phoneNumber) {
        // 實作發送頻率檢查邏輯
        // 例如：每分鐘最多 1 條，每小時最多 10 條
        return true; // 暫時返回 true
    }
    
    /**
     * 記錄 SMS 發送日誌
     */
    private void logSmsDelivery(Notification notification, String phoneNumber, boolean success, String errorMessage) {
        // 可以記錄到資料庫或日誌系統
        if (success) {
            logger.info("SMS delivered successfully - Notification: {}, Phone: {}", 
                       notification.getId(), maskPhoneNumber(phoneNumber));
        } else {
            logger.error("SMS delivery failed - Notification: {}, Phone: {}, Error: {}", 
                        notification.getId(), maskPhoneNumber(phoneNumber), errorMessage);
        }
    }
    
    /**
     * 遮罩手機號碼（隱私保護）
     */
    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 6) {
            return "***";
        }
        return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(phoneNumber.length() - 3);
    }
}