package com.eams.common.log.service;

//Spring Framework 相關
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

//Spring Data JPA 相關
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;

//JPA Criteria API 相關 (Spring Boot 2.4+ 使用 Jakarta)
import jakarta.persistence.criteria.Predicate;

import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.dto.OperationLogQueryDto;
import com.eams.common.log.entity.OperationLog;
import com.eams.common.log.entity.OperationLog.ApprovalStatus;
import com.eams.common.log.entity.OperationLog.Priority;
import com.eams.common.log.repository.OperationLogRepository;
//Jackson JSON 處理
import com.fasterxml.jackson.databind.ObjectMapper;

//Java 基礎類別
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//Lombok 相關
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class OperationLogService {
    
    @Autowired
    private OperationLogRepository operationLogRepository;
    
    @Autowired
    private ObjectMapper objectMapper; // JSON 轉換
    
    // ===== 原有方法 =====
    
    /**
     * 記錄操作日誌
     */
    public void logOperation(OperationLogDto logDto) {
        try {
            OperationLog operationLog = OperationLog.builder()
                    .operationType(logDto.getOperationType())
                    .operationName(logDto.getOperationName())
                    .operationDesc(logDto.getOperationDesc())
                    .userId(logDto.getUserId())
                    .username(logDto.getUsername())
                    .userRole(logDto.getUserRole())
                    .targetType(logDto.getTargetType())
                    .targetId(logDto.getTargetId())
                    .targetName(logDto.getTargetName())
                    .oldValue(objectToJson(logDto.getOldValue()))
                    .newValue(objectToJson(logDto.getNewValue()))
                    .requestIp(logDto.getRequestIp())
                    .userAgent(logDto.getUserAgent())
                    .requestUrl(logDto.getRequestUrl())
                    .requestMethod(logDto.getRequestMethod())
                    .operationStatus(logDto.getOperationStatus())
                    .errorMessage(logDto.getErrorMessage())
                    .executionTime(logDto.getExecutionTime())
                    // 新增審計欄位
                    .requiresApproval(logDto.getRequiresApproval())
                    .approvalStatus(logDto.getApprovalStatus())
                    .priority(logDto.getPriority())
                    .expiresAt(logDto.getExpiresAt())
                    .approverId(logDto.getApproverId())
                    .approverName(logDto.getApproverName())
                    .approvalReason(logDto.getApprovalReason())
                    .approvedAt(logDto.getApprovedAt())
                    .build();
            
            operationLogRepository.save(operationLog);
            log.debug("操作日誌已記錄: {}", logDto.getOperationName());
            
        } catch (Exception e) {
            log.error("記錄操作日誌失敗", e);
            // 日誌記錄失敗不應該影響主要業務
        }
    }
    
    /**
     * 查詢操作日誌
     */
    public Page<OperationLogDto> getOperationLogs(OperationLogQueryDto queryDto) {
        Specification<OperationLog> spec = buildSpecification(queryDto);
        Pageable pageable = PageRequest.of(queryDto.getPage(), queryDto.getSize(), 
                                         Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<OperationLog> logPage = operationLogRepository.findAll(spec, pageable);
        return logPage.map(this::convertToDto);
    }
    
    /**
     * 根據ID獲取日誌
     */
    public OperationLogDto getLogById(Long logId) {
        return operationLogRepository.findById(logId)
                .map(this::convertToDto)
                .orElse(null);
    }
    
    /**
     * 清理舊日誌
     */
    public long cleanupOldLogs(int days) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(days);
        return operationLogRepository.deleteByCreatedAtBefore(cutoffTime);
    }
    
    /**
     * 異步記錄操作日誌
     */
    @Async("logTaskExecutor")  // 指定使用我們自定義的執行器
    public void logOperationAsync(OperationLogDto logDto) {
        logOperation(logDto);
    }
    
    /**
     * 獲取操作統計
     */
    public Map<String, Object> getOperationStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 今日操作數量
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        long todayCount = operationLogRepository.countByCreatedAtBetween(todayStart, todayEnd);
        
        // 按類型統計
        List<Object[]> typeStats = operationLogRepository.countByOperationType();
        
        // 新增：審計統計
        List<Object[]> approvalStats = operationLogRepository.countByApprovalStatus();
        List<Object[]> priorityStats = operationLogRepository.countByPriority();
        
        statistics.put("todayOperations", todayCount);
        statistics.put("operationsByType", typeStats);
        statistics.put("approvalStatusStats", approvalStats);
        statistics.put("priorityStats", priorityStats);
        
        return statistics;
    }
    
    // ===== 新增：審計相關方法 =====
    
    /**
     * 獲取所有待審查的記錄
     */
    public List<OperationLogDto> getPendingApprovals() {
        List<OperationLog> logs = operationLogRepository
                .findByRequiresApprovalTrueAndApprovalStatusOrderByPriorityDescCreatedAtAsc(ApprovalStatus.PENDING);
        return logs.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    /**
     * 分頁獲取待審查記錄
     */
    public Page<OperationLogDto> getPendingApprovals(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OperationLog> logs = operationLogRepository
                .findByRequiresApprovalTrueAndApprovalStatusOrderByPriorityDescCreatedAtAsc(
                        ApprovalStatus.PENDING, pageable);
        return logs.map(this::convertToDto);
    }
    
    /**
     * 獲取特定用戶的待審查記錄
     */
    public List<OperationLogDto> getUserPendingApprovals(Long userId) {
        List<OperationLog> logs = operationLogRepository
                .findByUserIdAndRequiresApprovalTrueAndApprovalStatusOrderByCreatedAtDesc(
                        userId, ApprovalStatus.PENDING);
        return logs.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    /**
     * 批准操作
     */
    public boolean approveOperation(Long logId, Long approverId, String approverName, String reason) {
        try {
            OperationLog operationLog = operationLogRepository.findById(logId)
                    .orElseThrow(() -> new IllegalArgumentException("操作日誌不存在"));
            
            operationLog.approve(approverId, approverName, reason);
            operationLogRepository.save(operationLog);
            
            log.info("操作已批准 - 日誌ID: {}, 審查人: {}", logId, approverName);
            return true;
            
        } catch (Exception e) {
            log.error("批准操作失敗 - 日誌ID: {}", logId, e);
            return false;
        }
    }
    
    /**
     * 拒絕操作
     */
    public boolean rejectOperation(Long logId, Long approverId, String approverName, String reason) {
        try {
            OperationLog operationLog = operationLogRepository.findById(logId)
                    .orElseThrow(() -> new IllegalArgumentException("操作日誌不存在"));
            
            operationLog.reject(approverId, approverName, reason);
            operationLogRepository.save(operationLog);
            
            log.info("操作已拒絕 - 日誌ID: {}, 審查人: {}, 原因: {}", logId, approverName, reason);
            return true;
            
        } catch (Exception e) {
            log.error("拒絕操作失敗 - 日誌ID: {}", logId, e);
            return false;
        }
    }
    
    /**
     * 獲取即將過期的審查請求（24小時內）
     */
    public List<OperationLogDto> getExpiringSoonApprovals() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusHours(24);
        
        List<OperationLog> logs = operationLogRepository
                .findExpiringSoon(ApprovalStatus.PENDING, now, expiryTime);
        return logs.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    /**
     * 處理過期的審查請求
     */
    @Transactional
    public int processExpiredApprovals() {
        LocalDateTime now = LocalDateTime.now();
        int expiredCount = operationLogRepository.markExpiredApprovals(now);
        
        if (expiredCount > 0) {
            log.info("已處理 {} 個過期的審查請求", expiredCount);
        }
        
        return expiredCount;
    }
    
    /**
     * 獲取審查人的處理記錄
     */
    public List<OperationLogDto> getApproverHistory(Long approverId) {
        List<OperationLog> logs = operationLogRepository.findByApproverIdOrderByApprovedAtDesc(approverId);
        return logs.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    /**
     * 獲取審查統計報告
     */
    public Map<String, Object> getApprovalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 審查狀態分布
        List<Object[]> statusStats = operationLogRepository.countByApprovalStatus();
        Map<String, Long> statusMap = new HashMap<>();
        for (Object[] stat : statusStats) {
            statusMap.put(stat[0].toString(), (Long) stat[1]);
        }
        
        // 優先級分布
        List<Object[]> priorityStats = operationLogRepository.countByPriority();
        Map<String, Long> priorityMap = new HashMap<>();
        for (Object[] stat : priorityStats) {
            priorityMap.put(stat[0].toString(), (Long) stat[1]);
        }
        
        // 平均審查時間（過去30天）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        LocalDateTime now = LocalDateTime.now();
        Double avgApprovalTime = operationLogRepository.getAverageApprovalTimeInHours(thirtyDaysAgo, now);
        
        // 審查人工作量（過去7天）
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> workloadStats = operationLogRepository.getApproverWorkload(sevenDaysAgo, now);
        
        stats.put("approvalStatusDistribution", statusMap);
        stats.put("priorityDistribution", priorityMap);
        stats.put("averageApprovalTimeHours", avgApprovalTime);
        stats.put("approverWorkload", workloadStats);
        
        return stats;
    }
    
    /**
     * 獲取高優先級待審查記錄
     */
    public List<OperationLogDto> getHighPriorityPendingApprovals() {
        List<OperationLog> logs = operationLogRepository
                .findByRequiresApprovalTrueAndApprovalStatusAndPriorityOrderByCreatedAtAsc(
                        ApprovalStatus.PENDING, Priority.HIGH);
        return logs.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    /**
     * 獲取緊急待審查記錄
     */
    public List<OperationLogDto> getUrgentPendingApprovals() {
        List<OperationLog> logs = operationLogRepository
                .findByRequiresApprovalTrueAndApprovalStatusAndPriorityOrderByCreatedAtAsc(
                        ApprovalStatus.PENDING, Priority.URGENT);
        return logs.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    
    /**
     * 分頁獲取所有已處理的操作日誌
     */
    public Page<OperationLogDto> getProcessedOperationLogs(int page, int size, String priority, String username) {
        // 定義所有已處理的狀態
        List<OperationLog.ApprovalStatus> processedStatuses = Arrays.asList(
            OperationLog.ApprovalStatus.APPROVED,
            OperationLog.ApprovalStatus.REJECTED,
            OperationLog.ApprovalStatus.EXPIRED
        );
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // 構建基本查詢條件
        Specification<OperationLog> spec = Specification.where(
            (root, query, criteriaBuilder) -> root.get("approvalStatus").in(processedStatuses)
        );
        
        // 只查詢需要審批的記錄
        spec = spec.and((root, query, criteriaBuilder) -> 
            criteriaBuilder.isTrue(root.get("requiresApproval"))
        );
        
        // 添加優先級過濾
        if (priority != null && !priority.isEmpty()) {
            try {
                OperationLog.Priority priorityEnum = OperationLog.Priority.valueOf(priority.toUpperCase());
                spec = spec.and((root, query, criteriaBuilder) -> 
                    criteriaBuilder.equal(root.get("priority"), priorityEnum)
                );
            } catch (IllegalArgumentException ignored) {
                // 忽略無效的優先級
            }
        }
        
        // 添加用戶名過濾
        if (username != null && !username.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), 
                    "%" + username.toLowerCase() + "%")
            );
        }
        
        Page<OperationLog> logs = operationLogRepository.findAll(spec, pageable);
        return logs.map(this::convertToDto);
    }
    
    // ===== 私有方法 =====
    
    private String objectToJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("轉換JSON失敗", e);
            return obj.toString();
        }
    }
    
    private Specification<OperationLog> buildSpecification(OperationLogQueryDto queryDto) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 原有查詢條件
            if (queryDto.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), queryDto.getUserId()));
            }
            
            if (StringUtils.hasText(queryDto.getOperationType())) {
                predicates.add(criteriaBuilder.equal(root.get("operationType"), queryDto.getOperationType()));
            }
            
            if (queryDto.getUsername() != null && !queryDto.getUsername().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("username"), "%" + queryDto.getUsername() + "%"));
            }
            
            if (queryDto.getOperationStatus() != null && !queryDto.getOperationStatus().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("operationStatus"), queryDto.getOperationStatus()));
            }
            
            if (queryDto.getStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), queryDto.getStartTime()));
            }
            
            if (queryDto.getEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), queryDto.getEndTime()));
            }
            
            // 新增審計相關查詢條件
            if (queryDto.getRequiresApproval() != null) {
                predicates.add(criteriaBuilder.equal(root.get("requiresApproval"), queryDto.getRequiresApproval()));
            }
            
            if (queryDto.getApprovalStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), queryDto.getApprovalStatus()));
            }
            
            if (queryDto.getPriority() != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), queryDto.getPriority()));
            }
            
            if (queryDto.getApproverId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("approverId"), queryDto.getApproverId()));
            }
            
            if (StringUtils.hasText(queryDto.getApproverName())) {
                predicates.add(criteriaBuilder.like(root.get("approverName"), "%" + queryDto.getApproverName() + "%"));
            }
            
            if (queryDto.getApprovalStartTime() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("approvedAt"), queryDto.getApprovalStartTime()));
            }
            
            if (queryDto.getApprovalEndTime() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("approvedAt"), queryDto.getApprovalEndTime()));
            }
            
            // 即將過期條件
            if (Boolean.TRUE.equals(queryDto.getExpiringSoon())) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime twentyFourHoursLater = now.plusHours(24);
                predicates.add(criteriaBuilder.between(root.get("expiresAt"), now, twentyFourHoursLater));
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), ApprovalStatus.PENDING));
            }
            
            // 已過期條件
            if (Boolean.TRUE.equals(queryDto.getExpired())) {
                LocalDateTime now = LocalDateTime.now();
                predicates.add(criteriaBuilder.lessThan(root.get("expiresAt"), now));
                predicates.add(criteriaBuilder.equal(root.get("approvalStatus"), ApprovalStatus.PENDING));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * 將 Entity 轉換為 DTO
     */
    private OperationLogDto convertToDto(OperationLog operationLog) {
        if (operationLog == null) {
            return null;
        }
        
        return OperationLogDto.builder()
                .logId(operationLog.getLogId())
                .operationType(operationLog.getOperationType())
                .operationName(operationLog.getOperationName())
                .operationDesc(operationLog.getOperationDesc())
                .userId(operationLog.getUserId())
                .username(operationLog.getUsername())
                .userRole(operationLog.getUserRole())
                .targetType(operationLog.getTargetType())
                .targetId(operationLog.getTargetId())
                .targetName(operationLog.getTargetName())
                // 處理 JSON 字段
                .oldValue(jsonToObject(operationLog.getOldValue()))
                .newValue(jsonToObject(operationLog.getNewValue()))
                .requestIp(operationLog.getRequestIp())
                .userAgent(operationLog.getUserAgent())
                .requestUrl(operationLog.getRequestUrl())
                .requestMethod(operationLog.getRequestMethod())
                .operationStatus(operationLog.getOperationStatus())
                .errorMessage(operationLog.getErrorMessage())
                .executionTime(operationLog.getExecutionTime())
                .createdAt(operationLog.getCreatedAt())
                // 新增審計欄位
                .requiresApproval(operationLog.getRequiresApproval())
                .approvalStatus(operationLog.getApprovalStatus())
                .priority(operationLog.getPriority())
                .expiresAt(operationLog.getExpiresAt())
                .approverId(operationLog.getApproverId())
                .approverName(operationLog.getApproverName())
                .approvalReason(operationLog.getApprovalReason())
                .approvedAt(operationLog.getApprovedAt())
                .build();
    }
    
    /**
     * 將 JSON 字串轉換為 Object
     */
    private Object jsonToObject(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            log.warn("解析JSON失敗: {}", json, e);
            return json; // 如果解析失敗，返回原始字串
        }
    }
}