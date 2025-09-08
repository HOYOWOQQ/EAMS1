package com.eams.Controller.fee;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;

import com.eams.Entity.fee.DTO.EditPaymentNoticeDTO;
import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;
import com.eams.Service.fee.PaymentNoticeService;
import com.eams.Service.wallet.StudentWalletService;
import com.eams.common.Security.Services.CustomUserDetails;
import com.eams.common.Security.Services.PermissionChecker;

@Controller
//@CrossOrigin(origins = "http://localhost:5173")
public class PaymentNoticeController {
	
    @Autowired
    private PaymentNoticeService noticeService;

   
    @Autowired
    private PermissionChecker permissionChecker;
    
    @Autowired
    private  StudentWalletService walletService;

    
    @GetMapping("/TuitionManage")
    public String showPaymentPage() {
        return "fee/TuitionManage";
    }

    // --- API 接口 ---

    @GetMapping("/apiGetAllPayment")
    @ResponseBody
    public ResponseEntity<?> getPaymentNotices(@AuthenticationPrincipal CustomUserDetails me) {
    	if (me == null) return ResponseEntity.status(401).body(Map.of("success", false, "message", "尚未登入"));
    	
    	if(permissionChecker.hasCurrentUserPermission("fee.manage")){
//    		
    		List<FindpaymentnoticeDTO> notices = noticeService.getAllNotices();
    		return ResponseEntity.ok(notices);
    	} else if (permissionChecker.hasCurrentUserPermission("payment.view")) {
    		List<FindpaymentnoticeDTO> notices = noticeService.getpaymentNoticeByStudentId(me.getId());
    		System.out.println("登入者的id!!!!"+me.getId());
    		System.out.println(notices);
    		return ResponseEntity.ok(notices);
		} else{
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
              .body(Map.of("success", false, "message", "沒有權限"));
    	}
    	
       
    }

    // 2️⃣ 單筆新增通知單 - 再次修正版本 (根據 studentAccount 獲取學生姓名)
    /**
     * 後台 API: 新增繳費通知單並生成專屬繳費連結
     *
     * @param requestBody 繳費單的請求資料 (例如: studentId, amount, itemDetails 等)
     * @return 包含繳費連結的響應
     */
   
    @PostMapping("/apicreatePayment")
    @ResponseBody
    public ResponseEntity<?> create(
    		@RequestBody Map<String, Object> payload) {
    	
    	if(!permissionChecker.hasCurrentUserPermission("fee.manage")&& !permissionChecker.hasCurrentUserPermission("payment.view")) {
    		return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "沒有權限"));
    		
    	}
    	
        try {
        	 String operator = permissionChecker.getCurrentUser().getName();
             Integer issuedById = permissionChecker.getCurrentUser().getId();
          // 調用 Service 層
             Map<String, Object> response = noticeService.createNotice(payload, operator, issuedById);
             return ResponseEntity.ok(Map.of("success", true, "response", response));
			
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "新增失敗: " + e.getMessage()));
        }
    }
             
         
    
 // ✅ 確保 Controller 路徑正確
    
    @PutMapping("/apiEditPayment/{id}")  // 添加 {id} //operator 由Token取得傳
    @ResponseBody
    public ResponseEntity<?> update(@PathVariable Integer id,  // 添加這行
                                   @RequestBody EditPaymentNoticeDTO dto
                 
                                   ) {
    	if(!permissionChecker.hasCurrentUserPermission("fee.manage")) {
    		return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("success", false, "message", "沒有權限"));
    		
    	}
    	
        try {
        	
        	String operator= permissionChecker.getCurrentUser().getUsername();
        	
        	
            boolean success = noticeService.updatePaymentNoticeByDTO( id,dto ,  operator);
            return ResponseEntity.ok(Map.of("success",success));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "更新失敗: " + e.getMessage()));
        }
    }

    @PutMapping("/apiVoidPayment/{id}")  // 添加 {id}
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable Integer id,  // 改為 @PathVariable
                                   @RequestParam String reason,
                                  @AuthenticationPrincipal CustomUserDetails me 
                                  ) {
    	if(!permissionChecker.hasCurrentUserPermission("fee.manage")) {
//    		  return ResponseEntity.status(403).build();
    		return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("success", false, "message", "沒有權限"));
    	}
    			try {
        	
        	//後端需要operater
        	 String operator = permissionChecker.getCurrentUser().getUsername();
        	
            boolean success = noticeService.softDelete(id, reason, me.getId());
            if (success) {
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "找不到通知單或刪除失敗"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "刪除失敗: " + e.getMessage()));
        }
    }
    
    

    @GetMapping("/payment/pay-intent")
    @ResponseBody
    public Map<String, Object> payIntent(
        @RequestParam String noticeNo,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
    	
    	System.out.println("呼叫查詢餘額api!!!!!!!!");
    	
        // 1) 找到該學生與該通知單
    	Optional<FindpaymentnoticeDTO> opt = noticeService.findByNoticeNoAndStudentId(noticeNo,user.getId());
    	
    	Integer netAmount =null;
    	
    	if (opt.isPresent()) {
    	     netAmount =  Integer.parseInt( opt.get().getNetAmount()) ;
    	    // do something with amount
    	}
    	
    	
        long balance = walletService.getWalletBalance(user.getId());
 
        boolean needModal = balance > 0 && netAmount > 0;
//
        return Map.of(
            "needModal", needModal,
            "balance", balance,
            "amount", netAmount,
            "noticeNo", noticeNo
        );
    }
    
    
    @PostMapping("/api/refund/{id}")  // 添加 {id}
    @ResponseBody
    public ResponseEntity<?> refunded(@PathVariable Integer id,  // 改為 @PathVariable
                                   @RequestParam String reason,
                                  @AuthenticationPrincipal CustomUserDetails me 
                                  ) {
    	if(!permissionChecker.hasCurrentUserPermission("fee.manage")) {
//    		  return ResponseEntity.status(403).build();
    		return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of("success", false, "message", "沒有權限"));
    	}
    			try {
        	
        	 boolean success = noticeService.refund(id, reason, me.getId());
            if (success) {
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "找不到通知單或刪除失敗"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "刪除失敗: " + e.getMessage()));
        }
    }
    
    
    
    
    
    
   
    
}