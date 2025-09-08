package com.eams.common.Monitoring.Services;

import java.time.LocalDateTime;

public class SystemHealthStatus {
    private Double cpuUsage;
    private Double memoryUsage;
    private Double diskUsage;
    private Integer overallScore;
    private String status;
    private LocalDateTime checkTime = LocalDateTime.now();

    // getters and setters
    public Double getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(Double cpuUsage) { this.cpuUsage = cpuUsage; }
    
    public Double getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(Double memoryUsage) { this.memoryUsage = memoryUsage; }
    
    public Double getDiskUsage() { return diskUsage; }
    public void setDiskUsage(Double diskUsage) { this.diskUsage = diskUsage; }
    
    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCheckTime() { return checkTime; }
    public void setCheckTime(LocalDateTime checkTime) { this.checkTime = checkTime; }
}