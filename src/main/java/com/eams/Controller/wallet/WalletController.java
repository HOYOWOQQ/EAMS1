package com.eams.Controller.wallet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;
import com.eams.Entity.scholarship.ScholarshipGrant;
import com.eams.Entity.wallet.StudentWalletTransaction;
import com.eams.Entity.wallet.WalletOverviewDTO;
import com.eams.Entity.wallet.WalletTransactionDTO;
import com.eams.Service.scholarshipGrant.ScholarshipService;
import com.eams.Service.wallet.StudentWalletService;
import com.eams.common.Security.Services.CustomUserDetails;
import com.eams.common.Security.Services.PermissionChecker;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class WalletController {
	
	@Autowired
	private StudentWalletService studentWalletService;
	
	@Autowired
	private ScholarshipService scholarshipService;
	
	@Autowired
	private PermissionChecker permissionChecker;
	//給錢包交易記錄
	@GetMapping("/wallet/transactions")
	public ResponseEntity<?> getWalleListById(@AuthenticationPrincipal CustomUserDetails me,
			 @RequestParam(required = false) String sourceType) {
		if (me == null) {
	        return ResponseEntity.status(401)
	                .body(Map.of("success", false, "message", "尚未登入"));
	    }

	    try {
	        List<WalletTransactionDTO> result;

	        if (permissionChecker.hasCurrentUserPermission("fee.manage")) {
	            // 管理員 → 全部
	            result = studentWalletService.findWalletTxAll();
	            System.out.println(result);
	            
	        } else if (permissionChecker.hasCurrentUserPermission("payment.view")) {
	            // 學生 → 自己
	            result = studentWalletService.findWalletTxByStudent(me.getId());
	        } else {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body(Map.of("success", false, "message", "沒有權限"));
	        }

	        // ✅ 如果有帶 sourceType，就過濾
	        if (sourceType != null && !sourceType.isBlank()) {
	            String st = sourceType.trim().toLowerCase();
	            result = result.stream()
	                    .filter(tx -> tx.getSourceType() != null 
	                               && st.equals(tx.getSourceType().toLowerCase()))
	                    .toList();
	        }

	        return ResponseEntity.ok(Map.of("success", true, "response", result));

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("success", false, "message", "查詢失敗：" + e.getMessage()));
	    }
	}
	
	//錢包餘額
	@GetMapping("/wallet/balance")
	public ResponseEntity<?> getWalletBalanceById(@AuthenticationPrincipal CustomUserDetails me) {
		if (me == null) return ResponseEntity.status(401).body(Map.of("success", false, "message", "尚未登入"));
		
		if(permissionChecker.hasCurrentUserPermission("payment.view")){
			Integer walletBalance = studentWalletService.getWalletBalance(me.getId());
			return ResponseEntity.ok(Map.of("success", true, "response", walletBalance));
			
		}else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("success", false, "message", "沒有權限"));
		}
	}
	
	
	@GetMapping("/wallet/overview")
    public ResponseEntity<?> getWalletOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        WalletOverviewDTO dto = studentWalletService.getOverview(start, end);
        return ResponseEntity.ok(Map.of("success", true, "response", dto));
    }
	

	 @PostMapping("/wallet/deposit")
	    public ResponseEntity<?> deposit(
	            @AuthenticationPrincipal CustomUserDetails me,
	            @RequestBody WalletOpRequest req
	    ) {
		 
		 System.out.println(req);
		 
	        if (me == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                    .body(Map.of("success", false, "message", "尚未登入"));
	        }
	        if (!permissionChecker.hasCurrentUserPermission("fee.manage")) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body(Map.of("success", false, "message", "沒有權限"));
	        }

	        try {
	        	Integer sourceId = req.sourceId();
	        	
	        	 if ("scholarship".equalsIgnoreCase(req.sourceType()) && (sourceId == null || sourceId == 0)) {
	                 // 1. 建立 scholarship_grant 記錄
	        		 System.out.println("建立+scholarship_grant 記錄  !!!!!!");
	        		 
	                 ScholarshipGrant grant = scholarshipService.manualGrant(
	                     req.studentId(),
	                     req.amount(),
	                     req.sourceType(),
	                     req.sourceId(),
	                     req.remark(),
	                     me.getId()
	                 );
	                 
	                 
	                 sourceId = grant.getId();
	        	 }
	        	
	        	
	        	studentWalletService.deposit(
	        			req.studentId(),
	        			req.amount(),
	                    req.sourceType(),   //前端帶 scholarship
	                    sourceId,
	                    req.remark(),
	                    me.getId()              // operatorId
	            );
	            return ResponseEntity.ok(Map.of("success", true, "message", "加值完成"));
	        } catch (IllegalArgumentException iae) {
	            return ResponseEntity.badRequest().body(Map.of(
	                    "success", false,
	                    "message", iae.getMessage()
	            ));
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
	                    "success", false,
	                    "message", "加值失敗：" + e.getMessage()
	            ));
	        }
	    }
	
	  public record WalletOpRequest(Integer studentId, Integer amount, String sourceType, Integer sourceId ,String remark ) {}
	

@PostMapping("/wallet/use")
public ResponseEntity<?> use(
        @AuthenticationPrincipal CustomUserDetails me,
        @RequestBody UseRequest req
) {
    if (me == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "尚未登入"));
    }
    if (!permissionChecker.hasCurrentUserPermission("fee.manage")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "沒有權限"));
    }

    try {
//    	Integer sourceId = req.sourceId();
//    	
//   	 if ("scholarship".equalsIgnoreCase(req.sourceType()) && (sourceId == null || sourceId == 0)) {
//            // 1. 建立 scholarship_grant 記錄
//   		 System.out.println("建立+scholarship_grant 記錄  !!!!!!");
//   		 
//            ScholarshipGrant grant = scholarshipService.manualGrant(
//                req.studentId(),
//                req.amount(),
//                req.sourceType(),
//                req.sourceId(),
//                req.remark(),
//                me.getId()
//            );
//            
//            
//            sourceId = grant.getId();
//   	 }
//    	
    	
    	
    	studentWalletService.debitInt(
                req.studentId(),
                req.amount(),                // 正數；服務內會轉為負數存入
                req.sourceType(),
                req.sourceId(),
                req.remark(),
                me.getId(),
                Boolean.TRUE.equals(req.allowOverdraft())
        );
        return ResponseEntity.ok(Map.of("success", true, "message", "扣款完成"));
    } catch (IllegalArgumentException iae) {
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", iae.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "扣款失敗：" + e.getMessage()));
    }
}

@PostMapping("/wallet/adjust")
public ResponseEntity<?> adjust(
        @AuthenticationPrincipal CustomUserDetails me,
        @RequestBody AdjustRequest req
) {
    if (me == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "尚未登入"));
    }
    if (!permissionChecker.hasCurrentUserPermission("fee.manage")) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "沒有權限"));
    }

    try {
    	studentWalletService.adjust(
                req.studentId(),
                req.deltaAmount(),           // 可正可負
                me.getId(),
                req.remark(),
                req.sourceType(),
                req.sourceId()
        );
        return ResponseEntity.ok(Map.of("success", true, "message", "校正完成"));
    } catch (IllegalArgumentException iae) {
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", iae.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "校正失敗：" + e.getMessage()));
    }
}

/** 扣款請求（amount 必須為正數；後端會轉負數存入） */
public record UseRequest(
        Integer studentId,
        Integer amount,
        String sourceType,
        Integer sourceId,
        String remark,
        Boolean allowOverdraft // 是否允許扣成負餘額（預設 false）
) {}

/** 校正請求（deltaAmount 可正可負） */
public record AdjustRequest(
        Integer studentId,
        Integer deltaAmount,
        String remark,
        String sourceType,
        Integer sourceId
) {}
}

	


	

