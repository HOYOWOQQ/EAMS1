package com.eams.common.Support.Controllers;

import com.eams.common.Support.Services.FileManager;
import com.eams.common.Support.DTO.FileUploadResult;
import com.eams.common.Support.DTO.FileDownloadResult;
import com.eams.common.ApiResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {
    
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    
    @Autowired
    private FileManager fileManager;

    /**
     * 文件上傳
     * POST /api/files/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResult>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "relatedTable", required = false) String relatedTable,
            @RequestParam(value = "relatedId", required = false) Long relatedId,
            @RequestParam(value = "category", defaultValue = "general") String category,
            @RequestParam("uploadedBy") Integer uploadedBy) {
        
        logger.info("Uploading file: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());
        
        try {
            FileUploadResult result = fileManager.uploadFileAsync(file, relatedTable, relatedId, category, uploadedBy).get();
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(ApiResponse.success("文件上傳成功", result));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error(result.getErrorMessage()));
            }
        } catch (Exception ex) {
            logger.error("Error uploading file", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("文件上傳失敗：" + ex.getMessage()));
        }
    }

    /**
     * 文件下載
     * GET /api/files/{fileId}/download
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable Long fileId,
            @RequestParam Integer userId) {
        
        logger.info("Downloading file: {} by user: {}", fileId, userId);
        
        try {
            FileDownloadResult result = fileManager.getFileAsync(fileId, userId).get();
            
            if (result.isSuccess()) {
                HttpHeaders headers = new HttpHeaders();
                
                if (result.getContentType() != null) {
                    headers.setContentType(MediaType.parseMediaType(result.getContentType()));
                } else {
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                }
                
                String encodedFileName = URLEncoder.encode(result.getFileName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
                headers.setContentDispositionFormData("attachment", encodedFileName);
                headers.setContentLength(result.getFileData().length);
                
                return new ResponseEntity<>(result.getFileData(), headers, HttpStatus.OK);
            } else {
                logger.warn("File download failed: {}", result.getErrorMessage());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Error downloading file", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 文件預覽（在線查看）
     * GET /api/files/{fileId}/preview
     */
    @GetMapping("/{fileId}/preview")
    public ResponseEntity<byte[]> previewFile(
            @PathVariable Long fileId,
            @RequestParam Integer userId) {
        
        try {
            FileDownloadResult result = fileManager.getFileAsync(fileId, userId).get();
            
            if (result.isSuccess()) {
                HttpHeaders headers = new HttpHeaders();
                
                if (result.getContentType() != null) {
                    headers.setContentType(MediaType.parseMediaType(result.getContentType()));
                } else {
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                }
                
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline");
                headers.set(HttpHeaders.CACHE_CONTROL, "max-age=3600");
                
                return new ResponseEntity<>(result.getFileData(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Error previewing file", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 刪除文件
     * DELETE /api/files/{fileId}
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse<String>> deleteFile(
            @PathVariable Long fileId,
            @RequestParam Integer userId) {
        
        logger.info("Deleting file: {} by user: {}", fileId, userId);
        
        try {
            Boolean success = fileManager.deleteFileAsync(fileId, userId).get();
            
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("文件刪除成功"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("文件刪除失敗"));
            }
        } catch (Exception ex) {
            logger.error("Error deleting file", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("文件刪除失敗：" + ex.getMessage()));
        }
    }

    /**
     * 批量上傳文件
     * POST /api/files/batch/upload
     */
    @PostMapping("/batch/upload")
    public ResponseEntity<ApiResponse<BatchUploadResult>> batchUploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "relatedTable", required = false) String relatedTable,
            @RequestParam(value = "relatedId", required = false) Long relatedId,
            @RequestParam(value = "category", defaultValue = "general") String category,
            @RequestParam("uploadedBy") Integer uploadedBy) {
        
        logger.info("Batch uploading {} files", files.length);
        
        try {
            BatchUploadResult batchResult = new BatchUploadResult();
            
            for (MultipartFile file : files) {
                try {
                    FileUploadResult result = fileManager
                        .uploadFileAsync(file, relatedTable, relatedId, category, uploadedBy)
                        .get();
                    
                    if (result.isSuccess()) {
                        batchResult.addSuccess(result);
                    } else {
                        batchResult.addFailure(file.getOriginalFilename(), result.getErrorMessage());
                    }
                } catch (Exception ex) {
                    batchResult.addFailure(file.getOriginalFilename(), ex.getMessage());
                }
            }
            
            if (batchResult.getFailureCount() == 0) {
                return ResponseEntity.ok(ApiResponse.success("所有文件上傳成功", batchResult));
            } else if (batchResult.getSuccessCount() > 0) {
                return ResponseEntity.ok(ApiResponse.success(
                    String.format("部分成功：%d 成功，%d 失敗", 
                        batchResult.getSuccessCount(), batchResult.getFailureCount()), batchResult));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("所有文件上傳失敗", batchResult));
            }
        } catch (Exception ex) {
            logger.error("Error in batch upload", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("批量上傳失敗：" + ex.getMessage()));
        }
    }

    /**
     * 獲取文件信息
     * GET /api/files/{fileId}/info
     */
    @GetMapping("/{fileId}/info")
    public ResponseEntity<ApiResponse<FileInfo>> getFileInfo(
            @PathVariable Long fileId,
            @RequestParam Integer userId) {
        
        try {
            FileDownloadResult result = fileManager.getFileAsync(fileId, userId).get();
            
            if (result.isSuccess()) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(result.getFileName());
                fileInfo.setContentType(result.getContentType());
                fileInfo.setFileSize(result.getFileData().length);
                
                return ResponseEntity.ok(ApiResponse.success("查詢成功", fileInfo));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error(result.getErrorMessage()));
            }
        } catch (Exception ex) {
            logger.error("Error getting file info", ex);
            return ResponseEntity.internalServerError()
                .body(ApiResponse.error("查詢失敗：" + ex.getMessage()));
        }
    }

    // 批量上傳結果DTO
    public static class BatchUploadResult {
        private List<FileUploadResult> successFiles = new ArrayList<>();
        private List<FailureInfo> failureFiles = new ArrayList<>();

        public void addSuccess(FileUploadResult result) {
            successFiles.add(result);
        }

        public void addFailure(String fileName, String error) {
            failureFiles.add(new FailureInfo(fileName, error));
        }

        public int getSuccessCount() { return successFiles.size(); }
        public int getFailureCount() { return failureFiles.size(); }
        
        public List<FileUploadResult> getSuccessFiles() { return successFiles; }
        public List<FailureInfo> getFailureFiles() { return failureFiles; }

        public static class FailureInfo {
            private String fileName;
            private String error;

            public FailureInfo(String fileName, String error) {
                this.fileName = fileName;
                this.error = error;
            }

            public String getFileName() { return fileName; }
            public String getError() { return error; }
        }
    }

    // 文件信息DTO
    public static class FileInfo {
        private String fileName;
        private String contentType;
        private long fileSize;

        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        
        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    }
}