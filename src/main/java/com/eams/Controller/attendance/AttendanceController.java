package com.eams.Controller.attendance;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.attendance.DTO.AttendanceDTO;
import com.eams.Entity.attendance.DTO.RollCallRequest;
import com.eams.Entity.course.Course;
import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.course.DTO.CourseDTO;
import com.eams.Repository.course.CourseScheduleRepository;
import com.eams.Service.attendance.AttendanceService;
import com.eams.Service.course.CourseService;
import com.eams.common.ApiResponse;
import com.eams.common.log.util.UserContextUtil;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;

	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseScheduleRepository courseScheduleRepository;

	@Autowired
	private UserContextUtil userContextUtil;
	/* ------------ 共用工具 ------------ */

	// 目前 getCurrentUserRole() 回傳單一字串
	private boolean hasAnyRole(String role, String... allowed) {
		if (role == null)
			return false;
		for (String a : allowed) {
			if (a.equalsIgnoreCase(role))
				return true;
		}
		return false;
	}

	/** uid 為 null 或超出 int 範圍時回應；否則回傳 null */
	private ResponseEntity<?> invalidIfOutOfIntRange(Long uid) {
		if (uid == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("請先登入");
		}
		if (uid > Integer.MAX_VALUE || uid < Integer.MIN_VALUE) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("使用者ID超出範圍");
		}
		return null;
	}

	// 查課程
	@GetMapping("/courseByStudentId")
	public ResponseEntity<ApiResponse<List<CourseDTO>>> getMyCourses(Authentication auth) {
		if (auth == null || auth.getPrincipal() == null) {
			return ResponseEntity.status(401).body(ApiResponse.error("未登入"));
		}

		Object p = auth.getPrincipal();
		Integer studentId = null;

		if (p instanceof com.eams.common.Security.Services.CustomUserDetails cud) {
			studentId = cud.getId(); // ✅ CustomUserDetails 有 id
		}

		if (studentId == null) {
			return ResponseEntity.badRequest().body(ApiResponse.error("找不到學生 ID"));
		}

		// 直接用 CourseService 的方法
		List<Course> courses = courseService.getCourseByStudentId(studentId);
		List<CourseDTO> dtos = courses.stream().map(CourseDTO::fromEntity).toList();

		return ResponseEntity.ok(ApiResponse.success("查詢成功", dtos));
	}

	/* ------------ 學生端 ------------ */


	// 學生查詢
	@GetMapping("/studentsearch")
	public ResponseEntity<?> searchAttendancesForStudent(
	        @RequestParam(required = false) Integer courseId,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lessonDate,
	        @RequestParam(required = false) Integer studentId) {

	    Long uid = userContextUtil.getCurrentUserId();
	    ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
	    if (err != null) return err;

	    // 允許學生、管理員、教師訪問
	    String role = userContextUtil.getCurrentUserRole();
	    if (!hasAnyRole(role, "student", "admin", "teacher")) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
	    }

	    int intUid = uid.intValue();
	    Integer targetStudentId;

	    // 學生身份 → 只能查自己
	    if ("student".equalsIgnoreCase(role)) {
	        targetStudentId = intUid;
	    } else {
	        // 管理員 / 教師身份 → 可查指定學生
	        if (studentId != null) {
	            targetStudentId = studentId;
	        } else {
	            // 沒有傳 studentId → 預設使用目前登入者 ID
	            targetStudentId = intUid;
	        }
	    }

	    List<AttendanceDTO> result =
	            attendanceService.searchAttendancesForStudent(targetStudentId, courseId, lessonDate);

	    return ResponseEntity.ok(result);
	}


	/* ------------ 老師端（只開放老師） ------------ */

	// 老師查詢（教師後台查詢）
	@GetMapping("/teachersearch")
	public ResponseEntity<?> searchAttendanceForTeacher(@RequestParam(required = false) String studentName,
			@RequestParam(required = false) String courseName,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lessonDate,
			@RequestParam(required = false) String status) {

		Long uid = userContextUtil.getCurrentUserId();
		ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
		if (err != null)
			return err;

		String role = userContextUtil.getCurrentUserRole();
		if (!hasAnyRole(role, "teacher")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
		}

		// 參數整理
		studentName = (studentName == null || studentName.isBlank()) ? null : studentName.trim();
		courseName = (courseName == null || courseName.isBlank()) ? null : courseName.trim();
		status = (status == null || status.isBlank()) ? null : status.trim().toUpperCase();

		List<AttendanceDTO> result = attendanceService.searchAttendanceForTeacher(studentName, courseName, lessonDate,
				status);
		return ResponseEntity.ok(result);
	}

	// 今日課表（老師看自己的）
	@GetMapping("/todaySchedules")
	public ResponseEntity<?> getTodaySchedules() {
		Long uid = userContextUtil.getCurrentUserId();
		ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
		if (err != null)
			return err;

		String role = userContextUtil.getCurrentUserRole();
		if (!hasAnyRole(role, "teacher")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
		}

		int teacherId = uid.intValue();
		LocalDate today = LocalDate.now();

		List<CourseSchedule> schedules = courseScheduleRepository.findByTeacher_Id(teacherId).stream()
				.filter(cs -> today.equals(cs.getLessonDate())).collect(Collectors.toList());

		List<Map<String, Object>> result = schedules.stream().map(cs -> {
			Map<String, Object> map = new HashMap<>();
			map.put("id", cs.getId());
			map.put("courseName", cs.getCourse().getName());
			map.put("lessonDate", cs.getLessonDate());
			map.put("periodStart", cs.getPeriodStart());
			map.put("periodEnd", cs.getPeriodEnd());
			map.put("weekdayText", getWeekdayText(cs.getLessonDate()));
			return map;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(result);
	}

	private String getWeekdayText(LocalDate date) {
		switch (date.getDayOfWeek()) {
		case MONDAY:
			return "一";
		case TUESDAY:
			return "二";
		case WEDNESDAY:
			return "三";
		case THURSDAY:
			return "四";
		case FRIDAY:
			return "五";
		case SATURDAY:
			return "六";
		case SUNDAY:
			return "日";
		default:
			return "未知";
		}
	}

	// 點名名單（老師必須是該課授課老師）
	@GetMapping("/rollcall/list")
	public ResponseEntity<?> getRollCallList(@RequestParam Integer courseScheduleId) {
		Long uid = userContextUtil.getCurrentUserId();
		ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
		if (err != null)
			return err;

		String role = userContextUtil.getCurrentUserRole();
		if (!hasAnyRole(role, "teacher")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
		}

		int teacherId = uid.intValue();
		if (!attendanceService.isTeacherOfSchedule(teacherId, courseScheduleId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("你不是這堂課的老師，禁止操作！");
		}

		List<AttendanceDTO> result = attendanceService.getStudentListForRollCall(courseScheduleId);
		return ResponseEntity.ok(result);
	}

	// 送出點名（老師必須是該課授課老師）
	@PostMapping("/rollcall")
	public ResponseEntity<?> submitRollCall(@RequestBody RollCallRequest request) {
		Long uid = userContextUtil.getCurrentUserId();
		ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
		if (err != null)
			return err;

		String role = userContextUtil.getCurrentUserRole();
		if (!hasAnyRole(role, "teacher")) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
		}

		int teacherId = uid.intValue();
		if (!attendanceService.isTeacherOfSchedule(teacherId, request.getCourseScheduleId())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("你不是這堂課的老師，禁止操作！");
		}

		attendanceService.updateAttendanceStatuses(request.getCourseScheduleId(), request.getRollCallMap());
		return ResponseEntity.ok(Map.of("success", true));
	}

	/* ------------ 管理員 ------------ */
	// 管理員查補點名清單
	@GetMapping("/admin/makeup/list")
	public ResponseEntity<?> getMakeupList(@RequestParam Integer courseScheduleId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lessonDate) {

		Long uid = userContextUtil.getCurrentUserId();
		ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
		if (err != null)
			return err;

		String role = userContextUtil.getCurrentUserRole();
		 if (!hasAnyRole(role, "super_admin", "admin", "academic_director","teacher")) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
		    }

		List<AttendanceDTO> result = attendanceService.getMakeupList(courseScheduleId, lessonDate);
		return ResponseEntity.ok(ApiResponse.success("查詢成功", result));
	}

	// 管理員補點名
	@GetMapping("/schedulesByDate")
	public ResponseEntity<?> getSchedulesByDate(
	        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	    Long uid = userContextUtil.getCurrentUserId();
	    ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
	    if (err != null) return err;

	    String role = userContextUtil.getCurrentUserRole();

	    // 老師看自己的；管理層看全部；其他拒絕
	    if (!hasAnyRole(role, "teacher", "super_admin", "admin", "academic_director")) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
	    }

	    List<Map<String, Object>> result =
	            attendanceService.listSchedulesByDate(date, uid.intValue(), role);

	    return ResponseEntity.ok(ApiResponse.success("查詢成功", result));
	}
	@PostMapping("/admin/makeup")
	public ResponseEntity<?> submitMakeupRollCall(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate lessonDate,
			@RequestBody RollCallRequest request) {

		Long uid = userContextUtil.getCurrentUserId();
		ResponseEntity<?> err = invalidIfOutOfIntRange(uid);
		if (err != null)
			return err;

		String role = userContextUtil.getCurrentUserRole();
		 if (!hasAnyRole(role, "super_admin", "admin", "academic_director","teacher")) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("沒有權限");
		    }

		Map<String, Object> result = attendanceService.applyMakeup(lessonDate, request);
		return ResponseEntity.ok(ApiResponse.success("補點名完成", result));
	}

}