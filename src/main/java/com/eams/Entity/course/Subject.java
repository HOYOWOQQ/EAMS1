package com.eams.Entity.course;

import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.fee.PaymentItem;
import com.eams.Entity.member.Teacher;
import com.eams.Entity.score.Question;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.gson.annotations.Expose;

import jakarta.persistence.*;

@Entity
@Table(name = "subject")
public class Subject {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Expose
	private Integer id;

	@Column(name = "name")
	@Expose
	private String name;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "course_teacher", joinColumns = { @JoinColumn(name = "subject_id") }, inverseJoinColumns = {
			@JoinColumn(name = "teacher_id") })
	@JsonBackReference
	private List<Teacher> teachers = new LinkedList<Teacher>();

	@ManyToMany(mappedBy = "subjects", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonBackReference
	private List<Course> courses = new LinkedList<Course>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subject", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<CourseSchedule> courseSchedule = new LinkedList<CourseSchedule>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "subject", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<PaymentItem> paymentItem = new LinkedList<PaymentItem>();
	
	@OneToMany(mappedBy = "subject", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonManagedReference("subject-questions")
	private List<Question> questions;
	
	public Subject() {
		super();
	}

	public Subject(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	
	
	 
	public List<PaymentItem> getPaymentItem() {
		return paymentItem;
	}

	public void setPaymentItem(List<PaymentItem> paymentItem) {
		this.paymentItem = paymentItem;
	}

	/**
	 * @return the teachers
	 */
	public List<Teacher> getTeachers() {
		return teachers;
	}

	/**
	 * @param teachers the teachers to set
	 */
	public void setTeachers(List<Teacher> teachers) {
		this.teachers = teachers;
	}

	/**
	 * @return the courses
	 */
	public List<Course> getCourses() {
		return courses;
	}

	/**
	 * @param courses the courses to set
	 */
	public void setCourses(List<Course> courses) {
		this.courses = courses;
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

	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Subject [id=" + id + ", name=" + name + "]";
	}

}
