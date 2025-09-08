package com.eams.Repository.member;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TeacherRepositoryTest {
	
	@Autowired
	private TeacherRepository teacherRepository;
	
	
	@Test
	void testfindByCourseId() {
		System.out.println(teacherRepository.findByCourseId(101));
	}

}
