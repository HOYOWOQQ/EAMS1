package com.eams.Entity.fee.DTO;

public class EditPaymentNoticeDTO {

	private Integer id;
    private String payDate;
    private String payStatus;
    private String netAmount;
    private String remark;
    
    
	public EditPaymentNoticeDTO(Integer id, String payDate, String payStatus, String netAmount, String remark) {
		super();
		this.id = id;
		this.payDate = payDate;
		this.payStatus = payStatus;
		this.netAmount = netAmount;
		this.remark = remark;
	}
	
	public EditPaymentNoticeDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	public String getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	public String getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(String netAmount) {
		this.netAmount = netAmount;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	
}
