package com.eams.Entity.fee;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "payment_notice_log")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PaymentNoticeLog {


	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "notice_id")
	    private Integer noticeId;

	    @Column(name = "operation_type")
	    private String operationType;

	    @Column(name = "old_data")
	    private String oldData;

	    @Column(name = "new_data")
	    private String newData;

	    @Column(name = "operated_by")
	    private String operatedBy;

	    @Column(name = "operation_date")
	    @JsonFormat(pattern = "yyyy-MM-dd")
	    private LocalDate operationDate;

	    @Column(name = "remarks")
	    private String remarks;

		
	
}
