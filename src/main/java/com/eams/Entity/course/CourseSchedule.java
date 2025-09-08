package com.eams.Entity.course;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.attendance.Attendance;
import com.eams.Entity.attendance.LeaveRequest;
import com.eams.Entity.member.Teacher;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;


@Entity
@Table(name="course_schedule")
public class CourseSchedule {
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "course_id")
	@JsonBackReference
	private Course course;
	
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "classroom_id")
	@JsonBackReference
	private Classroom  classroom;
	
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "teacher_id")
	@JsonBackReference
	private Teacher  teacher;
	
	@ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "subject_id")
	@JsonBackReference
	private Subject subject;
	
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "courseSchedule", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Attendance> attendance=new LinkedList<Attendance>();
	
	@OneToMany(fetch = FetchType.LAZY,mappedBy = "courseSchedule", cascade = CascadeType.ALL)
	@JsonManagedReference
   	private List<LeaveRequest> leaveRequest=new LinkedList<LeaveRequest>();
	
	@Column(name = "lesson_date")
	private LocalDate lessonDate;
	
	@Column(name = "period_start")
	private Integer periodStart;
	
	@Column(name = "period_end")
	private Integer periodEnd;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "remark")
	private String remark;
	

	
	public CourseSchedule() {
		super();
	}
	
	
	public CourseSchedule(Integer id, Course course, Classroom classroom, Teacher teacher, Subject subject,
			LocalDate lessonDate, Integer periodStart, Integer periodEnd, String status, String remark) {
		super();
		this.id = id;
		this.course = course;
		this.classroom = classroom;
		this.teacher = teacher;
		this.subject = subject;
		this.lessonDate = lessonDate;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.status = status;
		this.remark = remark;
	}



	public String getWeekdayText() {
	    if (lessonDate == null) return "";
	    String[] weekdays = {"", "一", "二", "三", "四", "五", "六", "日"};
	    int day = lessonDate.getDayOfWeek().getValue(); // 1=Monday
	    return weekdays[day];
	}


	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}


	/**
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}


	/**
	 * @param course the course to set
	 */
	public void setCourse(Course course) {
		this.course = course;
	}


	/**
	 * @return the classroom
	 */
	public Classroom getClassroom() {
		return classroom;
	}


	/**
	 * @param classroom the classroom to set
	 */
	public void setClassroom(Classroom classroom) {
		this.classroom = classroom;
	}


	


	/**
	 * @return the teacher
	 */
	public Teacher getTeacher() {
		return teacher;
	}


	/**
	 * @param teacher the teacher to set
	 */
	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}


	/**
	 * @return the subject
	 */
	public Subject getSubject() {
		return subject;
	}


	/**
	 * @param subject the subject to set
	 */
	public void setSubject(Subject subject) {
		this.subject = subject;
	}


	/**
	 * @return the lessonDate
	 */
	public LocalDate getLessonDate() {
		return lessonDate;
	}


	/**
	 * @param lessonDate the lessonDate to set
	 */
	public void setLessonDate(LocalDate lessonDate) {
		this.lessonDate = lessonDate;
	}


	/**
	 * @return the periodStart
	 */
	public Integer getPeriodStart() {
		return periodStart;
	}


	/**
	 * @param periodStart the periodStart to set
	 */
	public void setPeriodStart(Integer periodStart) {
		this.periodStart = periodStart;
	}


	/**
	 * @return the periodEnd
	 */
	public Integer getPeriodEnd() {
		return periodEnd;
	}


	/**
	 * @param periodEnd the periodEnd to set
	 */
	public void setPeriodEnd(Integer periodEnd) {
		this.periodEnd = periodEnd;
	}


	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}


	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}


//	@Override
//	public String toString() {
//		return "CourseSchedule [id=" + id + ", courseID=" + course.getId() + ", classroomID=" + classroom.getId() + ", teahcerID=" + teacher.getId()
//				+ ", subjectId=" + subject.getId() + ", lessonDate=" + lessonDate + ", periodStart=" + periodStart
//				+ ", periodEnd=" + periodEnd + ", status=" + status + ", remark=" + remark + "]";
//	}
//	
//	


	
	



	
	
	
	
	
	
}
