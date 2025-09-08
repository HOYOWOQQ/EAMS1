//package com.eams.Controller.course;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import utils.HibernateUtil;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.hibernate.Session;
//
//import dao.course.CourseDao;
//
//
//@WebServlet("/AutoScheduleServlet")
//public class AutoScheduleServlet extends HttpServlet {
//	private static final long serialVersionUID = 1L;
//
//	public AutoScheduleServlet() {
//		super();
//	}
//
//	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		resp.setContentType("application/json; charset=UTF-8");
//		PrintWriter out = resp.getWriter();
//
//		LocalDate start = LocalDate.parse(req.getParameter("start_date"));
//		LocalDate end = LocalDate.parse(req.getParameter("end_date"));
//		String[] courseIdArr = req.getParameterValues("courseIds[]");
//		String[] teacherIdArr = req.getParameterValues("teacherIds[]");
//		String[] classroomIdArr=req.getParameterValues("classroomIds[]");
//		String[] subjectIdsArr=req.getParameterValues("subjectIds[]");
//		
//		try {
//			if (courseIdArr == null || teacherIdArr == null || classroomIdArr ==null || subjectIdsArr==null) {
//				out.print("{\"success\":false,\"message\":\"缺少 課程或老師或教室 參數\"}");
//				return;
//			}
//			List<Integer> courseIds = Arrays.stream(courseIdArr).map(Integer::parseInt).collect(Collectors.toList());
//			List<Integer> teacherIds = Arrays.stream(teacherIdArr).map(Integer::parseInt).collect(Collectors.toList());
//			List<Integer> classroomIds = Arrays.stream(classroomIdArr).map(Integer::parseInt).collect(Collectors.toList());
//			List<Integer> subjectIds = Arrays.stream(subjectIdsArr).map(Integer::parseInt).collect(Collectors.toList());
//			Session session = HibernateUtil.getSessionFactory().openSession() ;
//			CourseDao dao = new CourseDao(session);
//			boolean success = dao.autoCourseSelectCT(start, end, courseIds, teacherIds,classroomIds,subjectIds);
//			if (success) {
//				out.print("{\"success\":true,\"message\":\"自動排課成功\"}");
//			} else {
//				out.print("{\"success\":false,\"message\":\"自動排課失敗\"}");
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			out.print("{\"success\":false,\"message\":\"排課失敗: " + e.getMessage() + "\"}");
//		}
//	}
//
//	protected void doPost(HttpServletRequest request, HttpServletResponse response)
//			throws ServletException, IOException {
//		doGet(request, response);
//	}
//
//}
