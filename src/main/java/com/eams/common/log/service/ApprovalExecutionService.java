package com.eams.common.log.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.log.entity.ApprovalRequest;
import com.eams.common.log.repository.ApprovalRequestRepository;
import com.eams.common.log.util.AutoExecutionContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;

/**
 * 事前審核自動執行服務 負責在審核通過後自動執行原本被攔截的操作
 */
@Service
@Transactional
@Slf4j
public class ApprovalExecutionService {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ApprovalRequestRepository approvalRequestRepository;

	/**
	 * 執行已批准的申請
	 * 
	 * @param approvalRequest 已批准的申請
	 * @return 執行結果
	 */
	
	public Object executeApprovedRequest(ApprovalRequest approvalRequest) {
	        AutoExecutionContext.markAutoExecution(); // ✅ 標記自動執行
		
		log.info("🚀 開始執行已批准的申請 - ID: {}, 操作: {}", approvalRequest.getRequestId(), approvalRequest.getOperationType());

		try {
			// 1. 驗證申請狀態
			if (!canExecute(approvalRequest)) {
				String error = "申請無法執行 - 狀態: " + approvalRequest.getRequestStatus();
				log.warn(error);
				markAsFailed(approvalRequest, error);
				return null;
			}

			// 2. 解析執行上下文
			ExecutionContext context = parseExecutionContext(approvalRequest);
			if (context == null) {
				String error = "解析執行上下文失敗";
				log.error(error);
				markAsFailed(approvalRequest, error);
				return null;
			}

			// 3. 標記為執行中
			markAsExecuting(approvalRequest);

			// 4. 執行原方法
			Object result = executeOriginalMethod(context);

			// 5. 標記執行成功
			markAsExecuted(approvalRequest, result);

			log.info("✅ 申請執行成功 - ID: {}", approvalRequest.getRequestId());
			return result;

		} catch (Exception e) {
			log.error("❌ 申請執行失敗 - ID: {}", approvalRequest.getRequestId(), e);
			markAsFailed(approvalRequest, e.getMessage());
			throw new RuntimeException("執行申請失敗: " + e.getMessage(), e);
		}
	}

	/**
	 * 檢查申請是否可以執行
	 */
	private boolean canExecute(ApprovalRequest request) {
		// 檢查申請狀態
		if (!ApprovalRequest.RequestStatus.APPROVED.equals(request.getRequestStatus())) {
			return false;
		}

		// 檢查是否已經執行過
		if (request.getExecutedAt() != null) {
			log.warn("申請已經執行過 - ID: {}, 執行時間: {}", request.getRequestId(), request.getExecutedAt());
			return false;
		}

		// 檢查是否過期
		if (request.getExpiresAt() != null && LocalDateTime.now().isAfter(request.getExpiresAt())) {
			log.warn("申請已過期 - ID: {}, 過期時間: {}", request.getRequestId(), request.getExpiresAt());
			return false;
		}

		return true;
	}

	/**
	 * 解析執行上下文
	 */
	private ExecutionContext parseExecutionContext(ApprovalRequest request) {
		try {
			String contextJson = request.getExecutionContext();
			if (contextJson == null || contextJson.trim().isEmpty()) {
				log.error("執行上下文為空");
				return null;
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> contextMap = objectMapper.readValue(contextJson, Map.class);

			ExecutionContext context = new ExecutionContext();
			context.targetBeanName = (String) contextMap.get("targetBeanName");
			context.methodName = (String) contextMap.get("methodName");
			context.methodParamsJson = (String) contextMap.get("methodParams");
			context.parameterTypes = parseParameterTypes(contextMap.get("parameterTypes"));
			context.returnType = (String) contextMap.get("returnType");

			// 驗證必要欄位
			if (context.targetBeanName == null || context.methodName == null) {
				log.error("執行上下文缺少必要欄位 - Bean: {}, Method: {}", context.targetBeanName, context.methodName);
				return null;
			}

			log.debug("✅ 執行上下文解析成功 - Bean: {}, Method: {}", context.targetBeanName, context.methodName);

			return context;

		} catch (Exception e) {
			log.error("解析執行上下文失敗", e);
			return null;
		}
	}

	/**
	 * 解析參數類型
	 */
	private String[] parseParameterTypes(Object parameterTypesObj) {
		try {
			if (parameterTypesObj instanceof String[]) {
				return (String[]) parameterTypesObj;
			} else if (parameterTypesObj instanceof java.util.List) {
				@SuppressWarnings("unchecked")
				java.util.List<String> typesList = (java.util.List<String>) parameterTypesObj;
				return typesList.toArray(new String[0]);
			}
			return new String[0];
		} catch (Exception e) {
			log.warn("解析參數類型失敗", e);
			return new String[0];
		}
	}

	/**
	 * 執行原方法
	 */
	private Object executeOriginalMethod(ExecutionContext context) throws Exception {
		// 1. 獲取目標 Bean
		Object targetBean = getTargetBean(context.targetBeanName);
		if (targetBean == null) {
			throw new IllegalStateException("無法找到目標Bean: " + context.targetBeanName);
		}

		// 2. 反序列化方法參數
		Object[] methodParams = deserializeMethodParams(context.methodParamsJson);

		// 3. 獲取方法
		Method targetMethod = findTargetMethod(targetBean, context.methodName, methodParams);
		if (targetMethod == null) {
			throw new NoSuchMethodException("無法找到目標方法: " + context.methodName);
		}

		// 4. 執行方法
		log.debug("🔧 執行方法 - Bean: {}, Method: {}, Params: {}", context.targetBeanName, context.methodName,
				Arrays.toString(methodParams));

		targetMethod.setAccessible(true);
		return targetMethod.invoke(targetBean, methodParams);
	}

	/**
	 * 獲取目標 Bean
	 */
	private Object getTargetBean(String beanName) {
		try {
			if (applicationContext.containsBean(beanName)) {
				return applicationContext.getBean(beanName);
			} else {
				log.warn("Bean 不存在: {}", beanName);
				return null;
			}
		} catch (Exception e) {
			log.error("獲取Bean失敗: {}", beanName, e);
			return null;
		}
	}

	/**
	 * 反序列化方法參數
	 */
	private Object[] deserializeMethodParams(String paramsJson) {
		try {
			if (paramsJson == null || paramsJson.trim().isEmpty() || "[]".equals(paramsJson)) {
				return new Object[0];
			}

			// 使用 ObjectMapper 反序列化為 Object[]
			return objectMapper.readValue(paramsJson, Object[].class);

		} catch (Exception e) {
			log.warn("反序列化方法參數失敗: {}", paramsJson, e);
			return new Object[0];
		}
	}

	/**
	 * 查找目標方法
	 */
	private Method findTargetMethod(Object targetBean, String methodName, Object[] params) {
		try {
			Class<?> targetClass = targetBean.getClass();
			Method[] methods = targetClass.getMethods();

			// 1. 先根據方法名和參數數量過濾
			for (Method method : methods) {
				if (method.getName().equals(methodName) && method.getParameterCount() == params.length) {

					// 2. 檢查參數類型是否匹配
					if (isParametersMatching(method, params)) {
						log.debug("✅ 找到匹配的方法: {}", method);
						return method;
					}
				}
			}

			// 3. 如果精確匹配失敗，嘗試寬鬆匹配
			for (Method method : methods) {
				if (method.getName().equals(methodName) && method.getParameterCount() == params.length) {
					log.debug("⚠️ 使用寬鬆匹配的方法: {}", method);
					return method;
				}
			}

			log.error("找不到匹配的方法 - 方法名: {}, 參數數量: {}", methodName, params.length);
			return null;

		} catch (Exception e) {
			log.error("查找目標方法失敗", e);
			return null;
		}
	}

	/**
	 * 檢查參數類型是否匹配
	 */
	private boolean isParametersMatching(Method method, Object[] params) {
		Class<?>[] parameterTypes = method.getParameterTypes();

		if (parameterTypes.length != params.length) {
			return false;
		}

		for (int i = 0; i < parameterTypes.length; i++) {
			if (params[i] == null) {
				continue; // null 值可以匹配任何引用類型
			}

			Class<?> expectedType = parameterTypes[i];
			Class<?> actualType = params[i].getClass();

			// 檢查是否可以賦值
			if (!isAssignable(expectedType, actualType)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 檢查類型是否可以賦值（包括基本類型和包裝類型）
	 */
	private boolean isAssignable(Class<?> expectedType, Class<?> actualType) {
		if (expectedType.isAssignableFrom(actualType)) {
			return true;
		}

		// 處理基本類型和包裝類型的轉換
		if (expectedType.isPrimitive()) {
			if (expectedType == int.class && actualType == Integer.class)
				return true;
			if (expectedType == long.class && actualType == Long.class)
				return true;
			if (expectedType == double.class && actualType == Double.class)
				return true;
			if (expectedType == float.class && actualType == Float.class)
				return true;
			if (expectedType == boolean.class && actualType == Boolean.class)
				return true;
			if (expectedType == char.class && actualType == Character.class)
				return true;
			if (expectedType == byte.class && actualType == Byte.class)
				return true;
			if (expectedType == short.class && actualType == Short.class)
				return true;
		}

		// 數字類型的寬鬆匹配
		if (Number.class.isAssignableFrom(expectedType) && Number.class.isAssignableFrom(actualType)) {
			return true;
		}

		return false;
	}

	/**
	 * 標記為執行中
	 */
	private void markAsExecuting(ApprovalRequest request) {
		try {
			// 如果有 execution_status 欄位的話
			// request.setExecutionStatus("EXECUTING");
			approvalRequestRepository.save(request);
			log.debug("申請已標記為執行中 - ID: {}", request.getRequestId());
		} catch (Exception e) {
			log.warn("標記執行中狀態失敗", e);
		}
	}

	/**
	 * 標記為執行成功
	 */
	private void markAsExecuted(ApprovalRequest request, Object result) {
		try {
			request.setExecutedAt(LocalDateTime.now());

			// 保存執行結果
			if (result != null) {
				String resultJson = objectMapper.writeValueAsString(result);
				request.setExecutionResult(resultJson);
			}

			approvalRequestRepository.save(request);
			log.debug("申請已標記為執行成功 - ID: {}", request.getRequestId());

		} catch (Exception e) {
			log.error("標記執行成功狀態失敗", e);
		}
	}

	/**
	 * 標記為執行失敗
	 */
	private void markAsFailed(ApprovalRequest request, String errorMessage) {
		try {
			request.setExecutedAt(LocalDateTime.now());
			request.setExecutionError(errorMessage);

			approvalRequestRepository.save(request);
			log.debug("申請已標記為執行失敗 - ID: {}, 錯誤: {}", request.getRequestId(), errorMessage);

		} catch (Exception e) {
			log.error("標記執行失敗狀態失敗", e);
		}
	}

	/**
	 * 執行上下文內部類
	 */
	private static class ExecutionContext {
		String targetBeanName;
		String methodName;
		String methodParamsJson;
		String[] parameterTypes;
		String returnType;

		@Override
		public String toString() {
			return String.format("ExecutionContext{beanName='%s', methodName='%s', paramCount=%d}", targetBeanName,
					methodName, parameterTypes != null ? parameterTypes.length : 0);
		}
	}
}