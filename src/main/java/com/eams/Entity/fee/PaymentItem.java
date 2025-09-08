package com.eams.Entity.fee;

import java.time.LocalDate;

import com.eams.Entity.course.Subject;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor


@Entity @Table(name = "payment_item")
public class PaymentItem {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	private Integer id;
	
	@Column(name = "amount")
	@Expose
	private String amount;

	@Column(name = "pay_status")
	@Expose
	private String payStatus;

	@Column(name = "pay_date")
	@Expose
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate payDate;


    @Column(name = "is_voided")
    private Boolean isVoided ;
	
	@Column(name = "remark")
	@Expose
	private String remark;
	
	//一對多 多對一關聯  notice_id 、subject_id
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notice_id")
	@JsonBackReference
	private PaymentNotice  paymentNotice;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id")
	@JsonBackReference
	private Subject subject;
	
	
	@Transient
	private Integer subjectId;

	public Integer getSubjectId() {
	    return subject != null ? subject.getId() : subjectId;
	}
	
	
}
