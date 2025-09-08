package com.eams.Service.course;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.CourseEnroll;
import com.eams.Repository.course.CourseEnrollRepository;
import com.eams.Repository.course.CourseRepository;



@Service
public class CourseEnrollService {
	
	
	@Autowired
	private CourseEnrollRepository courseEnrollRepository;
	
	@Autowired
	private CourseRepository courseRepository;
	
	//新增+更新
	@Transactional
	public void saveCourseEnroll(CourseEnroll courseEnroll) {
		courseEnrollRepository.save(courseEnroll);
	}
	
	//查單筆
	public CourseEnroll getCourseEnrollById(Integer id) {
		return courseEnrollRepository.findById(id).orElse(null);
	}
	
	//查全部
	public List<CourseEnroll> getAllCourseEnroll(){
		return courseEnrollRepository.findAll();
	}
	

	// 刪除by course&&student
	@Transactional
	public boolean deleteByCouseIdAndStudentId(Integer courseId, Integer studentId) {
		boolean success = courseEnrollRepository.deleteByCourseIdAndStudentId(courseId,studentId) > 0;
		return success;
	}
	
	//查多少學生註冊
	public Map<String, Integer> getStudentAndCourseCount(){
		Map<String, Integer> map = new HashMap<>();
		map.put("student_count", courseEnrollRepository.countDistinctStudent());
		map.put("course_count",courseEnrollRepository.countAllCourse());
		return map;
	}

	// 查是否可註冊
	public boolean isAvailable(Integer studentId, Integer courseId) {
		if (courseEnrollRepository.isAlreadyEnrolled( studentId, courseId)>0)
			return false;
		int enrolledCount = courseEnrollRepository.getEnrolledCount(courseId);
		int maxCapacity = courseRepository.getMaxCapacity(courseId);
		if (enrolledCount >= maxCapacity)
			return false;
		
		LocalDate dateline = courseEnrollRepository.getRegistrationEndDate(courseId);
		if (dateline == null || LocalDate.now().isAfter(dateline))
			return false;

		return true;
		
	}
	
	//查註冊數
	public Integer getEnrolledCount(Integer courseId) {
		Integer count = courseEnrollRepository.getEnrolledCount(courseId);
			return count != null ? count : null;
	}
	
	//查課程最大人數
	public Integer getMaxCapacity(Integer courseId) {
			Integer count = courseRepository.getMaxCapacity(courseId);
			return count != null ? count : null;
	}
	
	//查報名截止日
	public LocalDate getRegistrationEndDate(Integer courseId) {
		LocalDate date = courseEnrollRepository.getRegistrationEndDate( courseId);
			return date != null ? date : null;
		
	}
	
	
	// 查當前用戶的報名紀錄
	public List<CourseEnroll> getCourseEnrollsByStudentId(Integer studentId) {
	    return courseEnrollRepository.findByStudentId(studentId);
	}

	// 或者如果需要分页查询
	public Page<CourseEnroll> getCourseEnrollsByStudentId(Integer studentId, Pageable pageable) {
	    return courseEnrollRepository.findByStudentId(studentId, pageable);
	}

	// 查當前用戶特定狀態的報名紀錄
	public List<CourseEnroll> getCourseEnrollsByStudentIdAndStatus(Integer studentId, String status) {
	    return courseEnrollRepository.findByStudentIdAndStatus(studentId, status);
	}
}
