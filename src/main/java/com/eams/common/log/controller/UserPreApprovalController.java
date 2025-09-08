package com.eams.common.log.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.common.log.entity.ApprovalRequest;
import com.eams.common.log.entity.ApprovalRequest.RequestStatus;
import com.eams.common.log.service.ApprovalRequestService;
import com.eams.common.log.util.UserContextUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用戶事前核准查詢控制器
 * 允許用戶查看自己提交的事前核准申請
 */
@RestController
@RequestMapping("/api/user/pre-approval")
public class UserPreApprovalController {
    
    @Autowired
    private ApprovalRequestService approvalRequestService;
    
    @Autowired
    private UserContextUtil userContextUtil;
     	
    // ===== 用戶申請查詢 =====
    
    /**
     * 獲取當前用戶的所有申請
     * GET /api/user/pre-approval/my-requests
     */
    @GetMapping("/my-requests")
    public ResponseEntity<List<ApprovalRequest>> getMyRequests() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ApprovalRequest> userRequests = approvalRequestService.getUserRequests(currentUserId);
        return ResponseEntity.ok(userRequests);
    }
    
    /**
     * 獲取當前用戶的待審查申請
     * GET /api/user/pre-approval/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ApprovalRequest>> getMyPendingRequests() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ApprovalRequest> pendingRequests = approvalRequestService.getUserPendingRequests(currentUserId);
        return ResponseEntity.ok(pendingRequests);
    }
    
    /**
     * 獲取當前用戶的申請摘要
     * GET /api/user/pre-approval/summary
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMyRequestsSummary() {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, Object> summary = new HashMap<>();
        
        // 獲取所有申請
        List<ApprovalRequest> allRequests = approvalRequestService.getUserRequests(currentUserId);
        
        // 按狀態分類統計
        Map<RequestStatus, Long> statusCounts = new HashMap<>();
        for (RequestStatus status : RequestStatus.values()) {
            statusCounts.put(status, 0L);
        }
        
        for (ApprovalRequest request : allRequests) {
            statusCounts.put(request.getRequestStatus(), 
                statusCounts.get(request.getRequestStatus()) + 1);
        }
        
        // 構建摘要
        summary.put("total", allRequests.size());
        summary.put("pending", statusCounts.get(RequestStatus.PENDING));
        summary.put("approved", statusCounts.get(RequestStatus.APPROVED));
        summary.put("rejected", statusCounts.get(RequestStatus.REJECTED));
        summary.put("executed", statusCounts.get(RequestStatus.EXECUTED));
        summary.put("expired", statusCounts.get(RequestStatus.EXPIRED));
        summary.put("cancelled", statusCounts.get(RequestStatus.CANCELLED));
        summary.put("failed", statusCounts.get(RequestStatus.FAILED));
        
        // 獲取最近的申請（最多5個）
        List<ApprovalRequest> recentRequests = allRequests.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(5)
                .toList();
        
        summary.put("recentRequests", recentRequests);
        
        return ResponseEntity.ok(summary);
    }
    
    /**
     * 獲取特定申請的詳情
     * GET /api/user/pre-approval/{requestId}
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApprovalRequest> getMyRequestDetail(@PathVariable Long requestId) {
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<ApprovalRequest> request = approvalRequestService.getRequestById(requestId);
        
        if (request.isPresent()) {
            ApprovalRequest req = request.get();
            
            // 確保用戶只能查看自己的申請
            if (currentUserId.equals(req.getUserId())) {
                return ResponseEntity.ok(req);
            } else {
            	return ResponseEntity.status(403).build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 獲取申請狀態說明
     * GET /api/user/pre-approval/status-help
     */
    @GetMapping("/status-help")
    public ResponseEntity<Map<String, Object>> getStatusHelp() {
        Map<String, Object> helpInfo = new HashMap<>();
        
        helpInfo.put("title", "申請狀態說明");
        helpInfo.put("description", "事前核准申請的各種狀態及其含義");
        
        Map<String, Map<String, String>> statusExplanation = new HashMap<>();
        
        statusExplanation.put("PENDING", Map.of(
            "name", "待審查",
            "description", "您的申請已提交，正在等待管理員審查",
            "action", "請耐心等待，您可以隨時查看申請狀態"
        ));
        
        statusExplanation.put("APPROVED", Map.of(
            "name", "已批准",
            "description", "您的申請已被批准，系統將自動執行操作",
            "action", "操作將在近期執行，請查看執行結果"
        ));
        
        statusExplanation.put("REJECTED", Map.of(
            "name", "已拒絕",
            "description", "您的申請被拒絕，操作不會執行",
            "action", "請查看拒絕原因，必要時可重新提交申請"
        ));
        
        statusExplanation.put("EXECUTED", Map.of(
            "name", "已執行",
            "description", "申請已批准且操作已成功執行",
            "action", "操作已完成，您可以查看執行結果"
        ));
        
        statusExplanation.put("FAILED", Map.of(
            "name", "執行失敗",
            "description", "申請已批准但操作執行時失敗",
            "action", "請聯繫系統管理員處理執行錯誤"
        ));
        
        statusExplanation.put("EXPIRED", Map.of(
            "name", "已過期",
            "description", "申請在審查期限內未被處理，已自動過期",
            "action", "如仍需執行操作，請重新提交申請"
        ));
        
        statusExplanation.put("CANCELLED", Map.of(
            "name", "已取消",
            "description", "申請已被取消，操作不會執行",
            "action", "如需重新申請，請提交新的申請"
        ));
        
        helpInfo.put("statusExplanation", statusExplanation);
        
        Map<String, String> priorityExplanation = new HashMap<>();
        priorityExplanation.put("URGENT", "緊急 - 通常在2-4小時內處理");
        priorityExplanation.put("HIGH", "高優先級 - 通常在4-12小時內處理");
        priorityExplanation.put("NORMAL", "一般優先級 - 通常在1-2天內處理");
        priorityExplanation.put("LOW", "低優先級 - 通常在3-5天內處理");
        
        helpInfo.put("priorityExplanation", priorityExplanation);
        
        helpInfo.put("tips", new String[]{
            "事前核准申請需要管理員審查後才能執行",
            "高風險操作通常需要更長的審查時間",
            "您可以隨時查看申請的進度和狀態",
            "如需取消申請，請在審查前進行操作",
            "申請被拒絕時，請仔細閱讀拒絕原因"
        });
        
        return ResponseEntity.ok(helpInfo);
    }
    
    /**
     * 取消自己的待審查申請
     * POST /api/user/pre-approval/{requestId}/cancel
     */
    @PostMapping("/{requestId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelMyRequest(
            @PathVariable Long requestId,
            @RequestBody CancelRequestDto cancelDto) {
        
        Map<String, Object> response = new HashMap<>();
        Long currentUserId = getCurrentUserId();
        
        if (currentUserId == null) {
            response.put("success", false);
            response.put("message", "用戶身份驗證失敗");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            boolean success = approvalRequestService.cancelRequest(
                    requestId, currentUserId, cancelDto.getReason());
            
            if (success) {
                response.put("success", true);
                response.put("message", "申請已成功取消");
                response.put("requestId", requestId);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "取消申請失敗，申請可能不存在或已無法取消");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "取消申請時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 獲取申請操作指南
     * GET /api/user/pre-approval/operation-guide
     */
    @GetMapping("/operation-guide")
    public ResponseEntity<Map<String, Object>> getOperationGuide() {
        Map<String, Object> guide = new HashMap<>();
        
        guide.put("title", "事前核准操作指南");
        guide.put("description", "需要事前核准的操作說明及申請流程");
        
        Map<String, Map<String, Object>> operationTypes = new HashMap<>();
        
        operationTypes.put("STUDENT_HARD_DELETE", Map.of(
            "name", "學生永久刪除",
            "description", "永久刪除學生的所有資料，無法恢復",
            "requiresPreApproval", true,
            "estimatedTime", "4-12小時",
            "requiredInfo", new String[]{"刪除原因", "確認已無關聯資料", "相關文件備份"}
        ));
        
        operationTypes.put("FINANCIAL_TRANSFER", Map.of(
            "name", "財務資金轉移",
            "description", "大額資金轉移或退款操作",
            "requiresPreApproval", true,
            "estimatedTime", "2-4小時",
            "requiredInfo", new String[]{"轉移金額", "轉移原因", "相關憑證"}
        ));
        
        operationTypes.put("COURSE_HARD_DELETE", Map.of(
            "name", "課程永久刪除",
            "description", "永久刪除課程及所有相關資料",
            "requiresPreApproval", true,
            "estimatedTime", "4-12小時",
            "requiredInfo", new String[]{"刪除原因", "確認無學生選課", "教材處理方案"}
        ));
        
        guide.put("operationTypes", operationTypes);
        
        guide.put("applicationProcess", new String[]{
            "1. 在系統中執行需要事前核准的操作",
            "2. 系統自動創建核准申請而不立即執行操作",
            "3. 您會收到申請已提交的確認信息",
            "4. 管理員審查您的申請",
            "5. 審查通過後，系統自動執行操作",
            "6. 您可以查看執行結果和完整過程"
        });
        
        guide.put("importantNotes", new String[]{
            "事前核准的操作通常是高風險或不可逆的操作",
            "申請提交後請耐心等待審查，避免重複提交",
            "提供準確的操作原因有助於加快審查速度",
            "緊急操作會被優先處理，但仍需要審查",
            "申請被拒絕時，請根據拒絕原因調整後重新申請"
        });
        
        return ResponseEntity.ok(guide);
    }
    
    // ===== 輔助方法 =====
    
    /**
     * 獲取當前用戶ID
     */
    private Long getCurrentUserId() {
    	return userContextUtil.getCurrentUserId();
    }
}

/**
 * 取消申請請求DTO
 */
class CancelRequestDto {
    private String reason;
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}