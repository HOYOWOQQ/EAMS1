package com.eams.common.log.enums;

/**
 * 建議的操作類型常數類
 * 統一管理所有操作類型，避免拼寫錯誤
 */
public class OperationTypes {
    
    // ===== 課程管理 =====
    public static final String COURSE_CREATE = "COURSE_CREATE";
    public static final String COURSE_UPDATE = "COURSE_UPDATE"; 
    public static final String COURSE_DELETE = "COURSE_DELETE";
    public static final String COURSE_SCHEDULE = "COURSE_SCHEDULE";
    public static final String COURSE_IMPORT = "COURSE_IMPORT";
    public static final String COURSE_EXPORT = "COURSE_EXPORT";
    public static final String COURSEENROLL_CREATE = "COURSEENROLL_CREATE";
    public static final String COURSEENROLL_DELETE = "COURSEENROLL_DELETE";
    public static final String COURSESCHEDULE_CREATE = "COURSESCHEDULE_CREATE";
    public static final String COURSESCHEDULE_UPDATE = "COURSESCHEDULE_UPDATE";
    public static final String COURSESCHEDULE_DELETE = "COURSESCHEDULE_DELETE";
    
    // ===== 學生管理 =====
    public static final String STUDENT_CREATE = "STUDENT_CREATE";
    public static final String STUDENT_UPDATE = "STUDENT_UPDATE";
    public static final String STUDENT_DELETE = "STUDENT_DELETE";
    public static final String STUDENT_IMPORT = "STUDENT_IMPORT";
    public static final String STUDENT_ENROLL = "STUDENT_ENROLL";
    public static final String STUDENT_DROP = "STUDENT_DROP";
    public static final String STUDENT_GRADE = "STUDENT_GRADE";
    
    // ===== 教師管理 =====
    public static final String TEACHER_CREATE = "TEACHER_CREATE";
    public static final String TEACHER_UPDATE = "TEACHER_UPDATE";
    public static final String TEACHER_DELETE = "TEACHER_DELETE";
    public static final String TEACHER_ASSIGN = "TEACHER_ASSIGN";
    
    // ===== 用戶認證 =====
    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_LOGOUT = "USER_LOGOUT";
    public static final String USER_REGISTER = "USER_REGISTER";
    public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";
    public static final String PASSWORD_RESET = "PASSWORD_RESET";
    
    // ===== 權限管理 =====
    public static final String ROLE_ASSIGN = "ROLE_ASSIGN";
    public static final String ROLE_REVOKE = "ROLE_REVOKE";
    public static final String PERMISSION_GRANT = "PERMISSION_GRANT";
    public static final String PERMISSION_DENY = "PERMISSION_DENY";
    
    // ===== 系統管理 =====
    public static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";
    public static final String SYSTEM_BACKUP = "SYSTEM_BACKUP";
    public static final String SYSTEM_RESTORE = "SYSTEM_RESTORE";
    public static final String DATA_EXPORT = "DATA_EXPORT";
    public static final String DATA_IMPORT = "DATA_IMPORT";
    public static final String LOG_CLEANUP = "LOG_CLEANUP";
    
    // ===== 安全相關 =====
    public static final String SECURITY_VIOLATION = "SECURITY_VIOLATION";
    public static final String ACCESS_DENIED = "ACCESS_DENIED";
    public static final String SUSPICIOUS_ACTIVITY = "SUSPICIOUS_ACTIVITY";
    
    // ===== 測試和調試 =====
    public static final String TEST_SUCCESS = "TEST_SUCCESS";
    public static final String TEST_ERROR = "TEST_ERROR";
    public static final String DEBUG_TEST = "DEBUG_TEST";
}

