package com.eams.common.Support.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ValidationResult {
    private boolean valid;
    private String errorMessage;
}