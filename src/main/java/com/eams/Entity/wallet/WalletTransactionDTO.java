package com.eams.Entity.wallet;

import java.time.LocalDateTime;


public class WalletTransactionDTO {
	private  Integer sid;
    private  Integer amount;           // 正=入帳, 負=扣款
    private  String description;
    private  String sourceType;        // e.g. "SCHOLARSHIP", "PAY_NOTICE", "ADJUST"
    private  Integer sourceId;         // 來源主鍵（例如 payment_notice.id）
    private  LocalDateTime createdAt;
    private  String studentName;
    private Integer balanceAfter;
    private String type; // deposit, used, adjust, refund, etc.
    private String createdBy;
    private Integer createdById; 
    
	public WalletTransactionDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	
	public WalletTransactionDTO(Integer sid, Integer amount, String description, String sourceType, Integer sourceId,
			LocalDateTime createdAt, String studentName,Integer balanceAfter,String type,String createdBy,Integer createdById) {
		super();
		this.sid = sid;
		this.amount = amount;
		this.description = description;
		this.sourceType = sourceType;
		this.sourceId = sourceId;
		this.createdAt = createdAt;
		this.studentName = studentName;
		this.balanceAfter=balanceAfter;
		this.type=type;
		this.createdBy=createdBy;
		this.createdById=createdById;
		
	}
	
	
	
	public Integer getCreatedById() {
		return createdById;
	}
	public void setCreatedById(Integer createdById) {
		this.createdById = createdById;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getBalanceAfter() {
		return balanceAfter;
	}
	public void setBalanceAfter(Integer balanceAfter) {
		this.balanceAfter = balanceAfter;
	}
	public String getStudentName() {
		return studentName;
	}
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}
	public Integer getSid() {
		return sid;
	}
	public void setSid(Integer sid) {
		this.sid = sid;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public Integer getSourceId() {
		return sourceId;
	}
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
    
    
    
    
}
