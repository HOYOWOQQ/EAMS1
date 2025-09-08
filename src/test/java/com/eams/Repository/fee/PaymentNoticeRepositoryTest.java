package com.eams.Repository.fee;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.eams.Entity.fee.DTO.FindpaymentnoticeDTO;

@SpringBootTest
class PaymentNoticeRepositoryTest {
	
	@Autowired
	private PaymentNoticeRepository rspo;
	
	@Test
	void testGetAllpaymentNotice() {
		List<FindpaymentnoticeDTO> list =rspo.getAllpaymentNotice();
		System.out.println(list);
	}

}
