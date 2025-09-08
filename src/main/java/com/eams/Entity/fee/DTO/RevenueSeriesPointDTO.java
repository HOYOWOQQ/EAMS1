package com.eams.Entity.fee.DTO;

import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class RevenueSeriesPointDTO {
    // label 建議用 yyyy-MM 或 yyyy-MM-dd
	 private String label;
	    private long tuitionPaid;        // 收入
	    private long scholarshipExpense; // 支出
	    private long refundExpense;      // 退費
	    private long netIncome;          // 淨收益
}