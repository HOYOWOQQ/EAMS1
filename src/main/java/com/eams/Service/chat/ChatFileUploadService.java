package com.eams.Service.chat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.eams.Entity.chat.ChatAttachment;
import com.eams.Entity.chat.ChatMessage;
import com.eams.Entity.chat.chatDTO.ChatAttachmentDTO;
import com.eams.Repository.chat.ChatAttachmentRepository;
import com.eams.Repository.chat.ChatMessageRepository;
import com.eams.Repository.chat.ChatRoomRepository;

@Service
@Transactional
public class ChatFileUploadService {
    
    @Autowired
    private ChatAttachmentRepository chatAttachmentRepository;
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    
    @Value("${chat.file.upload.path:uploads/chat}")
    private String uploadPath;
    
    @Value("${chat.file.max-size:52428800}") // 50MB 默認
    private long maxFileSize;
    
    @Value("${chat.file.allowed-types:jpg,png,gif,pdf,doc,docx,xlsx,ppt,pptx,txt,zip,rar}")
    private String allowedFileTypes;
    
    // 上傳聊天文件
    public ChatAttachmentDTO uploadChatFile(MultipartFile file, Integer messageId, Integer userId) {
        try {
            // 驗證文件
            validateFile(file);
            
            // 驗證消息權限
            Optional<ChatMessage> messageOpt = chatMessageRepository.findById(messageId);
            if (!messageOpt.isPresent()) {
                throw new RuntimeException("消息不存在");
            }
            
            ChatMessage message = messageOpt.get();
            if (!message.getSenderId().equals(userId)) {
                throw new RuntimeException("您只能為自己的消息上傳附件");
            }
            
            // 檢查聊天室權限
            if (!chatRoomRepository.canUserAccessRoom(message.getRoomId(), userId)) {
                throw new RuntimeException("您沒有權限在此聊天室上傳文件");
            }
            
            // 生成文件名和路徑
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String fileName = generateFileName(originalFileName);
            String relativePath = generateFilePath(message.getRoomId(), fileName);
            
            // 確保上傳目錄存在
            Path uploadDir = Paths.get(uploadPath);
            Files.createDirectories(uploadDir);
            
            // 保存文件
            Path targetPath = uploadDir.resolve(relativePath);
            Files.createDirectories(targetPath.getParent());
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 保存附件信息到數據庫
            ChatAttachment attachment = new ChatAttachment(messageId, originalFileName, relativePath);
            attachment.setFileType(fileExtension);
            attachment.setFileSize(file.getSize());
            
            ChatAttachment savedAttachment = chatAttachmentRepository.save(attachment);
            
            return convertToDTO(savedAttachment);
            
        } catch (IOException e) {
            throw new RuntimeException("文件上傳失敗：" + e.getMessage());
        }
    }
    
    // 獲取消息的附件列表
    public List<ChatAttachmentDTO> getMessageAttachments(Integer messageId, Integer userId) {
        // 驗證權限
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(messageId);
        if (!messageOpt.isPresent()) {
            throw new RuntimeException("消息不存在");
        }
        
        ChatMessage message = messageOpt.get();
        if (!chatRoomRepository.canUserAccessRoom(message.getRoomId(), userId)) {
            throw new RuntimeException("您沒有權限查看此消息的附件");
        }
        
        List<ChatAttachment> attachments = chatAttachmentRepository.findByMessageIdOrderByUploadedAtAsc(messageId);
        return attachments.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    // 獲取用戶上傳的文件總大小
    public long getUserTotalFileSize(Integer userId) {
        Long totalSize = chatAttachmentRepository.getTotalFileSizeByUserId(userId);
        return totalSize != null ? totalSize : 0L;
    }
    
    // 獲取聊天室的所有附件
    public List<ChatAttachmentDTO> getRoomAttachments(Integer roomId, Integer userId) {
        // 檢查權限
        if (!chatRoomRepository.canUserAccessRoom(roomId, userId)) {
            throw new RuntimeException("您沒有權限查看此聊天室的附件");
        }
        
        List<ChatAttachment> attachments = chatAttachmentRepository.findAttachmentsByRoomId(roomId);
        return attachments.stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    // 刪除附件
    public boolean deleteAttachment(Integer attachmentId, Integer userId) {
        Optional<ChatAttachment> attachmentOpt = chatAttachmentRepository.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            return false;
        }
        
        ChatAttachment attachment = attachmentOpt.get();
        
        // 檢查權限
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(attachment.getMessageId());
        if (!messageOpt.isPresent()) {
            return false;
        }
        
        ChatMessage message = messageOpt.get();
        if (!message.getSenderId().equals(userId)) {
            throw new RuntimeException("您只能刪除自己上傳的附件");
        }
        
        try {
            // 刪除物理文件
            Path filePath = Paths.get(uploadPath).resolve(attachment.getFilePath());
            Files.deleteIfExists(filePath);
            
            // 刪除數據庫記錄
            chatAttachmentRepository.delete(attachment);
            
            return true;
        } catch (IOException e) {
            System.err.println("刪除附件文件時發生錯誤: " + e.getMessage());
            // 即使文件刪除失敗，也刪除數據庫記錄
            chatAttachmentRepository.delete(attachment);
            return true;
        }
    }
    
    // 獲取文件下載路徑
    public String getFileDownloadUrl(Integer attachmentId, Integer userId) {
        Optional<ChatAttachment> attachmentOpt = chatAttachmentRepository.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            throw new RuntimeException("附件不存在");
        }
        
        ChatAttachment attachment = attachmentOpt.get();
        
        // 檢查權限
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(attachment.getMessageId());
        if (!messageOpt.isPresent()) {
            throw new RuntimeException("消息不存在");
        }
        
        ChatMessage message = messageOpt.get();
        if (!chatRoomRepository.canUserAccessRoom(message.getRoomId(), userId)) {
            throw new RuntimeException("您沒有權限下載此附件");
        }
        
        return "/api/chat/files/download/" + attachmentId;
    }
    
    // 獲取文件物理路徑
    public Path getFilePhysicalPath(Integer attachmentId) {
        Optional<ChatAttachment> attachmentOpt = chatAttachmentRepository.findById(attachmentId);
        if (!attachmentOpt.isPresent()) {
            throw new RuntimeException("附件不存在");
        }
        
        ChatAttachment attachment = attachmentOpt.get();
        return Paths.get(uploadPath).resolve(attachment.getFilePath());
    }
    
    // 驗證文件
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能為空");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("文件大小超過限制（" + (maxFileSize / 1024 / 1024) + "MB）");
        }
        
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new RuntimeException("文件名不能為空");
        }
        
        String fileExtension = getFileExtension(fileName).toLowerCase();
        List<String> allowedTypes = Arrays.asList(allowedFileTypes.toLowerCase().split(","));
        
        if (!allowedTypes.contains(fileExtension)) {
            throw new RuntimeException("不支持的文件類型：" + fileExtension + "。支持的類型：" + allowedFileTypes);
        }
    }
    
    // 獲取文件擴展名
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex >= 0 ? fileName.substring(dotIndex + 1) : "";
    }
    
    // 生成唯一文件名
    private String generateFileName(String originalFileName) {
        String fileExtension = getFileExtension(originalFileName);
        String nameWithoutExtension = originalFileName.substring(0, 
            originalFileName.length() - fileExtension.length() - 1);
        
        // 清理文件名中的特殊字符
        nameWithoutExtension = nameWithoutExtension.replaceAll("[^a-zA-Z0-9\\u4e00-\\u9fff]", "_");
        
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return nameWithoutExtension + "_" + uuid + "." + fileExtension;
    }
    
    // 生成文件存儲路徑
    private String generateFilePath(Integer roomId, String fileName) {
        LocalDateTime now = LocalDateTime.now();
        String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "room_" + roomId + "/" + datePath + "/" + fileName;
    }
    
    // 轉換為DTO
    private ChatAttachmentDTO convertToDTO(ChatAttachment attachment) {
        ChatAttachmentDTO dto = new ChatAttachmentDTO();
        dto.setId(attachment.getId());
        dto.setMessageId(attachment.getMessageId());
        dto.setFileName(attachment.getFileName());
        dto.setFilePath(attachment.getFilePath());
        dto.setFileType(attachment.getFileType());
        dto.setFileSize(attachment.getFileSize());
        dto.setUploadedAt(attachment.getUploadedAt());
        
        // 設置下載URL
        dto.setDownloadUrl("/api/chat/files/download/" + attachment.getId());
        
        return dto;
    }
}
