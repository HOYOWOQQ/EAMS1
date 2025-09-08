package com.eams.Controller.notice;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.notice.DTO.CommentCreateDTO;
import com.eams.Entity.notice.DTO.CommentDTO;
import com.eams.Entity.notice.DTO.CommentUpdateDTO;
import com.eams.Service.notice.CommentService;
import com.eams.common.log.util.UserContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@Validated
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    // 創建留言
    @PostMapping
    public ResponseEntity<Map<String, Object>> createComment(@Valid @RequestBody CommentCreateDTO createDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            CommentDTO comment = commentService.createComment(createDTO, userId.intValue());
            
            response.put("success", true);
            response.put("data", comment);
            response.put("message", "留言創建成功");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "創建留言失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 更新留言
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateComment(@PathVariable Integer id,
                                                           @Valid @RequestBody CommentUpdateDTO updateDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            CommentDTO comment = commentService.updateComment(id, updateDTO, userId.intValue());
            if (comment == null) {
                response.put("success", false);
                response.put("message", "留言不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("data", comment);
            response.put("message", "留言更新成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新留言失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 刪除留言
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            boolean deleted = commentService.deleteComment(id, userId.intValue(), userRole);
            if (!deleted) {
                response.put("success", false);
                response.put("message", "留言不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "留言刪除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除留言失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}