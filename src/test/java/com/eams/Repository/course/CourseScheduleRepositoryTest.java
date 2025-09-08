package com.eams.Repository.course;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eams.Entity.course.CourseSchedule;


@SpringBootTest
class CourseScheduleRepositoryTest {
	
	@Autowired
	private CourseScheduleRepository repository;

	@Test
	void test() {
		 LocalDate startDate = LocalDate.parse("2025-07-28");
		    LocalDate endDate = LocalDate.parse("2025-08-31");
		    Integer userId = 45;          // 可以設 null，表示不篩選
		    Integer courseId = null;     // 不篩選課程
		    Integer classroomId = 101;  // 不篩
		
		List<CourseSchedule> list = repository.findByDateRangeAndFilters(startDate, endDate, userId, courseId, classroomId);
		System.out.println(list);
	}

}
