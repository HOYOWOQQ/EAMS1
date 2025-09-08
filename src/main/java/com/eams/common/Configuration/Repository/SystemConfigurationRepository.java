package com.eams.common.Configuration.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.eams.common.Configuration.entity.SystemConfiguration;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Integer> {
    
    // 根據分類和鍵查找配置
    @Query("SELECT sc FROM SystemConfiguration sc WHERE sc.configCategory = :category AND sc.configKey = :key AND sc.isActive = true")
    SystemConfiguration findByCategoryAndKey(@Param("category") String category, @Param("key") String key);
    
    // 根據分類查找所有配置
    @Query("SELECT sc FROM SystemConfiguration sc WHERE sc.configCategory = :category AND sc.isActive = true ORDER BY sc.sortOrder")
    List<SystemConfiguration> findByCategory(@Param("category") String category);
    
    // 根據分類查找啟用的配置
    List<SystemConfiguration> findByConfigCategoryAndIsActiveOrderBySortOrder(String configCategory, Boolean isActive);
    
    // 查找所有啟用的配置
    List<SystemConfiguration> findByIsActiveTrueOrderByConfigCategoryAscSortOrderAsc();
    
    // 根據數據類型查找配置
    List<SystemConfiguration> findByDataTypeAndIsActiveTrue(String dataType);
    
    // 模糊查詢配置鍵
    @Query("SELECT sc FROM SystemConfiguration sc WHERE sc.configKey LIKE %:keyword% AND sc.isActive = true")
    List<SystemConfiguration> findByKeywordInConfigKey(@Param("keyword") String keyword);
    
    // 根據更新者查找配置
    List<SystemConfiguration> findByUpdatedByOrderByUpdatedAtDesc(Integer updatedBy);
    
    // 檢查配置鍵是否存在
    boolean existsByConfigCategoryAndConfigKey(String configCategory, String configKey);
}