package com.eams.Controller.course;
import com.eams.common.log.util.UserContextUtil;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.course.Subject;
import com.eams.Entity.course.DTO.CourseDTO;
import com.eams.Entity.course.DTO.CourseScheduleDTO;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Teacher;
import com.eams.Service.course.ClassroomService;
import com.eams.Service.course.CourseScheduleService;
import com.eams.Service.course.CourseService;
import com.eams.Service.course.SubjectService;
import com.eams.Service.member.TeacherService;
import com.eams.common.ApiResponse;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.log.annotation.LogOperation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/schedule")
public class CourseScheduleController {

    private final UserContextUtil userContextUtil;

	@Autowired
	private CourseScheduleService courseScheduleService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private SubjectService subjectService;

	@Autowired
	private ClassroomService classroomService;
	
	 @Autowired
	 private PermissionChecker permissionChecker;
	private static final String[] PERIOD_TIME = { "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00",
			"15:00", "16:00", "17:00", "18:00", "19:00" };


    CourseScheduleController(UserContextUtil userContextUtil) {
        this.userContextUtil = userContextUtil;
    }


	// 課程統計卡片
	@GetMapping("/statistics")
	@ResponseBody
	public ResponseEntity<ApiResponse<Map<String, Object>>> getScheduleStatistics(
			@RequestParam("startdate") String startDate, @RequestParam("enddate") String endDate) {

		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);

		int totalCourses = courseScheduleService.countScheduledCourses(start, end);
		int activeTeachers = courseScheduleService.countActiveTeachers(start, end);
		int roomUsageRate = courseScheduleService.calculateRoomUtilization(start, end);

		Map<String, Object> stats = new HashMap<>();
		stats.put("totalCourses", totalCourses);
		stats.put("activeTeachers", activeTeachers);
		stats.put("roomUsageRate", roomUsageRate);

		return ResponseEntity.ok(ApiResponse.success("查詢成功", stats));
	}

	// 查某週課表資訊
	@GetMapping("/week")
	@ResponseBody
	public ResponseEntity<ApiResponse<List<CourseScheduleDTO>>> getScheduleByWeek(
			@RequestParam("startdate") String startdate, @RequestParam("enddate") String enddate,@RequestParam(value = "userId", required = false) Integer userId,
			 @RequestParam(value = "courseId", required = false) Integer courseId,@RequestParam(value = "roomId", required = false) Integer roomId) {
		
		System.out.println(startdate);
		System.out.println(userId);
		System.out.println(courseId);
		
		List<CourseSchedule> all = courseScheduleService.getCourseScheduleByFilters(LocalDate.parse(startdate), LocalDate.parse(enddate), userId, courseId, roomId);
		List<CourseScheduleDTO> dtolist = all.stream().map(CourseScheduleDTO::fromEntity).collect(Collectors.toList());
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", dtolist));
	}
	
	@GetMapping
	public List<Map<String, Object>> getSchedule(@RequestParam String type,
			@RequestParam(required = false) String userQuery, @RequestParam(required = false) Integer roomId,
			@RequestParam(required = false) Integer courseId, HttpSession session) {

		List<CourseSchedule> cs = new ArrayList<CourseSchedule>();
		Member member = (Member) session.getAttribute("member");
		if ("my".equals(type)) {
			if (member != null) {
				String role = member.getRole();
				if ("student".equals(role)) {
					cs = courseScheduleService.getCourseScheduleByStudentId(member.getId());
				} else if ("teacher".equals(role)) {
					cs = courseScheduleService.getCourseScheduleByTeacherId(member.getId());
				}
			}
		} else if ("search".equals(type)) {
			cs = courseScheduleService.searchCourseSchedule(userQuery, roomId, courseId);
		} else if ("room".equals(type)) {
			if (roomId != null) {
				cs = courseScheduleService.getCourseScheduleByClassroomId(roomId);
			}
		} else if ("course".equals(type)) {
			if (courseId != null) {
				cs = courseScheduleService.getCourseScheduleByCourseId(courseId);
			}
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 轉型成指定格式的字串
		List<Map<String, Object>> arr = new ArrayList<>();

		for (CourseSchedule c : cs) {
			Map<String, Object> obj = new HashMap<>();
			String date = c.getLessonDate().format(formatter); // 2025-09-01
			int startPeriod = c.getPeriodStart(); // 1
			int endPeriod = c.getPeriodEnd(); // 2
			String start = date + "T" + PERIOD_TIME[startPeriod - 1] + ":00";
			String end = date + "T" + PERIOD_TIME[endPeriod] + ":00";

			obj.put("id", c.getId());
			obj.put("title", c.getCourse().getName());
			obj.put("start", start);
			obj.put("end", end);
			Map<String, Object> ext = new HashMap<>();

			ext.put("courseId", c.getCourse().getId());
			ext.put("classroomId", c.getClassroom().getId());
			ext.put("teacherId", c.getTeacher().getId());
			ext.put("teacherName", c.getTeacher().getMember().getName());
			ext.put("periodStart", startPeriod);
			ext.put("periodEnd", endPeriod);
			ext.put("subjectId", c.getSubject().getId());
			ext.put("subjectName", c.getSubject().getName());

			obj.put("extendedProps", ext);
			arr.add(obj);
		}
		return arr;
	}

	// 刪除課程
	@DeleteMapping("/delete")
	@ResponseBody
	// @LogOperation(type = "COURSESCHEDULE_DELETE", name = "刪除課表", description =
	// "刪除課程安排時間表", targetType = "COURSESCHEDULE")
	public ResponseEntity<ApiResponse<CourseScheduleDTO>> deleteCourse(
			@RequestBody Map<String, Object> courseScheduleData) {
		
		System.out.println("收到 API 請求：" + courseScheduleData);
		System.out.println("id:" + (Integer) courseScheduleData.get("id"));

		CourseScheduleDTO dto = courseScheduleService.deleteCourseScheduleById((Integer) courseScheduleData.get("id"));
		return ResponseEntity.ok(ApiResponse.success("刪除成功", dto));

	}

	// 新增課表
	@PostMapping("/add")
	@ResponseBody
	@LogOperation(type = "COURSESCHEDULE_CREATE", name = "新增課表", description = "新增課程安排時間表", targetType = "COURSESCHEDULE")
	public ResponseEntity<ApiResponse<?>> addCourseSchedule(@RequestBody Map<String, Object> courseScheduleData) {
		System.out.println("收到 API 請求：" + courseScheduleData);

		CourseSchedule courseSchedule = new CourseSchedule();
		// 手動設定各個欄位，避免 Jackson 轉換問題
		if (courseScheduleData.get("id") != null) {
			courseSchedule = courseScheduleService.getCourseScheduleById((Integer) courseScheduleData.get("id"));
		}
		courseSchedule.setCourse(courseService.getCourseById((Integer) courseScheduleData.get("courseId")));
		courseSchedule.setSubject(subjectService.getSubjectById((Integer) courseScheduleData.get("subjectId")));
		courseSchedule.setClassroom(classroomService.getClassroomById((Integer) courseScheduleData.get("classroomId")));
		courseSchedule.setPeriodStart((Integer) courseScheduleData.get("periodStart"));
		courseSchedule.setPeriodEnd((Integer) courseScheduleData.get("periodEnd"));
		courseSchedule.setTeacher(teacherService.getTeacherById((Integer) courseScheduleData.get("teacherId")));

		String lessonDateStr = (String) courseScheduleData.get("lessonDate");
		if (lessonDateStr != null && !lessonDateStr.trim().isEmpty()) {
			courseSchedule.setLessonDate(LocalDate.parse(lessonDateStr));
		}

		System.out.println("準備儲存的 courseSchedule：" + courseSchedule);
		CourseSchedule update = courseScheduleService.saveCourseSchedule(courseSchedule);
		System.out.println("新增成功");
		CourseScheduleDTO dto = CourseScheduleDTO.fromEntity(update);

		return ResponseEntity.ok(ApiResponse.success("成功新增課表", dto));
	}

	// 更新課表
	@PostMapping("/update")
	@ResponseBody
	@LogOperation(type = "COURSESCHEDULE_CREATE", name = "更新課表", description = "更新課程安排時間表內容", targetType = "COURSESCHEDULE")
	public ResponseEntity<ApiResponse<?>> updateCourseSchedule(@RequestBody Map<String, Object> courseScheduleData) {
		System.out.println("收到 API 請求：" + courseScheduleData);

		CourseSchedule courseSchedule = new CourseSchedule();
		// 手動設定各個欄位，避免 Jackson 轉換問題
		if (courseScheduleData.get("id") != null) {
			courseSchedule = courseScheduleService.getCourseScheduleById((Integer) courseScheduleData.get("id"));
		}
		courseSchedule.setCourse(courseService.getCourseById((Integer) courseScheduleData.get("courseId")));
		courseSchedule.setSubject(subjectService.getSubjectById((Integer) courseScheduleData.get("subjectId")));
		courseSchedule.setClassroom(classroomService.getClassroomById((Integer) courseScheduleData.get("classroomId")));
		courseSchedule.setPeriodStart((Integer) courseScheduleData.get("periodStart"));
		courseSchedule.setPeriodEnd((Integer) courseScheduleData.get("periodEnd"));
		courseSchedule.setTeacher(teacherService.getTeacherById((Integer) courseScheduleData.get("teacherId")));

		String lessonDateStr = (String) courseScheduleData.get("lessonDate");
		if (lessonDateStr != null && !lessonDateStr.trim().isEmpty()) {
			courseSchedule.setLessonDate(LocalDate.parse(lessonDateStr));
		}

		System.out.println("準備儲存的 courseSchedule：" + courseSchedule);
		CourseSchedule update = courseScheduleService.saveCourseSchedule(courseSchedule);
		System.out.println("更新成功");
		CourseScheduleDTO dto = CourseScheduleDTO.fromEntity(update);

		return ResponseEntity.ok(ApiResponse.success("成功更新課表", dto));

	}
	@DeleteMapping("/delete/{courseScheduleId}")
	@ResponseBody
	// @LogOperation(type = "COURSESCHEDULE_DELETE", name = "刪除課表", description =
	// "刪除課程安排時間表", targetType = "COURSESCHEDULE")
	public ResponseEntity<ApiResponse<CourseScheduleDTO>> deleteCourseById(
			@PathVariable Integer courseScheduleId) {
		
		if (!permissionChecker.hasCurrentUserPermission("course.manage")) {
	         return ResponseEntity.status(403).build();
	     }
		

		CourseScheduleDTO dto = courseScheduleService.deleteCourseScheduleById((Integer) courseScheduleId);
		return ResponseEntity.ok(ApiResponse.success("刪除成功", dto));

	}

	// 查老師by科目
	@GetMapping("/teacherBySubject/{subjectId}")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTeacherBySubject(@PathVariable Integer subjectId) {

		List<Teacher> all = teacherService.getTeacherBySubjectId(subjectId);
		List<Map<String, Object>> list = new ArrayList<>();

		for (Teacher t : all) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", t.getId());
			map.put("name", t.getMember().getName());

			list.add(map);
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
	}

	// 查科目by課程
	@GetMapping("/subjectByCourse/{courseId}")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSubjectByCourse(@PathVariable Integer courseId) {

		List<Subject> all = subjectService.findSubjectByCourseId(courseId);
		List<Map<String, Object>> list = new ArrayList<>();

		for (Subject s : all) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", s.getId());
			map.put("name", s.getName());

			list.add(map);
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
	}
}
