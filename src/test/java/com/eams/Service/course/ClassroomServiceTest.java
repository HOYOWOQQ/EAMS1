package com.eams.Service.course;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.Transient;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Classroom;



@SpringBootTest
class ClassroomServiceTest {
	
	@Autowired
	private ClassroomService s;

	@Test
	@Transactional
	void test() {
		
		 Classroom cr = new Classroom();
		 cr.setId(558);
		 
		 s.saveClassroom(cr);
		 
//		 List<Classroom> list = s.getAllClassroom();
//		 System.out.println(list);
	}

}
