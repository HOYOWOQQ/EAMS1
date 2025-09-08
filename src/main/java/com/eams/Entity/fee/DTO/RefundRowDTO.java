package com.eams.Entity.fee.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRowDTO {
    private Integer id;           // 退費單/通知單 id
    private String noticeNo;      // 通知單號
    private Integer studentId;    // 學生 id
    private String studentName;   // 學生姓名
    private String courseName;    // 課程名稱
    private String subjectName;   // 科目（如果有需要，可為 null）
    private String netAmount;     // 退費金額（正數）
    private String payStatus;     // 應該是 "refunded"
    private LocalDate payDate;    // 原繳費日
    private String voidReason;    // 退費原因
}