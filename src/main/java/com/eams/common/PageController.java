package com.eams.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
	
	@GetMapping("/OpenIndex")
	public String openIndex() {
		// 直接回傳 JSP 檔案路徑，不加副檔名
		return "common/index";
	}

	@GetMapping("/CourseMain")
	public String openCourseMain() {
		return "course/course-main";
	}

	@GetMapping("/CourseQuery")
	public String CourseQuery() {
		return "course/course-query";
	}

	@GetMapping("/CourseEnroll")
	public String CourseErnoll() {
		return "course/course-enroll";
	}

	@GetMapping("/CourseEdit")
	public String CourseEdit() {
		return "course/course-edit";
	}
	
	@GetMapping("/memberList")
	public String openMemberList() {
		return "member/memberList";
	}
	
	@GetMapping("/addMember")
	public String addMember() {
	    return "member/addMember";
	}
	
	@GetMapping("/profile")
	public String profilePage() {
		return "member/profile";
	}
	
	@GetMapping("/addMemberPage")
	public String addMemberPage() {
		return "member/addMember";
	}
	
	@GetMapping("/changePassword")
	public String changePasswordPage() {
		return "member/changePassword";
	}
	
	@GetMapping("/forgotPassword")
	public String forgotPasswordPage() {
		return "member/forgotPassword";
	}
	
	@GetMapping("/resetPassword")
	public String resetPasswordPage() {
		return "member/resetPassword";
	}
	
	@GetMapping("/verifyEmail")
	public String verifyEmailPage() {
		return "member/verifyEmail";
	}
	
	@GetMapping("/verifyResult")
	public String verifyResultPage() {
		return "member/verifyResult";
	}
	
	@GetMapping("/newpayment")
	public String newpaymentPage(){
		return "fee/Newpayment";
	}
	
	@GetMapping("/tuitionManage")
	public String tuitionManage(){
		return "fee/TuitionManage";
	}
	
	@GetMapping("/takeAttendance")
	public String takeAttendance() {
		return "attendance/takeAttendance";
	}
	
	@GetMapping("/myAttendance")
	public String attendanceQueryByStudent() {
		return "attendance/myAttendance";
	}
	
	@GetMapping("/teacherSearch")
	public String teacherSearch() {
		return "attendance/teacherSearch";
	}
	
//	@GetMapping("/BatchInsertExamresult")
//	public String BatchInsertExamresult() {
//		return "score/BatchInsertExamresult";
//	}
	
//	@GetMapping("/InsertExamPaper")
//	public String InsertExamPaper() {
//		return "score/InsertExamPaper";
//	}
//	
//	@GetMapping("/SelectExamResult")
//	public String SelectExamResult() {
//		return "score/SelectExamResult";
//	}
//	
//	@GetMapping("/SelectExamPaper")
//	public String SelectExamPaper() {
//		return "score/SelectExamPaper";
//	}
	
	
	
}
