package com.eams.Controller.course;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.Entity.course.Course;
import com.eams.Entity.course.CourseEnroll;
import com.eams.Entity.course.CourseSchedule;
import com.eams.Entity.member.Student;
import com.eams.Service.course.CourseEnrollService;
import com.eams.Service.course.CourseService;
import com.eams.Service.member.StudentService;

@RestController
@RequestMapping("/api/course-enroll")
public class CourseEnrollController {

    @Autowired
    private CourseEnrollService courseEnrollService;
    
    @Autowired
    private CourseService courseService;
    
    @Autowired
    private StudentService studentService;



    /**
     * 取消报名
     */
    @DeleteMapping("/cancel/{courseId}/{studentId}")
    public ResponseEntity<Map<String, Object>> cancelEnrollment(
            @PathVariable Integer courseId,
            @PathVariable Integer studentId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean success = courseEnrollService.deleteByCouseIdAndStudentId(courseId, studentId);
            
            if (success) {
                response.put("success", true);
                response.put("message", "取消报名成功");
            } else {
                response.put("success", false);
                response.put("message", "取消报名失败，找不到相关记录");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "取消报名失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    

    /**
     * 查询学生的报名记录（不分页）- 包含学生和课程名称
     */
    @GetMapping("/student/{studentId}/all")
    public ResponseEntity<Map<String, Object>> getStudentAllEnrollments(@PathVariable Integer studentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CourseEnroll> enrollments = courseEnrollService.getCourseEnrollsByStudentId(studentId);
            
            // 转换为包含名称的数据
            List<Map<String, Object>> enrichedData = new ArrayList<>();
            
            for (CourseEnroll enrollment : enrollments) {
                Map<String, Object> enrollData = new HashMap<>();
                
                // 基本报名信息
                enrollData.put("enrollId", enrollment.getId());
                enrollData.put("studentId", enrollment.getStudentId());
                enrollData.put("courseId", enrollment.getCourseId());
                enrollData.put("status", enrollment.getStatus());
                enrollData.put("enrolledAt", enrollment.getEnrollDate());
                
                // 查询并添加学生名称
                Student student = studentService.getStudentById(enrollment.getStudentId());
                enrollData.put("studentName", student != null ? student.getMember().getName() : "未知学生");
                
                // 查询并添加课程名称
                Course course = courseService.getCourseById(enrollment.getCourseId());
                enrollData.put("courseName", course != null ? course.getName() : "未知课程");
                
                enrichedData.add(enrollData);
            }
            
            response.put("success", true);
            response.put("data", enrichedData);
            response.put("count", enrichedData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    

   
}