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
@Table(name = "course_enroll")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEnroll {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "course_id", nullable = false)
	private Integer courseId;

	@Column(name = "student_id", nullable = false)
	private Integer studentId;

	@Column(name = "status")
	private String status;

	@Column(name = "enroll_date")
	private LocalDate enrollDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id", insertable = false, updatable = false)
	@JsonBackReference
	private Course course;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", insertable = false, updatable = false)
	@JsonBackReference
	private Student student;
	
	
}
