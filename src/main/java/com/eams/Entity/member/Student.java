package com.eams.Entity.member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.attendance.Attendance;
import com.eams.Entity.attendance.LeaveRequest;
import com.eams.Entity.course.Classroom;
import com.eams.Entity.course.CourseEnroll;
import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.course.Registration;
import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.scholarship.ScholarshipGrant;
import com.eams.Entity.score.ExamAttempt;
import com.eams.Entity.score.ExamResult;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "grade")
    private Byte grade;

    @Column(name = "guardian_name", length = 50)
    private String guardianName;

    @Column(name = "guardian_phone", length = 20)
    private String guardianPhone;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "enroll_date")
    private LocalDate enrollDate;

    @Column(name = "remark", length = 100)
    private String remark;
    
    @OneToOne
    @JoinColumn(name = "id") 
    @JsonBackReference
    private Member member;
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "student", cascade = CascadeType.ALL)
    @JsonManagedReference
	private List<CourseEnroll> courseEnroll=new LinkedList<CourseEnroll>();
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "studentInfo", cascade = CascadeType.ALL)
    @JsonManagedReference
   	private List<PaymentNotice> paymentNotice =new LinkedList<PaymentNotice>();
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "student", cascade = CascadeType.ALL)
    @JsonManagedReference
	private List<ExamResult> examResult=new LinkedList<ExamResult>();
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "student", cascade = CascadeType.ALL)
    @JsonManagedReference
	private List<Attendance> attendance=new LinkedList<Attendance>();
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "student", cascade = CascadeType.ALL)
    @JsonManagedReference
   	private List<LeaveRequest> leaveRequest=new LinkedList<LeaveRequest>();
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("student-attempts")
    private List<ExamAttempt> attempts = new ArrayList<>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "student", cascade = CascadeType.ALL)
	@JsonManagedReference 
	private List<Registration> registration = new LinkedList<Registration>();
    
	
}
