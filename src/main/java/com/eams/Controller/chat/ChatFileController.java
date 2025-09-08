package com.eams.Controller.chat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eams.Entity.chat.chatDTO.ChatAttachmentDTO;
import com.eams.Service.chat.ChatFileUploadService;
import com.eams.common.log.util.UserContextUtil;

@RestController
@RequestMapping("/api/chat/files")
public class ChatFileController {
    
    @Autowired
    private ChatFileUploadService chatFileUploadService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    // 上傳文件
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("messageId") Integer messageId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            ChatAttachmentDTO attachment = chatFileUploadService.uploadChatFile(file, messageId, userId.intValue());
            
            response.put("success", true);
            response.put("data", attachment);
            response.put("message", "文件上傳成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "文件上傳失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取消息附件列表
    @GetMapping("/message/{messageId}")
    public ResponseEntity<Map<String, Object>> getMessageAttachments(@PathVariable Integer messageId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<ChatAttachmentDTO> attachments = chatFileUploadService.getMessageAttachments(messageId, userId.intValue());
            
            response.put("success", true);
            response.put("data", attachments);
            response.put("message", "獲取附件列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取附件列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取聊天室附件列表
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoomAttachments(@PathVariable Integer roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<ChatAttachmentDTO> attachments = chatFileUploadService.getRoomAttachments(roomId, userId.intValue());
            
            response.put("success", true);
            response.put("data", attachments);
            response.put("message", "獲取聊天室附件列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取聊天室附件列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 下載文件
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Integer attachmentId) {
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Path filePath = chatFileUploadService.getFilePhysicalPath(attachmentId);
            
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            
            String contentType;
            try {
                contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
            } catch (IOException e) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                    .body(resource);
                    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 刪除附件
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Map<String, Object>> deleteAttachment(@PathVariable Integer attachmentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            boolean deleted = chatFileUploadService.deleteAttachment(attachmentId, userId.intValue());
            if (!deleted) {
                response.put("success", false);
                response.put("message", "附件不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "附件刪除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除附件失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取用戶文件使用統計
    @GetMapping("/usage/stats")
    public ResponseEntity<Map<String, Object>> getUserFileStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            long totalSize = chatFileUploadService.getUserTotalFileSize(userId.intValue());
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSizeBytes", totalSize);
            stats.put("totalSizeMB", Math.round(totalSize / 1024.0 / 1024.0 * 100.0) / 100.0);
            
            response.put("success", true);
            response.put("data", stats);
            response.put("message", "獲取文件使用統計成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取文件使用統計失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}