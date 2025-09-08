package com.eams.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.eams.Service.chat.OnlineStatusService;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    @Autowired
    private OnlineStatusService onlineStatusService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        System.out.println("WebSocket連接建立: " + event.getMessage());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Integer userId = (Integer) headerAccessor.getSessionAttributes().get("userId");
        
        if (username != null && userId != null) {
            System.out.println("用戶斷開連接: " + username);
            
            // 更新用戶為離線狀態
            try {
                onlineStatusService.handleUserOffline(userId);
                
                // 廣播用戶離線狀態
                messagingTemplate.convertAndSend("/topic/user.disconnected", 
                    new UserStatusMessage(userId, username, "offline"));
                    
            } catch (Exception e) {
                System.err.println("處理用戶離線狀態時發生錯誤: " + e.getMessage());
            }
        }
    }
    
    // 用戶狀態消息類
    public static class UserStatusMessage {
        private Integer userId;
        private String username;
        private String status;
        
        public UserStatusMessage(Integer userId, String username, String status) {
            this.userId = userId;
            this.username = username;
            this.status = status;
        }
        
        // Getter 和 Setter
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
