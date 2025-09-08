package com.eams.common.Support.DTO;

public class FileDownloadResult {
    private boolean success;
    private String fileName;
    private String contentType;
    private byte[] fileData;
    private String errorMessage;

    public FileDownloadResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public FileDownloadResult(boolean success, String fileName, String contentType, byte[] fileData) {
        this.success = success;
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileData = fileData;
    }

    // getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { this.fileData = fileData; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}