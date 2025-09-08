package com.eams.Entity.attendance;

import java.time.LocalDateTime;

import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.member.Student;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.gson.annotations.Expose;

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
@Table(name = "leave_request")
public class LeaveRequest {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "student_id")
	private Student student;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "course_schedule_id")
	@JsonBackReference
	private CourseSchedule courseSchedule;

	@Expose
	@Column(name = "leave_type")
	private String leaveType;

	@Expose
	@Column(name = "reason")
	private String reason;
	@Expose
	@Column(name = "attachment_path")
	private String attachmentPath;
	@Expose
	@Column(name = "submitted_at")
	private LocalDateTime submittedAt;
	@Expose
	@Column(name = "status")
	private String status;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "attendance_id")
	private Attendance attendance;

	public LeaveRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LeaveRequest(int id, Student student, CourseSchedule courseSchedule, String leaveType, String reason,
			String attachmentPath, LocalDateTime submittedAt, String status, Attendance attendance) {
		super();
		this.id = id;
		this.student = student;
		this.courseSchedule = courseSchedule;
		this.leaveType = leaveType;
		this.reason = reason;
		this.attachmentPath = attachmentPath;
		this.submittedAt = submittedAt;
		this.status = status;
		this.attendance = attendance;
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

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public LocalDateTime getSubmittedAt() {
		return submittedAt;
	}

	public void setSubmittedAt(LocalDateTime submittedAt) {
		this.submittedAt = submittedAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Attendance getAttendance() {
		return attendance;
	}

	public void setAttendance(Attendance attendance) {
		this.attendance = attendance;
	}

}
