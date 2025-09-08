package com.eams.Entity.fee.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class RevenueOverviewDTO {
    private long totalRevenue;          // 已收學費（TWD）
    private long accountsReceivable;    // 應收（未繳）
    private long scholarshipExpense;    // 獎學金支出（不含已撤銷）
    private long refundExpense;         // ✅ 新增：退費金額
    private long netIncome;             // 淨收益 = totalRevenue - scholarshipExpense

    // 與上期比較（%），前端可選用
    private Double revenueDelta;
    private Double arDelta;
    private Double scholarDelta;
    private Double refundDelta;
    private Double netDelta;
}
