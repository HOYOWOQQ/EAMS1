package com.eams.common.log.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日誌記錄註解
 * 用於自動記錄方法執行的操作日誌
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogOperation {
    
    /**
     * 操作類型
     * 例如：COURSE_CREATE, STUDENT_UPDATE, USER_DELETE 等
     */
    String type();
    
    /**
     * 操作名稱
     * 例如：新增課程, 修改學生資料, 刪除用戶 等
     */
    String name();
    
    /**
     * 操作描述
     * 可以包含動態內容的描述模板
     */
    String description() default "";
    
    /**
     * 目標類型
     * 例如：COURSE, STUDENT, USER 等
     */
    String targetType() default "";
    
    /**
     * 是否記錄請求參數
     */
    boolean logArgs() default true;
    
    /**
     * 是否記錄返回值
     */
    boolean logResult() default true;
    
    
    /**
     * 審計模式
     * POST_AUDIT: 事後審計（先執行後審查）
     * PRE_APPROVAL: 事前核准（先審查後執行）
     * AUTO: 根據配置自動決定
     */
    ApprovalMode approvalMode() default ApprovalMode.AUTO;
    
    /**
     * 強制需要審計（僅當 approvalMode = AUTO 時有效）
     * true: 無論角色權限如何都需要審計
     * false: 根據角色權限和配置決定
     */
    boolean forceApproval() default false;
    
    /**
     * 審計模式枚舉
     */
    enum ApprovalMode {
        /**
         * 事後審計模式
         * - 操作立即執行
         * - 同時記錄審計日誌
         * - 管理員後續審查
         * - 必要時回滾操作
         * 
         * 適用：可回滾的操作，如軟刪除、狀態修改等
         */
        POST_AUDIT,
        
        /**
         * 事前核准模式  
         * - 提交申請不執行操作
         * - 等待管理員審查
         * - 核准後才執行操作
         * - 操作結果記錄到申請中
         * 
         * 適用：不可回滾的操作，如硬刪除、資金操作等
         */
        PRE_APPROVAL,
        
        /**
         * 自動模式
         * - 根據系統配置和操作類型自動決定
         * - 高風險操作使用事前核准
         * - 一般操作使用事後審計
         * - 無需審計的操作直接執行
         */
        AUTO
    }
}