package com.eams.common.log.dto;

import java.time.LocalDateTime;
import com.eams.common.log.entity.ApprovalRequest.Priority;
import lombok.Data;

/**
 * 創建核准申請的DTO
 */
@Data
public class CreateApprovalRequestDto {
    private String operationType;
    private String operationName;
    private String operationDesc;
    private Long userId;
    private String username;
    private String userRole;
    private String targetType;
    private Long targetId;
    private String targetName;
    private Object requestData;
    private Object executionContext;
    private Priority priority;
    private LocalDateTime expiresAt;
}