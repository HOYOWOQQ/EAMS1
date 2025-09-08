//package com.eams.config.chat;
//
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@ConfigurationProperties(prefix = "chat")
//public class ChatConfig {
//    
//    private File file = new File();
//    private Message message = new Message();
//    private Room room = new Room();
//    private Online online = new Online();
//    
//    // 文件配置
//    public static class File {
//        private String uploadPath = "uploads/chat";
//        private long maxSize = 52428800; // 50MB
//        private String allowedTypes = "jpg,png,gif,pdf,doc,docx,xlsx,ppt,pptx,txt,zip,rar";
//        
//        // Getter 和 Setter
//        public String getUploadPath() { return uploadPath; }
//        public void setUploadPath(String uploadPath) { this.uploadPath = uploadPath; }
//        
//        public long getMaxSize() { return maxSize; }
//        public void setMaxSize(long maxSize) { this.maxSize = maxSize; }
//        
//        public String getAllowedTypes() { return allowedTypes; }
//        public void setAllowedTypes(String allowedTypes) { this.allowedTypes = allowedTypes; }
//    }
//    
//    // 消息配置
//    public static class Message {
//        private int maxLength = 2000;
//        private int retentionDays = 365;
//        private int pageSize = 20;
//        
//        // Getter 和 Setter
//        public int getMaxLength() { return maxLength; }
//        public void setMaxLength(int maxLength) { this.maxLength = maxLength; }
//        
//        public int getRetentionDays() { return retentionDays; }
//        public void setRetentionDays(int retentionDays) { this.retentionDays = retentionDays; }
//        
//        public int getPageSize() { return pageSize; }
//        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
//    }
//    
//    // 聊天室配置
//    public static class Room {
//        private int maxMembers = 50;
//        private int maxRoomsPerUser = 10;
//        private boolean enableGroupChat = true;
//        private boolean enablePrivateChat = true;
//        
//        // Getter 和 Setter
//        public int getMaxMembers() { return maxMembers; }
//        public void setMaxMembers(int maxMembers) { this.maxMembers = maxMembers; }
//        
//        public int getMaxRoomsPerUser() { return maxRoomsPerUser; }
//        public void setMaxRoomsPerUser(int maxRoomsPerUser) { this.maxRoomsPerUser = maxRoomsPerUser; }
//        
//        public boolean isEnableGroupChat() { return enableGroupChat; }
//        public void setEnableGroupChat(boolean enableGroupChat) { this.enableGroupChat = enableGroupChat; }
//        
//        public boolean isEnablePrivateChat() { return enablePrivateChat; }
//        public void setEnablePrivateChat(boolean enablePrivateChat) { this.enablePrivateChat = enablePrivateChat; }
//    }
//    
//    // 在線狀態配置
//    public static class Online {
//        private int offlineTimeoutMinutes = 5;
//        private int heartbeatIntervalSeconds = 30;
//        
//        // Getter 和 Setter
//        public int getOfflineTimeoutMinutes() { return offlineTimeoutMinutes; }
//        public void setOfflineTimeoutMinutes(int offlineTimeoutMinutes) { 
//            this.offlineTimeoutMinutes = offlineTimeoutMinutes; 
//        }
//        
//        public int getHeartbeatIntervalSeconds() { return heartbeatIntervalSeconds; }
//        public void setHeartbeatIntervalSeconds(int heartbeatIntervalSeconds) { 
//            this.heartbeatIntervalSeconds = heartbeatIntervalSeconds; 
//        }
//    }
//    
//    // Getter 和 Setter
//    public File getFile() { return file; }
//    public void setFile(File file) { this.file = file; }
//    
//    public Message getMessage() { return message; }
//    public void setMessage(Message message) { this.message = message; }
//    
//    public Room getRoom() { return room; }
//    public void setRoom(Room room) { this.room = room; }
//    
//    public Online getOnline() { return online; }
//    public void setOnline(Online online) { this.online = online; }
//}
//
//// 定時任務配置
//package com.eams.Schedule.chat;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import com.eams.Service.chat.OnlineStatusService;
//
//@Component
//public class ChatScheduledTasks {
//    
//    @Autowired
//    private OnlineStatusService onlineStatusService;
//    
//    // 每5分鐘清理離線用戶狀態
//    @Scheduled(fixedRate = 300000) // 5分鐘 = 300,000毫秒
//    public void cleanupOfflineUsers() {
//        try {
//            int affectedRows = onlineStatusService.batchSetOfflineStatus(5); // 5分鐘內無活動的用戶設為離線
//            if (affectedRows > 0) {
//                System.out.println("清理離線用戶狀態，影響行數：" + affectedRows);
//            }
//        } catch (Exception e) {
//            System.err.println("清理離線用戶狀態時發生錯誤：" + e.getMessage());
//        }
//    }
//    
//    // 每小時統計在線用戶數量（可用於監控）
//    @Scheduled(fixedRate = 3600000) // 1小時 = 3,600,000毫秒
//    public void logOnlineUserStats() {
//        try {
//            long onlineCount = onlineStatusService.getOnlineMemberCount();
//            System.out.println("當前在線用戶數量：" + onlineCount);
//        } catch (Exception e) {
//            System.err.println("統計在線用戶數量時發生錯誤：" + e.getMessage());
//        }
//    }
//    
//    // 每天凌晨2點清理過期的消息（如果有設置保留天數）
//    @Scheduled(cron = "0 0 2 * * ?")
//    public void cleanupExpiredMessages() {
//        try {
//            // 這裡可以實現消息清理邏輯
//            // 根據系統配置的消息保留天數來清理過期消息
//            System.out.println("執行過期消息清理任務");
//        } catch (Exception e) {
//            System.err.println("清理過期消息時發生錯誤：" + e.getMessage());
//        }
//    }
//}