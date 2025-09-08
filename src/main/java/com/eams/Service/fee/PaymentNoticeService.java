package com.eams.Service.fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.eams.Controller.fee.JwtTokenUtilForPay;
import com.eams.Entity.course.Course;
import com.eams.Entity.fee.PaymentItem;
import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.fee.PaymentNoticeLog;
import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.wallet.StudentWalletTransaction;
import com.eams.Entity.fee.DTO.EditPaymentNoticeDTO;
import com.eams.Entity.fee.DTO.FindpaymentitemDTO;
import com.eams.Repository.course.SubjectRepository;
import com.eams.Repository.fee.GenerateNoticeNoService;
import com.eams.Repository.fee.PaymentItemRepository;
import com.eams.Repository.fee.PaymentNoticeLogRepository;
import com.eams.Repository.fee.PaymentNoticeRepository;
import com.eams.Repository.course.CourseRepository;
import com.eams.Repository.member.MemberRepository; // 確保引入 MemberRepository
import com.eams.Repository.member.StudentRepository;
import com.eams.Repository.wallet.StudentWalletTransactionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PaymentNoticeService {
	
	@Autowired @Qualifier("feeEmailService")
	private EmailService emailService;
	
	    // ✅ 添加 StudentRepository
	    @Autowired(required = false)
	    private StudentRepository studentRepo;
	

    @Autowired
    private SubjectRepository subjectRepo;

    @Autowired
    private PaymentNoticeRepository noticeRepo;

    @Autowired
    private PaymentItemRepository itemRepo;

    @Autowired
    private PaymentNoticeLogRepository logRepo;

    @Autowired(required = false)
    private CourseRepository courseRepo;

    @Autowired(required = false)
    private MemberRepository memberRepo; // 使用 MemberRepository
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
	private GenerateNoticeNoService generateNoticeNoService;
    
    @Autowired
    private StudentWalletTransactionRepository walletRepo;
    

    public List<FindpaymentnoticeDTO> getAllNotices() {
        return noticeRepo.getAllpaymentNotice();
    }
    
    public  List<FindpaymentnoticeDTO> getpaymentNoticeByStudentId(Integer studentId) {
		return noticeRepo.getpaymentNoticeByStudentId(studentId);
    	
    }
    
    public Optional<FindpaymentnoticeDTO> findByNoticeNoAndStudentId(String NoticeNo ,Integer sid) {
    	return noticeRepo.findByNoticeNoAndStudentId(NoticeNo, sid);
    }
    
    

    public FindpaymentnoticeDTO getNoticeById(Integer userId,Integer id) {
        Optional<PaymentNotice> noticeOptional = noticeRepo.findById(id);
        if (noticeOptional.isPresent()) {
            PaymentNotice notice = noticeOptional.get();

            // 取得費用細項 DTO 列表
            List<FindpaymentitemDTO> itemDTOs = itemRepo.findDTOByNoticeId(notice.getId());

            // 用完整建構子建立 DTO（與 JPQL 一致）
            FindpaymentnoticeDTO dto = new FindpaymentnoticeDTO(
                notice.getId(),
                notice.getNoticeNo(),          
                notice.getStudentAccount() != null ? notice.getStudentAccount().getId() : null,
                notice.getStudentAccount() != null ? notice.getStudentAccount().getName() : null,
                notice.getCourse() != null ? notice.getCourse().getId() : null,
                notice.getCourse() != null ? notice.getCourse().getName() : null,
                notice.getNetAmount(),
                notice.getDiscount(),
                notice.getStartDate(),
                notice.getEndDate(),
                notice.getPayDate(),
                notice.getPayStatus(),
                notice.getRemark(),
                notice.getWalletUsedAmount(),
                notice.getCreatedAt());
            // 補上細項清單
            dto.setPaymentItems(itemDTOs);

            return dto;
        }
        return null;
    }

    // updatePaymentNotice 方法保持不變，因為 BeanUtils.copyProperties 不會影響 ManyToOne 關聯，
    // 而 @RequestBody 傳入的 PaymentNotice 會自動處理 studentAccount 和 course 物件的綁定。
    // 如果 updateNotice 中會更新 studentId 或 courseId，Service 需要有邏輯來查詢並重新設置關聯實體。
    // 這裡維持之前的邏輯，即如果 updatedNotice 包含新的實體資訊（帶有ID），則嘗試重新載入。
    public boolean updatePaymentNoticeByDTO(Integer id, EditPaymentNoticeDTO dto, String operator) {
    	try {
    	    Optional<PaymentNotice> optional = noticeRepo.findById(id);
    	    

    	    if (optional.isPresent()) {
    	        PaymentNotice entity = optional.get();
    	        String oldData = objectMapper.writeValueAsString(entity);
    	        entity.setPayDate(LocalDate.parse(dto.getPayDate()));  //前端回傳字串 轉成DATE
    	        entity.setPayStatus(dto.getPayStatus());
    	        entity.setNetAmount(dto.getNetAmount());
    	        entity.setRemark(dto.getRemark());
    	        noticeRepo.save(entity);
    	       
    	 
            // 3. 紀錄異動 Log (保持不變)
            PaymentNoticeLog log = new PaymentNoticeLog();
            log.setNoticeId(id);
            log.setOperationType("update");
            log.setOldData(oldData);
            log.setNewData(objectMapper.writeValueAsString(entity));
            log.setOperatedBy(operator);
            log.setOperationDate(LocalDate.now());
            log.setRemarks("通知單修改");
            logRepo.save(log);
    	    }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("更新通知單失敗: " + e.getMessage(), e);
        }
    }
    
    
   
    	public boolean softDelete(Integer noticeId, String reason, Integer integer) {

        // 1) 取單據（悲觀鎖，避免重覆作廢/併發）
        PaymentNotice p = noticeRepo.findByIdForUpdate(noticeId).orElse(null);
        if (p == null) return false;

        // 2) 已作廢就略過（冪等）
        if (Boolean.TRUE.equals(p.getIsVoided()) || "voided".equalsIgnoreCase(p.getPayStatus())) {
            return true;
        }

//       
        // 6) 其他未知狀態：保守起見僅作廢，不動錢
        markVoidedAndCascade(p, noticeId, reason, integer);
        return true;
    }
    	
    	public boolean refund(Integer noticeId, String reason, Integer operatorId) {
    	    PaymentNotice p = noticeRepo.findByIdForUpdate(noticeId).orElse(null);
    	    if (p == null || !"paid".equalsIgnoreCase(p.getPayStatus())) {
    	        return false;
    	    } int netAmount   = parseIntSafe(p.getNetAmount());        // 你的欄位若是 Integer 就直接 p.getNetAmount()
          int walletUsed  = parseIntSafe(p.getWalletUsedAmount()); // 可能為 null
          int gatewayPaid = Math.max(0, netAmount - walletUsed);

          // 預設全退回錢包；若要原路退金流，這裡改為只退 walletUsed，gatewayPaid 走金流退款
          int refundToWallet = walletUsed + gatewayPaid; // = netAmount
          
          Member m = memberRepo.findById(operatorId).orElseThrow(
                  () -> new RuntimeException("找不到操作者=" + operatorId)
              );

          // (b) 取得學生目前餘額（取「最後一筆交易」）
          Optional<StudentWalletTransaction> lastTxnOpt =
                  walletRepo.findFirstByStudentInfo_IdOrderByCreatedAtDescIdDesc(p.getStudentInfo().getId());
          int lastBalance = lastTxnOpt.map(StudentWalletTransaction::getBalanceAfter).orElse(0);
          int newBalance  = lastBalance + refundToWallet;

          // (c) 建立退款交易（正數=入帳）
          StudentWalletTransaction txn = new StudentWalletTransaction();
          txn.setStudentInfo(p.getStudentInfo());
          txn.setAmount(refundToWallet);
          txn.setDescription("繳費通知單退款: " + p.getNoticeNo());
          txn.setSourceType("payment_notice");
          txn.setSourceId(p.getId());
          txn.setType("refund");
          txn.setCreatedBy(m);                // 用操作人帳號/名稱字串
          txn.setCreatedAt(LocalDateTime.now());
          txn.setBalanceAfter(newBalance);
          walletRepo.save(txn);
    	
       // 更新通知單狀態
          p.setPayStatus("refunded");
          p.setVoidReason(reason);
          p.setVoidDate(LocalDate.now());
          p.setIssuedById(operatorId);
          noticeRepo.save(p);

          return true;
    	}
    	

    /** 將通知單標記作廢、更新 pay_status=voided、同步作廢 item、寫 Log。 */
    private void markVoidedAndCascade(PaymentNotice p, Integer noticeId, String reason, Integer operator) {
        // 先標記通知單
        p.setIsVoided(true);
        p.setVoidReason(reason);
        p.setVoidDate(LocalDate.now());
        p.setIssuedById(operator);
//        p.setPayStatus("voided");
        noticeRepo.save(p);

        // 再處理明細（若你現有就是 softDelete by id，沿用）
        List<PaymentItem> itemsToDelete = itemRepo.findByPaymentNoticeId(noticeId);
        for (PaymentItem item : itemsToDelete) {
            itemRepo.softDelete(item.getId());
        }

        // 寫稽核日誌
        PaymentNoticeLog log = new PaymentNoticeLog();
        log.setNoticeId(noticeId);
        log.setOperationType("void");
        log.setOldData(null);  // 如需可序列化舊資料
        log.setNewData(null);
        log.setOperatedBy(operator.toString());
        log.setOperationDate(LocalDate.now());
        log.setRemarks(reason);
        logRepo.save(log);
    }

    /** 將可能為 String/Number 的金額安全轉 int；null -> 0 */
    private int parseIntSafe(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            String s = v.toString().trim().replaceAll(",", "");
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }


    public Map<String, Object> createNotice(Map<String, Object> payload, String operator, Integer issuedById) {
        
    	Integer studentId = Integer.valueOf(payload.get("student_id").toString());
        Integer courseId = Integer.valueOf(payload.get("course_id").toString());
        String startDateStr = (String) payload.get("start_date");
        String endDateStr = (String) payload.get("end_date");
        String payDateStr = (String) payload.get("due_date");
        String discountScholar = (String) payload.get("discount_scholar");
        String discountSibling = (String) payload.get("discount_sibling");
        String netAmount = payload.get("net_amount").toString();
        String remark = (String) payload.get("remark");

     // 日期轉換
        LocalDate payDate = LocalDate.parse(payDateStr);
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        
     // 創建 PaymentNotice 物件 
        PaymentNotice notice = new PaymentNotice();
        
        Student student = studentRepo.findById(studentId).orElse(null);
        if (student == null) {
            throw new IllegalArgumentException("找不到學生 ID: " + studentId + " 對應的學生資訊。");
        }
        notice.setStudentInfo(student);

        // （可選）同時設置 StudentAccount，僅用於顯示/查詢，不強制存在
        Member studentAccount = memberRepo.findById(studentId).orElse(null);
        if (studentAccount != null) {
            notice.setStudentAccount(studentAccount);
        }

        Course course = courseRepo.findById(courseId).orElse(null);
        if (course == null) {
            throw new IllegalArgumentException("找不到課程 ID: " + courseId + " 對應的課程資訊。");
        }
        notice.setCourse(course);
     // 設置其他屬性
        notice.setPayDate(payDate);
        notice.setStartDate(startDate);
        notice.setEndDate(endDate);
        notice.setDiscount(discountScholar);
        notice.setNetAmount(netAmount);
        notice.setRemark(remark);
        notice.setIssuedById(issuedById);
        notice.setPayStatus("unpaid");
        notice.setIsVoided(false);

        // 保持不變
        notice.setCreatedAt(LocalDateTime.now());
        
        String generatedNoticeNo = generateNoticeNoService.generateNextNoticeNo();
        if (generatedNoticeNo == null) {
        	throw new IllegalStateException("通知單編號產生失敗！");
        }
        notice.setNoticeNo(generatedNoticeNo);
        
     // ✅ 先存 Notice，這樣 DB 會給它一個 id
        PaymentNotice savedNotice = noticeRepo.save(notice);
        
        
     // 取出 paymentItems (要轉型成 List<Map<String,Object>>)
        List<Map<String, Object>> itemsMap = (List<Map<String, Object>>) payload.get("paymentItems");
        System.out.println("aaaaaaaaaaa" +itemsMap);
        
        List<PaymentItem> paymentItems = new ArrayList<>();
        for (Map<String, Object> itemMap : itemsMap) {
            PaymentItem item = new PaymentItem();
           String subjectId = itemMap.get("subjectId").toString();
           item.setSubjectId(Integer.valueOf(subjectId));
            if (item.getSubject() == null && item.getSubjectId() != null) 
            { item.setSubject(subjectRepo.findById(item.getSubjectId()).orElse(null));}
            item.setAmount(String.valueOf(itemMap.get("amount")));
            item.setRemark((String) itemMap.get("remark"));
            item.setPayStatus((String) itemMap.get("payStatus"));
            String payDateStr1 = (String) itemMap.get("paydate");
            // 日期轉換
            LocalDate payDate1 = LocalDate.parse(payDateStr1);
            item.setPayDate(payDate1);
            item.setPaymentNotice(savedNotice);

            paymentItems.add(item);
        }

        
        System.out.println("11111"+generatedNoticeNo);

        
        
    	
       
        itemRepo.saveAll(paymentItems);

        
        // === Step 3: 建立 Log ===
        PaymentNoticeLog log = new PaymentNoticeLog();
        log.setNoticeId(savedNotice.getId());
        log.setOperationType("add");
        log.setOldData(" ");

        try {
            log.setNewData(objectMapper.writeValueAsString(savedNotice));
        } catch (Exception e) {
            log.setNewData("Failed to serialize new data: " + e.getMessage());
            e.printStackTrace();
        }
        log.setOperatedBy(operator);
        log.setOperationDate(LocalDate.now());
        log.setRemarks("新增通知單");
        logRepo.save(log);

    
        // === Step 4: 產生付款連結 + 發送 Email ===
        final String FRONTEND_PAYMENT_BASE_URL = "http://localhost:5173/payment/frontendpayment";
    			
    			// 2. 使用 JwtTokenUtilForPay 生成 JWT Token
                String paymentToken = JwtTokenUtilForPay.generateToken(notice.getNoticeNo());

                // 3. 組合完整的繳費 URL
                String fullPaymentUrl = FRONTEND_PAYMENT_BASE_URL + "?paylink=" + paymentToken;
    			
                
    			
                
                
                
                // 模擬取得 email，例如從 member 物件中拿 guardian email
                
                String toEmail = notice.getStudentAccount().getEmail(); // 假設你有這個欄位
             
                if (toEmail != null && !toEmail.isBlank()) {
                    emailService.sendPaymentLinkHtmlEmail(toEmail, generatedNoticeNo, fullPaymentUrl);
                }
                
                
             // 9. 回傳
                return Map.of(
                        "message", "繳費通知單已創建，連結已生成。",
                        "paymentUrl", fullPaymentUrl,
                        "noticeNo", notice.getNoticeNo()
                );
        
        
        
    }
}