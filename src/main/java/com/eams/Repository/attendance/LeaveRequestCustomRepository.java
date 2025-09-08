package com.eams.Repository.attendance;


import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eams.Entity.attendance.LeaveRequest;

public interface LeaveRequestCustomRepository {
	 Page<LeaveRequest> search(
		        Integer studentId,
		        Integer courseScheduleId,
		        String status,                // PENDING / APPROVED / REJECTED（先用字串）
		        LocalDateTime submittedFrom,  // 以 submittedAt 篩選起
		        LocalDateTime submittedTo,    // 以 submittedAt 篩選迄
		        Boolean hasAttachment,        // true=只要有附件；null/false=不限制
		        String keyword,               // 針對 reason 模糊查詢
		        Pageable pageable
		    );
}