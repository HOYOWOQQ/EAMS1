package com.eams.common.log.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.eams.Entity.course.Course;
import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.course.DTO.CourseDTO;
import com.eams.Entity.course.DTO.CourseScheduleDTO;
import com.eams.Service.course.CourseEnrollService;
import com.eams.Service.course.CourseScheduleService;
import com.eams.Service.course.CourseService;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.log.annotation.LogOperation;
import com.eams.common.log.annotation.LogOperation.ApprovalMode;
import com.eams.common.log.dto.CreateApprovalRequestDto;
import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.entity.OperationLog.OperationStatus;
import com.eams.common.log.entity.ApprovalRequest;
import com.eams.common.log.entity.OperationLog;
import com.eams.common.log.entity.OperationLog.ApprovalStatus;
import com.eams.common.log.entity.OperationLog.Priority;
import com.eams.common.log.service.OperationLogService;
import com.eams.common.log.util.AutoExecutionContext;
import com.eams.common.log.util.UserContextUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eams.common.log.service.ApprovalConfigService;
import com.eams.common.log.service.ApprovalRequestService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日誌 AOP 切面 自動記錄使用 @LogOperation 註解的方法執行日誌
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

	@Autowired
	private OperationLogService operationLogService;

	@Autowired(required = false)
	private CourseScheduleService courseScheduleService;

	@Autowired(required = false)
	private CourseService courseService;

	@Autowired(required = false)
	private CourseEnrollService courseEnrollService;
	
	@Autowired(required = false)
	private ApprovalConfigService approvalConfigService;
	
	@Autowired(required = false)
	private ApprovalRequestService approvalRequestService;
	
	@Autowired(required = false)
	private ApplicationContext applicationContext;

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserContextUtil userContextUtil;
	
	@Autowired
	private PermissionChecker permissionChecker;
	

	/**
	 * 環繞通知：處理操作日誌和審計
	 */
	@Around("@annotation(logOperation)")
	public Object logOperation(ProceedingJoinPoint joinPoint, LogOperation logOperation) throws Throwable {
		
		if (AutoExecutionContext.isInAutoExecution()) {
	        log.debug("自動執行模式 → 跳過日誌與審核");
	        return joinPoint.proceed();
	    }
		
		long startTime = System.currentTimeMillis();
		HttpServletRequest request = getCurrentRequest();
		
		// 確定審計模式
		ApprovalMode finalMode = determineApprovalMode(logOperation, joinPoint);
		
		log.debug("操作審計模式確定 - 操作: {}, 模式: {}", logOperation.type(), finalMode);
		
		// 根據模式執行不同邏輯
		switch (finalMode) {
			case PRE_APPROVAL:
				return handlePreApprovalMode(joinPoint, logOperation, request, startTime);
			case POST_AUDIT:
				return handlePostAuditMode(joinPoint, logOperation, request, startTime);
			default:
				// 無需審計，直接執行
				return joinPoint.proceed();
		}
	}
	

	
	/**
	 * 確定最終的審計模式
	 */
	private ApprovalMode determineApprovalMode(LogOperation logOperation, ProceedingJoinPoint joinPoint) {
	    ApprovalMode mode = logOperation.approvalMode();
	    
	    if (mode != ApprovalMode.AUTO) {
	        return mode; // 直接返回指定模式
	    }
	    
	    // AUTO 模式：簡化判斷
	    String operationType = logOperation.type();
	    
	    // 高風險操作用事前核准，其他用事後審計
	    if (isHighRiskOperation(operationType)) {
	        return ApprovalMode.PRE_APPROVAL;
	    } else {
	        return ApprovalMode.POST_AUDIT;
	    }
	}
	
	
	
	/**
	 * 處理事前核准模式
	 */
	private Object handlePreApprovalMode(ProceedingJoinPoint joinPoint, LogOperation logOperation, 
	        HttpServletRequest request, long startTime) throws Throwable {
	    
	    log.info("🚨 事前核准模式 - 操作: {}, 用戶: {}", logOperation.type(), getCurrentUsername());
	    
	    try {
	        // 創建核准申請
	        ApprovalRequest approvalRequest = createApprovalRequest(joinPoint, logOperation, request);
	        
	        // 構造回應，告知用戶申請已提交
	        Map<String, Object> response = new HashMap<>();
	        response.put("status", "APPROVAL_REQUIRED");
	        response.put("message", "操作需要事前核准，申請已提交");
	        response.put("requestId", approvalRequest.getRequestId());
	        response.put("operationType", logOperation.type());
	        response.put("operationName", logOperation.name());
	        response.put("priority", approvalRequest.getPriority().toString());
	        response.put("expiresAt", approvalRequest.getExpiresAt());
	        
	        // 🔧 調整：使用 ApprovalConfigService 獲取預估時間
	        String estimatedTime = getEstimatedApprovalTimeForRequest(approvalRequest);
	        response.put("estimatedApprovalTime", estimatedTime);
	        
	        // 記錄申請提交日誌（事後審計模式）
	        recordSubmissionLog(joinPoint, logOperation, request, startTime, response, approvalRequest);
	        
	        // 返回包裝的 ResponseEntity
	        return createApprovalRequiredResponse(response);
	        
	    } catch (Exception e) {
	        log.error("事前核准申請創建失敗", e);
	        throw new RuntimeException("提交審批申請失敗: " + e.getMessage());
	    }
	}
	
	/**
	 * 處理事後審計模式（原有邏輯）
	 */
	private Object handlePostAuditMode(ProceedingJoinPoint joinPoint, LogOperation logOperation,
			HttpServletRequest request, long startTime) throws Throwable {
		
		Object result = null;
		Exception exception = null;
		OperationStatus status = OperationStatus.SUCCESS;

		Object oldValue = extractOldValueBeforeExecution(joinPoint, logOperation);

		try {
			log.debug("開始執行方法: {}.{}", joinPoint.getTarget().getClass().getSimpleName(),
					joinPoint.getSignature().getName());

			// 執行原方法
			result = joinPoint.proceed();

			// 檢查返回結果是否表示業務失敗
			if (isBusinessError(result)) {
				status = OperationStatus.FAILED;
				log.debug("檢測到業務錯誤，狀態設為 FAILED");
			}

			// 記錄日誌
			recordLog(joinPoint, logOperation, request, startTime, result, null, status, oldValue);

			return result;

		} catch (Exception e) {
			exception = e;
			status = OperationStatus.FAILED;

			log.debug("方法執行拋出異常: {}", e.getMessage());

			// 記錄失敗日誌
			recordLog(joinPoint, logOperation, request, startTime, null, e, status, oldValue);

			throw e;
		}
	}
	
	/**
	 * 創建核准申請 - 完整版（支援自動執行）
	 */
	private ApprovalRequest createApprovalRequest(ProceedingJoinPoint joinPoint, LogOperation logOperation, 
	        HttpServletRequest request) {
	    
	    // 建構申請數據
	    CreateApprovalRequestDto requestDto = new CreateApprovalRequestDto();
	    requestDto.setOperationType(logOperation.type());
	    requestDto.setOperationName(logOperation.name());
	    requestDto.setOperationDesc(buildDescription(logOperation, joinPoint, null));
	    requestDto.setUserId(getCurrentUserId());
	    requestDto.setUsername(getCurrentUsername());
	    requestDto.setUserRole(getCurrentUserRole());
	    requestDto.setTargetType(logOperation.targetType());
	    requestDto.setTargetId(extractTargetIdFromArgs(joinPoint.getArgs()));
	    requestDto.setTargetName("待確定"); // 在事前階段可能無法確定
	    
	    // 保存請求數據
	    Map<String, Object> requestData = new HashMap<>();
	    if (logOperation.logArgs()) {
	        requestData.put("args", joinPoint.getArgs());
	    }
	    requestData.put("methodName", joinPoint.getSignature().getName());
	    requestData.put("className", joinPoint.getTarget().getClass().getName());
	    requestDto.setRequestData(requestData);
	    
	    // 🔧 完整的執行上下文 - 支援自動執行
	    Map<String, Object> executionContext = new HashMap<>();
	    try {
	        // === 自動執行需要的核心資訊 ===
	        executionContext.put("targetBeanName", getTargetBeanName(joinPoint));
	        executionContext.put("methodName", joinPoint.getSignature().getName());
	        executionContext.put("methodParams", serializeMethodParams(joinPoint.getArgs()));
	        executionContext.put("parameterTypes", getParameterTypes(joinPoint));
	        executionContext.put("returnType", getReturnType(joinPoint));
	        
	        // === 原有的環境資訊 ===
	        executionContext.put("methodSignature", joinPoint.getSignature().toString());
	        executionContext.put("requestIp", getClientIp(request));
	        executionContext.put("userAgent", request != null ? request.getHeader("User-Agent") : null);
	        executionContext.put("requestUrl", request != null ? request.getRequestURL().toString() : null);
	        executionContext.put("requestMethod", request != null ? request.getMethod() : null);
	        
	        // === 執行環境資訊 ===
	        executionContext.put("timestamp", System.currentTimeMillis());
	        executionContext.put("sessionId", request != null && request.getSession(false) != null ? 
	            request.getSession().getId() : null);
	        
	        log.debug("✅ 執行上下文已保存 - Bean: {}, Method: {}, Params: {}", 
	            executionContext.get("targetBeanName"), 
	            executionContext.get("methodName"),
	            executionContext.get("methodParams"));
	            
	    } catch (Exception e) {
	        log.error("保存執行上下文失敗", e);
	        // 即使上下文保存失敗，也要能創建申請（降級處理）
	        executionContext.put("error", "執行上下文保存失敗: " + e.getMessage());
	        executionContext.put("methodSignature", joinPoint.getSignature().toString());
	    }
	    
	    requestDto.setExecutionContext(executionContext);
	    
	    // 🔧 調整：使用 ApprovalConfigService 設置優先級和過期時間
	    if (approvalConfigService != null) {
	        try {
	            // 獲取操作優先級
	            OperationLog.Priority logPriority = approvalConfigService.getPriority(logOperation.type());
	            ApprovalRequest.Priority reqPriority = ApprovalRequest.Priority.valueOf(logPriority.name());
	            requestDto.setPriority(reqPriority);
	            
	            // 獲取過期時間
	            LocalDateTime expiryTime = approvalConfigService.getExpiryTime(logOperation.type());
	            requestDto.setExpiresAt(expiryTime);
	            
	            log.debug("✅ 使用配置服務設置 - 優先級: {}, 過期時間: {}", reqPriority, expiryTime);
	            
	        } catch (Exception e) {
	            log.error("使用配置服務設置優先級和過期時間失敗，使用預設值", e);
	            requestDto.setPriority(ApprovalRequest.Priority.NORMAL);
	            requestDto.setExpiresAt(LocalDateTime.now().plusDays(7));
	        }
	    } else {
	        // 降級處理：配置服務不可用
	        log.warn("ApprovalConfigService 不可用，使用預設值");
	        requestDto.setPriority(ApprovalRequest.Priority.NORMAL);
	        requestDto.setExpiresAt(LocalDateTime.now().plusDays(7));
	    }
	    
	    return approvalRequestService.createRequest(requestDto);
	}

	// ===== 輔助方法：自動執行支援 =====

	/**
	 * 獲取目標 Bean 名稱
	 */
	private String getTargetBeanName(ProceedingJoinPoint joinPoint) {
	    try {
	        Object target = joinPoint.getTarget();
	        Class<?> targetClass = target.getClass();
	        
	        // 處理 CGLIB 代理類
	        if (targetClass.getName().contains("$$")) {
	            targetClass = targetClass.getSuperclass();
	        }
	        
	        // 嘗試從 Spring 容器中找到 Bean 名稱
	        if (applicationContext != null) {
	            String[] beanNames = applicationContext.getBeanNamesForType(targetClass);
	            if (beanNames.length > 0) {
	                return beanNames[0]; // 返回第一個匹配的 Bean 名稱
	            }
	        }
	        
	        // 降級：使用類名的小寫作為 Bean 名稱（Spring 預設規則）
	        String className = targetClass.getSimpleName();
	        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
	        
	    } catch (Exception e) {
	        log.warn("獲取目標Bean名稱失敗: {}", e.getMessage());
	        return "unknown";
	    }
	}

	/**
	 * 序列化方法參數
	 */
	private String serializeMethodParams(Object[] args) {
	    try {
	        if (args == null || args.length == 0) {
	            return "[]";
	        }
	        
	        // 創建可序列化的參數列表
	        Object[] serializableArgs = new Object[args.length];
	        for (int i = 0; i < args.length; i++) {
	            serializableArgs[i] = makeSerializable(args[i]);
	        }
	        
	        return objectMapper.writeValueAsString(serializableArgs);
	        
	    } catch (Exception e) {
	        log.warn("序列化方法參數失敗: {}", e.getMessage());
	        return "[\"序列化失敗: " + e.getMessage() + "\"]";
	    }
	}

	/**
	 * 將對象轉換為可序列化的格式
	 */
	private Object makeSerializable(Object obj) {
	    if (obj == null) {
	        return null;
	    }
	    
	    // 基本類型和包裝類
	    if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
	        return obj;
	    }
	    
	    // 日期類型
	    if (obj instanceof java.time.LocalDateTime || obj instanceof java.time.LocalDate) {
	        return obj.toString();
	    }
	    
	    // 數組
	    if (obj.getClass().isArray()) {
	        return Arrays.toString((Object[]) obj);
	    }
	    
	    // 集合
	    if (obj instanceof java.util.Collection || obj instanceof java.util.Map) {
	        return obj; // Jackson 可以處理
	    }
	    
	    // 複雜對象：嘗試序列化，失敗則返回類名
	    try {
	        objectMapper.writeValueAsString(obj);
	        return obj; // 可以序列化
	    } catch (Exception e) {
	        return obj.getClass().getSimpleName() + "@" + obj.hashCode();
	    }
	}

	/**
	 * 獲取參數類型
	 */
	private String[] getParameterTypes(ProceedingJoinPoint joinPoint) {
	    try {
	        Object[] args = joinPoint.getArgs();
	        if (args == null || args.length == 0) {
	            return new String[0];
	        }
	        
	        String[] types = new String[args.length];
	        for (int i = 0; i < args.length; i++) {
	            if (args[i] == null) {
	                types[i] = "java.lang.Object"; // null 值的處理
	            } else {
	                types[i] = args[i].getClass().getName();
	            }
	        }
	        
	        return types;
	        
	    } catch (Exception e) {
	        log.warn("獲取參數類型失敗: {}", e.getMessage());
	        return new String[0];
	    }
	}

	/**
	 * 獲取返回類型
	 */
	private String getReturnType(ProceedingJoinPoint joinPoint) {
	    try {
	        if (joinPoint.getSignature() instanceof org.aspectj.lang.reflect.MethodSignature) {
	            org.aspectj.lang.reflect.MethodSignature methodSignature = 
	                (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();
	            return methodSignature.getReturnType().getName();
	        }
	        return "java.lang.Object";
	    } catch (Exception e) {
	        log.warn("獲取返回類型失敗: {}", e.getMessage());
	        return "java.lang.Object";
	    }
	}

	
	
	/**
	 * 記錄申請提交日誌
	 */
	private void recordSubmissionLog(ProceedingJoinPoint joinPoint, LogOperation logOperation,
			HttpServletRequest request, long startTime, Object response, ApprovalRequest approvalRequest) {
		
		try {
			OperationLogDto logDto = OperationLogDto.builder()
					.operationType(logOperation.type() + "_SUBMISSION") // 標記為申請提交
					.operationName("提交" + logOperation.name() + "申請")
					.operationDesc("用戶提交了事前核准申請")
					.userId(getCurrentUserId())
					.username(getCurrentUsername())
					.userRole(getCurrentUserRole())
					.targetType("APPROVAL_REQUEST")
					.targetId(approvalRequest.getRequestId())
					.targetName("核准申請#" + approvalRequest.getRequestId())
					.newValue(response)
					.requestIp(getClientIp(request))
					.userAgent(request != null ? request.getHeader("User-Agent") : null)
					.requestUrl(request != null ? request.getRequestURL().toString() : null)
					.requestMethod(request != null ? request.getMethod() : null)
					.operationStatus(OperationStatus.SUCCESS)
					.executionTime((int) (System.currentTimeMillis() - startTime))
					// 申請提交本身不需要審計
					.requiresApproval(false)
					.approvalStatus(ApprovalStatus.NONE)
					.priority(Priority.NORMAL)
					.build();

			operationLogService.logOperationAsync(logDto);
			
		} catch (Exception e) {
			log.error("記錄申請提交日誌失敗", e);
		}
	}
	
	/**
	 * 創建需要核准的響應
	 */
	
	private Object createApprovalRequiredResponse(Map<String, Object> response) {
	    return org.springframework.http.ResponseEntity.ok(response);
	}
	
	/**
	 * 獲取預估審批時間
	 */
	private String getEstimatedApprovalTime(Priority priority) {
	    if (approvalConfigService != null) {
	        return approvalConfigService.getEstimatedApprovalTime(priority);
	    }
	    
	    return "7天";
	}

	/**
	 * 判斷是否為業務錯誤 根據返回值判斷業務是否成功
	 */
	private boolean isBusinessError(Object result) {
		if (result == null)
			return false;

		try {
			// 如果是 ResponseEntity，檢查狀態碼
			if (result instanceof org.springframework.http.ResponseEntity) {
				org.springframework.http.ResponseEntity<?> response = (org.springframework.http.ResponseEntity<?>) result;
				return !response.getStatusCode().is2xxSuccessful();
			}

			// 如果是 Map，檢查 status 字段
			if (result instanceof java.util.Map) {
				java.util.Map<?, ?> map = (java.util.Map<?, ?>) result;
				Object status = map.get("status");
				if (status != null) {
					return "error".equals(status.toString()) || "failed".equals(status.toString());
				}
			}

			// 其他情況可以根據需要擴展
			return false;

		} catch (Exception e) {
			log.debug("判斷業務錯誤時發生異常", e);
			return false;
		}
	}

	/**
	 * 記錄操作日誌
	 */
	private void recordLog(ProceedingJoinPoint joinPoint, LogOperation logOperation, HttpServletRequest request,
			long startTime, Object result, Exception exception, OperationStatus status, Object oldValue) {
String operationType = logOperation.type();
        
        // 🔍 添加調試日誌
        System.out.println("=== recordLog 調試 ===");
        System.out.println("操作類型: " + operationType);
        System.out.println("當前線程: " + Thread.currentThread().getName());
        System.out.println("Request 物件: " + (request != null ? "存在" : "null"));
        
        // 🔍 重點：檢查此時能否獲取用戶資訊
        Long currentUserId = getCurrentUserId();
        String currentUsername = getCurrentUsername();
        String currentUserRole = getCurrentUserRole();
        
        System.out.println("recordLog 中獲取到的用戶ID: " + currentUserId);
        System.out.println("recordLog 中獲取到的用戶名: " + currentUsername);
        System.out.println("recordLog 中獲取到的用戶角色: " + currentUserRole);
        
        // 🔍 檢查 Request 和 Session 狀態
        if (request != null) {
            HttpSession session = request.getSession(false);
            System.out.println("Session 存在: " + (session != null));
            if (session != null) {
                Object sessionUserId = session.getAttribute("id");
                Object sessionUsername = session.getAttribute("name");
                System.out.println("Session 中的用戶ID: " + sessionUserId);
                System.out.println("Session 中的用戶名: " + sessionUsername);
            }
        }
        
        // 🔍 檢查 RequestContextHolder 狀態
        try {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            System.out.println("RequestContextHolder 可用: " + (attrs != null));
            if (attrs instanceof ServletRequestAttributes) {
                HttpServletRequest contextRequest = ((ServletRequestAttributes) attrs).getRequest();
                System.out.println("從 RequestContextHolder 獲取的 Request: " + (contextRequest != null));
                
                if (contextRequest != null) {
                    HttpSession contextSession = contextRequest.getSession(false);
                    System.out.println("從 RequestContextHolder 獲取的 Session: " + (contextSession != null));
                    
                    if (contextSession != null) {
                        Object contextUserId = contextSession.getAttribute("id");
                        Object contextUsername = contextSession.getAttribute("name");
                        System.out.println("RequestContextHolder Session 用戶ID: " + contextUserId);
                        System.out.println("RequestContextHolder Session 用戶名: " + contextUsername);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("RequestContextHolder 檢查異常: " + e.getMessage());
        }
        
        System.out.println("====================");
		
		
		try {
			// 使用配置服務判斷審計規則
			ApprovalRule approvalRule = determineApprovalRule(logOperation, joinPoint, result);
			
			OperationLogDto logDto = OperationLogDto.builder()
					.operationType(logOperation.type())
					.operationName(logOperation.name())
					.operationDesc(buildDescription(logOperation, joinPoint, result))
					.userId(getCurrentUserId())
					.username(getCurrentUsername())
					.userRole(getCurrentUserRole())
					.targetType(logOperation.targetType())
					.targetId(extractTargetId(result))
					.targetName(extractTargetName(result))
					.oldValue(oldValue)
					.newValue(logOperation.logResult() ? result : null)
					.requestIp(getClientIp(request))
					.userAgent(request != null ? request.getHeader("User-Agent") : null)
					.requestUrl(request != null ? request.getRequestURL().toString() : null)
					.requestMethod(request != null ? request.getMethod() : null)
					.operationStatus(status)
					.errorMessage(exception != null ? exception.getMessage() : null)
					.executionTime((int) (System.currentTimeMillis() - startTime))
					// 審計相關欄位
					.requiresApproval(approvalRule.requiresApproval)
					.approvalStatus(approvalRule.requiresApproval ? ApprovalStatus.PENDING : ApprovalStatus.NONE)
					.priority(approvalRule.priority)
					.expiresAt(approvalRule.requiresApproval ? approvalRule.expiresAt : null)
					.build();

			operationLogService.logOperationAsync(logDto);

		} catch (Exception e) {
			log.error("記錄操作日誌時發生錯誤", e);
		}
	}
	
	/**
	 * 使用配置服務判斷審計規則
	 */
	private ApprovalRule determineApprovalRule(LogOperation logOperation, ProceedingJoinPoint joinPoint, Object result) {
	    String operationType = logOperation.type();
	    String userRole = getCurrentUserRole();
	    Object[] args = joinPoint.getArgs();
	    
	    ApprovalRule rule = new ApprovalRule();
	    
	    if (approvalConfigService != null) {
	        rule.requiresApproval = approvalConfigService.requiresApproval(operationType, userRole, args);
	        rule.priority = approvalConfigService.getPriority(operationType);
	        rule.expiresAt = approvalConfigService.getExpiryTime(operationType);
	    } else {
	        // 降級處理
	        rule.requiresApproval = true;
	        rule.priority = Priority.NORMAL;
	        rule.expiresAt = LocalDateTime.now().plusDays(7);
	    }
	    
	    return rule;
	}
	
	/**
	 * 獲取申請的預估審批時間
	 */
	private String getEstimatedApprovalTimeForRequest(ApprovalRequest approvalRequest) {
	    try {
	        if (approvalConfigService != null) {
	            // 轉換優先級類型
	            OperationLog.Priority logPriority = OperationLog.Priority.valueOf(
	                approvalRequest.getPriority().name());
	            
	            // 使用配置服務計算時間
	            return approvalConfigService.getEstimatedApprovalTime(logPriority);
	        }
	    } catch (Exception e) {
	        log.error("獲取預估審批時間失敗", e);
	    }
	    
	    // 降級處理
	    return getDefaultEstimatedTime(approvalRequest.getPriority());
	}

	/**
	 * 預設預估時間（降級邏輯）
	 */
	private String getDefaultEstimatedTime(ApprovalRequest.Priority priority) {
	    switch (priority) {
	        case URGENT: return "1天內";
	        case HIGH: return "3天內";
	        case NORMAL: return "7天內";
	        case LOW: return "14天內";
	        default: return "7天內";
	    }
	}
	
	/**
	 * 檢查是否為高風險操作
	 */
	private boolean isHighRiskOperation(String operationType) {
		return operationType.contains("DELETE") || 
			   operationType.contains("HARD") ||
			   operationType.contains("PERMANENT") ||
			   operationType.contains("FINANCIAL") ||
			   operationType.contains("SYSTEM") ||
			   operationType.contains("DATABASE");
	}

	
	
	
	
	/**
	 * 審計規則類
	 */
	private static class ApprovalRule {
		boolean requiresApproval;
		Priority priority;
		LocalDateTime expiresAt;
	}

	/**
	 * 獲取當前請求
	 */
	private HttpServletRequest getCurrentRequest() {
		try {
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
					.getRequestAttributes();
			return attributes != null ? attributes.getRequest() : null;
		} catch (Exception e) {
			log.warn("獲取當前請求失敗", e);
			return null;
		}
	}

	/**
	 * 獲取當前用戶ID
	 */
	private Long getCurrentUserId() {
		 Long userId = userContextUtil.getCurrentUserId();
		    String authType = userContextUtil.getCurrentAuthType();
		    log.debug("獲取用戶ID: {} (認證方式: {})", userId, authType);
		    return userId;
	}
	/**
	 * 獲取當前用戶名
	 */
	private String getCurrentUsername() {
		return userContextUtil.getCurrentUsername();
	}

	/**
	 * 獲取當前用戶角色
	 */
	private String getCurrentUserRole() {
		return userContextUtil.getCurrentUserRole();
	}

	/**
	 * 構建操作描述
	 */
	private String buildDescription(LogOperation logOperation, ProceedingJoinPoint joinPoint, Object result) {
		String description = logOperation.description();

		// 如果沒有自定義描述，使用默認格式
		if (description == null || description.trim().isEmpty()) {
			String methodName = joinPoint.getSignature().getName();
			String className = joinPoint.getTarget().getClass().getSimpleName();
			description = String.format("執行 %s.%s 方法", className, methodName);
		}

		// 可以在這裡添加動態參數替換邏輯
		// 例如：description = description.replace("{targetName}",
		// extractTargetName(result));

		return description;
	}

	/**
	 * 提取目標對象ID
	 */
	private Long extractTargetId(Object result) {
	    if (result == null) return null;
	    try {
	        // 第1層，判斷是否為 ResponseEntity
	        Object body = (result instanceof org.springframework.http.ResponseEntity)
	            ? ((org.springframework.http.ResponseEntity<?>) result).getBody()
	            : result;

	        // 第2層，判斷是否為 ApiResponse
	        if (body != null && "ApiResponse".equals(body.getClass().getSimpleName())) {
	            Method getData = body.getClass().getMethod("getData");
	            body = getData.invoke(body);
	        }
	        if (body == null) return null;

	        // 嘗試呼叫 getId()
	        try {
	            Method getId = body.getClass().getMethod("getId");
	            Object id = getId.invoke(body);
	            if (id instanceof Number) {
	                return ((Number) id).longValue();
	            }
	        } catch (NoSuchMethodException ignore) {}

	        // 增強：如果是 Map，檢查常用 key
	        if (body instanceof Map) {
	            Map map = (Map) body;
	            Object id = null;
	            // 常見 id key 優先順序
	            String[] keys = {"id", "memberId", "userId", "memberRoleId", "targetId"};
	            for (String k : keys) {
	                if (map.containsKey(k)) {
	                    id = map.get(k);
	                    break;
	                }
	            }
	            if (id instanceof Number) {
	                return ((Number) id).longValue();
	            } else if (id != null) {
	                // 嘗試轉型
	                try {
	                    return Long.valueOf(id.toString());
	                } catch (Exception ignore) {}
	            }
	        }

	    } catch (Exception e) {
	        log.error("extractTargetId error", e);
	    }
	    return null;
	}


	/**
	 * 提取目標對象名稱
	 */
	private String extractTargetName(Object result) {
	    if (result == null) return null;
	    try {
	        Object body = (result instanceof org.springframework.http.ResponseEntity)
	            ? ((org.springframework.http.ResponseEntity<?>) result).getBody()
	            : result;

	        if (body != null && "ApiResponse".equals(body.getClass().getSimpleName())) {
	            Method getData = body.getClass().getMethod("getData");
	            body = getData.invoke(body);
	        }
	        if (body == null) return null;

	        // 優先呼叫 getDisplayName(), getName(), getTitle()
	        for (String methodName : new String[]{"getDisplayName", "getName", "getTitle"}) {
	            try {
	                Method m = body.getClass().getMethod(methodName);
	                Object name = m.invoke(body);
	                if (name != null && !name.toString().isBlank()) {
	                    return name.toString();
	                }
	            } catch (NoSuchMethodException ignore) {}
	        }

	        // 若為 Map，搜尋常見 key
	        if (body instanceof Map) {
	            Map map = (Map) body;
	            String[] keys = {"displayName", "userName", "realName", "name", "title"};
	            for (String k : keys) {
	                Object val = map.get(k);
	                if (val != null && !val.toString().isBlank()) {
	                    return val.toString();
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("extractTargetName error", e);
	    }
	    return null;
	}



	/**
	 * 提取修改前的值（對於更新操作）
	 */
	private Object extractOldValueBeforeExecution(ProceedingJoinPoint joinPoint, LogOperation logOperation) {
		String methodName = joinPoint.getSignature().getName();
		Object[] args = joinPoint.getArgs();

		log.info("提取 old_value - 方法: {}, 參數數量: {}", methodName, args.length);
		log.info("=== 分析方法參數 ===");
		log.info("方法名: {}", methodName);
		log.info("參數數量: {}", args.length);

		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			log.info("參數[{}]: 類型={}, 內容={}", i, arg != null ? arg.getClass().getSimpleName() : "null", arg);

			// 如果是 Map，展開顯示內容
			if (arg instanceof Map) {
				Map<?, ?> map = (Map<?, ?>) arg;
				log.info("  Map 內容詳情:");
				map.forEach((key, value) -> {
					log.info("    🔑 {} -> 📄 {} (類型: {})", key, value,
							value != null ? value.getClass().getSimpleName() : "null");
				});
			}
		}
		log.info("=== 分析完成 ===");

		Long targetId = extractTargetIdFromArgs(args);

		if (methodName.contains("add")) {
			log.debug("✅ 新增操作，old_value 為 null");
			return null;
		}
		

		String targetType = logOperation.targetType();

		switch (targetType) {
		case "COURSESCHEDULE":
			return queryOldValueForCourseSchedule(targetId);
		case "COURSE":
			return queryOldValueForCourse(targetId);
		default:
			return null;
		}

	}

	/**
	 * 獲取客戶端IP地址
	 */
	private String getClientIp(HttpServletRequest request) {
		if (request == null)
			return null;

		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		// 對於多個IP的情況，取第一個非unknown的有效IP
		if (ip != null && ip.contains(",")) {
			ip = ip.split(",")[0].trim();
		}

		return ip;
	}

	// 課表舊值
	private Object queryOldValueForCourseSchedule(long id) {
		int targetId = (int) id;
		try {
			CourseSchedule entity = courseScheduleService.getCourseScheduleById(targetId);
			return entity != null ? CourseScheduleDTO.fromEntity(entity) : null;
		} catch (Exception e) {
			log.warn("查詢 CourseSchedule 失敗: id={}", targetId, e);
		}
		return null;
	}

	// 課程舊值
	private Object queryOldValueForCourse(long id) {
		int targetId = (int) id;
		try {
			Course entity = courseService.getCourseById(targetId);
			return entity != null ? CourseDTO.fromEntity(entity) : null;
		} catch (Exception e) {
			log.warn("查詢 Course 失敗: id={}", targetId, e);
		}
		return null;
	}
	
	/**
	 * 從參數中提取目標 ID（支持多種參數格式）
	 */
	private Long extractTargetIdFromArgs(Object[] args) {
	    if (args == null || args.length == 0) {
	        return null;
	    }
	    
	    log.debug("🔍 分析參數提取ID - 參數數量: {}", args.length);
	    
	    for (int i = 0; i < args.length; i++) {
	        Object arg = args[i];
	        log.debug("參數[{}]: 類型={}, 內容={}", i, 
	            arg != null ? arg.getClass().getSimpleName() : "null", arg);
	    }
	    
	    // 🎯 情況1：第一個參數直接是 Number（如您的情況）
	    if (args[0] instanceof Number) {
	        Long id = ((Number) args[0]).longValue();
	        log.debug("✅ 從第一個參數提取到ID: {}", id);
	        return id;
	    }
	    
	    // 🎯 情況2：第一個參數是 String（可以轉換為 Number）
	    if (args[0] instanceof String) {
	        try {
	            Long id = Long.parseLong((String) args[0]);
	            log.debug("✅ 從字串參數提取到ID: {}", id);
	            return id;
	        } catch (NumberFormatException e) {
	            log.debug("❌ 字串參數無法轉換為ID: {}", args[0]);
	        }
	    }
	    
	    // 🎯 情況3：第一個參數是 Map（更新操作常見）
	    if (args[0] instanceof Map) {
	        Map<?, ?> paramMap = (Map<?, ?>) args[0];
	        
	        // 嘗試不同的 ID 字段名
	        for (String idKey : Arrays.asList("id", "courseId", "scheduleId", "courseScheduleId")) {
	            Object idValue = paramMap.get(idKey);
	            if (idValue instanceof Number) {
	                Long id = ((Number) idValue).longValue();
	                log.debug("✅ 從Map參數提取到ID: {} (key: {})", id, idKey);
	                return id;
	            }
	        }
	    }
	    
	    // 🎯 情況4：第一個參數是 DTO 對象（有 getId 方法）
	    try {
	        Method getIdMethod = args[0].getClass().getMethod("getId");
	        Object idValue = getIdMethod.invoke(args[0]);
	        if (idValue instanceof Number) {
	            Long id = ((Number) idValue).longValue();
	            log.debug("✅ 從DTO對象提取到ID: {}", id);
	            return id;
	        }
	    } catch (Exception e) {
	        log.debug("❌ DTO對象沒有getId方法或調用失敗");
	    }
	    
	    // 🎯 情況5：查找其他位置的 ID 參數
	    for (int i = 1; i < args.length; i++) {
	        if (args[i] instanceof Number) {
	            Long id = ((Number) args[i]).longValue();
	            log.debug("✅ 從參數[{}]提取到ID: {}", i, id);
	            return id;
	        }
	    }
	    
	    log.debug("❌ 無法從參數中提取ID");
	    return null;
	}

}