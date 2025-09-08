package com.eams.Controller.chat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.chat.chatDTO.MemberOnlineStatusDTO;
import com.eams.Service.chat.OnlineStatusService;
import com.eams.common.log.util.UserContextUtil;

@RestController
@RequestMapping("/api/chat/online")
@Validated
public class OnlineStatusController {
    
    @Autowired
    private OnlineStatusService onlineStatusService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    // 更新在線狀態
    @PostMapping("/status")
    public ResponseEntity<Map<String, Object>> updateOnlineStatus(@RequestParam String status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            onlineStatusService.updateOnlineStatus(userId.intValue(), status);
            
            response.put("success", true);
            response.put("message", "在線狀態更新成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新在線狀態失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取在線用戶列表
    @GetMapping("/members")
    public ResponseEntity<Map<String, Object>> getOnlineMembers() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<MemberOnlineStatusDTO> onlineMembers = onlineStatusService.getOnlineMembers();
            
            response.put("success", true);
            response.put("data", onlineMembers);
            response.put("message", "獲取在線用戶列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取在線用戶列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取聊天室成員在線狀態
    @GetMapping("/room/{roomId}/members")
    public ResponseEntity<Map<String, Object>> getRoomMembersOnlineStatus(@PathVariable Integer roomId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<MemberOnlineStatusDTO> memberStatuses = onlineStatusService.getRoomMembersOnlineStatus(roomId);
            
            response.put("success", true);
            response.put("data", memberStatuses);
            response.put("message", "獲取聊天室成員在線狀態成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取聊天室成員在線狀態失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取在線用戶數量
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getOnlineMemberCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            long onlineCount = onlineStatusService.getOnlineMemberCount();
            
            response.put("success", true);
            response.put("data", onlineCount);
            response.put("message", "獲取在線用戶數量成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取在線用戶數量失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}