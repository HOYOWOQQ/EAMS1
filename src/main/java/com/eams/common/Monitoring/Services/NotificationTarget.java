package com.eams.common.Monitoring.Services;

import java.util.List;

public class NotificationTarget {
    private String type; // ALL, ROLE, INDIVIDUAL
    private List<String> roles;
    private List<Integer> userIds;

    // getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    
    public List<Integer> getUserIds() { return userIds; }
    public void setUserIds(List<Integer> userIds) { this.userIds = userIds; }
}