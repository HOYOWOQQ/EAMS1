package com.eams.Service.wallet;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.wallet.StudentWalletTransaction;
import com.eams.Entity.wallet.WalletOverviewDTO;
import com.eams.Entity.wallet.WalletTransactionDTO;
import com.eams.Repository.member.MemberRepository;
import com.eams.Repository.member.StudentRepository;
import com.eams.Repository.wallet.StudentWalletTransactionRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class StudentWalletService {
	
	@Autowired
    private StudentWalletTransactionRepository walletRepo;
	
	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private MemberRepository memberRepository;


    public List<StudentWalletTransaction> getAllByStudentId(Integer studentId) {
        return walletRepo.findByStudentInfo_Id(studentId);
    }
    
    public List<WalletTransactionDTO> findWalletTxByStudent(Integer studentId) {
      	return walletRepo.findWalletTxByStudent(studentId);
    }
    


    public Integer getWalletBalance(Integer studentId) {
        return walletRepo.getWalletBalanceByStudentId(studentId);
    }
    
    /** 建議提供：扣帳 */
    public void debitInt(Integer studentId, Integer usedAmount,
            String sourceType, Integer sourceId, String description,Integer createdBy,  boolean allowOverdraft) {
			
    	 if (studentId == null) throw new IllegalArgumentException("必須指定學生");
    	
			if (usedAmount == null || usedAmount <= 0) {
			throw new IllegalArgumentException("折抵金額必須大於 0");
			}
			
			// ✅ 冪等：用「學生 + 來源類型 + 來源ID + 類型」判斷是否已入帳（避免 description 造成不一致）
		    boolean exists = walletRepo.existsByStudentInfo_IdAndSourceTypeAndSourceIdAndType(
		            studentId, Objects.toString(sourceType, "manual"), sourceId, "used");
		    if (exists) return;
	        
	      
		    int delta = -usedAmount; // 統一存負數
			
			// 1) 查餘額（資料庫 SUM）
		    var lastTxOpt = walletRepo.findFirstByStudentInfo_IdOrderByCreatedAtDescIdDesc(studentId);
		    int balanceBefore = lastTxOpt.map(StudentWalletTransaction::getBalanceAfter).orElse(0);
		    int balanceAfter = balanceBefore + delta;

			
		     if (!allowOverdraft && balanceAfter < 0) {
		            throw new IllegalArgumentException("餘額不足，無法扣款（目前餘額：" + balanceBefore + "）");
		        }
		     
			// 2) 建立扣款交易（負數）
			StudentWalletTransaction tx = new StudentWalletTransaction();
			// 👉 這裡依你的 entity 關聯擇一設定：
			// tx.setStudentAccount(member); // 若用 Member 關聯
			
			tx.setStudentInfo(studentRepository.findById(studentId).orElseThrow()); // 若用 Student 關聯
			
			tx.setAmount(delta);                          // 扣款：負數
			tx.setSourceType(sourceType != null ? sourceType : "manual");  // e.g. "payment_notice"
			tx.setSourceId(sourceId);                           // e.g. 繳費單 id
			tx.setDescription(description);                     // e.g. "折抵 2025-09 學費"
			tx.setCreatedAt(LocalDateTime.now());
			tx.setBalanceAfter(balanceAfter);
			tx.setCreatedBy(memberRepository.findById(createdBy).orElseThrow());
			tx.setType("used");
			
			walletRepo.save(tx);
}
    
    
    /** 建議提供：以 studentId 入帳（常用於發獎學金） */
    public void deposit(Integer studentId, Integer amount, String sourceType, Integer sourceId, String desc,Integer createdBy) {
    	if (studentId == null) throw new IllegalArgumentException("必須指定學生");
        if (amount == null || amount <= 0) throw new IllegalArgumentException("入帳金額必須大於 0");
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("找不到學生 id=" + studentId));
        
        // 以「最後一筆」為準抓到目前餘額（並上悲觀鎖，避免併發）
        var lastTxOpt = walletRepo.findFirstByStudentInfo_IdOrderByCreatedAtDescIdDesc(studentId);
              
        int balanceBefore = lastTxOpt.map(StudentWalletTransaction::getBalanceAfter).orElse(0);
        int balanceAfter = balanceBefore + amount;

        StudentWalletTransaction tx = new StudentWalletTransaction();
        tx.setStudentInfo(student);
        tx.setSourceType(sourceType != null ? sourceType : "manual");  //手動存入
        tx.setSourceId(sourceId);
        tx.setAmount(amount); // 正數
        tx.setDescription(desc);
        tx.setCreatedAt(LocalDateTime.now());
       
        tx.setType("deposit");
        tx.setBalanceAfter(balanceAfter);
        tx.setCreatedBy(memberRepository.findById(createdBy).orElseThrow());
	
        walletRepo.save(tx);
    }

	public List<WalletTransactionDTO> findWalletTxAll() {
		// TODO Auto-generated method stub
		System.out.println();
		return walletRepo.findWalletTxAll();
		
	}
	
	
	/** ✅ 新增：校正（可正可負），常用於對帳或修正錯帳 */
    public void adjust(Integer studentId, Integer deltaAmount, Integer operatorId,
                       String remark, String sourceType, Integer sourceId) {
        if (studentId == null) throw new IllegalArgumentException("必須指定學生");
        if (deltaAmount == null || deltaAmount == 0) {
            throw new IllegalArgumentException("校正金額不得為 0");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("找不到學生 id=" + studentId));

        var lastTxOpt = walletRepo.findFirstByStudentInfo_IdOrderByCreatedAtDescIdDesc(studentId);
        
        int balanceBefore = lastTxOpt.map(StudentWalletTransaction::getBalanceAfter).orElse(0);
        int balanceAfter = balanceBefore + deltaAmount;


        StudentWalletTransaction tx = new StudentWalletTransaction();
        tx.setStudentInfo(student);
        tx.setType("adjust");
        tx.setAmount(deltaAmount);          // 可正可負
        tx.setBalanceAfter(balanceAfter);
        tx.setSourceType(sourceType != null ? sourceType : "manual");
        tx.setSourceId(sourceId);
        tx.setDescription(remark);
        tx.setCreatedAt(LocalDateTime.now());

        Member operator = memberRepository.findById(operatorId).orElse(null);
        tx.setCreatedBy(operator);

        walletRepo.save(tx);
        
    }
   
	
	
	
	
    
    
//	<====================錢包總體===================>
	
	public WalletOverviewDTO getOverview(LocalDate start, LocalDate end) {
        long totalDeposit = 0;
        long totalUsed = 0;
        long totaladjust = 0;
        long totalRefund = 0;
        
        LocalDateTime startDt = (start != null) ? start.atStartOfDay() : null;
        LocalDateTime endDt = (end != null) ? end.atTime(LocalTime.MAX) : null;
        

        List<Object[]> sums = walletRepo.sumByType(startDt, endDt);
        for (Object[] row : sums) {
            String type = (String) row[0];
            Long amount = (Long) row[1];
            switch (type.toLowerCase()) {
            
            case "deposit" -> totalDeposit = amount;
            case "used" -> totalUsed = amount;
            case "adjust" -> totaladjust = amount;
            case "refund" -> totalRefund = amount; // ✅ 退費獨立出來
            }
        }

        Long totalBalance = walletRepo.totalBalanceAll();

        return WalletOverviewDTO.builder()
                .totalBalance(totalBalance != null ? totalBalance : 0)
                .totalDeposit(totalDeposit)
                .totalUsed(totalUsed)
                .totalAdjust(totaladjust)
                .totalRefund(totalRefund) // ✅ 回傳
                .build();
    
	}
	}
	
	
    
    

