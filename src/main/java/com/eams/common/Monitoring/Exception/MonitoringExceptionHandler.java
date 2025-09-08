package com.eams.common.Monitoring.Exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eams.common.ApiResponse;
import com.eams.common.Monitoring.Services.EventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 監控系統專用異常處理器
 * 整合到現有的 EAMS 全域異常處理架構中
 * 注意：這個類只處理監控系統特定的異常，其他異常由現有的 GlobalExceptionHandler 處理
 */
@RestControllerAdvice(basePackages = "com.eams.common.Monitoring")
public class MonitoringExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitoringExceptionHandler.class);
    
    @Autowired
    private EventPublisher eventPublisher;

    /**
     * 處理監控系統特定異常
     */
    @ExceptionHandler(MonitoringException.class)
    public ResponseEntity<ApiResponse<MonitoringErrorDetails>> handleMonitoringException(
            MonitoringException ex, HttpServletRequest request) {
        
        logger.error("監控系統異常: {}", ex.getMessage(), ex);
        
        // 記錄系統錯誤事件
        try {
            eventPublisher.publishSystemError(
                ex.getComponent() != null ? ex.getComponent() : "Monitoring",
                ex.getMessage(),
                ex,
                getClientIpAddress(request)
            );
        } catch (Exception eventEx) {
            logger.error("記錄監控異常事件失敗", eventEx);
        }
        
        MonitoringErrorDetails errorDetails = new MonitoringErrorDetails(
            ex.getErrorCode(),
            ex.getComponent(),
            request.getRequestURI(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getMessage(), errorDetails));
    }

    /**
     * 處理監控系統資源未找到異常
     */
    @ExceptionHandler(MonitoringResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<MonitoringErrorDetails>> handleResourceNotFoundException(
            MonitoringResourceNotFoundException ex, HttpServletRequest request) {
        
        logger.info("監控資源未找到: {}", ex.getMessage());
        
        MonitoringErrorDetails errorDetails = new MonitoringErrorDetails(
            "RESOURCE_NOT_FOUND",
            ex.getResourceType(),
            request.getRequestURI(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), errorDetails));
    }

    /**
     * 處理監控系統配置異常
     */
    @ExceptionHandler(MonitoringConfigurationException.class)
    public ResponseEntity<ApiResponse<MonitoringErrorDetails>> handleConfigurationException(
            MonitoringConfigurationException ex, HttpServletRequest request) {
        
        logger.error("監控系統配置異常: {}", ex.getMessage(), ex);
        
        // 記錄配置錯誤事件
        try {
            eventPublisher.publishSystemError(
                "Configuration",
                "監控系統配置異常: " + ex.getMessage(),
                ex,
                getClientIpAddress(request)
            );
        } catch (Exception eventEx) {
            logger.error("記錄配置異常事件失敗", eventEx);
        }
        
        MonitoringErrorDetails errorDetails = new MonitoringErrorDetails(
            "CONFIGURATION_ERROR",
            "MonitoringConfiguration",
            request.getRequestURI(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("監控系統配置錯誤", errorDetails));
    }

    /**
     * 處理監控系統安全異常
     */
    @ExceptionHandler(MonitoringSecurityException.class)
    public ResponseEntity<ApiResponse<MonitoringErrorDetails>> handleSecurityException(
            MonitoringSecurityException ex, HttpServletRequest request) {
        
        logger.warn("監控系統安全異常: {} from IP: {}", ex.getMessage(), getClientIpAddress(request));
        
        // 記錄安全事件
        try {
            eventPublisher.publishSecurityEvent(
                "MONITORING_SECURITY_VIOLATION",
                "監控系統安全異常: " + ex.getMessage(),
                getClientIpAddress(request),
                null,
                Map.of(
                    "request_uri", request.getRequestURI(),
                    "user_agent", request.getHeader("User-Agent"),
                    "exception_type", ex.getClass().getSimpleName()
                )
            );
        } catch (Exception eventEx) {
            logger.error("記錄安全異常事件失敗", eventEx);
        }
        
        MonitoringErrorDetails errorDetails = new MonitoringErrorDetails(
            "SECURITY_VIOLATION",
            "MonitoringSecurity",
            request.getRequestURI(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("監控系統存取被拒絕", errorDetails));
    }

    /**
     * 處理監控系統資料存取異常
     */
    @ExceptionHandler(MonitoringDataAccessException.class)
    public ResponseEntity<ApiResponse<MonitoringErrorDetails>> handleDataAccessException(
            MonitoringDataAccessException ex, HttpServletRequest request) {
        
        logger.error("監控系統資料存取異常: {}", ex.getMessage(), ex);
        
        // 記錄資料庫錯誤事件
        try {
            eventPublisher.publishSystemError(
                "MonitoringDatabase",
                "監控系統資料存取失敗: " + ex.getMessage(),
                ex,
                getClientIpAddress(request)
            );
        } catch (Exception eventEx) {
            logger.error("記錄資料存取異常事件失敗", eventEx);
        }
        
        MonitoringErrorDetails errorDetails = new MonitoringErrorDetails(
            "DATA_ACCESS_ERROR",
            "MonitoringDatabase",
            request.getRequestURI(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("監控系統資料操作失敗", errorDetails));
    }

    /**
     * 獲取客戶端 IP 地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    // ==================== 監控系統專用異常類別 ====================

    /**
     * 監控系統通用異常
     */
    public static class MonitoringException extends RuntimeException {
        private final String errorCode;
        private final String component;
        private final HttpStatus httpStatus;

        public MonitoringException(String message, String errorCode, String component) {
            this(message, errorCode, component, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        public MonitoringException(String message, String errorCode, String component, HttpStatus httpStatus) {
            super(message);
            this.errorCode = errorCode;
            this.component = component;
            this.httpStatus = httpStatus;
        }

        public MonitoringException(String message, Throwable cause, String errorCode, String component) {
            super(message, cause);
            this.errorCode = errorCode;
            this.component = component;
            this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        public String getErrorCode() { return errorCode; }
        public String getComponent() { return component; }
        public HttpStatus getHttpStatus() { return httpStatus; }
    }

    /**
     * 監控系統資源未找到異常
     */
    public static class MonitoringResourceNotFoundException extends RuntimeException {
        private final String resourceType;
        private final Object resourceId;

        public MonitoringResourceNotFoundException(String resourceType, Object resourceId) {
            super(String.format("監控系統中的 %s (ID: %s) 未找到", resourceType, resourceId));
            this.resourceType = resourceType;
            this.resourceId = resourceId;
        }

        public MonitoringResourceNotFoundException(String message, String resourceType, Object resourceId) {
            super(message);
            this.resourceType = resourceType;
            this.resourceId = resourceId;
        }

        public String getResourceType() { return resourceType; }
        public Object getResourceId() { return resourceId; }
    }

    /**
     * 監控系統配置異常
     */
    public static class MonitoringConfigurationException extends RuntimeException {
        public MonitoringConfigurationException(String message) {
            super(message);
        }

        public MonitoringConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 監控系統安全異常
     */
    public static class MonitoringSecurityException extends RuntimeException {
        public MonitoringSecurityException(String message) {
            super(message);
        }

        public MonitoringSecurityException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 監控系統資料存取異常
     */
    public static class MonitoringDataAccessException extends RuntimeException {
        public MonitoringDataAccessException(String message) {
            super(message);
        }

        public MonitoringDataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 監控系統錯誤詳情 DTO
     */
    public static class MonitoringErrorDetails {
        private final String errorCode;
        private final String component;
        private final String requestPath;
        private final LocalDateTime timestamp;

        public MonitoringErrorDetails(String errorCode, String component, String requestPath, LocalDateTime timestamp) {
            this.errorCode = errorCode;
            this.component = component;
            this.requestPath = requestPath;
            this.timestamp = timestamp;
        }

        public String getErrorCode() { return errorCode; }
        public String getComponent() { return component; }
        public String getRequestPath() { return requestPath; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}