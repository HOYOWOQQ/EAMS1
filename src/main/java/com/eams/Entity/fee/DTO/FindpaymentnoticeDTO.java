package com.eams.Entity.fee.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// 假設這是一個用於前端展示的 DTO
public class FindpaymentnoticeDTO {
    private Integer id; // 從 Entity 帶過來，用於編輯
    private String noticeNo;
    private String studentName; // 學生姓名
    private String courseName;
    private String netAmount; // 如果 DTO 需要 Integer
    private LocalDate payDate;
    private String payStatus;
    private String remark;
    private LocalDateTime createdAt;
    private Boolean isVoided;
    private String voidReason;
    

    // 編輯時可能需要的原始 ID 和日期
    private Integer studentId;
    private Integer courseId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String discount; // 如果 DTO 需要 Integer
    private Integer walletUsedAmount;

    
    private List<FindpaymentitemDTO> paymentItems; // 包含付款細項 DTO 列表

    // ✅ 對應完整 JPQL 建構子用
    public FindpaymentnoticeDTO(Integer id, String noticeNo,Integer studentId, String studentName,
                                Integer courseId, String courseName, String netAmount, String discount,
                                LocalDate startDate, LocalDate endDate,LocalDate payDate, String payStatus, String remark , 
                                Integer walletUsedAmount,LocalDateTime createdAt,Boolean isVoided,String voidReason) {
        this.id = id;
        this.noticeNo = noticeNo;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.netAmount = netAmount;
        this.discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.payDate = payDate;
        this.payStatus = payStatus;
        this.remark = remark;
        this.walletUsedAmount=walletUsedAmount;
        this.createdAt=createdAt;
        this.isVoided=isVoided;
        this.voidReason=voidReason;
    }
    
    
    public FindpaymentnoticeDTO(Integer id, String noticeNo,Integer studentId, String studentName,
            Integer courseId, String courseName, String netAmount, String discount,
            LocalDate startDate, LocalDate endDate,LocalDate payDate, String payStatus, String remark , 
            Integer walletUsedAmount,LocalDateTime createdAt) {
this.id = id;
this.noticeNo = noticeNo;
this.studentId = studentId;
this.studentName = studentName;
this.courseId = courseId;
this.courseName = courseName;
this.netAmount = netAmount;
this.discount = discount;
this.startDate = startDate;
this.endDate = endDate;
this.payDate = payDate;
this.payStatus = payStatus;
this.remark = remark;
this.walletUsedAmount=walletUsedAmount;
this.createdAt=createdAt;
}
    
    
    public FindpaymentnoticeDTO(String noticeNo, String studentName, String courseName,
            String netAmount, LocalDate payDate, String payStatus, String remark, Integer walletUsedAmount,LocalDateTime createdAt) {
this.noticeNo = noticeNo;
this.studentName = studentName;
this.courseName = courseName;
this.netAmount = netAmount;
this.payDate = payDate;
this.payStatus = payStatus;
this.remark = remark;
this.walletUsedAmount=walletUsedAmount;
this.createdAt=createdAt;
}
    
   

	public Boolean getIsVoided() {
		return isVoided;
	}

	public void setIsVoided(Boolean isVoided) {
		this.isVoided = isVoided;
	}

	public String getVoidReason() {
		return voidReason;
	}

	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	// Getter & Setter（已根據命名規則與類型修正）
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNoticeNo() { return noticeNo; }
    public void setNoticeNo(String noticeNo) { this.noticeNo = noticeNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getNetAmount() { return netAmount; }
    public void setNetAmount(String netAmount) { this.netAmount = netAmount; }

    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }

    public String getPayStatus() { return payStatus; }
    public void setPayStatus(String payStatus) { this.payStatus = payStatus; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }

    public List<FindpaymentitemDTO> getPaymentItems() { return paymentItems; }
    public void setPaymentItems(List<FindpaymentitemDTO> paymentItems) {
        this.paymentItems = paymentItems;
    }

	public Integer getWalletUsedAmount() {
		return walletUsedAmount;
	}

	public void setWalletUsedAmount(Integer walletUsedAmount) {
		this.walletUsedAmount = walletUsedAmount;
	}
    
    
}