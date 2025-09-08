package com.eams.common.Report.Controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CustomReportControllerTest {
	
	@Autowired
	private CustomReportController controller;

	@Test
	void test() {
		Map<String, Object> request = new HashMap<>();
		request.put("sql", "SELECT COUNT(*) FROM member m LEFT JOIN student s ON m.id = s.id WHERE m.status = 1");
		request.put("parameters", new Object[0]);
				
		System.out.println(controller.countQuery(request));
		
	}

}
