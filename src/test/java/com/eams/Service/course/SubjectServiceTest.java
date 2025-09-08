package com.eams.Service.course;


import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eams.Entity.course.Subject;



@SpringBootTest
class SubjectServiceTest {
	
	
	
	@Autowired
	private SubjectService subjectService;
	
	
	@Test
	void testfindSubjectByCourseId() {
		List<Subject> list =subjectService.findSubjectByCourseId(101);
		System.out.println(list);
	}

}
