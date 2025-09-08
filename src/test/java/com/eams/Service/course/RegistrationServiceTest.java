package com.eams.Service.course;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eams.Entity.course.DTO.RegistrationRequest;


@SpringBootTest
class RegistrationServiceTest {
	
	@Autowired
	private RegistrationService service;
	
	
	@Test
	void test() {
		
		RegistrationRequest rr= new RegistrationRequest();
		rr.setStudentId(1);
		rr.setCourseId(103);
				
		
		service.submitRegistration(rr);
	}

}
