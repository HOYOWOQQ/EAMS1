package com.eams.common.Support.DTO;

public class FileUploadResult {
    private boolean success;
    private Long fileId;
    private String filePath;
    private Long fileSize;
    private String errorMessage;

    public FileUploadResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public FileUploadResult(boolean success, Long fileId, String filePath, Long fileSize) {
        this.success = success;
        this.fileId = fileId;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }

    // getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
