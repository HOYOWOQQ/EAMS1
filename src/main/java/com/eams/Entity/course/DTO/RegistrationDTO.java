// 1. 創建簡潔的 RegistrationDTO
package com.eams.Entity.course.DTO;

import com.eams.Entity.course.Course;
import com.eams.Entity.course.Registration;
import com.eams.Entity.course.Enum.RegistrationStatus;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegistrationDTO {
    // Registration 基本資訊
    private Integer id;
    private Integer courseId;
    private Integer studentId;
    private RegistrationStatus status;
    private LocalDateTime registrationDate;
    private String reviewNote;
    private Integer reviewedBy;
    private LocalDateTime reviewedAt;
    private String remark;
    
    // 新學生申請資訊
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
    
    // 課程資訊
    private String courseName;
    private String courseType;
    private String courseDescription;
    private Integer courseFee;
    private LocalDate courseStartDate;
    private LocalDate courseEndDate;
    
    // 現有學生資訊（來自 Member 和 Student）
    private String memberName;
    private String memberAccount;
    private String memberEmail;
    private String memberPhone;
    private String studentGenderFromDB;
    private LocalDate studentBirthdayFromDB;
    private Byte studentGradeFromDB;
    private String guardianNameFromDB;
    private String guardianPhoneFromDB;
    private String studentAddressFromDB;
    
    // 輔助方法
    public boolean isNewStudentApplication() {
        return studentId == null;
    }
    
    // 獲取顯示用的學生姓名（優先使用現有學生資料）
    public String getDisplayStudentName() {
        return isNewStudentApplication() ? studentName : memberName;
    }
    
    // 獲取顯示用的學生帳號
    public String getDisplayStudentAccount() {
        return isNewStudentApplication() ? studentAccount : memberAccount;
    }
    
    // 獲取顯示用的學生郵箱
    public String getDisplayStudentEmail() {
        return isNewStudentApplication() ? studentEmail : memberEmail;
    }
    
    // 獲取顯示用的學生電話
    public String getDisplayStudentPhone() {
        return isNewStudentApplication() ? studentPhone : memberPhone;
    }
    
    // 獲取顯示用的學生性別
    public String getDisplayStudentGender() {
        return isNewStudentApplication() ? studentGender : studentGenderFromDB;
    }
    
    // 獲取顯示用的監護人姓名
    public String getDisplayGuardianName() {
        return isNewStudentApplication() ? guardianName : guardianNameFromDB;
    }
    
    // 獲取顯示用的監護人電話
    public String getDisplayGuardianPhone() {
        return isNewStudentApplication() ? guardianPhone : guardianPhoneFromDB;
    }
    
    public static  RegistrationDTO fromEntity(Registration registration) {
        RegistrationDTO dto = new RegistrationDTO();
        
        // Registration 基本資訊
        dto.setId(registration.getId());
        dto.setCourseId(registration.getCourseId());
        dto.setStudentId(registration.getStudentId());
        dto.setStatus(registration.getStatus());
        dto.setRegistrationDate(registration.getRegistrationDate());
        dto.setReviewNote(registration.getReviewNote());
        dto.setReviewedBy(registration.getReviewedBy());
        dto.setReviewedAt(registration.getReviewedAt());
        dto.setRemark(registration.getRemark());
        
        // 新學生申請資訊
        dto.setStudentName(registration.getStudentName());
        dto.setStudentAccount(registration.getStudentAccount());
        dto.setStudentEmail(registration.getStudentEmail());
        dto.setStudentPhone(registration.getStudentPhone());
        dto.setStudentGender(registration.getStudentGender());
        dto.setStudentBirthday(registration.getStudentBirthday());
        dto.setStudentGrade(registration.getStudentGrade());
        dto.setGuardianName(registration.getGuardianName());
        dto.setGuardianPhone(registration.getGuardianPhone());
        dto.setStudentAddress(registration.getStudentAddress());
        
        // 課程資訊
        if (registration.getCourse() != null) {
            Course course = registration.getCourse();
            dto.setCourseName(course.getName());
            dto.setCourseType(course.getType());
            dto.setCourseDescription(course.getDescription());
            dto.setCourseFee(course.getFee());
            dto.setCourseStartDate(course.getStartDate());
            dto.setCourseEndDate(course.getEndDate());
        }
        
        // 現有學生資訊
        if (registration.getStudent() != null) {
            Student student = registration.getStudent();
            dto.setStudentGenderFromDB(student.getGender());
            dto.setStudentBirthdayFromDB(student.getBirthday());
            dto.setStudentGradeFromDB(student.getGrade());
            dto.setGuardianNameFromDB(student.getGuardianName());
            dto.setGuardianPhoneFromDB(student.getGuardianPhone());
            dto.setStudentAddressFromDB(student.getAddress());
            
            // Member 資訊
            if (student.getMember() != null) {
                Member member = student.getMember();
                dto.setMemberName(member.getName());
                dto.setMemberAccount(member.getAccount());
                dto.setMemberEmail(member.getEmail());
                dto.setMemberPhone(member.getPhone());
            }
        }
        
        return dto;
    }

}