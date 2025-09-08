package com.eams.Entity.fee.DTO;

import com.google.auto.value.AutoValue.Builder;

import lombok.Data;

@Data
@Builder
public class WalletOverviewDTO {
    private long totalBalance;   // 所有學生錢包總餘額
    private long totalDeposit;   // 學生加值
    private long totalUsed;      // 扣款
    private long totalAdjust;    // 校正
    private long totalRefund;    // 退費 ✅ 新增
}