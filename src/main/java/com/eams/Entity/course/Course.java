package com.eams.Entity.course;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.member.Student;
import com.eams.Entity.notice.Notice;
import com.eams.Entity.score.ExamPaper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.Expose;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

	@Id
	@Column(name = "ID")
	@Expose
	private Integer id;
	@Expose
	@Column(name = "name")
	private String name;
	@Expose
	@Column(name = "type")
	private String type;
	@Expose
	@Column(name = "description")
	private String description;
	@Expose
	@Column(name = "max_capacity")
	private Integer maxCapacity;
	@Expose
	@Column(name = "min_capacity")
	private Integer minCapacity;
	@Expose
	@Column(name = "fee")
	private Integer fee;
	@Expose
	@Column(name = "start_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@Expose
	@Column(name = "end_date")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	@Expose
	@Column(name = "registration_start_date")
	private LocalDate registrationStartDate;
	@Expose
	@Column(name = "registration_end_date")
	private LocalDate registrationEndDate;
	@Expose
	@Column(name = "created_by")
	private Integer createdBy;
	@Expose
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	@Expose
	@Column(name = "updated_by")
	private Integer updatedBy;
	@Expose
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	@Expose
	@Column(name = "status")
	private String status;
	@Expose
	@Column(name = "status_note")
	private String statusNote;
	@Expose
	@Column(name = "remark")
	private String remark;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL)
	@JsonManagedReference 
	private List<CourseEnroll> courseEnroll = new LinkedList<CourseEnroll>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL)
	@JsonManagedReference 
	private List<CourseSchedule> courseSchedule = new LinkedList<CourseSchedule>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL)
	@JsonManagedReference 
	private List<Notice> notice = new LinkedList<Notice>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL)
	@JsonManagedReference 
	private List<PaymentNotice> tuitionFee = new LinkedList<PaymentNotice>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL)
	@JsonManagedReference("course-examPapers")
	private List<ExamPaper> examPaper = new LinkedList<ExamPaper>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL)
	@JsonManagedReference 
	private List<Registration> registration = new LinkedList<Registration>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "course_subject", joinColumns = { @JoinColumn(name = "course_id") }, inverseJoinColumns = {
			@JoinColumn(name = "subject_id") })
	@JsonManagedReference 
	private List<Subject> subjects = new LinkedList<Subject>();
	
	@Override
	public String toString() {
	    return "Course{" +
	            "id=" + id +
	            ", name='" + name + '\'' +
	            ", type='" + type + '\'' +
	            ", status='" + status + '\'' +
	            ", enrollCount=" + (courseEnroll == null ? 0 : courseEnroll.size()) +
	            '}';
	}

}
