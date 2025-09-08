package com.eams.Controller.chat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.chat.chatDTO.ChatMessageCreateDTO;
import com.eams.Entity.chat.chatDTO.ChatMessageDTO;
import com.eams.Entity.chat.chatDTO.ChatMessageUpdateDTO;
import com.eams.Service.chat.ChatMessageService;
import com.eams.common.log.util.UserContextUtil;

import jakarta.validation.Valid;
@RestController
@RequestMapping("/api/chat/messages")
@Validated
public class ChatMessageController {
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    // 發送消息
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(@Valid @RequestBody ChatMessageCreateDTO createDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            ChatMessageDTO message = chatMessageService.sendMessage(createDTO, userId.intValue());
            
            response.put("success", true);
            response.put("data", message);
            response.put("message", "消息發送成功");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發送消息失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取聊天室消息
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getChatRoomMessages(@PathVariable Integer roomId,
                                                                  @RequestParam(defaultValue = "0") Integer page,
                                                                  @RequestParam(defaultValue = "20") Integer size) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            Map<String, Object> result = chatMessageService.getChatRoomMessages(roomId, page, size, userId.intValue());
            
            response.put("success", true);
            response.putAll(result);
            response.put("message", "獲取消息列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取消息列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 編輯消息
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editMessage(@PathVariable Integer id,
                                                          @Valid @RequestBody ChatMessageUpdateDTO updateDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            ChatMessageDTO message = chatMessageService.editMessage(id, updateDTO.getContent(), userId.intValue());
            
            response.put("success", true);
            response.put("data", message);
            response.put("message", "消息編輯成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "編輯消息失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 刪除消息
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            boolean deleted = chatMessageService.deleteMessage(id, userId.intValue());
            if (!deleted) {
                response.put("success", false);
                response.put("message", "消息不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "消息刪除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除消息失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取未讀消息數量
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadMessageCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            long unreadCount = chatMessageService.getUnreadMessageCount(userId.intValue());
            
            response.put("success", true);
            response.put("data", unreadCount);
            response.put("message", "獲取未讀消息數量成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取未讀消息數量失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 搜索消息
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMessages(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<ChatMessageDTO> messages = chatMessageService.searchMessages(keyword, userId.intValue());
            
            response.put("success", true);
            response.put("data", messages);
            response.put("message", "搜索消息成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索消息失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}