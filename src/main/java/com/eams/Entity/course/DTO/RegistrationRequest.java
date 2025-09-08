package com.eams.Entity.course.DTO;

import java.time.LocalDate;

public class RegistrationRequest {
    private Integer courseId;
    
    // ===== 現有學生報名用 =====
    private Integer studentId;  // 如果是現有學生，只需填這個
    
    // ===== 新學生申請資料 =====
    private String studentName;
    private String studentAccount;
    private String studentEmail;
    private String studentPhone;
    private String studentGender;
    private LocalDate studentBirthday;
    private Integer studentGrade;
    private String guardianName;
    private String guardianPhone;
    private String studentAddress;
    private String remark;
    
    // ===== 建構子 =====
    public RegistrationRequest() {}
    
    // 現有學生報名建構子
    public RegistrationRequest(Integer courseId, Integer studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
    }
    
    // 新學生申請建構子
    public RegistrationRequest(Integer courseId, String studentName, String studentAccount,
                              String studentEmail, String studentPhone, String studentGender,
                              LocalDate studentBirthday, Integer studentGrade, 
                              String guardianName, String guardianPhone, 
                              String studentAddress, String remark) {
        this.courseId = courseId;
        this.studentName = studentName;
        this.studentAccount = studentAccount;
        this.studentEmail = studentEmail;
        this.studentPhone = studentPhone;
        this.studentGender = studentGender;
        this.studentBirthday = studentBirthday;
        this.studentGrade = studentGrade;
        this.guardianName = guardianName;
        this.guardianPhone = guardianPhone;
        this.studentAddress = studentAddress;
        this.remark = remark;
    }
    
    // ===== Getters and Setters =====
    
    public Integer getCourseId() { 
        return courseId; 
    }
    
    public void setCourseId(Integer courseId) { 
        this.courseId = courseId; 
    }
    
    public Integer getStudentId() { 
        return studentId; 
    }
    
    public void setStudentId(Integer studentId) { 
        this.studentId = studentId; 
    }
    
    public String getStudentName() { 
        return studentName; 
    }
    
    public void setStudentName(String studentName) { 
        this.studentName = studentName; 
    }
    
    public String getStudentAccount() { 
        return studentAccount; 
    }
    
    public void setStudentAccount(String studentAccount) { 
        this.studentAccount = studentAccount; 
    }
    
    public String getStudentEmail() { 
        return studentEmail; 
    }
    
    public void setStudentEmail(String studentEmail) { 
        this.studentEmail = studentEmail; 
    }
    
    public String getStudentPhone() { 
        return studentPhone; 
    }
    
    public void setStudentPhone(String studentPhone) { 
        this.studentPhone = studentPhone; 
    }
    
    public String getStudentGender() { 
        return studentGender; 
    }
    
    public void setStudentGender(String studentGender) { 
        this.studentGender = studentGender; 
    }
    
    public LocalDate getStudentBirthday() { 
        return studentBirthday; 
    }
    
    public void setStudentBirthday(LocalDate studentBirthday) { 
        this.studentBirthday = studentBirthday; 
    }
    
    public Integer getStudentGrade() { 
        return studentGrade; 
    }
    
    public void setStudentGrade(Integer studentGrade) { 
        this.studentGrade = studentGrade; 
    }
    
    public String getGuardianName() { 
        return guardianName; 
    }
    
    public void setGuardianName(String guardianName) { 
        this.guardianName = guardianName; 
    }
    
    public String getGuardianPhone() { 
        return guardianPhone; 
    }
    
    public void setGuardianPhone(String guardianPhone) { 
        this.guardianPhone = guardianPhone; 
    }
    
    public String getStudentAddress() { 
        return studentAddress; 
    }
    
    public void setStudentAddress(String studentAddress) { 
        this.studentAddress = studentAddress; 
    }
    
    public String getRemark() { 
        return remark; 
    }
    
    public void setRemark(String remark) { 
        this.remark = remark; 
    }
    
    // ===== 輔助方法（與 Entity 保持一致） =====
    
    /**
     * 判斷是否為現有學生報名
     */
    public boolean isExistingStudent() {
        return studentId != null;
    }
    
    /**
     * 判斷是否為新學生申請
     */
    public boolean isNewStudentApplication() {
        return studentId == null && studentName != null;
    }
    
    /**
     * 轉換為 Registration Entity（更新版）
     */
    public com.eams.Entity.course.Registration toRegistration() {
        if (isExistingStudent()) {
            // 現有學生報名
            return new com.eams.Entity.course.Registration(this.courseId, this.studentId);
        } else {
            // 新學生申請
            return new com.eams.Entity.course.Registration(
                this.courseId,
                this.studentName,
                this.studentAccount,
                this.studentEmail,
                this.studentPhone,
                this.studentGender,
                this.studentBirthday,
                this.studentGrade,
                this.guardianName,
                this.guardianPhone,
                this.studentAddress,
                this.remark
            );
        }
    }
    
    /**
     * 驗證必填欄位
     */
    public boolean isValid() {
        if (courseId == null) {
            return false;
        }
        
        // 現有學生報名：只需要 studentId
        if (isExistingStudent()) {
            return true;
        }
        
        // 新學生申請：需要必填欄位
        return studentName != null && !studentName.trim().isEmpty() &&
               studentAccount != null && !studentAccount.trim().isEmpty() &&
               studentEmail != null && !studentEmail.trim().isEmpty() &&
               guardianName != null && !guardianName.trim().isEmpty() &&
               guardianPhone != null && !guardianPhone.trim().isEmpty();
    }
    
    /**
     * 獲取驗證錯誤訊息
     */
    public String getValidationError() {
        if (courseId == null) {
            return "課程ID不能為空";
        }
        
        if (isExistingStudent()) {
            return null; // 現有學生報名無需額外驗證
        }
        
        // 新學生申請驗證
        if (studentName == null || studentName.trim().isEmpty()) {
            return "學生姓名不能為空";
        }
        if (studentAccount == null || studentAccount.trim().isEmpty()) {
            return "學生帳號不能為空";
        }
        if (studentEmail == null || studentEmail.trim().isEmpty()) {
            return "學生信箱不能為空";
        }
        if (guardianName == null || guardianName.trim().isEmpty()) {
            return "監護人姓名不能為空";
        }
        if (guardianPhone == null || guardianPhone.trim().isEmpty()) {
            return "監護人電話不能為空";
        }
        
        return null; // 驗證通過
    }
    
    @Override
    public String toString() {
        if (isExistingStudent()) {
            return "RegistrationRequest{courseId=" + courseId + ", studentId=" + studentId + "}";
        } else {
            return "RegistrationRequest{" +
                    "courseId=" + courseId +
                    ", studentName='" + studentName + '\'' +
                    ", studentAccount='" + studentAccount + '\'' +
                    ", studentEmail='" + studentEmail + '\'' +
                    ", guardianName='" + guardianName + '\'' +
                    '}';
        }
    }
}