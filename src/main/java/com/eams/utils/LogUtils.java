package com.eams.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日誌工具類
 */
public class LogUtils {
    
    private static final DateTimeFormatter LOG_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 記錄信息日誌
     */
    public static void info(String message) {
        log("INFO", message);
    }
    
    /**
     * 記錄警告日誌
     */
    public static void warn(String message) {
        log("WARN", message);
    }
    
    /**
     * 記錄錯誤日誌
     */
    public static void error(String message) {
        log("ERROR", message);
    }
    
    /**
     * 記錄錯誤日誌（包含異常）
     */
    public static void error(String message, Throwable throwable) {
        log("ERROR", message + " - " + throwable.getMessage());
        throwable.printStackTrace();
    }
    
    /**
     * 記錄調試日誌
     */
    public static void debug(String message) {
        log("DEBUG", message);
    }
    
    /**
     * 通用日誌記錄方法
     */
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        String logMessage = String.format("[%s] %s - %s", timestamp, level, message);
        
        // 在實際應用中，這裡可以寫入文件或發送到日誌系統
        System.out.println(logMessage);
    }
    
    /**
     * 記錄用戶操作日誌
     */
    public static void logUserAction(String username, String action, String details) {
        String message = String.format("User [%s] performed [%s]: %s", username, action, details);
        info(message);
    }
    
    /**
     * 記錄系統事件
     */
    public static void logSystemEvent(String event, String details) {
        String message = String.format("System Event [%s]: %s", event, details);
        info(message);
    }
    
    /**
     * 記錄安全事件
     */
    public static void logSecurityEvent(String event, String userAgent, String ipAddress) {
        String message = String.format("Security Event [%s] from IP [%s] with UserAgent [%s]", 
                                      event, ipAddress, userAgent);
        warn(message);
    }
}