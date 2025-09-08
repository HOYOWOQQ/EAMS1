package com.eams.Controller.fee;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;
import com.eams.Repository.fee.PaymentNoticeRepository;
import com.eams.Service.wallet.StudentWalletService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/ecpay")
public class ECPayCallbackController {

	@Autowired
	PaymentNoticeRepository paymentNoticeRepository;
	
	@Autowired
	StudentWalletService studentWalletService;
	
	@PostMapping("/payment-callback")
	@Transactional
    public ResponseEntity<String> handlePaymentCallback(HttpServletRequest request,@RequestParam Map<String, String> params) {
		// 1) 取參數
	    final String rtnCode   = params.get("RtnCode");           // 1=成功
	    final String orderNo   = params.get("MerchantTradeNo");   // 交易單號
	    final String noticeNo  = params.get("CustomField1");      // 你的繳費單編號
	    final String tradeAmtS = params.get("TradeAmt");          // 綠界實收（整數字串）
	    final String payDateS  = params.get("PaymentDate");       // yyyy/MM/dd HH:mm:ss
	    final String walletS   = params.get("CustomField2");      // 你送的折抵（整數字串）

	    // TODO：驗證 CheckMacValue 與 MerchantID（務必實作）
	    if (!"1".equals(rtnCode)) return ResponseEntity.ok("1|OK");

	    // 2) 取繳費單
	    PaymentNotice notice = paymentNoticeRepository.findEntityByNoticeNo(noticeNo)
	            .orElseThrow(() -> new IllegalStateException("Notice not found: " + noticeNo));
	    // 冪等：已處理就直接返回
	    if ("paid".equalsIgnoreCase(notice.getPayStatus())) return ResponseEntity.ok("1|OK");

	    // 3) 解析金額（整數）
	    int tradeAmt = safeParseInt(tradeAmtS, 0);                     // 綠界實收
	    int net      = safeParseInt(notice.getNetAmount(), 0);         // 你的應付（資料表存字串就轉一下）
	    int walletDeduct = safeParseInt(walletS, -1);                  // -1 表示未帶

	    // 未帶 CustomField2 時，回推折抵 = net - tradeAmt
	    if (walletDeduct < 0) walletDeduct = Math.max(0, net - tradeAmt);

	    // 邊界保護
	    if (walletDeduct < 0) walletDeduct = 0;
	    if (walletDeduct > net) walletDeduct = net;

	    // 4) 扣錢包（若有折抵）
	    if (walletDeduct > 0) {
	        Integer studentId = notice.getStudentAccount().getId();
	        int balance = studentWalletService.getWalletBalance(studentId); // 你可以提供一個回傳 int 的方法
	        if (balance >= walletDeduct) {
	        	studentWalletService.debitInt(studentId, walletDeduct, "tuition_fee", notice.getId(), "學費 - "+noticeNo ,studentId,false);
	        } else {
	            // 餘額不足 → 放棄折抵以確保對帳（也可選擇回 0|Error 讓綠界重送）
	            walletDeduct = 0;
	        }
	    }

	    // 5) 更新繳費單
	    notice.setPayStatus("paid");
	    try {
	        var fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	        var ldt = java.time.LocalDateTime.parse(payDateS, fmt);
	        notice.setPayDate(ldt.toLocalDate());
	    } catch (Exception e) {
	        notice.setPayDate(java.time.LocalDate.now());
	    }
	    // 寫入此次實際使用的錢包金額（INT）
	    notice.setWalletUsedAmount(walletDeduct);
	    paymentNoticeRepository.save(notice);

	    System.out.printf("✅ ECPay OK - notice:%s trade:%d wallet:%d order:%s%n",
	            noticeNo, tradeAmt, walletDeduct, orderNo);

	    return ResponseEntity.ok("1|OK");
	}
	private int safeParseInt(String s, int def) {
	    try {
	        if (s == null || s.isBlank()) return def;
	        return Integer.parseInt(s.trim());
	    } catch (NumberFormatException e) {
	        return def;
	    }
	}
		
		
		//        String rtnCode = request.getParameter("RtnCode");
//        String orderNo = request.getParameter("MerchantTradeNo");
//        String noticeNo = params.get("CustomField1");
//        final String tradeAmtS = params.get("TradeAmt");          // 綠界實收金額
//        final String payDateS  = params.get("PaymentDate");       // yyyy/MM/dd HH:mm:ss
//        final String walletS   = params.getOrDefault("CustomField2", ""); // 推薦改送單時寫入
//        
//        // TODO: 驗證簽章、更新資料庫狀態...
//        Optional<PaymentNotice> noticeOpt = paymentNoticeRepository.findEntityByNoticeNo(noticeNo);
//
//        if (noticeOpt.isPresent()) {
//            PaymentNotice notice = noticeOpt.get();
//            notice.setPayStatus("paid");
//            notice.setPayDate(LocalDate.now()); // 如果有這個欄位
//            paymentNoticeRepository.save(notice);
////        
//        
//        System.out.println("✅ 綠界通知收到！訂單編號: " + orderNo + ", 狀態碼: " + rtnCode);
//
//        // 綠界要求成功回應必須是 "1|OK"
//    }
//        return ResponseEntity.ok("1|OK");
        
        
        
        

	@GetMapping("/check-payment-status")
    public ResponseEntity<?> checkPaymentStatus(@RequestParam("noticeNo") String noticeNo) {
        Optional<FindpaymentnoticeDTO> dtoOpt = paymentNoticeRepository.findByNoticeNo(noticeNo);

        if (dtoOpt.isPresent()) {
            FindpaymentnoticeDTO dto = dtoOpt.get();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "status", dto.getPayStatus(),
                "studentName", dto.getStudentName(),
                "amount", dto.getNetAmount()
            ));
        }
    	
    	

//            return ResponseEntity.ok(Map.of("success", true, "message", "付款狀態已更新為已付款",
//            		"status",notice.getPayStatus(),"studentName",notice.getStudentAccount().getName(),"amount",notice.getNetAmount()));
//        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("success", false, "message", "查無此繳費通知單"));
    }
	
	// 綠界回傳後，先打這支
	 
	@PostMapping("/success1")
	public void  handleECPayOrderResult(@RequestParam Map<String, String> params,
	                                   HttpServletResponse response) throws IOException {
		 System.out.println("✅ 綠界回傳參數：");
		    for (Map.Entry<String, String> entry : params.entrySet()) {
		        System.out.println(entry.getKey() + " = " + entry.getValue());
		    }
		
		String noticeNo = params.get("CustomField1");
	    

//	    // ✅ 然後手動轉跳去前端 Vue 畫面（GET）
//	    String frontendUrl = "https://893aa3b1ffc9.ngrok-free.app/payment/success?noticeNo=" + noticeNo;
	    String frontendUrl = "http://localhost:5173/payment/success?noticeNo=" + noticeNo;
	    response.sendRedirect(frontendUrl);
		
		
//		 return "<html><head><meta charset='UTF-8'>" +
//        "<script>window.location.href='" + frontendUrl + "';</script>" +
//        "</head><body>付款完成，正在跳轉...</body></html>";
	}
	
	
	



}
