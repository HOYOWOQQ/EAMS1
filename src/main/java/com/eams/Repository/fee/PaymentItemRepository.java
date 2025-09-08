package com.eams.Repository.fee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.fee.PaymentItem;
import com.eams.Entity.fee.DTO.FindpaymentitemDTO;

public interface PaymentItemRepository extends JpaRepository<PaymentItem, Integer> {
	
	//查詢改寫
	@Query("SELECT new com.eams.Entity.fee.DTO.FindpaymentitemDTO(" +
		       "i.id, s.name, i.amount, i.payStatus, i.payDate, i.remark) " +
		       "FROM PaymentItem i " +
		       "JOIN i.subject s " +
		       "WHERE i.paymentNotice.id = :noticeId AND i.isVoided = false")
		List<FindpaymentitemDTO> findDTOByNoticeId(@Param("noticeId") Integer noticeId);
		
	
	//刪除改寫 軟刪除
		@Modifying
		@Query("UPDATE PaymentItem p SET p.isVoided = true  WHERE p.id = :id")
		public int softDelete(@Param("id") Integer id);
	
	
		 // 根據 PaymentNoticeId 查詢所有 PaymentItem
	    List<PaymentItem> findByPaymentNoticeId(Integer paymentNoticeId);
		
	
}
