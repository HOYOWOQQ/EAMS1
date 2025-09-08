package com.eams.Controller.chat;
import java.util.ArrayList;
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

import com.eams.Entity.chat.chatDTO.ChatRoomCreateDTO;
import com.eams.Entity.chat.chatDTO.ChatRoomDTO;
import com.eams.Entity.chat.chatDTO.ChatRoomMemberDTO;
import com.eams.Entity.chat.chatDTO.ChatRoomUpdateDTO;
import com.eams.Entity.chat.chatDTO.InvitableMemberDTO;
import com.eams.Service.chat.ChatRoomService;
import com.eams.common.log.util.UserContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat/rooms")
@Validated
public class ChatRoomController {
    
    @Autowired
    private ChatRoomService chatRoomService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    // 獲取用戶的聊天室列表
    @GetMapping
    public ResponseEntity<Map<String, Object>> getUserChatRooms() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<ChatRoomDTO> chatRooms = chatRoomService.getUserChatRooms(userId.intValue());
            
            response.put("success", true);
            response.put("data", chatRooms);
            response.put("message", "獲取聊天室列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取聊天室列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取聊天室詳情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getChatRoomDetail(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            ChatRoomDTO chatRoom = chatRoomService.getChatRoomDetail(id, userId.intValue());
            if (chatRoom == null) {
                response.put("success", false);
                response.put("message", "聊天室不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("data", chatRoom);
            response.put("message", "獲取聊天室詳情成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取聊天室詳情失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 創建聊天室 - 增加角色權限檢查
    @PostMapping
    public ResponseEntity<Map<String, Object>> createChatRoom(@Valid @RequestBody ChatRoomCreateDTO createDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            ChatRoomDTO chatRoom = chatRoomService.createChatRoom(createDTO, userId.intValue());
            
            response.put("success", true);
            response.put("data", chatRoom);
            response.put("message", "聊天室創建成功");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "創建聊天室失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 更新聊天室
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateChatRoom(@PathVariable Integer id,
                                                             @Valid @RequestBody ChatRoomUpdateDTO updateDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            ChatRoomDTO chatRoom = chatRoomService.updateChatRoom(id, updateDTO, userId.intValue());
            
            response.put("success", true);
            response.put("data", chatRoom);
            response.put("message", "聊天室更新成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新聊天室失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 刪除聊天室
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteChatRoom(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            boolean deleted = chatRoomService.deleteChatRoom(id, userId.intValue());
            if (!deleted) {
                response.put("success", false);
                response.put("message", "聊天室不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "聊天室刪除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除聊天室失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取聊天室成員列表
    @GetMapping("/{id}/members")
    public ResponseEntity<Map<String, Object>> getChatRoomMembers(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<ChatRoomMemberDTO> members = chatRoomService.getChatRoomMembers(id, userId.intValue());
            
            response.put("success", true);
            response.put("data", members);
            response.put("message", "獲取聊天室成員列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取聊天室成員列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 邀請成員到聊天室 - 新增專門的邀請端點
    @PostMapping("/{id}/invite")
    public ResponseEntity<Map<String, Object>> inviteMemberToRoom(@PathVariable Integer id,
                                                                 @RequestParam Integer memberId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            boolean invited = chatRoomService.inviteMemberToRoom(id, memberId, userId.intValue());
            
            response.put("success", true);
            response.put("message", "成員邀請成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "邀請成員失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 批量邀請成員到聊天室
    @PostMapping("/{id}/invite/batch")
    public ResponseEntity<Map<String, Object>> batchInviteMembers(@PathVariable Integer id,
                                                                 @RequestBody List<Integer> memberIds) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            int successCount = 0;
            List<String> errors = new ArrayList<>();
            
            for (Integer memberId : memberIds) {
                try {
                    boolean invited = chatRoomService.inviteMemberToRoom(id, memberId, userId.intValue());
                    if (invited) {
                        successCount++;
                    }
                } catch (RuntimeException e) {
                    errors.add("邀請用戶 " + memberId + " 失敗：" + e.getMessage());
                }
            }
            
            response.put("success", true);
            response.put("successCount", successCount);
            response.put("totalCount", memberIds.size());
            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }
            response.put("message", "批量邀請完成，成功邀請 " + successCount + " 人");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量邀請失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取可邀請的成員列表
    @GetMapping("/{id}/invitable-members")
    public ResponseEntity<Map<String, Object>> getInvitableMembers(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<InvitableMemberDTO> invitableMembers = chatRoomService.getInvitableMembers(id, userId.intValue());
            
            response.put("success", true);
            response.put("data", invitableMembers);
            response.put("message", "獲取可邀請成員列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取可邀請成員列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 添加成員到聊天室（保留原有端點）
    @PostMapping("/{id}/members")
    public ResponseEntity<Map<String, Object>> addMemberToRoom(@PathVariable Integer id,
                                                              @RequestParam Integer memberId) {
        return inviteMemberToRoom(id, memberId);
    }
    
    // 移除聊天室成員
    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<Map<String, Object>> removeMemberFromRoom(@PathVariable Integer id,
                                                                   @PathVariable Integer memberId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            boolean removed = chatRoomService.removeMemberFromRoom(id, memberId, userId.intValue());
            
            response.put("success", true);
            response.put("message", "成員移除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "移除成員失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 搜索聊天室
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchChatRooms(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<ChatRoomDTO> chatRooms = chatRoomService.searchChatRooms(keyword, userId.intValue());
            
            response.put("success", true);
            response.put("data", chatRooms);
            response.put("message", "搜索聊天室成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索聊天室失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}