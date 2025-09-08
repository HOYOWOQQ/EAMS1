package com.eams.Entity.fee.DTO;

import lombok.*;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ExpenseRowDTO {
    private Integer id;             // ScholarshipGrant id
    private Integer studentId;
    private String  studentName;
    private String  examPaperName;
    private String  title;           // 若你有獎學金類型可放
    private String    amount;
    private String  status;         // granted / revoked ...
    private LocalDateTime grantTime;
}