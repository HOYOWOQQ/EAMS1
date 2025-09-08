package com.eams.common.Configuration.Services;

import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eams.common.Configuration.Repository.SystemConfigurationRepository;
import com.eams.common.Configuration.entity.SystemConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ConfigurationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String CACHE_NAME = "systemConfigs";
    private static final int CACHE_EXPIRATION_MINUTES = 60;
    
    private final SystemConfigurationRepository configRepo;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    public ConfigurationManager(
            SystemConfigurationRepository configRepo,
            CacheManager cacheManager,
            ObjectMapper objectMapper) {
        this.configRepo = configRepo;
        this.cacheManager = cacheManager;
        this.objectMapper = objectMapper;
    }

    public <T> CompletableFuture<T> getConfigAsync(String category, String key, T defaultValue, Class<T> clazz) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = "config_" + category + "_" + key;
            Cache cache = cacheManager.getCache(CACHE_NAME);
            
            if (cache != null) {
                Cache.ValueWrapper cached = cache.get(cacheKey);
                if (cached != null) {
                    @SuppressWarnings("unchecked")
                    T cachedValue = (T) cached.get();
                    return cachedValue;
                }
            }

            try {
                SystemConfiguration config = configRepo.findByCategoryAndKey(category, key);
                if (config == null || !config.getIsActive()) {
                    return defaultValue;
                }

                T value = convertValue(config.getConfigValue(), config.getDataType(), defaultValue, clazz);
                if (cache != null) {
                    cache.put(cacheKey, value);
                }
                return value;
            } catch (Exception ex) {
                logger.error("Error getting config {}.{}", category, key, ex);
                return defaultValue;
            }
        });
    }

    public CompletableFuture<Void> setConfigAsync(String category, String key, Object value, String description, Integer userId) {
        return CompletableFuture.runAsync(() -> {
            try {
                SystemConfiguration config = configRepo.findByCategoryAndKey(category, key);
                String stringValue = value != null ? value.toString() : null;
                String dataType = getDataType(value);

                if (config == null) {
                    config = new SystemConfiguration();
                    config.setConfigCategory(category);
                    config.setConfigKey(key);
                    config.setConfigValue(stringValue);
                    config.setDataType(dataType);
                    config.setDescription(description);
                    config.setIsActive(true);
                    config.setCreatedAt(LocalDateTime.now());
                    config.setUpdatedBy(userId);
                    configRepo.save(config);
                } else {
                    config.setConfigValue(stringValue);
                    config.setDataType(dataType);
                    if (description != null) {
                        config.setDescription(description);
                    }
                    config.setUpdatedAt(LocalDateTime.now());
                    config.setUpdatedBy(userId);
                    configRepo.save(config);
                }

                invalidateCache(category, key);
                logger.info("Configuration updated: {}.{} = {}", category, key, stringValue);
            } catch (Exception ex) {
                logger.error("Error setting config {}.{}", category, key, ex);
                throw new RuntimeException(ex);
            }
        });
    }

    public CompletableFuture<Map<String, Object>> getCategoryConfigsAsync(String category) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<SystemConfiguration> configs = configRepo.findByCategory(category);
                Map<String, Object> result = new HashMap<>();

                for (SystemConfiguration config : configs) {
                    if (config.getIsActive()) {
                        Object value = convertValue(config.getConfigValue(), config.getDataType(), null, Object.class);
                        result.put(config.getConfigKey(), value);
                    }
                }

                return result;
            } catch (Exception ex) {
                logger.error("Error getting category configs {}", category, ex);
                return new HashMap<>();
            }
        });
    }

    public CompletableFuture<Boolean> isFeatureEnabledAsync(String featureKey) {
        return getConfigAsync("features", featureKey, false, Boolean.class);
    }

    public void invalidateCache(String category, String key) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            if (category != null && key != null) {
                String cacheKey = "config_" + category + "_" + key;
                cache.evict(cacheKey);
            } else {
                cache.clear(); // 清除所有緩存
            }
        }
        logger.info("Configuration cache invalidated for category: {}", category != null ? category : "ALL");
    }

    @SuppressWarnings("unchecked")
    private <T> T convertValue(String value, String dataType, T defaultValue, Class<T> clazz) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }

        try {
            switch (dataType != null ? dataType.toLowerCase() : "string") {
                case "boolean":
                    return (T) Boolean.valueOf(value);
                case "number":
                    if (clazz == Integer.class) {
                        return (T) Integer.valueOf(value);
                    } else if (clazz == Long.class) {
                        return (T) Long.valueOf(value);
                    } else if (clazz == Double.class) {
                        return (T) Double.valueOf(value);
                    }
                    return (T) Integer.valueOf(value);
                case "json":
                    return objectMapper.readValue(value, clazz);
                default:
                    return (T) value;
            }
        } catch (Exception ex) {
            logger.warn("Error converting value {} to type {}", value, clazz.getSimpleName(), ex);
            return defaultValue;
        }
    }

    private String getDataType(Object value) {
        if (value instanceof Boolean) {
            return "boolean";
        } else if (value instanceof Number) {
            return "number";
        } else if (value instanceof String) {
            return "string";
        } else {
            return "json";
        }
    }
}