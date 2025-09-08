package com.eams.Controller.course;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.eams.Service.course.CourseScheduleService;


@SpringBootTest
class CourseScheduleControllerTest {
	
	@Autowired
	private CourseScheduleController controller;
	
	@Autowired
	private CourseScheduleService cService;
	
	Integer id = null;
	
	private Map<String, Object> courseScheduleData = new HashMap<String, Object>();
	
	
	
//	@Test
//	@Commit 
	void testdelete() {
		Integer id = 10;
		courseScheduleData.put("id",id);
		controller.deleteCourse(courseScheduleData);
		System.out.println(cService.getCourseScheduleById(id));
	}
	
	@Test 
	void testCARD() {
		
		controller.getScheduleStatistics("2025-08-03","2025-08-10");
	}

}
