package com.eams.Service.attendance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
    
    private final Path uploadDir = Paths.get("uploads", "attachments").toAbsolutePath().normalize();
    
    public UploadService() throws IOException {
        Files.createDirectories(uploadDir);
    }
    
    public String saveAttachment(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("未選擇檔案");
        }

        // 產生安全檔名
        String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
        String savedName = UUID.randomUUID() + "_" + original.replaceAll("[\\\\/:*?\"<>|]", "_");
        if (!ext.isEmpty() && !savedName.endsWith(ext)) {
            savedName += ext;
        }

        // 儲存檔案
        Path target = uploadDir.resolve(savedName);
        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return savedName; // 存 DB 用
    }

    // 讀取檔案（提供 Controller 傳給前端）
    public Resource loadAttachment(String fileName) throws IOException {
        Path filePath = uploadDir.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new IOException("找不到檔案：" + fileName);
        }

        return resource;
    }
}