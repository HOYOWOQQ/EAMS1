package com.eams.common.Support.DTO;

public class FileValidationResult {
    private boolean valid;
    private String errorMessage;

    public FileValidationResult(boolean valid, String errorMessage) {
        this.valid = valid;
        this.errorMessage = errorMessage;
    }

    // getters and setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}