package com.eams.common.Configuration.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.eams.common.Configuration.entity.ContentTemplate;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentTemplateRepository extends JpaRepository<ContentTemplate, Integer> {
    
    // 根據模板鍵和分類查找模板
    @Query("SELECT ct FROM ContentTemplate ct WHERE ct.templateKey = :templateKey AND ct.templateCategory = :category AND ct.isActive = true")
    ContentTemplate findByKeyAndCategory(@Param("templateKey") String templateKey, @Param("category") String category);
    
    // 根據分類查找模板
    List<ContentTemplate> findByTemplateCategoryAndIsActiveTrueOrderByTemplateNameAsc(String templateCategory);
    
    // 查找所有啟用的模板
    List<ContentTemplate> findByIsActiveTrueOrderByTemplateCategoryAscTemplateNameAsc();
    
    // 根據使用次數排序
    @Query("SELECT ct FROM ContentTemplate ct WHERE ct.isActive = true ORDER BY ct.usageCount DESC")
    List<ContentTemplate> findByPopularity();
    
    // 增加使用次數
    @Modifying
    @Transactional
    @Query("UPDATE ContentTemplate ct SET ct.usageCount = ct.usageCount + 1 WHERE ct.id = :id")
    void incrementUsageCount(@Param("id") Integer id);
    
    // 模糊查詢模板名稱
    @Query("SELECT ct FROM ContentTemplate ct WHERE ct.templateName LIKE %:keyword% AND ct.isActive = true")
    List<ContentTemplate> findByKeywordInTemplateName(@Param("keyword") String keyword);
    
    // 根據創建者查找模板
    List<ContentTemplate> findByCreatedByOrderByCreatedAtDesc(Integer createdBy);
    
    // 檢查模板鍵是否存在
    boolean existsByTemplateCategoryAndTemplateKey(String templateCategory, String templateKey);
}
