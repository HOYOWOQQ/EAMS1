package com.eams.Controller.course;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.Entity.course.Registration;
import com.eams.Entity.course.DTO.RegistrationRequest;
import com.eams.Entity.course.DTO.RegistrationReviewRequest;
import com.eams.Entity.course.DTO.BatchReviewRequest;
import com.eams.Entity.course.DTO.RegistrationDTO;
import com.eams.Entity.course.Enum.RegistrationStatus;
import com.eams.Service.course.RegistrationService;

@RestController
@RequestMapping("/api/registration")
public class RegistrationController {
    
    @Autowired
    private RegistrationService registrationService;
    
    // ===== 報名相關 API =====
    
    /**
     * 統一報名入口（支援雙軌報名）
     * POST /api/registration/submit
     */
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitRegistration(@RequestBody RegistrationRequest request) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(request);
        try {
            Registration registration = registrationService.submitRegistration(request);
            
            response.put("success", true);
            response.put("message", registration.isExistingStudentRegistration() ? "現有學生報名成功" : "新學生申請提交成功，請等待審核");
            response.put("data", registration);
            response.put("registrationType", registration.isExistingStudentRegistration() ? "existing" : "new");
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 取消報名申請
     * PUT /api/registration/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelRegistration(
            @PathVariable Integer id,
            @RequestParam Integer requesterId) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Registration registration = registrationService.cancelRegistration(id, requesterId);
            
            response.put("success", true);
            response.put("message", "報名已取消");
            response.put("data", registration);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ===== 審核相關 API =====
    
    /**
     * 審核單筆報名申請
     * PUT /api/registration/{id}/review
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<Map<String, Object>> reviewRegistration(
            @PathVariable Integer id,
            @RequestBody RegistrationReviewRequest reviewRequest) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Registration registration = registrationService.reviewRegistration(
                id, 
                reviewRequest.getStatus(), 
                reviewRequest.getReviewNote(), 
                reviewRequest.getReviewerId()
            );
            
            response.put("success", true);
            response.put("message", "審核完成");
            response.put("data", registration);
            response.put("approved", registration.getStatus() == RegistrationStatus.APPROVED);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
            
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "系統錯誤：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 批量審核報名申請
     * PUT /api/registration/batch-review
     */
    @PutMapping("/batch-review")
    public ResponseEntity<Map<String, Object>> batchReviewRegistrations(@RequestBody BatchReviewRequest batchRequest) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Registration> results = batchRequest.getRegistrationIds().stream()
                .map(id -> registrationService.reviewRegistration(
                    id, 
                    batchRequest.getStatus(), 
                    batchRequest.getReviewNote(), 
                    batchRequest.getReviewerId()
                ))
                .toList();
            
            response.put("success", true);
            response.put("message", "批量審核完成");
            response.put("data", results);
            response.put("processedCount", results.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "批量審核失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ===== 查詢相關 API =====
    
    /**
     * 根據ID查詢報名記錄
     * GET /api/registration/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRegistrationById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Registration> registration = registrationService.getRegistrationById(id);
        
        if (registration.isPresent()) {
            response.put("success", true);
            response.put("data", registration.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "找不到指定的報名記錄");
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 查詢學生的報名記錄
     * GET /api/registration/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentRegistrations(@PathVariable Integer studentId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Registration> registrations = registrationService.getStudentRegistrations(studentId);
            
            response.put("success", true);
            response.put("data", registrations);
            response.put("count", registrations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查詢失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 查詢課程的報名記錄
     * GET /api/registration/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseRegistrations(@PathVariable Integer courseId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Registration> registrations = registrationService.getCourseRegistrations(courseId);
            
            response.put("success", true);
            response.put("data", registrations);
            response.put("count", registrations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查詢失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 查詢待審核的報名記錄
     * GET /api/registration/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> getPendingRegistrations() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Registration> registrations = registrationService.getPendingRegistrations();
            
            response.put("success", true);
            response.put("data", registrations);
            response.put("count", registrations.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查詢失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 查詢待審核的新學生申請
     * GET /api/registration/pending/new-students
     */
    @GetMapping("/pending/new-students")
    public ResponseEntity<Map<String, Object>> getPendingNewStudentApplications() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Registration> applications = registrationService.getPendingNewStudentApplications();
            
            response.put("success", true);
            response.put("data", applications);
            response.put("count", applications.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查詢失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 分頁查詢報名記錄（支援篩選）
     * GET /api/registration/search
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchRegistrations(
            @RequestParam(required = false) Integer courseId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(required = false) RegistrationStatus status,
            @RequestParam(required = false) Boolean isNewStudent,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "registrationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 現在返回 Page<RegistrationDTO>
            Page<RegistrationDTO> registrationPage = registrationService.getRegistrations(
                    courseId, studentId, status, isNewStudent, page, size, sortBy, sortDir
            );

            response.put("success", true);
            response.put("data", registrationPage.getContent());
            response.put("pagination", Map.of(
                    "currentPage", registrationPage.getNumber(),
                    "totalPages", registrationPage.getTotalPages(),
                    "totalElements", registrationPage.getTotalElements(),
                    "pageSize", registrationPage.getSize(),
                    "hasNext", registrationPage.hasNext(),
                    "hasPrevious", registrationPage.hasPrevious()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查詢失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // ===== 統計相關 API =====
    
    /**
     * 獲取課程報名統計
     * GET /api/registration/stats/course/{courseId}
     */
    @GetMapping("/stats/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseRegistrationStats(@PathVariable Integer courseId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> stats = registrationService.getCourseRegistrationStats(courseId);
            
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "統計查詢失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 獲取整體統計
     * GET /api/registration/stats/overall
     */
    @GetMapping("/stats/overall")
    public ResponseEntity<Map<String, Object>> getOverallStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Integer> stats = registrationService.getOverallStats();
            
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "統計查詢失敗：" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // ===== 輔助 API =====
    
    /**
     * 獲取報名狀態列表
     * GET /api/registration/status-list
     */
    @GetMapping("/status-list")
    public ResponseEntity<Map<String, Object>> getRegistrationStatusList() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, String> statusMap = Map.of(
            "PENDING", "待審核",
            "APPROVED", "已核准",
            "REJECTED", "已拒絕",
            "CANCELLED", "已取消"
        );
        
        response.put("success", true);
        response.put("data", statusMap);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 健康檢查
     * GET /api/registration/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "Registration Service");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}