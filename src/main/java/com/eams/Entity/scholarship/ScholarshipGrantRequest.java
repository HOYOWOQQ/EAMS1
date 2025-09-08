package com.eams.Entity.scholarship;

import lombok.Data;

@Data
public class ScholarshipGrantRequest {
    private Integer studentId;
    private Integer examResultId;
    private String title;
    private String amount;
}