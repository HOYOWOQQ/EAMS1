	package com.eams.Controller.course;

import com.eams.Entity.course.Classroom;
import com.eams.Entity.course.Course;
import com.eams.Entity.course.CourseEnroll;
import com.eams.Entity.course.Subject;
import com.eams.Entity.course.DTO.ClassroomDTO;
import com.eams.Entity.course.DTO.CourseDTO;
import com.eams.Entity.course.DTO.QuotaInfo;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.eams.Service.course.ClassroomService;
import com.eams.Service.course.CourseEnrollService;
import com.eams.Service.course.CourseService;
import com.eams.Service.course.SubjectService;
import com.eams.Service.member.StudentService;
import com.eams.Service.member.TeacherService;
import com.eams.common.ApiResponse;
import com.eams.common.log.annotation.LogOperation;

import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;



import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/course")
public class CourseApiController {

	@Autowired
	private CourseService courseService;

	@Autowired
	private CourseEnrollService courseEnrollService;

	@Autowired
	private StudentService studentService;

	@Autowired
	private TeacherService teacherService;

	@Autowired
	private SubjectService subjectService;

	@Autowired
	private ClassroomService classroomService;
	
	
	private static final String COURSE_IMAGE_DIR = System.getProperty("user.dir") + "/uploads/course-images/";
	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
	
	@GetMapping("/byId")
	public ResponseEntity<ApiResponse<CourseDTO>> getCourseById(@RequestParam("courseId") Integer courseId) {

		Course c = courseService.getCourseById(courseId);

		CourseDTO dto= CourseDTO.fromEntity(c);

		return ResponseEntity.ok(ApiResponse.success("查詢成功", dto));

	}

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCourse() {

		List<Course> all = courseService.getAllCourse();

		List<CourseDTO> dtolist = all.stream().map(CourseDTO::fromEntity).collect(Collectors.toList());

		return ResponseEntity.ok(ApiResponse.success("查詢成功", dtolist));

	}

	// 查某學生註冊課程
	@GetMapping("/ByStudentId")
	public ResponseEntity<ApiResponse<List<CourseDTO>>> getCourseByStudentId(HttpSession session) {

		Integer studentId = (Integer) session.getAttribute("studentId");
		List<Course> all = courseService.getCourseByStudentId(studentId);
		List<CourseDTO> dtolist = all.stream().map(CourseDTO::fromEntity).collect(Collectors.toList());


		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", dtolist));

	}

	// 查全部課目
	@GetMapping("/subject/all")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllSubject() {

		List<Subject> all = subjectService.getAllSubject();
		List<Map<String, Object>> list = new ArrayList<>();

		for (Subject s : all) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", s.getId());
			map.put("name", s.getName());

			list.add(map);
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
	}

	// 查所有教室id
	@GetMapping("/room/allId")
	public ResponseEntity<ApiResponse<List<Integer>>> getAllClassroomId() {
		List<Integer> list = classroomService.getAllClassroomId();
		return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
	}

	// 全部老師
	@GetMapping("/teacherAll")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllTeacher() {
		List<Teacher> all = teacherService.getAllTeacher();
		List<Map<String, Object>> list = new ArrayList<>();

		for (Teacher t : all) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", t.getId());
			map.put("name", t.getMember().getName());

			list.add(map);
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
	}

	// 查某課可以上課的老師
	@GetMapping("/teacherByCourse")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTeacherByCourse(
			@RequestParam("courseId") Integer courseId) {

		List<Teacher> all = teacherService.getTeacherByCourseId(courseId);
		List<Map<String, Object>> list = new ArrayList<>();

		for (Teacher t : all) {
			Map<String, Object> map = new HashMap<>();
			map.put("id", t.getId());
			map.put("name", t.getMember().getName());

			list.add(map);
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
	}

	// 查老師by科目
	@GetMapping("/teacherBySubject")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTeacherBySubject(
			@RequestParam("subjectId") Integer subjectId) {

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
	@GetMapping("/subjectByCourse")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSubjectByCourse(
			@RequestParam("courseId") Integer courseId) {

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

	// 查報名相關數據
	@GetMapping("/quota")
	public ResponseEntity<ApiResponse<QuotaInfo>> getCourseQuota(@RequestParam("courseId") Integer courseId) {
		if (courseId == null) {
			return ResponseEntity.status(500).body(ApiResponse.error("error:" + "缺乏參數courseId"));
		}

		int quota = courseEnrollService.getMaxCapacity(courseId);
		int enrolled = courseEnrollService.getEnrolledCount(courseId);
		String deadline = courseEnrollService.getRegistrationEndDate(courseId)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		QuotaInfo info = new QuotaInfo(quota, enrolled, deadline);
		return ResponseEntity.ok(ApiResponse.success("查詢成功", info));

	}
	@DeleteMapping("/delete/{courseId}")
	@ResponseBody
	 @LogOperation(type = "COURSE_DELETE", name = "刪除課程", description =
	 "刪除課程主檔", targetType = "COURSESCHEDULE",forceApproval = true)
	public ResponseEntity<ApiResponse<CourseDTO>> deleteCourseById(
			@PathVariable Integer courseId) {
		Course todelete = courseService.getCourseById(courseId);
		CourseDTO dto = CourseDTO.fromEntity(todelete);
		System.out.println("dto:"+dto);
		courseService.deleteCourseById(courseId);
		return ResponseEntity.ok(ApiResponse.success("刪除成功",dto));

	}
	


	// course新增
	@PostMapping("/add")
	@ResponseBody
	@LogOperation(type = "COURSE_CREATE", name = "新增課程", description = "創建新的課程記錄", targetType = "COURSE")
	public ResponseEntity<ApiResponse<?>> addCourse(@RequestBody Map<String, Object> courseData) {
		System.out.println("收到 API 請求：" + courseData);

		Course course = new Course();
		
		Integer id = Integer.parseInt((String)courseData.get("id"));
		Integer maxCapacity = Integer.parseInt((String)courseData.get("maxCapacity"));
		Integer minCapacity = Integer.parseInt((String)courseData.get("minCapacity"));
		Integer fee = Integer.parseInt((String)courseData.get("fee"));
		
		// 手動設定各個欄位，避免 Jackson 轉換問題
		if (courseData.get("id") != null) {
			course.setId(id);
			
		}
		course.setName((String) courseData.get("name"));
		course.setType((String) courseData.get("type"));
		course.setDescription((String) courseData.get("description"));
		course.setMaxCapacity(maxCapacity);
		course.setMinCapacity(minCapacity);
		course.setFee(fee);
		course.setStatus((String) courseData.get("status"));
		course.setRemark((String) courseData.get("remark"));
		

		// 手動處理日期轉換
		String startDateStr = (String) courseData.get("startDate");
		if (startDateStr != null && !startDateStr.trim().isEmpty()) {
			course.setStartDate(LocalDate.parse(startDateStr));
		}

		String endDateStr = (String) courseData.get("endDate");
		if (endDateStr != null && !endDateStr.trim().isEmpty()) {
			course.setEndDate(LocalDate.parse(endDateStr));
		}
		
		String registrationStartDate = (String) courseData.get("registrationStartDate");
		if(registrationStartDate != null && !registrationStartDate.trim().isEmpty()) {
			course.setRegistrationStartDate(LocalDate.parse(registrationStartDate));
		}
		
		String registrationEndDate = (String) courseData.get("registrationEndDate");
		if(registrationEndDate != null && !registrationEndDate.trim().isEmpty()) {
			course.setRegistrationEndDate(LocalDate.parse(registrationEndDate));
		}

		System.out.println("準備儲存的 Course：" + course);
		Course update = courseService.saveCourse(course);
		System.out.println("新增成功");
		CourseDTO dto = CourseDTO.fromEntity(update);

		return ResponseEntity.ok(ApiResponse.success("成功新增課程" , dto));

	}

	// course更新
	@PutMapping("/update")
	@ResponseBody
	@LogOperation(type = "COURSE_UPDATE", name = "更新課程", description = "更新課程內容", targetType = "COURSE")
	public ResponseEntity<ApiResponse<?>> updateCourse(@RequestBody Map<String, Object> courseData) {
		System.out.println("收到 API 請求：" + courseData);

		Integer courseId = (Integer) courseData.get("id");
	    if (courseId == null) {
	        return ResponseEntity.badRequest().body(ApiResponse.error("課程 ID 不能為空"));
	    }

	    Course course = courseService.getCourseById(courseId);
	    if (course == null) {
	        return ResponseEntity.badRequest().body(ApiResponse.error("找不到指定的課程"));
	    }
	    
		course.setName((String) courseData.get("name"));
		course.setType((String) courseData.get("type"));
		course.setDescription((String) courseData.get("description"));
		course.setMaxCapacity((Integer) courseData.get("maxCapacity"));
		course.setMinCapacity((Integer) courseData.get("minCapacity"));
		course.setFee((Integer) courseData.get("fee"));
		course.setStatus((String) courseData.get("status"));
		course.setRemark((String) courseData.get("remark"));

		// 手動處理日期轉換
		String startDateStr = (String) courseData.get("startDate");
		if (startDateStr != null && !startDateStr.trim().isEmpty()) {
			course.setStartDate(LocalDate.parse(startDateStr));
		}

		String endDateStr = (String) courseData.get("endDate");
		if (endDateStr != null && !endDateStr.trim().isEmpty()) {
			course.setEndDate(LocalDate.parse(endDateStr));
		}
		
		String registrationStartDate = (String) courseData.get("registrationStartDate");
		if(registrationStartDate != null && !registrationStartDate.trim().isEmpty()) {
			course.setRegistrationStartDate(LocalDate.parse(registrationStartDate));
		}
		
		String registrationEndDate = (String) courseData.get("registrationEndDate");
		if(registrationEndDate != null && !registrationEndDate.trim().isEmpty()) {
			course.setRegistrationEndDate(LocalDate.parse(registrationEndDate));
		}

		System.out.println("準備儲存的 Course：" + course);
		Course update = courseService.saveCourse(course);
		System.out.println("更新成功");
		CourseDTO dto = CourseDTO.fromEntity(update);

		return ResponseEntity.ok(ApiResponse.success("成功更新課程" ,dto));

	}
	@GetMapping("/checkExists")
	public ResponseEntity<?> checkCourseExists(@RequestParam ("courseId") Integer courseId){
		if(courseService.getCourseById(courseId) != null) {
			return ResponseEntity.ok("exists");
		}
		return ResponseEntity.ok("null");
	}

	/* Enroll區塊 */

	// 新增報名
	@PostMapping("/enroll/add")
	@ResponseBody
	@LogOperation(type = "COURSEENROLL_CREATE", name = "新增報名", description = "新增學生與課程註冊關係", targetType = "COURSEENROLL")
	public ResponseEntity<ApiResponse<?>> addCourseEnroll(@RequestBody Map<String, Object> courseEnrollData,
			HttpSession session) {
		CourseEnroll courseEnroll = new CourseEnroll();
		Integer studentId = (Integer) session.getAttribute("studentId");
		Student student = studentService.getStudentById(studentId);
		Course course = courseService.getCourseById((Integer) courseEnrollData.get("courseId"));

		courseEnroll.setStudent(student);
		courseEnroll.setCourse(course);
		courseEnroll.setEnrollDate(LocalDate.now());
		courseEnroll.setStatus("enrolled");

		courseEnrollService.saveCourseEnroll(courseEnroll);
		return ResponseEntity.ok(ApiResponse.success("成功報名課程：" + course.getName()));

	}

	// 查註冊總人數
	@GetMapping("/enroll/studentCount")
	public ResponseEntity<ApiResponse<Map<String, Integer>>> getStudentAndCourseCount() {
		Map<String, Integer> map = courseEnrollService.getStudentAndCourseCount();
		return ResponseEntity.ok(ApiResponse.success("查詢成功", map));
	}
	
	//查課程當下報名成功人數
	@GetMapping("/enroll/studentCountByCourse/{courseId}")
	public ResponseEntity<ApiResponse<?>> getStudentAndCourseCount(@PathVariable Integer courseId) {
		Integer count = courseEnrollService.getEnrolledCount(courseId);
		return ResponseEntity.ok(ApiResponse.success("查詢成功", count));
	}
	
	

	// 刪除報名請求
	@DeleteMapping("/courseEnroll/delete")
	@LogOperation(type = "COURSEENROLL_DELETE", name = "取消報名", description = "取消學生與課程註冊關係", targetType = "COURSEENROLL")
	public ResponseEntity<ApiResponse<?>> deletecourseEnroll(@RequestParam("courseId") Integer courseId,
			HttpSession session) {
		Integer studentId = (Integer) session.getAttribute("studentId");
		courseEnrollService.deleteByCouseIdAndStudentId(courseId, studentId);
		return ResponseEntity.ok(ApiResponse.success("刪除成功"));
	}
	
	//查詢教室
	@GetMapping("/classroom/getAll")
	public ResponseEntity<ApiResponse<List<ClassroomDTO>>> getClassroom(){
		
		List<Classroom> all = classroomService.getAllClassroom();
		
		List<ClassroomDTO> dtolist = all.stream().map(ClassroomDTO::fromEntity).collect(Collectors.toList());
		
		return ResponseEntity.ok(ApiResponse.success("查詢成功", dtolist));
		
	}
	
	
	@PostMapping("/image/upload")
	public ResponseEntity<Map<String, Object>> uploadCourseImage(
	        @RequestParam("image") MultipartFile file,
	        @RequestParam("courseId") Integer courseId) {
	    
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 驗證課程是否存在
	        Course course = courseService.getCourseById(courseId);
	        if (course == null) {
	            response.put("success", false);
	            response.put("message", "課程不存在");
	            return ResponseEntity.badRequest().body(response);
	        }
	        
	        // 驗證檔案
	        if (file.isEmpty()) {
	            response.put("success", false);
	            response.put("message", "請選擇檔案");
	            return ResponseEntity.badRequest().body(response);
	        }
	        
	        if (file.getSize() > MAX_FILE_SIZE) {
	            response.put("success", false);
	            response.put("message", "檔案大小不可超過 5MB");
	            return ResponseEntity.badRequest().body(response);
	        }
	        
	        String contentType = file.getContentType();
	        if (!isValidImageType(contentType)) {
	            response.put("success", false);
	            response.put("message", "檔案格式不支援，請選擇 JPG、PNG 或 GIF 格式");
	            return ResponseEntity.badRequest().body(response);
	        }
	        
	        // 確保上傳目錄存在
	        File uploadDir = new File(COURSE_IMAGE_DIR);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdirs();
	        }
	        
	        // 生成檔案名稱
	        String fileExtension = getFileExtension(file.getOriginalFilename());
	        String fileName = "course_" + courseId + "_" + System.currentTimeMillis() + fileExtension;
	        String filePath = COURSE_IMAGE_DIR + fileName;
	        
	        // 刪除舊圖片
	        deleteOldCourseImage(courseId);
	        
	        // 儲存新圖片
	        Path targetPath = Paths.get(filePath);
	        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
	        
	        response.put("success", true);
	        response.put("message", "課程圖片上傳成功");
	        response.put("data", Map.of(
	            "imageUrl", "/api/course/image?courseId=" + courseId,
	            "timestamp", System.currentTimeMillis()
	        ));
	        
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "上傳過程中發生錯誤: " + e.getMessage());
	        return ResponseEntity.status(500).body(response);
	    }
	}

	// 取得課程圖片
	@GetMapping("/image")
	public ResponseEntity<Resource> getCourseImage(@RequestParam("courseId") Integer courseId) {
	    try {
	        // 查找課程圖片檔案
	        File imageDir = new File(COURSE_IMAGE_DIR);
	        if (!imageDir.exists()) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        File[] imageFiles = imageDir.listFiles((dir, name) -> 
	            name.startsWith("course_" + courseId + "_"));
	        
	        if (imageFiles == null || imageFiles.length == 0) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        File imageFile = imageFiles[imageFiles.length - 1];
	        Path filePath = imageFile.toPath();
	        Resource resource = new UrlResource(filePath.toUri());
	        
	        if (!resource.exists() || !resource.isReadable()) {
	            return ResponseEntity.notFound().build();
	        }
	        
	        String contentType = Files.probeContentType(filePath);
	        if (contentType == null) {
	            contentType = "image/jpeg";
	        }
	        
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header(HttpHeaders.CACHE_CONTROL, "max-age=3600")
	                .body(resource);
	                
	    } catch (Exception e) {
	        return ResponseEntity.status(500).build();
	    }
	}

	// 刪除課程圖片
	@DeleteMapping("/image/delete")
	public ResponseEntity<Map<String, Object>> deleteCourseImage(@RequestParam("courseId") Integer courseId) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        // 驗證課程是否存在
	        Course course = courseService.getCourseById(courseId);
	        if (course == null) {
	            response.put("success", false);
	            response.put("message", "課程不存在");
	            return ResponseEntity.badRequest().body(response);
	        }
	        
	        boolean deleted = deleteOldCourseImage(courseId);
	        
	        response.put("success", true);
	        response.put("message", deleted ? "課程圖片刪除成功" : "沒有找到圖片檔案");
	        
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        response.put("success", false);
	        response.put("message", "刪除過程中發生錯誤: " + e.getMessage());
	        return ResponseEntity.status(500).body(response);
	    }
	}

	// 檢查課程圖片狀態
	@GetMapping("/image/status")
	public ResponseEntity<Map<String, Object>> getCourseImageStatus(@RequestParam("courseId") Integer courseId) {
	    try {
	        // 檢查是否有課程圖片
	        File imageDir = new File(COURSE_IMAGE_DIR);
	        boolean hasImage = false;
	        String imageUrl = null;
	        
	        if (imageDir.exists()) {
	            File[] imageFiles = imageDir.listFiles((dir, name) -> 
	                name.startsWith("course_" + courseId + "_"));
	            
	            hasImage = imageFiles != null && imageFiles.length > 0;
	            if (hasImage) {
	                imageUrl = "/api/course/image?courseId=" + courseId;
	            }
	        }
	        
	        Map<String, Object> response = Map.of(
	            "hasImage", hasImage,
	            "imageUrl", imageUrl != null ? imageUrl : "",
	            "courseId", courseId
	        );
	        
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Map.of("error", "檢查課程圖片狀態失敗"));
	    }
	}

	// 輔助方法
	private boolean isValidImageType(String contentType) {
	    return contentType != null && (
	        contentType.equals("image/jpeg") ||
	        contentType.equals("image/jpg") ||
	        contentType.equals("image/png") ||
	        contentType.equals("image/gif")
	    );
	}

	private String getFileExtension(String fileName) {
	    if (fileName == null || !fileName.contains(".")) {
	        return ".jpg";
	    }
	    return fileName.substring(fileName.lastIndexOf("."));
	}

	private boolean deleteOldCourseImage(Integer courseId) {
	    try {
	        File imageDir = new File(COURSE_IMAGE_DIR);
	        if (!imageDir.exists()) {
	            return false;
	        }
	        
	        File[] oldFiles = imageDir.listFiles((dir, name) -> 
	            name.startsWith("course_" + courseId + "_"));
	        
	        if (oldFiles != null) {
	            for (File oldFile : oldFiles) {
	                oldFile.delete();
	            }
	            return oldFiles.length > 0;
	        }
	        
	        return false;
	    } catch (Exception e) {
	        return false;
	    }
	}


}
