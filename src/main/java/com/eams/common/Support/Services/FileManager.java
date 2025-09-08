package com.eams.common.Support.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.eams.common.Support.DTO.FileDownloadResult;
import com.eams.common.Support.DTO.FileUploadResult;
import com.eams.common.Support.DTO.FileValidationResult;
import com.eams.common.Support.Repository.FileAttachmentRepository;
import com.eams.common.Support.entity.FileAttachment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class FileManager {
    
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
    
    private final FileAttachmentRepository fileRepo;
    private final String uploadPath;
    private final long maxFileSize;
    private final List<String> allowedExtensions;

    public FileManager(
            FileAttachmentRepository fileRepo,
            @Value("${file.upload.path:uploads}") String uploadPath,
            @Value("${file.upload.max-size:10485760}") long maxFileSize,
            @Value("${file.upload.allowed-extensions:.jpg,.jpeg,.png,.pdf,.doc,.docx}") String allowedExtensions) {
        this.fileRepo = fileRepo;
        this.uploadPath = uploadPath;
        this.maxFileSize = maxFileSize;
        this.allowedExtensions = Arrays.asList(allowedExtensions.split(","));
    }

    public CompletableFuture<FileUploadResult> uploadFileAsync(MultipartFile file, String relatedTable, Long relatedId, String category, Integer uploadedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FileValidationResult validation = validateFile(file);
                if (!validation.isValid()) {
                    return new FileUploadResult(false, validation.getErrorMessage());
                }

                String storedName = generateUniqueFileName(file.getOriginalFilename());
                String relativePath = Paths.get(category, storedName).toString();
                
                // 修正：確保使用絕對路徑
                Path uploadDir = Paths.get(uploadPath).toAbsolutePath();
                Path fullPath = uploadDir.resolve(relativePath);

                // 確保目錄存在
                Files.createDirectories(fullPath.getParent());

                // 儲存檔案 - 使用 Files.copy 替代 transferTo
                try (var inputStream = file.getInputStream()) {
                    Files.copy(inputStream, fullPath, StandardCopyOption.REPLACE_EXISTING);
                }

                // 計算檔案雜湊
                String fileHash = computeFileHash(fullPath);

                // 建立資料庫記錄
                FileAttachment fileAttachment = new FileAttachment();
                fileAttachment.setOriginalName(file.getOriginalFilename());
                fileAttachment.setStoredName(storedName);
                fileAttachment.setFilePath(relativePath);
                fileAttachment.setFileSize(file.getSize());
                fileAttachment.setFileType(getFileExtension(file.getOriginalFilename()));
                fileAttachment.setMimeType(file.getContentType());
                fileAttachment.setFileHash(fileHash);
                fileAttachment.setRelatedTable(relatedTable);
                fileAttachment.setRelatedId(relatedId);
                fileAttachment.setCategory(category);
                fileAttachment.setUploadedBy(uploadedBy);
                fileAttachment.setCreatedAt(LocalDateTime.now());
                fileAttachment.setAccessLevel("private");
                fileAttachment.setProcessingStatus("completed");

                fileRepo.save(fileAttachment);

                logger.info("File uploaded successfully: {} ({} bytes) to {}", 
                           file.getOriginalFilename(), file.getSize(), fullPath.toString());

                return new FileUploadResult(true, fileAttachment.getId(), relativePath, file.getSize());
            } catch (Exception ex) {
                logger.error("Error uploading file {}", file.getOriginalFilename(), ex);
                return new FileUploadResult(false, "檔案上傳失敗: " + ex.getMessage());
            }
        });
    }

    public CompletableFuture<FileDownloadResult> getFileAsync(Long fileId, Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FileAttachment file = fileRepo.findById(fileId).orElse(null);
                if (file == null || file.getIsDeleted()) {
                    return new FileDownloadResult(false, "檔案不存在");
                }

                // 檢查存取權限
                if (!checkAccessPermission(file, userId)) {
                    return new FileDownloadResult(false, "無檔案存取權限");
                }

                Path fullPath = Paths.get(uploadPath, file.getFilePath());
                if (!Files.exists(fullPath)) {
                    return new FileDownloadResult(false, "檔案不存在於儲存位置");
                }

                byte[] fileBytes = Files.readAllBytes(fullPath);

                // 更新存取記錄
                file.setDownloadCount(file.getDownloadCount() + 1);
                file.setLastAccessedAt(LocalDateTime.now());
                fileRepo.save(file);

                return new FileDownloadResult(true, file.getOriginalName(), file.getMimeType(), fileBytes);
            } catch (Exception ex) {
                logger.error("Error getting file {}", fileId, ex);
                return new FileDownloadResult(false, "檔案下載失敗");
            }
        });
    }

    public CompletableFuture<Boolean> deleteFileAsync(Long fileId, Integer userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                FileAttachment file = fileRepo.findById(fileId).orElse(null);
                if (file == null) return false;

                // 軟刪除
                file.setIsDeleted(true);
                file.setDeletedAt(LocalDateTime.now());
                file.setDeletedBy(userId);
                fileRepo.save(file);

                logger.info("File {} marked as deleted by user {}", fileId, userId);
                return true;
            } catch (Exception ex) {
                logger.error("Error deleting file {}", fileId, ex);
                return false;
            }
        });
    }

    private FileValidationResult validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new FileValidationResult(false, "檔案不能為空");
        }

        if (file.getSize() > maxFileSize) {
            return new FileValidationResult(false, "檔案大小不能超過 " + (maxFileSize / 1024 / 1024) + " MB");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            return new FileValidationResult(false, "不支援的檔案格式: " + extension);
        }

        return new FileValidationResult(true, null);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String computeFileHash(Path filePath) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(filePath);
        byte[] hashBytes = digest.digest(fileBytes);
        
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private boolean checkAccessPermission(FileAttachment file, Integer userId) {
        switch (file.getAccessLevel()) {
            case "public":
                return true;
            case "private":
                return file.getUploadedBy().equals(userId);
            case "restricted":
                // 實作角色權限檢查
                return true; // 簡化實作
            default:
                return false;
        }
    }
}
