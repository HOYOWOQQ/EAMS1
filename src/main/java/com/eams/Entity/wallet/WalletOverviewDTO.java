package com.eams.Entity.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class WalletOverviewDTO {
    private long totalBalance;   // 平台總餘額
    private long totalDeposit;   // 期間入帳 (deposit)
    private long totalUsed;      // 期間支出 (used)
    private long totalAdjust;    // 校正 (adjust)
    private long totalRefund;    // 退款 (refund)
}