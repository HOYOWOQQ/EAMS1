package com.eams.common.Support.Services;

import com.eams.Repository.member.MemberRepository;
import com.eams.Service.member.GmailOAuth2Service;
import com.eams.common.Support.entity.Notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailNotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationService.class);
    
    @Autowired
    private GmailOAuth2Service gmailOAuth2Service;

    @Autowired
    private MemberRepository memberRepository;

    public void sendNotification(Notification notification) {
        try {
            // 檢查 Gmail 服務是否可用
            if (!gmailOAuth2Service.isGmailServiceAvailable()) {
                logger.error("Gmail service not available for notification: {}", notification.getId());
                throw new RuntimeException("Gmail 服務未設定，請完成 OAuth2 授權");
            }

            List<String> emailAddresses = getRecipientEmails(notification);
            
            if (emailAddresses.isEmpty()) {
                logger.warn("No email addresses found for notification: {}", notification.getId());
                return;
            }
            
            for (String email : emailAddresses) {
                sendEmailToAddress(notification, email);
            }
            
            logger.info("Email notification sent to {} recipients: {}", 
                       emailAddresses.size(), notification.getId());
        } catch (Exception ex) {
            logger.error("Failed to send email notification: {}", notification.getId(), ex);
            throw ex;
        }
    }
    
    private List<String> getRecipientEmails(Notification notification) {
        switch (notification.getTargetType()) {
            case "user":
                return getEmailsByUserIds(notification.getTargetUsers());
            case "role":
                return getEmailsByRoles(notification.getTargetRoles());
            case "all":
                return memberRepository.findAllActiveEmails();
            default:
                return List.of();
        }
    }

    private List<String> getEmailsByUserIds(String targetUsers) {
        if (targetUsers == null || targetUsers.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            List<Integer> userIds = Arrays.stream(targetUsers.split(","))
                .map(String::trim)
                .map(Integer::parseInt)
                .toList();
            
            return memberRepository.findEmailsByIds(userIds);
            
        } catch (NumberFormatException ex) {
            logger.error("Invalid user IDs format: {}", targetUsers, ex);
            return List.of();
        }
    }

    private List<String> getEmailsByRoles(String targetRoles) {
        if (targetRoles == null || targetRoles.trim().isEmpty()) {
            return List.of();
        }
        
        List<String> roleCodes = Arrays.stream(targetRoles.split(","))
            .map(String::trim)
            .toList();
        
        return memberRepository.findEmailsByRoleCodes(roleCodes);
    }
    
    private void sendEmailToAddress(Notification notification, String email) {
        try {
            // 驗證郵件地址格式
            if (!isValidEmail(email)) {
                logger.warn("Invalid email address format: {}", email);
                return;
            }
            
            // 使用現有的 Gmail OAuth2 服務發送通知郵件
            gmailOAuth2Service.sendNotificationEmail(
                email, 
                notification.getTitle(), 
                notification.getContent(), 
                notification.getActionUrl()
            );
            
            logger.debug("Email sent successfully to: {}", maskEmail(email));
            
        } catch (Exception ex) {
            logger.error("Failed to send email to {}: {}", maskEmail(email), ex.getMessage());
            throw ex;
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        if (localPart.length() <= 3) {
            return "***@" + parts[1];
        }
        return localPart.substring(0, 2) + "***@" + parts[1];
    }
}