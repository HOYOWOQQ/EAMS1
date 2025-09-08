// 修正的 CourseSubjectController.java
package com.eams.Controller.course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.eams.Entity.course.Course;
import com.eams.Entity.course.Subject;
import com.eams.Service.course.CourseSubjectService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/course-subjects")
@CrossOrigin(origins = "*")
public class CourseSubjectController {
    
    @Autowired
    private CourseSubjectService courseSubjectService;
    
    // 新增單一科目到課程
    @PostMapping("/courses/{courseId}/subjects/{subjectId}")
    public ResponseEntity<Map<String, Object>> addSubjectToCourse(
            @PathVariable Integer courseId, 
            @PathVariable Integer subjectId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean added = courseSubjectService.addSubjectToCourse(courseId, subjectId);
            
            if (added) {
                response.put("success", true);
                response.put("message", "科目已成功新增到課程");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "科目已經存在於課程中");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 批量新增科目到課程
    @PostMapping("/courses/{courseId}/subjects")
    public ResponseEntity<Map<String, Object>> addSubjectsToCourse(
            @PathVariable Integer courseId,
            @RequestBody List<Integer> subjectIds) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int addedCount = courseSubjectService.addSubjectsToCourse(courseId, subjectIds);
            
            response.put("success", true);
            response.put("message", String.format("成功新增 %d 個科目到課程", addedCount));
            response.put("addedCount", addedCount);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 從課程移除科目
    @DeleteMapping("/courses/{courseId}/subjects/{subjectId}")
    public ResponseEntity<Map<String, Object>> removeSubjectFromCourse(
            @PathVariable Integer courseId,
            @PathVariable Integer subjectId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean removed = courseSubjectService.removeSubjectFromCourse(courseId, subjectId);
            
            if (removed) {
                response.put("success", true);
                response.put("message", "科目已從課程中移除");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "找不到相關的課程或科目關聯");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 取得課程的所有科目 - 用 Map 包裝避免循環參照
    @GetMapping("/courses/{courseId}/subjects")
    public ResponseEntity<Map<String, Object>> getCourseSubjects(@PathVariable Integer courseId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Subject> subjects = courseSubjectService.getCourseSubjects(courseId);
            
            // 轉換為簡單的 Map 結構，只包含需要的欄位
            List<Map<String, Object>> subjectData = subjects.stream()
                .map(subject -> {
                    Map<String, Object> subjectMap = new HashMap<>();
                    subjectMap.put("id", subject.getId());
                    subjectMap.put("name", subject.getName());
                    return subjectMap;
                })
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("data", subjectData);
            response.put("count", subjectData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 取得可新增到課程的科目 - 用 Map 包裝避免循環參照
    @GetMapping("/courses/{courseId}/available-subjects")
    public ResponseEntity<Map<String, Object>> getAvailableSubjectsForCourse(@PathVariable Integer courseId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Subject> subjects = courseSubjectService.getAvailableSubjectsForCourse(courseId);
            
            // 轉換為簡單的 Map 結構，只包含需要的欄位
            List<Map<String, Object>> subjectData = subjects.stream()
                .map(subject -> {
                    Map<String, Object> subjectMap = new HashMap<>();
                    subjectMap.put("id", subject.getId());
                    subjectMap.put("name", subject.getName());
                    return subjectMap;
                })
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("data", subjectData);
            response.put("count", subjectData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 取得包含特定科目的課程 - 用 Map 包裝避免循環參照
    @GetMapping("/subjects/{subjectId}/courses")
    public ResponseEntity<Map<String, Object>> getCoursesContainingSubject(@PathVariable Integer subjectId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Course> courses = courseSubjectService.getCoursesContainingSubject(subjectId);
            
            // 轉換為簡單的 Map 結構，只包含需要的欄位
            List<Map<String, Object>> courseData = courses.stream()
                .map(course -> {
                    Map<String, Object> courseMap = new HashMap<>();
                    courseMap.put("id", course.getId());
                    courseMap.put("name", course.getName());
                    courseMap.put("type", course.getType());
                    courseMap.put("status", course.getStatus());
                    return courseMap;
                })
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("data", courseData);
            response.put("count", courseData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 檢查課程是否包含科目
    @GetMapping("/courses/{courseId}/subjects/{subjectId}/exists")
    public ResponseEntity<Map<String, Object>> checkCourseContainsSubject(
            @PathVariable Integer courseId,
            @PathVariable Integer subjectId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean exists = courseSubjectService.isCourseContainsSubject(courseId, subjectId);
            
            response.put("success", true);
            response.put("exists", exists);
            response.put("message", exists ? "課程包含此科目" : "課程不包含此科目");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // 取得課程科目統計
    @GetMapping("/courses/{courseId}/subjects/count")
    public ResponseEntity<Map<String, Object>> getCourseSubjectCount(@PathVariable Integer courseId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            int count = courseSubjectService.getCourseSubjectCount(courseId);
            
            response.put("success", true);
            response.put("count", count);
            response.put("message", String.format("課程包含 %d 個科目", count));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}