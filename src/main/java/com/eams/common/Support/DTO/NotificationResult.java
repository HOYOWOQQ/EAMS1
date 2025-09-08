package com.eams.common.Support.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResult {
    private boolean success;
    private Long notificationId;
    private String message;
    
    public NotificationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}