package com.eams.Entity.scholarship;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class GrantPerExamResult {
    private Integer examPaperId;
    private int grantedCount;        // 寫入 scholarship_grant 成功數
    private int skippedCount;        // 已發過/被唯一鍵擋掉
    private String error;            // 該張考卷錯誤訊息（若有）
}
