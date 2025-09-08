package com.eams.Service.course;


import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Course;



@SpringBootTest
class CourseServiceTest {
	
	@Autowired
	private CourseService courseService;
	
	
	//@Test
	void testgetCourseByStudentId() {
		List<Course> list= courseService.getCourseByStudentId(1);
		System.out.println(list);
	}
	//@Test
	void testgetAllCourse() {
		List<Course> list= courseService.getAllCourse();
		System.out.println(list);
	}
	
	//@Test
	void testDelete() {
		courseService.deleteCourseById(303);;
	}
	
	//@Test
	void testAddCourse() {
		Course course = new Course();
		course.setId(404);
		course.setName("test");
		
		courseService.saveCourse(course);
	}

}
