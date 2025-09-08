package com.eams.Entity.attendance;


import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.member.Student;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.gson.annotations.Expose;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "attendance")
public class Attendance  {
	
	// 狀態常數
    public static final String STATUS_ATTEND = "ATTEND";
    public static final String STATUS_LEAVE = "LEAVE";
    public static final String STATUS_ABSENT = "ABSENT";
    public static final String STATUS_UNMARKED = "UNMARKED";
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "student_id")
	private Student student;
	
	@ManyToOne(fetch = FetchType.EAGER) @JoinColumn(name = "course_schedule_id")
	@JsonBackReference
	private CourseSchedule courseSchedule;
	
	@OneToOne(fetch = FetchType.EAGER,mappedBy = "attendance", cascade = CascadeType.ALL) 
	private LeaveRequest leaveRequest;


	@Expose
	@Column(name = "status")
	private String status;
	@Expose
	@Column(name = "remark")
	private String remark;


	// 額外顯示用途（JOIN 用，不存 DB，但查詢時會填）
	//private String leaveReviewStatusText;

	public Attendance() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

	
	public Attendance(int id, Student student, CourseSchedule courseSchedule, LeaveRequest leaveRequest, String status,
			String remark) {
		super();
		this.id = id;
		this.student = student;
		this.courseSchedule = courseSchedule;
		this.leaveRequest = leaveRequest;
		this.status = status;
		this.remark = remark;
	}

	
	


	public int getId() {
		return id;
	}




	public void setId(int id) {
		this.id = id;
	}




	public Student getStudent() {
		return student;
	}




	public void setStudent(Student student) {
		this.student = student;
	}




	public CourseSchedule getCourseSchedule() {
		return courseSchedule;
	}




	public void setCourseSchedule(CourseSchedule courseSchedule) {
		this.courseSchedule = courseSchedule;
	}




	public LeaveRequest getLeaveRequest() {
		return leaveRequest;
	}




	public void setLeaveRequest(LeaveRequest leaveRequest) {
		this.leaveRequest = leaveRequest;
	}




	public String getStatus() {
		return status;
	}




	public void setStatus(String status) {
		this.status = status;
	}




	public String getRemark() {
		return remark;
	}




	public void setRemark(String remark) {
		this.remark = remark;
	}



	@Override
	public String toString() {
		return "Attendance [id=" + id + ", student=" + student + ", courseSchedule=" + courseSchedule
				+ ", leaveRequest=" + leaveRequest + ", status=" + status + ", remark=" + remark + "]";
	}

	public String getStatusText() {
	    if (STATUS_ATTEND.equals(status)) return "出席";
	    if (STATUS_LEAVE.equals(status)) return "請假";
	    if (STATUS_ABSENT.equals(status)) return "缺席";
	    if (STATUS_UNMARKED.equals(status)) return "未點名";
	    return "未知";
	}
	
	
}
