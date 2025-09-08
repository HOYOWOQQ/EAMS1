package com.eams.Controller.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eams.Entity.course.Course;
import com.eams.Entity.member.Teacher;
import com.eams.Entity.notice.DTO.NoticeCreateDTO;
import com.eams.Entity.notice.DTO.NoticeDTO;
import com.eams.Entity.notice.DTO.NoticeUpdateDTO;
import com.eams.Service.course.CourseService;
import com.eams.Service.member.TeacherService;
import com.eams.Service.notice.NoticeService;
import com.eams.common.log.util.UserContextUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notices")
@Validated
public class NoticeController {
	
	@Autowired
	private CourseService courseService;

	@Autowired 
	private TeacherService teacherService;
    
    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    // 獲取通知列表
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotices() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            List<NoticeDTO> notices = noticeService.getNoticesByUserId(userId.intValue(), userRole);
            
            response.put("success", true);
            response.put("data", notices);
            response.put("message", "獲取通知列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取通知列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取通知詳情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getNoticeDetail(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 檢查訪問權限
            if (!noticeService.canUserAccessNotice(userId.intValue(), userRole, id)) {
                response.put("success", false);
                response.put("message", "您沒有權限查看此通知");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            NoticeDTO notice = noticeService.getNoticeDetailById(id, userId.intValue());
            if (notice == null) {
                response.put("success", false);
                response.put("message", "通知不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // 標記為已讀
            noticeService.markNoticeAsRead(id, userId.intValue());
            
            response.put("success", true);
            response.put("data", notice);
            response.put("message", "獲取通知詳情成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取通知詳情失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 創建通知
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNotice(@Valid @RequestBody NoticeCreateDTO createDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null || !"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "只有教師可以發布通知");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            NoticeDTO notice = noticeService.createNotice(createDTO, userId.intValue());
            
            response.put("success", true);
            response.put("data", notice);
            response.put("message", "通知創建成功");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "創建通知失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 更新通知
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateNotice(@PathVariable Integer id, 
                                                           @Valid @RequestBody NoticeUpdateDTO updateDTO) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null || !"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "只有教師可以編輯通知");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            NoticeDTO notice = noticeService.updateNotice(id, updateDTO, userId.intValue());
            if (notice == null) {
                response.put("success", false);
                response.put("message", "通知不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("data", notice);
            response.put("message", "通知更新成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新通知失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 刪除通知
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteNotice(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null || !"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "只有教師可以刪除通知");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            boolean deleted = noticeService.deleteNotice(id, userId.intValue());
            if (!deleted) {
                response.put("success", false);
                response.put("message", "通知不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            response.put("success", true);
            response.put("message", "通知刪除成功");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除通知失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取未讀通知數量
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            long unreadCount = noticeService.getUnreadNoticeCount(userId.intValue(), userRole);
            
            response.put("success", true);
            response.put("data", unreadCount);
            response.put("message", "獲取未讀數量成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取未讀數量失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取教師可以發布通知的課程列表
    @GetMapping("/courses")
    public ResponseEntity<Map<String, Object>> getTeacherCourses() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            if (!"teacher".equals(userRole) && !"主任".equals(userRole)) {
                response.put("success", false);
                response.put("message", "只有教師可以查看課程列表");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // 獲取該教師教授的課程
            List<Map<String, Object>> teacherCourses = getCoursesForTeacher(userId.intValue());
            
            if (teacherCourses.isEmpty()) {
                System.out.println("警告：教師 ID " + userId + " 沒有找到任何關聯的課程");
            }
            
            response.put("success", true);
            response.put("data", teacherCourses);
            response.put("message", "獲取課程列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取課程列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    private List<Map<String, Object>> getCoursesForTeacher(Integer teacherId) {
        List<Map<String, Object>> courseList = new ArrayList<>();
        
        try {
            // 獲取所有課程
            List<Course> allCourses = courseService.getAllCourse();
            
            // 遍歷所有課程，找出該教師教授的課程
            for (Course course : allCourses) {
                try {
                    // 檢查該教師是否教授這門課程
                    List<Teacher> courseTeachers = teacherService.getTeacherByCourseId(course.getId());
                    
                    // 查找當前教師是否在這門課程的教師列表中
                    boolean isTeachingThisCourse = false;
                    for (Teacher teacher : courseTeachers) {
                        if (Objects.equals(teacher.getMember().getId(), teacherId)) {
                            isTeachingThisCourse = true;
                            break;
                        }
                    }
                    // 如果教師教授這門課程且課程狀態為活躍，則加入列表
                    if (isTeachingThisCourse && isActiveCourse(course)) {
                        Map<String, Object> courseMap = new HashMap<>();
                        courseMap.put("id", course.getId());
                        courseMap.put("name", course.getName());
                        courseMap.put("description", course.getDescription() != null ? course.getDescription() : "");
                        courseMap.put("type", course.getType() != null ? course.getType() : "");
                        courseMap.put("status", course.getStatus());
                        courseList.add(courseMap);
                    }
                } catch (Exception e) {
                    // 單個課程處理失敗不影響整體流程
                    System.err.println("處理課程 " + course.getId() + " 時發生錯誤: " + e.getMessage());
                }
            }
            
            System.out.println("教師 " + teacherId + " 可選擇的課程數量: " + courseList.size());
            
        } catch (Exception e) {
            System.err.println("獲取教師課程列表時發生錯誤: " + e.getMessage());
            e.printStackTrace();
        }
        
        return courseList;
    }

    // 輔助方法：檢查課程是否為活躍狀態
    private boolean isActiveCourse(Course course) {
        String status = course.getStatus();
        return "active".equals(status) || 
               "進行中".equals(status) || 
               "開放".equals(status) ||
               "available".equals(status);
    }
    
    // 獲取通知已讀人員列表
    @GetMapping("/{id}/readers")
    public ResponseEntity<Map<String, Object>> getNoticeReaders(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 檢查是否有權限查看此通知的已讀狀態（只有教師可以查看）
            if (!"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "只有教師可以查看已讀狀態");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // 檢查通知是否存在且屬於該教師
            if (!noticeService.canTeacherAccessNotice(userId.intValue(), id)) {
                response.put("success", false);
                response.put("message", "您沒有權限查看此通知的已讀狀態");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            List<Map<String, Object>> readers = noticeService.getNoticeReaders(id);
            
            response.put("success", true);
            response.put("data", readers);
            response.put("message", "獲取已讀人員列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取已讀人員列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 獲取通知未讀人員列表
    @GetMapping("/{id}/unread-users")
    public ResponseEntity<Map<String, Object>> getNoticeUnreadUsers(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 檢查是否有權限查看此通知的未讀狀態（只有教師可以查看）
            if (!"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "只有教師可以查看未讀狀態");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // 檢查通知是否存在且屬於該教師
            if (!noticeService.canTeacherAccessNotice(userId.intValue(), id)) {
                response.put("success", false);
                response.put("message", "您沒有權限查看此通知的未讀狀態");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            List<Map<String, Object>> unreadUsers = noticeService.getNoticeUnreadUsers(id);
            
            response.put("success", true);
            response.put("data", unreadUsers);
            response.put("message", "獲取未讀人員列表成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取未讀人員列表失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 獲取通知已讀統計
    @GetMapping("/{id}/read-stats")
    public ResponseEntity<Map<String, Object>> getNoticeReadStats(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long userId = userContextUtil.getCurrentUserId();
            String userRole = userContextUtil.getCurrentUserRole();
            
            if (userId == null) {
                response.put("success", false);
                response.put("message", "用戶未登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 檢查是否有權限查看此通知的已讀統計（只有教師可以查看）
            if (!"teacher".equals(userRole)) {
                response.put("success", false);
                response.put("message", "只有教師可以查看已讀統計");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // 檢查通知是否存在且屬於該教師
            if (!noticeService.canTeacherAccessNotice(userId.intValue(), id)) {
                response.put("success", false);
                response.put("message", "您沒有權限查看此通知的已讀統計");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            Map<String, Object> stats = noticeService.getNoticeReadStats(id);
            
            response.put("success", true);
            response.put("data", stats);
            response.put("message", "獲取已讀統計成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取已讀統計失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}