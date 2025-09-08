package com.eams.Service.course;


import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;




@SpringBootTest
class CourseEnrollServiceTest {
	
	@Autowired
	private CourseEnrollService courseEnrollService;
	
	
	//@Test
	void testdeleteByCouseIdAndStudentId() {
		if (courseEnrollService.deleteByCouseIdAndStudentId(301,25)) {
			System.out.println("成功刪除");
		}
	}
	
	@Test
	void testgetStudentAndCourseCount() {
		Map<String, Integer> map = courseEnrollService.getStudentAndCourseCount();
		System.out.println("學生數：" + map.get("student_count"));
		System.out.println("課程數：" + map.get("course_count"));
	}
	
	@Test
	void testisAvailable() {
		if(courseEnrollService.isAvailable(2, 203)) {
			System.out.println("可報名");
		}else {
			System.out.println("不可報名");
		}
		
	}

}
