package com.eams.Repository.fee;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eams.Entity.fee.PaymentNoticeLog;

public interface PaymentNoticeLogRepository extends JpaRepository<PaymentNoticeLog, Integer> {

	  List<PaymentNoticeLog> findByNoticeId(Integer noticeId);

	  List<PaymentNoticeLog> findByOperatedBy(String operatedBy);
	
}
