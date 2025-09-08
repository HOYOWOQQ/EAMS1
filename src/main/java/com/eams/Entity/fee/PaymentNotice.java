package com.eams.Entity.fee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.course.Course;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity @Table(name = "payment_notice")
public class PaymentNotice {
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "notice_no")
    private String noticeNo;


    @Column(name = "pay_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate payDate;

    @Column(name = "start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(name = "end_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Column(name = "discount")
    private String discount;

    @Column(name = "remark")
    private String remark;

    @Column(name = "is_voided")
    private Boolean isVoided;

    @Column(name = "void_reason")
    private String voidReason;

    @Column(name = "void_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate voidDate;

    @Column(name = "issued_by_id")
    private Integer issuedById;

    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdAt;

    @Column(name = "net_amount")
    private String netAmount;

    @Column(name = "pay_status", nullable = false)
    private String payStatus;
    
    @Column(name = "wallet_used_amount")
    private Integer walletUsedAmount;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id" ,insertable = false, updatable = false)
    @JsonBackReference
	private Member studentAccount;
    
    

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private Student studentInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "paymentNotice", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PaymentItem> paymentItems =new  LinkedList<PaymentItem>();
    
    
   
}
