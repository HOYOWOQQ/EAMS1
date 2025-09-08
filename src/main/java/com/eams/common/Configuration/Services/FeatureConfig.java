package com.eams.common.Configuration.Services;

import java.time.LocalDateTime;

public class FeatureConfig {
    private String featureKey;
    private Boolean isEnabled;
    private LocalDateTime lastModified;
    private String description;

    // getters and setters
    public String getFeatureKey() { return featureKey; }
    public void setFeatureKey(String featureKey) { this.featureKey = featureKey; }
    
    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}