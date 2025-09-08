package com.eams.common.Configuration.Services;


import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Service
public class FeatureToggleManager {
    
    private static final Logger logger = LoggerFactory.getLogger(FeatureToggleManager.class);
    
    private final ConfigurationManager configManager;

    public FeatureToggleManager(ConfigurationManager configManager) {
        this.configManager = configManager;
    }

    public CompletableFuture<Boolean> isEnabledAsync(String featureKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return configManager.getConfigAsync("features", featureKey, false, Boolean.class).join();
            } catch (Exception ex) {
                logger.error("Error checking feature toggle {}", featureKey, ex);
                return false;
            }
        });
    }

    public CompletableFuture<Void> enableFeatureAsync(String featureKey, Integer userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                configManager.setConfigAsync("features", featureKey, true, "功能開關: " + featureKey, userId).join();
                logger.info("Feature {} enabled by user {}", featureKey, userId);
            } catch (Exception ex) {
                logger.error("Error enabling feature {}", featureKey, ex);
                throw new RuntimeException(ex);
            }
        });
    }

    public CompletableFuture<Void> disableFeatureAsync(String featureKey, Integer userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                configManager.setConfigAsync("features", featureKey, false, "功能開關: " + featureKey, userId).join();
                logger.info("Feature {} disabled by user {}", featureKey, userId);
            } catch (Exception ex) {
                logger.error("Error disabling feature {}", featureKey, ex);
                throw new RuntimeException(ex);
            }
        });
    }

    public CompletableFuture<Map<String, Boolean>> getAllFeaturesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> configs = configManager.getCategoryConfigsAsync("features").join();
                Map<String, Boolean> result = new HashMap<>();
                
                for (Map.Entry<String, Object> entry : configs.entrySet()) {
                    result.put(entry.getKey(), Boolean.valueOf(entry.getValue().toString()));
                }
                
                return result;
            } catch (Exception ex) {
                logger.error("Error getting all features", ex);
                return new HashMap<>();
            }
        });
    }

    public CompletableFuture<FeatureConfig> getFeatureConfigAsync(String featureKey) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Boolean isEnabled = isEnabledAsync(featureKey).join();
                FeatureConfig config = new FeatureConfig();
                config.setFeatureKey(featureKey);
                config.setIsEnabled(isEnabled);
                config.setLastModified(LocalDateTime.now());
                return config;
            } catch (Exception ex) {
                logger.error("Error getting feature config {}", featureKey, ex);
                FeatureConfig config = new FeatureConfig();
                config.setFeatureKey(featureKey);
                config.setIsEnabled(false);
                return config;
            }
        });
    }

    // 同步版本
    public boolean isEnabled(String featureKey) {
        return isEnabledAsync(featureKey).join();
    }
}