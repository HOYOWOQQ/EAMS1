package com.eams.Entity.member;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.course.Registration;
import com.eams.Entity.course.Subject;
import com.eams.Entity.score.ExamResult;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "teacher")
public class Teacher {

    @Id
    @Column(name = "id")
    private Integer id; 

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "position", length = 20)
    private String position;

    @Column(name = "specialty", length = 100)
    private String specialty;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "remark", length = 100)
    private String remark;

    @Column(name = "supervisor_id")
    private Integer supervisorId;
    
    @OneToOne
    @JoinColumn(name = "id") 
    @JsonBackReference
    private Member member;
    
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
	private List<Subject> subjects = new LinkedList<Subject>();
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "teacher", cascade = CascadeType.ALL)
    @JsonManagedReference
	private List<CourseSchedule> courseSchedule=new LinkedList<CourseSchedule>();
	
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "teacher", cascade = CascadeType.ALL)
    @JsonManagedReference
  	private List<ExamResult> examResult=new LinkedList<ExamResult>();
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reviewer", cascade = CascadeType.ALL)
	@JsonManagedReference 
	private List<Registration> registration = new LinkedList<Registration>();

    
    
	public Teacher() {
		super();
	}
	
	
	public Teacher(Integer id, String gender, LocalDate birthday, String position, String specialty, LocalDate hireDate,
			String address, String remark, Integer supervisorId) {
		super();
		this.id = id;
		this.gender = gender;
		this.birthday = birthday;
		this.position = position;
		this.specialty = specialty;
		this.hireDate = hireDate;
		this.address = address;
		this.remark = remark;
		this.supervisorId = supervisorId;
	}
	
	
	
	
	public List<Registration> getRegistration() {
		return registration;
	}


	public void setRegistration(List<Registration> registration) {
		this.registration = registration;
	}


	/**
	 * @return the examResult
	 */
	public List<ExamResult> getExamResult() {
		return examResult;
	}


	/**
	 * @param examResult the examResult to set
	 */
	public void setExamResult(List<ExamResult> examResult) {
		this.examResult = examResult;
	}


	/**
	 * @return the subjects
	 */
	public List<Subject> getSubjects() {
		return subjects;
	}


	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
	}


	/**
	 * @return the courseSchedule
	 */
	public List<CourseSchedule> getCourseSchedule() {
		return courseSchedule;
	}


	/**
	 * @param courseSchedule the courseSchedule to set
	 */
	public void setCourseSchedule(List<CourseSchedule> courseSchedule) {
		this.courseSchedule = courseSchedule;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public LocalDate getBirthday() {
		return birthday;
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public LocalDate getHireDate() {
		return hireDate;
	}
	public void setHireDate(LocalDate hireDate) {
		this.hireDate = hireDate;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Integer getSupervisorId() {
		return supervisorId;
	}
	public void setSupervisorId(Integer supervisorId) {
		this.supervisorId = supervisorId;
	}
	
	
	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}


	@Override
	public String toString() {
		return "Teacher [id=" + id + ", gender=" + gender + ", birthday=" + birthday + ", position=" + position
				+ ", specialty=" + specialty + ", hireDate=" + hireDate + ", address=" + address + ", remark=" + remark
				+ ", supervisorId=" + supervisorId + "]";
	}
}
