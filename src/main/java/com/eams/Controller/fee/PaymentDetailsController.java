package com.eams.Controller.fee;

import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;
import com.eams.Repository.fee.PaymentNoticeRepository;
import com.eams.common.Security.Services.CustomUserDetails;
import com.eams.common.Security.Services.PermissionChecker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment") // 這是供前端繳費頁面使用的 API 路徑
//@CrossOrigin(origins = "http://localhost:5173")
public class PaymentDetailsController {

    @Autowired
    private PaymentNoticeRepository paymentNoticeRepository; // 注入你的繳費單 Repository

    @Autowired
    private PermissionChecker permissionChecker;
    
    
    /**
     * 前端 API: 根據 paylink 獲取繳費單詳情
     *
     * @param token 從前端 URL 傳來的 paylink
     * @return 繳費單的詳細資訊
     */
    @GetMapping("/details")
    public ResponseEntity<?> getPaymentNoticeDetails(@RequestParam("paylink") String paylink,
    		@AuthenticationPrincipal CustomUserDetails me) {
       
    	
    	
    	
    	try {
        	
            // 1. 驗證 Token 是否有效 (簽名正確且未過期)
            if (!JwtTokenUtilForPay.isTokenValid(paylink)) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "繳費連結無效或已過期，請聯繫管理員。");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
            
           
            // 2. 從 Token 中解析出通知單編號 (noticeNo)
            String noticeNo = JwtTokenUtilForPay.getNoticeNo(paylink);
            
            if (me == null) {
                // 匿名：只用 paylink 取資料（資訊最小化）
                return paymentNoticeRepository.findByNoticeNo(noticeNo)
                       .<ResponseEntity<?>>map(ResponseEntity::ok)
                       .orElse(notFound("找不到對應的繳費單"));
            }

            

        	if(!permissionChecker.hasCurrentUserPermission("payment.view")) {
        		return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("success", false, "message", "沒有權限進行付款"));
        		
        	}
        	
            
         // 3) 只查屬於目前登入者的那一張（避免資料外洩）
            Integer currentUserId = me.getId();

            // 3. 根據 noticeNo 從資料庫查詢繳費單詳情
            Optional<FindpaymentnoticeDTO> paymentNoticeOptional = paymentNoticeRepository.findByNoticeNoAndStudentId(noticeNo ,currentUserId); // 假設 PaymentNoticeRepository 有 findByNoticeNo 方法

            if (!paymentNoticeOptional.isPresent()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "找不到對應的繳費單。");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            FindpaymentnoticeDTO paymentNotice = paymentNoticeOptional.get();
            System.out.println(paymentNotice);
            
            // 4. 返回繳費單詳情給前端
            // 注意：這裡返回的 PaymentNotice 物件應該只包含前端需要的資訊，
            // 避免暴露敏感的後台資料。你可以創建一個 DTO (Data Transfer Object) 來處理。
            return ResponseEntity.ok(paymentNotice);

        } catch (Exception e) {
            // 處理其他錯誤，例如解析 Token 失敗
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "獲取繳費單詳情失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    private ResponseEntity<?> notFound(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}