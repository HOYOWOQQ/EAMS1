package com.eams.common.Configuration.Controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eams.common.Configuration.Services.ConfigurationManager;
import com.eams.common.Configuration.Repository.SystemConfigurationRepository;
import com.eams.common.Configuration.entity.SystemConfiguration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/system-config")
public class SystemConfigController {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigController.class);
    
    private final ConfigurationManager configurationManager;
    private final SystemConfigurationRepository configRepository;

    public SystemConfigController(
            ConfigurationManager configurationManager,
            SystemConfigurationRepository configRepository) {
        this.configurationManager = configurationManager;
        this.configRepository = configRepository;
    }

    /**
     * 獲取指定分類的所有配置
     * GET /EAMS/api/system-config/category/{category}
     */
    @GetMapping("/category/{category}")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getCategoryConfigs(
            @PathVariable String category) {
        
        return configurationManager.getCategoryConfigsAsync(category)
            .thenApply(configs -> {
                if (configs.isEmpty()) {
                    logger.warn("No configurations found for  category: {}", category);
                }
                return ResponseEntity.ok(configs);
            })
            .exceptionally(ex -> {
                logger.error("Error getting category configs for: {}", category, ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get configurations"));
            });
    }

    /**
     * 獲取配置列表（包含詳細資訊）
     * GET /EAMS/api/system-config/list?category=system
     */
    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> getConfigsList(
            @RequestParam(required = false) String category) {
        
        try {
            List<SystemConfiguration> configs;
            
            if (category != null && !category.isEmpty()) {
                configs = configRepository.findByCategory(category);
            } else {
                configs = configRepository.findAll();
            }
            
            List<Map<String, Object>> result = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            for (SystemConfiguration config : configs) {
                if (config.getIsActive()) {
                    Map<String, Object> configMap = new HashMap<>();
                    configMap.put("id", config.getId());
                    configMap.put("configCategory", config.getConfigCategory());
                    configMap.put("configKey", config.getConfigKey());
                    configMap.put("configValue", config.getConfigValue());
                    configMap.put("dataType", config.getDataType());
                    configMap.put("description", config.getDescription());
                    configMap.put("sortOrder", config.getSortOrder());
                    configMap.put("isActive", config.getIsActive());
                    configMap.put("createdAt", config.getCreatedAt() != null ? 
                        config.getCreatedAt().format(formatter) : null);
                    configMap.put("updatedAt", config.getUpdatedAt() != null ? 
                        config.getUpdatedAt().format(formatter) : null);
                    configMap.put("updatedBy", config.getUpdatedBy());
                    
                    result.add(configMap);
                }
            }
            
            // 按排序順序和配置鍵排序
//            result.sort((a, b) -> {
//                Integer sortA = (Integer) a.get("sortOrder");
//                Integer sortB = (Integer) b.get("sortOrder");
//                if (sortA != null && sortB != null && !sortA.equals(sortB)) {
//                    return sortA.compareTo(sortB);
//                }
//                String keyA = (String) a.get("configKey");
//                String keyB = (String) b.get("configKey");
//                return keyA.compareTo(keyB);
//            });
//            
            return ResponseEntity.ok(result);
            
        } catch (Exception ex) {
            logger.error("Error getting configs list for category: {}", category, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ArrayList<>());
        }
    }

    /**
     * 設定配置值
     * POST /EAMS/api/system-config/set
     */
    @PostMapping("/set")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> setConfig(
            @RequestBody SetConfigRequest request) {
        
        // TODO: 從JWT或Session獲取當前用戶ID，這裡先使用預設值
        Integer currentUserId = getCurrentUserId();
        
        return configurationManager.setConfigAsync(
                request.getCategory(), 
                request.getKey(), 
                request.getValue(), 
                request.getDescription(), 
                currentUserId)
            .thenApply(result -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "配置已成功更新");
                response.put("category", request.getCategory());
                response.put("key", request.getKey());
                response.put("value", request.getValue());
                response.put("updatedAt", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                logger.info("Configuration updated: {}.{} = {}", 
                    request.getCategory(), request.getKey(), request.getValue());
                
                return ResponseEntity.ok(response);
            })
            .exceptionally(ex -> {
                logger.error("Error setting config {}.{}", 
                    request.getCategory(), request.getKey(), ex);
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "配置更新失敗: " + ex.getMessage());
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
            });
    }

    /**
     * 批量設定配置
     * POST /EAMS/api/system-config/batch-set
     */
    @PostMapping("/batch-set")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> setBatchConfigs(
            @RequestBody BatchSetConfigRequest request) {
        
        Integer currentUserId = getCurrentUserId();
        
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        
        for (SetConfigRequest config : request.getConfigs()) {
            CompletableFuture<Void> future = configurationManager.setConfigAsync(
                config.getCategory(), 
                config.getKey(), 
                config.getValue(), 
                config.getDescription(), 
                currentUserId);
            futures.add(future);
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(result -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "批量配置已成功更新");
                response.put("updatedCount", request.getConfigs().size());
                response.put("updatedAt", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                
                logger.info("Batch configuration updated: {} configs", request.getConfigs().size());
                
                return ResponseEntity.ok(response);
            })
            .exceptionally(ex -> {
                logger.error("Error batch setting configs", ex);
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "批量配置更新失敗: " + ex.getMessage());
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
            });
    }

    /**
     * 獲取單個配置值
     * GET /EAMS/api/system-config/get/{category}/{key}
     */
    @GetMapping("/get/{category}/{key}")
    public CompletableFuture<ResponseEntity<Map<String, Object>>> getConfig(
            @PathVariable String category,
            @PathVariable String key,
            @RequestParam(required = false) String defaultValue) {
        
        return configurationManager.getConfigAsync(category, key, defaultValue, String.class)
            .thenApply(value -> {
                Map<String, Object> response = new HashMap<>();
                response.put("category", category);
                response.put("key", key);
                response.put("value", value);
                response.put("found", value != null && !value.equals(defaultValue));
                
                return ResponseEntity.ok(response);
            })
            .exceptionally(ex -> {
                logger.error("Error getting config {}.{}", category, key, ex);
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Failed to get configuration");
                errorResponse.put("category", category);
                errorResponse.put("key", key);
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
            });
    }

    /**
     * 匯出配置
     * GET /EAMS/api/system-config/export?category=system
     */
    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportConfigs(
            @RequestParam(required = false) String category) {
        
        try {
            List<SystemConfiguration> configs;
            
            if (category != null && !category.isEmpty()) {
                configs = configRepository.findByCategory(category);
            } else {
                configs = configRepository.findAll();
            }
            
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("exportTime", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            exportData.put("category", category != null ? category : "all");
            exportData.put("totalCount", configs.size());
            
            List<Map<String, Object>> configList = new ArrayList<>();
            
            for (SystemConfiguration config : configs) {
                if (config.getIsActive()) {
                    Map<String, Object> configMap = new HashMap<>();
                    configMap.put("category", config.getConfigCategory());
                    configMap.put("key", config.getConfigKey());
                    configMap.put("value", config.getConfigValue());
                    configMap.put("dataType", config.getDataType());
                    configMap.put("description", config.getDescription());
                    configMap.put("sortOrder", config.getSortOrder());
                    
                    configList.add(configMap);
                }
            }
            
            exportData.put("configurations", configList);
            
            logger.info("Exported {} configurations for category: {}", 
                configList.size(), category != null ? category : "all");
            
            return ResponseEntity.ok(exportData);
            
        } catch (Exception ex) {
            logger.error("Error exporting configs for category: {}", category, ex);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "匯出配置失敗");
            errorResponse.put("message", ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }

    /**
     * 清除配置快取
     * POST /EAMS/api/system-config/clear-cache
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, Object>> clearCache(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String key) {
        
        try {
            configurationManager.invalidateCache(category, key);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "快取已清除");
            response.put("clearedAt", LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            if (category != null) {
                response.put("category", category);
            }
            if (key != null) {
                response.put("key", key);
            }
            
            logger.info("Cache cleared for category: {}, key: {}", category, key);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception ex) {
            logger.error("Error clearing cache", ex);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "清除快取失敗: " + ex.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }

    /**
     * 獲取當前用戶ID
     * TODO: 實作JWT或Session驗證
     */
    private Integer getCurrentUserId() {
        // 這裡應該從JWT Token或Session中獲取當前用戶ID
        // 暫時返回預設值，需要根據實際認證機制實作
        return 1; // 預設管理員ID
    }

    // ================================
    // 請求和回應的資料類別
    // ================================

    public static class SetConfigRequest {
        private String category;
        private String key;
        private Object value;
        private String description;

        // getters and setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class BatchSetConfigRequest {
        private List<SetConfigRequest> configs;

        public List<SetConfigRequest> getConfigs() { return configs; }
        public void setConfigs(List<SetConfigRequest> configs) { this.configs = configs; }
    }
}