package com.eams.Controller.member;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController  {

		@GetMapping("/logout")
	    public String logoutPage(HttpSession session) {
			
			if (session != null) {
	            session.invalidate();
	        }
			
	        return "common/index"; 
	    }
}
