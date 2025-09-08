package com.eams.Entity.scholarship;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class GrantTop3BatchResponse {
    private boolean success;
    private List<GrantPerExamResult> results;
}