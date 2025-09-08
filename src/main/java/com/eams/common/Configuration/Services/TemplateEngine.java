package com.eams.common.Configuration.Services;

import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.eams.common.Configuration.Repository.ContentTemplateRepository;
import com.eams.common.Configuration.entity.ContentTemplate;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class TemplateEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(TemplateEngine.class);
    private static final String CACHE_NAME = "contentTemplates";
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{(\\w+)\\}\\}");
    
    private final ContentTemplateRepository templateRepo;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;

    public TemplateEngine(
            ContentTemplateRepository templateRepo,
            CacheManager cacheManager,
            ObjectMapper objectMapper) {
        this.templateRepo = templateRepo;
        this.cacheManager = cacheManager;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<String> renderTemplateAsync(String templateKey, Object data, String category) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TemplateInfo template = getTemplateAsync(templateKey, category).join();
                if (template == null) {
                    throw new TemplateNotFoundException("Template " + category + "." + templateKey + " not found");
                }

                templateRepo.incrementUsageCount(template.getId());
                return renderContent(template.getContent(), data);
            } catch (Exception ex) {
                logger.error("Error rendering template {}.{}", category, templateKey, ex);
                throw new RuntimeException(ex);
            }
        });
    }

    public String renderContent(String content, Object data) {
        if (content == null || content.isEmpty() || data == null) {
            return content;
        }

        Map<String, Object> properties = getObjectProperties(data);
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = properties.get(key);
            String replacement = value != null ? value.toString() : matcher.group(0);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public CompletableFuture<TemplateInfo> getTemplateAsync(String templateKey, String category) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = "template_" + category + "_" + templateKey;
            Cache cache = cacheManager.getCache(CACHE_NAME);
            
            if (cache != null) {
                Cache.ValueWrapper cached = cache.get(cacheKey);
                if (cached != null) {
                    return (TemplateInfo) cached.get();
                }
            }

            try {
                ContentTemplate template = templateRepo.findByKeyAndCategory(templateKey, category);
                if (template == null || !template.getIsActive()) {
                    return null;
                }

                TemplateInfo templateInfo = new TemplateInfo();
                templateInfo.setId(template.getId());
                templateInfo.setTemplateKey(template.getTemplateKey());
                templateInfo.setTemplateName(template.getTemplateName());
                templateInfo.setSubject(template.getSubject());
                templateInfo.setContent(template.getContent());
                templateInfo.setVariables(parseVariables(template.getVariables()));

                if (cache != null) {
                    cache.put(cacheKey, templateInfo);
                }
                return templateInfo;
            } catch (Exception ex) {
                logger.error("Error getting template {}.{}", category, templateKey, ex);
                return null;
            }
        });
    }

    public CompletableFuture<Boolean> validateTemplateAsync(String content, String[] requiredVariables) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> variablesInContent = extractVariables(content);
                return Arrays.stream(requiredVariables)
                        .allMatch(variablesInContent::contains);
            } catch (Exception ex) {
                logger.error("Error validating template", ex);
                return false;
            }
        });
    }

    private Map<String, Object> getObjectProperties(Object data) {
        if (data instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) data;
            return map;
        }

        try {
            return objectMapper.convertValue(data, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            logger.warn("Error converting object to map", ex);
            return new HashMap<>();
        }
    }

    private List<String> extractVariables(String content) {
        List<String> variables = new ArrayList<>();
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables.stream().distinct().collect(Collectors.toList());
    }

    private List<String> parseVariables(String variablesJson) {
        try {
            if (variablesJson == null || variablesJson.isEmpty()) {
                return new ArrayList<>();
            }

            Map<String, String> variableMap = objectMapper.readValue(variablesJson, 
                    new TypeReference<Map<String, String>>() {});
            return new ArrayList<>(variableMap.keySet());
        } catch (Exception ex) {
            logger.warn("Error parsing variables JSON", ex);
            return new ArrayList<>();
        }
    }
}
