package com.eams.Controller.fee;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller; // 注意這裡使用 @Controller
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody; // 用於返回字串作為響應體

import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;
import com.eams.Repository.fee.PaymentNoticeRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID; // 用於生成唯一訂單號
import java.util.stream.Collectors;

/**
 * 綠界付款 HTML 表單生成控制器
 * 該控制器直接返回綠界所需的 HTML 表單，用於客戶端自動提交跳轉至綠界支付頁面
 */
@Controller // 使用 @Controller 而不是 @RestController
@RequestMapping("/payment") // 設定基礎路徑，這裡不使用 /api/
public class EcpayHtmlController {

    @Autowired
    private PaymentNoticeRepository paymentNoticeRepo;

    // TODO: 在實際生產環境中，HashKey 和 HashIV 應該從安全配置中獲取，不應硬編碼
    // 這兩個是綠界金流串接的關鍵，必須與你的綠界特店設定一致
    private static final String ECPAY_HASH_KEY = "pwFHCqoQZGmho4w6"; // 替換為你的 HashKey
    private static final String ECPAY_HASH_IV = "EkRm7iFT261dpevs"; // 替換為你的 HashIV
    private static final String ECPAY_MERCHANT_ID = "3002607"; // 替換為你的綠界特店編號

    /**
     * 根據通知單編號組裝綠界付款 HTML 表單
     * GET /EAMS/payment/ecpay-html-form?noticeNo={noticeNo}
     *
     * @param noticeNo 繳費通知單編號
     * @return 包含綠界付款表單的 HTML 字串，或錯誤提示 HTML
     */
    @GetMapping(value = "/ecpayhtmlform", produces = MediaType.TEXT_HTML_VALUE) // 設定返回 HTML 內容
    @ResponseBody // 表示方法返回值直接作為響應體
    public String getEcpayHtmlForm(@RequestParam String noticeNo,@RequestParam Integer walletDeduct ) {

        // 1. 根據 noticeNo 查詢繳費單詳細資訊
        Optional<FindpaymentnoticeDTO> paymentNoticeOptional = paymentNoticeRepo.findByNoticeNo(noticeNo);
        if (paymentNoticeOptional.isEmpty()) {
            // 如果繳費單不存在，返回一個錯誤提示 HTML
            return buildErrorHtml("繳費單不存在或無效，請聯繫補習班。");
        }
        FindpaymentnoticeDTO paymentNotice = paymentNoticeOptional.get();

//        // 2. 進行繳費單的狀態檢查 (例如：是否已繳費、是否已過期)
//        LocalDate dueDate = LocalDate(paymentNotice.getEndDate(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//        if (LocalDate.now().isAfter(dueDate)) {
//             return buildErrorHtml("繳費單已逾期，無法付款。");
//        }
//        if ("已繳".equals(paymentNotice.getPayStatus())) { // 假設 PaymentNotice 有 status 欄位
//            return buildErrorHtml("此繳費單已完成付款，請勿重複繳費。");
//        }

        // 3. 準備綠界金流參數 (這是實際組裝的核心部分)
        String merchantTradeNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20); // 生成唯一訂單號
        System.out.println("=========="+merchantTradeNo+"===========");
//        String merchantTradeNo = ("E" + noticeNo + System.currentTimeMillis())
//                .replaceAll("[^A-Za-z0-9]", ""); // 生成唯一訂單號
        String merchantTradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        System.out.println("=========="+merchantTradeDate+"===========");
        String totalAmount = Integer.toString(Integer.parseInt(paymentNotice.getNetAmount())-walletDeduct); // 金額轉為整數
        String itemName = "補習班繳費通知單 - " + paymentNotice.getNoticeNo();
        String CustomField1 =paymentNotice.getNoticeNo();
        String tradeDesc = "EAMS學費繳費";
        String returnURL = "https://b95c41441ed6.ngrok-free.app/EAMS/ecpay/payment-callback"; // 綠界付款完成後的結果回傳網址 (後端接收)----這段無法實作
//        String orderResultURL = "http://localhost:8080/EAMS/orderResultURL"; // 客戶端回傳網址 (前端頁面跳轉)
        String orderResultURL = "https://b95c41441ed6.ngrok-free.app/EAMS/ecpay/success1"; // 客戶端回傳網址 (前端頁面跳轉)

        // 綠界金流的其他固定或可選參數
        String paymentType = "aio";
        String choosePayment = "ALL";
        String encryptType = "1"; // SHA256 加密
        String CustomField2=Integer.toString(walletDeduct);
        
     // 4. 將所有參數放入 TreeMap 以便自動排序
        Map<String, String> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        params.put("MerchantID", ECPAY_MERCHANT_ID);
        params.put("MerchantTradeNo", merchantTradeNo);
        params.put("MerchantTradeDate", merchantTradeDate);
        params.put("PaymentType", paymentType);
        params.put("TotalAmount", totalAmount);
        params.put("TradeDesc", tradeDesc);
        params.put("ItemName", itemName);
        params.put("ReturnURL", returnURL);
        params.put("ChoosePayment", choosePayment);
        params.put("EncryptType", encryptType);
        params.put("OrderResultURL", orderResultURL);
        params.put("CustomField1", CustomField1);
        params.put("CustomField2", CustomField2);

        // 這裡使用一個假的 CheckMacValue 佔位，你必須替換為實際的計算邏輯
        String checkMacValue = generateCheckMacValue(params, ECPAY_HASH_KEY, ECPAY_HASH_IV);
        System.out.println(checkMacValue);


        // 5. 組裝完整的 HTML 表單
        String htmlForm = String.format(
            "<html><head><meta charset='utf-8'></head><body>" +
            "<form id='ecpayForm' method='post' action='https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5'>" + // 綠界測試環境付款網址
            "<input type='hidden' name='MerchantID' value='%s'>" +
            "<input type='hidden' name='MerchantTradeNo' value='%s'>" +
            "<input type='hidden' name='MerchantTradeDate' value='%s'>" +
            "<input type='hidden' name='PaymentType' value='%s'>" +
            "<input type='hidden' name='TotalAmount' value='%s'>" +
            "<input type='hidden' name='TradeDesc' value='%s'>" +
            "<input type='hidden' name='ItemName' value='%s'>" +
            "<input type='hidden' name='ReturnURL' value='%s'>" +
            "<input type='hidden' name='ChoosePayment' value='%s'>" +
            "<input type='hidden' name='EncryptType' value='%s'>" +
            "<input type='hidden' name='OrderResultURL' value='%s'>" + // 客戶端回傳網址
            "<input type='hidden' name='CustomField1' value='%s'>" + // 客戶端回傳網址
            "<input type='hidden' name='CustomField2' value='%s'>" + // 客戶端回傳網址
            "<input type='hidden' name='CheckMacValue' value='%s'>" + // 實際的 CheckMacValue
            "</form>" +
            "<script>document.getElementById('ecpayForm').submit();</script>" +
            "</body></html>",
            ECPAY_MERCHANT_ID,
            merchantTradeNo,
            merchantTradeDate,
            paymentType,
            totalAmount,
            tradeDesc,
            itemName,
            returnURL,
            choosePayment,
            encryptType,
            orderResultURL,
            CustomField1,
            CustomField2,
            checkMacValue // 實際的 CheckMacValue
            
            
            
            
            
        );

        return htmlForm;
    }

 

	/**
     * 輔助方法：用於構建錯誤提示的 HTML 頁面
     * @param message 錯誤訊息
     * @return 錯誤提示的 HTML 字串
     */
    private String buildErrorHtml(String message) {
        return String.format(
            "<html><head><meta charset='utf-8'><title>錯誤</title>" +
            "<style>body{font-family: Arial, sans-serif; background-color: #f8d7da; color: #721c24; padding: 20px; text-align: center;}" +
            "h2{color: #721c24;} .container{background-color: #f8d7da; border: 1px solid #f5c6cb; border-radius: 5px; padding: 30px; margin: 50px auto; max-width: 600px;}</style>" +
            "</head><body><div class='container'><h2>錯誤</h2><p>%s</p>" +
            "<p>請確認您的連結或聯繫補習班客服。</p></div></body></html>",
            message
        );
    }
    
    /**
     * 綠界官方 CheckMacValue 計算方法
     * 步驟遵循綠界技術文件，請確保 HashKey 和 HashIV 正確無誤。
     * @param params 待計算 CheckMacValue 的所有參數 (不包含 CheckMacValue 本身)
     * @param hashKey 綠界提供的 HashKey
     * @param hashIv 綠界提供的 HashIV
     * @return 計算出的 CheckMacValue
     */
    private String generateCheckMacValue(Map<String, String> params, String hashKey, String hashIv) {
        // (1) 將傳遞參數依照第一個英文字母，由A到Z的順序來排序
        // TreeMap 會自動按 Key 排序，因此我們直接使用它來生成字串
        String paramString = params.entrySet().stream()
                                   .map(entry -> entry.getKey() + "=" + entry.getValue())
                                   .collect(Collectors.joining("&"));

        // (2) 參數最前面加上HashKey、最後面加上HashIV
        String fullString = "HashKey=" + hashKey + "&" + paramString + "&HashIV=" + hashIv;

        // (3) 將整串字串進行URL encode
        // Java 的 URLEncoder.encode 默認將空格轉為 '+'，但綠界要求轉為 %20
        // 且需要多次 URL encode
        String urlEncodedString = urlEncodeEcpay(fullString);

        // (4) 轉為小寫
        String lowerCaseString = urlEncodedString.toLowerCase();

        // (5) 以SHA256加密方式來產生雜凑值
        String sha256Hash = sha256(lowerCaseString);

        // (6) 再轉大寫產生CheckMacValue
        return sha256Hash.toUpperCase();
    }

    /**
     * 綠界專用的 URL encode (RFC 1866)
     * Java 的 URLEncoder 預設會將空格轉為 '+'，但綠界要求轉為 %20。
     * 並且需要替換 !'()* 這些字符。
     */
    private String urlEncodeEcpay(String value) {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8)
                                    .replace("%21", "!")
                                    .replace("%2A", "*")
                                    .replace("%27", "'")
                                    .replace("%28", "(")
                                    .replace("%29", ")")
//                                    .replace("%20", "+") // 將空格替換為 + (PHP的 urlencode 習慣)
                                    .replace("%7e", "~"); // PHP的 urlencode 不會編碼 ~
        return encoded;
    }

    /**
     * SHA256 加密
     */
    private String sha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating SHA256 hash", e);
        }
    }
    
   
    
    
}
