package com.eams.Entity.fee.DTO;

import java.time.LocalDate;

public class FindpaymentitemDTO {
    private Integer id;
    private String subjectName;
    private String amount;
    private String payStatus;
    private LocalDate payDate;
    private String remark;

    public FindpaymentitemDTO(Integer id, String subjectName, String amount, String payStatus, LocalDate payDate, String remark) {
        this.id = id;
        this.subjectName = subjectName;
        this.amount = amount;
        this.payStatus = payStatus;
        this.payDate = payDate;
        this.remark = remark;
    }

    // Getters
    public Integer getId() { return id; }
    public String getSubjectName() { return subjectName; }
    public String getAmount() { return amount; }
    public String getPayStatus() { return payStatus; }
    public LocalDate getPayDate() { return payDate; }
    public String getRemark() { return remark; }
}