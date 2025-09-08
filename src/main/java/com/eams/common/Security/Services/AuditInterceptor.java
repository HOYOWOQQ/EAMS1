package com.eams.common.Security.Services;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eams.common.log.entity.OperationLog;
import com.eams.common.log.repository.OperationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Aspect
@Component
public class AuditInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(AuditInterceptor.class);
    
    private final OperationLogRepository logRepo;
    private final ObjectMapper objectMapper;

    public AuditInterceptor(OperationLogRepository logRepo, ObjectMapper objectMapper) {
        this.logRepo = logRepo;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(Auditable)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        HttpServletRequest request = getCurrentRequest();
        String methodName = joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            CompletableFuture.runAsync(() -> 
                logOperationAsync(joinPoint, request, methodName, "SUCCESS", null, executionTime));
            
            return result;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            CompletableFuture.runAsync(() -> 
                logOperationAsync(joinPoint, request, methodName, "FAILED", ex.getMessage(), executionTime));
            
            throw ex;
        }
    }

    private void logOperationAsync(ProceedingJoinPoint joinPoint, HttpServletRequest request, 
                                 String methodName, String status, String errorMessage, long executionTime) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationType(joinPoint.getTarget().getClass().getSimpleName());
            operationLog.setOperationName(methodName);
            operationLog.setOperationDesc("執行 " + joinPoint.getSignature().getName() + " 操作");
            operationLog.setUserId(getCurrentUserId(request));
            operationLog.setUsername(getCurrentUsername(request));
            operationLog.setUserRole(getCurrentUserRole(request));
            operationLog.setTargetType(getTargetType(joinPoint));
            operationLog.setTargetId(getTargetId(joinPoint));
            operationLog.setNewValue(getNewValue(joinPoint));
            operationLog.setRequestIp(getClientIpAddress(request));
            operationLog.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
            operationLog.setRequestUrl(request != null ? request.getRequestURI() : null);
            operationLog.setRequestMethod(request != null ? request.getMethod() : null);
//            operationLog.setOperationStatus(status);
            operationLog.setErrorMessage(errorMessage);
            operationLog.setExecutionTime((int) executionTime);
            operationLog.setCreatedAt(LocalDateTime.now());

            logRepo.save(operationLog);
        } catch (Exception ex) {
            logger.error("Failed to log operation {}", methodName, ex);
        }
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        // 從 JWT 或 Session 中獲取用戶ID
        return null; // 實作待補充
    }

    private String getCurrentUsername(HttpServletRequest request) {
        // 從 JWT 或 Session 中獲取用戶名
        return null; // 實作待補充
    }

    private String getCurrentUserRole(HttpServletRequest request) {
        // 從 JWT 或 Session 中獲取用戶角色
        return null; // 實作待補充
    }

    private String getTargetType(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        return args.length > 0 ? args[0].getClass().getSimpleName() : null;
    }

    private Long getTargetId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            try {
                Object firstArg = args[0];
                // 使用反射獲取ID屬性
                return (Long) firstArg.getClass().getMethod("getId").invoke(firstArg);
            } catch (Exception ignored) {
                // 忽略錯誤
            }
        }
        return null;
    }

    private String getNewValue(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                return objectMapper.writeValueAsString(args[0]);
            }
        } catch (Exception ignored) {
            // 忽略序列化錯誤
        }
        return null;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) return null;
        
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
