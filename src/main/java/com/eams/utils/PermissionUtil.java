package com.eams.utils;

import jakarta.servlet.http.HttpSession;

/**
 * 權限檢查工具類
 * 用於統一管理和檢查使用者權限
 */
public class PermissionUtil {
    
    // 角色常數
    public static final String ROLE_ADMIN = "主任";
    public static final String ROLE_TEACHER = "老師";
    public static final String ROLE_TEACHER_EN = "teacher";
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_ADMIN_EN = "admin";
    
    /**
     * 檢查使用者是否為主任
     */
    public static boolean isAdmin(HttpSession session) {
        if (session == null) return false;
        
        String position = (String) session.getAttribute("position");
        String role = (String) session.getAttribute("role");
        
        return ROLE_ADMIN.equals(position) || ROLE_ADMIN_EN.equals(role);
    }
    
    /**
     * 檢查使用者是否為老師
     */
    public static boolean isTeacher(HttpSession session) {
        if (session == null) return false;
        
        String position = (String) session.getAttribute("position");
        String role = (String) session.getAttribute("role");
        
        return ROLE_TEACHER.equals(position) || ROLE_TEACHER_EN.equals(position) || 
               ROLE_TEACHER_EN.equals(role);
    }
    
    /**
     * 檢查使用者是否為學生
     */
    public static boolean isStudent(HttpSession session) {
        if (session == null) return false;
        
        String position = (String) session.getAttribute("position");
        String role = (String) session.getAttribute("role");
        
        return ROLE_STUDENT.equals(position) || ROLE_STUDENT.equals(role);
    }
    
    /**
     * 檢查使用者是否有管理權限（主任或老師）
     */
    public static boolean hasManagementPermission(HttpSession session) {
        return isAdmin(session) || isTeacher(session);
    }
    
    /**
     * 檢查使用者是否可以查看會員列表
     */
    public static boolean canViewMemberList(HttpSession session) {
        return hasManagementPermission(session);
    }
    
    /**
     * 檢查使用者是否可以新增會員
     */
    public static boolean canAddMember(HttpSession session) {
        return isAdmin(session); // 只有主任可以新增
    }
    
    /**
     * 檢查使用者是否可以刪除會員
     */
    public static boolean canDeleteMember(HttpSession session) {
        return isAdmin(session); // 只有主任可以刪除
    }
    
    /**
     * 檢查使用者是否可以編輯會員
     */
    public static boolean canEditMember(HttpSession session) {
        return isAdmin(session); // 只有主任可以編輯
    }
    
    /**
     * 檢查使用者是否可以查看會員詳細資料
     */
    public static boolean canViewMemberDetail(HttpSession session) {
        return hasManagementPermission(session);
    }
    
    /**
     * 獲取使用者角色顯示名稱
     */
    public static String getRoleDisplayName(HttpSession session) {
        if (session == null) return "訪客";
        
        if (isAdmin(session)) return "主任";
        if (isTeacher(session)) return "老師";
        if (isStudent(session)) return "學生";
        
        return "未知";
    }
    
    /**
     * 獲取使用者姓名
     */
    public static String getUserName(HttpSession session) {
        if (session == null) return null;
        return (String) session.getAttribute("name");
    }
    
    /**
     * 獲取使用者ID
     */
    public static Integer getUserId(HttpSession session) {
        if (session == null) return null;
        return (Integer) session.getAttribute("id");
    }
    
    /**
     * 檢查是否為同一個使用者（用於防止刪除自己）
     */
    public static boolean isSameUser(HttpSession session, int targetUserId) {
        Integer currentUserId = getUserId(session);
        return currentUserId != null && currentUserId == targetUserId;
    }
    
    /**
     * 驗證會話是否有效
     */
    public static boolean isValidSession(HttpSession session) {
        if (session == null) return false;
        
        Integer id = getUserId(session);
        String name = getUserName(session);
        
        return id != null && name != null;
    }
}