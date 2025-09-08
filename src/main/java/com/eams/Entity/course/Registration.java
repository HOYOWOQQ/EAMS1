package com.eams.Entity.course;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eams.Entity.course.Enum.RegistrationStatus;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "registration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "course_id", nullable = false)
    private Integer courseId;
    
    @Column(name = "student_id")  // 移除 nullable = false，允許為 null
    private Integer studentId;  // 新學生申請時為 null，審核通過後才會有值
    
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RegistrationStatus status = RegistrationStatus.PENDING;
    
    @Column(name = "review_note", columnDefinition = "TEXT")
    private String reviewNote;
    
    @Column(name = "reviewed_by")
    private Integer reviewedBy;
    
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // ===== 新學生申請資料欄位 =====
    
    @Column(name = "student_name", length = 50)
    private String studentName;
    
    @Column(name = "student_account", length = 100)
    private String studentAccount;
    
    @Column(name = "student_email", length = 100)
    private String studentEmail;
    
    @Column(name = "student_phone", length = 20)
    private String studentPhone;
    
    @Column(name = "student_gender", length = 10)
    private String studentGender;
    
    @Column(name = "student_birthday")
    private LocalDate studentBirthday;
    
    @Column(name = "student_grade")
    private Integer studentGrade;
    
    @Column(name = "guardian_name", length = 50)
    private String guardianName;
    
    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;
    
    @Column(name = "student_address", length = 200)
    private String studentAddress;
    
    @Column(name = "remark", length = 100)
    private String remark;
    
    // ===== 關聯對象 =====
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonBackReference
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", insertable = false, updatable = false)
    @JsonBackReference
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by", insertable = false, updatable = false)
    @JsonBackReference
    private Teacher reviewer;
    
    // ===== 生命週期方法 =====
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }
    
    // ===== 建構子 =====
    
    // 現有學生報名建構子
    public Registration(Integer courseId, Integer studentId) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.status = RegistrationStatus.APPROVED; // 現有學生直接通過
    }
    
    // 新學生申請建構子（還沒有 studentId）
    public Registration(Integer courseId, String studentName, String studentAccount, 
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
        this.status = RegistrationStatus.PENDING; // 新學生申請需要審核
    }
    
    // ===== 輔助方法 =====
    
    /**
     * 判斷是否為現有學生報名
     */
    public boolean isExistingStudentRegistration() {
        return studentId != null;
    }
    
    /**
     * 判斷是否為新學生申請
     */
    public boolean isNewStudentApplication() {
        return studentId == null && studentName != null;
    }
    
    /**
     * 設定審核結果（通過時需要提供 studentId）
     */
    public void setReviewResult(RegistrationStatus status, String reviewNote, Integer reviewerId, Integer approvedStudentId) {
        this.status = status;
        this.reviewNote = reviewNote;
        this.reviewedBy = reviewerId;
        this.reviewedAt = LocalDateTime.now();
        
        // 如果審核通過且是新學生申請，設定 studentId
        if (status == RegistrationStatus.APPROVED && isNewStudentApplication() && approvedStudentId != null) {
            this.studentId = approvedStudentId;
        }
    }
    
    /**
     * 獲取學生姓名（優先使用關聯的 Student 實體）
     */
    public String getEffectiveStudentName() {
        if (student != null && student.getMember().getName() != null) {
            return student.getMember().getName();
        }
        return studentName;
    }
    
    /**
     * 獲取學生帳號（優先使用關聯的 Student 實體）
     */
    public String getEffectiveStudentAccount() {
        if (student != null && student.getMember().getAccount() != null) {
            return student.getMember().getAccount();
        }
        return studentAccount;
    }
    
    /**
     * 獲取學生信箱（優先使用關聯的 Student 實體）
     */
    public String getEffectiveStudentEmail() {
        if (student != null && student.getMember().getEmail() != null) {
            return student.getMember().getEmail();
        }
        return studentEmail;
    }
    
    /**
     * 檢查是否可以審核
     */
    public boolean canBeReviewed() {
        return status == RegistrationStatus.PENDING;
    }
    
    /**
     * 檢查是否已完成審核
     */
    public boolean isReviewed() {
        return status != RegistrationStatus.PENDING;
    }
}