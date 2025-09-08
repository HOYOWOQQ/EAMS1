package com.eams.Entity.fee.DTO;

import lombok.*;
import java.time.LocalDate;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class IncomeRowDTO {
    private Integer id;           // PaymentNotice id
    private String  noticeNo;
    private Integer studentId;
    private String  studentName;
    private String  courseName;
    private String  subjectName;
    private String    amount;       // 以 netAmount
    private String  payStatus;    // paid / unpaid//refunded
    private LocalDate payDate;    // 只對 paid 有值
}