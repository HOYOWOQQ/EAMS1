package com.eams.Entity.scholarship;

import java.time.LocalDateTime;

public class ScholarshipGrantDTO {
	private Integer id;
    private String title;
    private String amount;
    private String status;
    private LocalDateTime grantTime;
    private String studentName; // 從 studentAccount 取出
    private String ExamName;
    private String createdBy;
    
    
	public ScholarshipGrantDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public ScholarshipGrantDTO(Integer id, String title, String amount, String status, LocalDateTime grantTime,
			String studentName,String ExamName,String createdBy) {
		super();
		this.id = id;
		this.title = title;
		this.amount = amount;
		this.status = status;
		this.grantTime = grantTime;
		this.studentName = studentName;
		this.ExamName = ExamName;
		this.createdBy=createdBy;
		
	}


	public ScholarshipGrantDTO(Integer id, String title, String amount, String status, LocalDateTime grantTime,
			String studentName,String ExamName) {
		super();
		this.id = id;
		this.title = title;
		this.amount = amount;
		this.status = status;
		this.grantTime = grantTime;
		this.studentName = studentName;
		this.ExamName = ExamName;
		
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	public String getExamName() {
		return ExamName;
	}


	public void setExamName(String examName) {
		ExamName = examName;
	}


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getGrantTime() {
		return grantTime;
	}
	public void setGrantTime(LocalDateTime grantTime) {
		this.grantTime = grantTime;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
    
    
    
    
}
