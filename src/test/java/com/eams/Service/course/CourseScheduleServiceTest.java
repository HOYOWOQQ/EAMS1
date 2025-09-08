package com.eams.Service.course;


import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import com.eams.Entity.course.CourseSchedule;


@SpringBootTest
class CourseScheduleServiceTest {
	
	@Autowired
	private CourseScheduleService courseScheduleService;
	
	@Autowired
	private CourseService courseService;

	//@Test
	void testgetCourseScheduleByTeacherId() {
		List<CourseSchedule> list =courseScheduleService.getCourseScheduleByTeacherId(45);
		System.out.println(list);
	}
	//@Test
	void testsaveCourseSchedule() {
		CourseSchedule courseSchedule = new CourseSchedule();
		courseSchedule.setCourse(courseService.getCourseById(101));
		courseSchedule.setLessonDate(LocalDate.parse("2022-07-20"));
		courseSchedule.setPeriodStart(1);
		courseSchedule.setPeriodEnd(2);
		courseScheduleService.saveCourseSchedule(courseSchedule);
		List<CourseSchedule> list = courseScheduleService.getAllCourseSchedule();
		System.out.println(list);
	}
	
//	@Test
//	@Commit 
	void testdeleteCourseSchedule() {
		courseScheduleService.deleteCourseScheduleById(31);
		System.out.println(courseScheduleService.getCourseScheduleById(31));
	}
	
	@Test
	void testWeekCS() {
		LocalDate start = LocalDate.parse("2025-08-03");
		LocalDate end = LocalDate.parse("2025-08-10");
//		List<CourseSchedule> list = courseScheduleService.getCourseScheduleByWeekDay(start, end);
//		System.out.println(list);
		int c = courseScheduleService.calculateRoomUtilization(start, end);
		int d = courseScheduleService.countScheduledCourses(start, end);
		System.out.println(d);
	}
	
	
}
