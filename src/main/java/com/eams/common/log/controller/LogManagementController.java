package com.eams.common.log.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.eams.common.log.dto.OperationLogDto;
import com.eams.common.log.dto.OperationLogQueryDto;
import com.eams.common.log.service.OperationLogService;

import java.util.Map;

/**
 * 操作日誌管理控制器 - 網頁界面
 */
@Controller
@RequestMapping("/admin/logs")
public class LogManagementController {
    
    @Autowired
    private OperationLogService operationLogService;
    
    /**
     * 日誌列表頁面
     */
    @GetMapping
    public String logList(Model model, OperationLogQueryDto queryDto) {
        // 設置默認分頁參數
        if (queryDto.getSize() <= 0) {
            queryDto.setSize(20);
        }
        
        // 查詢日誌數據
        Page<OperationLogDto> logs = operationLogService.getOperationLogs(queryDto);
        
        // 獲取統計信息
        Map<String, Object> statistics = operationLogService.getOperationStatistics();
        
        // 添加到模型中
        model.addAttribute("logs", logs);
        model.addAttribute("statistics", statistics);
        model.addAttribute("query", queryDto);
        
        // 添加操作類型選項（用於下拉選單）
        model.addAttribute("operationTypes", getOperationTypes());
        
        return "admin/log-list"; // 對應的模板文件
    }
    
    /**
     * 日誌詳情頁面
     */
    @GetMapping("/{logId}")
    public String logDetail(@PathVariable Long logId, Model model) {
        // 這裡可以實現獲取單個日誌詳情的邏輯
        model.addAttribute("logId", logId);
        return "admin/log-detail"; // 對應的模板文件
    }
    
    /**
     * 獲取操作類型列表（用於前端下拉選單）
     */
    private String[] getOperationTypes() {
        return new String[]{
            "COURSE_CREATE", "COURSE_UPDATE", "COURSE_DELETE",
            "STUDENT_CREATE", "STUDENT_UPDATE", "STUDENT_DELETE",
            "USER_LOGIN", "USER_LOGOUT", "DATA_EXPORT"
        };
    }
}

