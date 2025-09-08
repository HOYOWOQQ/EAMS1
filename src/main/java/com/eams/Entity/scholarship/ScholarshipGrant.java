package com.eams.Entity.scholarship;

import java.time.LocalDateTime;

import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.score.ExamResult;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity @Table(name = "scholarship_grant")
public class ScholarshipGrant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
//	@Column(name = "student_id")
//	private Integer  studentId  ;
	
//	@Column(name = "exam_result_id")
//	private Integer examResultId ;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "amount")
	private  String amount;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "grant_time")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime grantTime;
	
	@Column(name = "received_time")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime receivedTime;
	
	@JoinColumn(name = "created_by")
	@ManyToOne
	private  Member  createdBy;
	
	@Column(name = "created_at")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime createdAt ;
	
	@Column(name = "updated_at")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id",insertable = false, updatable = false)
//    @JsonBackReference
    private  Member studentAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id")
	private Student studentInfo;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exam_result_id")
	@JsonBackReference
	private ExamResult examResult;
	
	@PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        grantTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
	
}
