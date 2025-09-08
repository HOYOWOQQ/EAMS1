package com.eams.Controller.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.eams.Service.member.MemberService;
import com.eams.Service.member.StudentService;
import com.eams.Service.member.TeacherService;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.eams.common.log.util.UserContextUtil;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.Security.Services.CustomUserDetails;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@Slf4j
public class ProfileApiController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private UserContextUtil userContextUtil;
    
    @Autowired
    private PermissionChecker permissionChecker;

    // ===== 🔥 新的認證和權限檢查方法 =====
    
    /**
     * 獲取當前用戶ID
     */
    private Integer getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                return userDetails.getId();
            }
        } catch (Exception e) {
            log.debug("從 SecurityContext 獲取用戶ID失敗", e);
        }
        
        // 備用方案
        Integer userIdFromChecker = permissionChecker.getCurrentUserId();
        if (userIdFromChecker != null) {
            return userIdFromChecker;
        }
        
        // 最後嘗試 UserContextUtil
        Long userIdFromUtil = userContextUtil.getCurrentUserId();
        return userIdFromUtil != null ? userIdFromUtil.intValue() : null;
    }

    /**
     * 權限判斷 - 使用新的權限系統
     */
    private boolean canEdit(Integer targetMemberId, Member targetMember) {
        Integer currentUserId = getCurrentUserId();
        
        if (currentUserId == null || targetMember == null) {
            log.debug("❌ 權限檢查失敗 - 當前用戶ID: {}, 目標會員: {}", currentUserId, targetMember);
            return false;
        }
        
        // 本人可以編輯自己
        if (currentUserId.equals(targetMember.getId())) {
            log.debug("✅ 權限檢查通過 - 用戶編輯自己的資料");
            return true;
        }
        
        // 檢查用戶管理權限（替代原來的主任權限）
        if (permissionChecker.hasCurrentUserPermission("user.manage")) {
            log.debug("✅ 權限檢查通過 - user.manage 權限");
            return true;
        }
        
        // 檢查年級管理權限（教師管理學生的權限）
        if (permissionChecker.hasCurrentUserPermission("grade.manage") && 
            "student".equals(targetMember.getRole())) {
            log.debug("✅ 權限檢查通過 - grade.manage 權限編輯學生資料");
            return true;
        }
        
        log.debug("❌ 權限檢查失敗 - 目標會員角色: {}", targetMember.getRole());
        return false;
    }
    
    /**
     * 檢查是否可以管理用戶狀態（啟用/停用）
     */
    private boolean canManageUserStatus(Integer targetMemberId) {
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        // 不能操作自己
        if (currentUserId.equals(targetMemberId)) {
            return false;
        }
        
        // 需要用戶管理權限
        return permissionChecker.hasCurrentUserPermission("user.manage");
    }

    // 獲得會員資料
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String disable,
            @RequestParam(required = false) String enable,
            HttpSession session) {

        try {
            Integer currentUserId = getCurrentUserId();
            String authType = userContextUtil.getCurrentAuthType();
            
            log.debug("🔍 獲取會員資料 - 當前用戶ID: {}, 認證方式: {}", currentUserId, authType);

            if (currentUserId == null) {
                log.warn("❌ 用戶未登入或無法獲取用戶信息");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            // 確定目標用戶ID
            Integer targetUserId = (id != null) ? id : currentUserId;
            log.debug("🎯 目標用戶ID: {}", targetUserId);

            // 處理啟用/停用操作（需要用戶管理權限）
            if (id != null && !currentUserId.equals(targetUserId)) {
                if ("1".equals(disable) && canManageUserStatus(targetUserId)) {
                    boolean success = memberService.setStatus(targetUserId, false);
                    log.info("✅ 停用帳號 - 目標ID: {}, 結果: {}", targetUserId, success);
                    return ResponseEntity.ok(Map.of("success", true, "message", "已停用帳號！"));
                } else if ("1".equals(enable) && canManageUserStatus(targetUserId)) {
                    boolean success = memberService.setStatus(targetUserId, true);
                    log.info("✅ 啟用帳號 - 目標ID: {}, 結果: {}", targetUserId, success);
                    return ResponseEntity.ok(Map.of("success", true, "message", "已啟用帳號！"));
                }
            }

            // 獲得會員基本資料
            Member member = memberService.getMemberById(targetUserId);
            if (member == null) {
                log.warn("❌ 查無會員資料 - ID: {}", targetUserId);
                return ResponseEntity.notFound().build();
            }

            // 權限檢查
            boolean canEdit = canEdit(targetUserId, member);
            log.debug("🔒 權限檢查結果: {}", canEdit);

            // 組裝回應資料
            Map<String, Object> response = new HashMap<>();
            response.put("member", memberToMap(member));
            response.put("canEdit", canEdit);
            
            // 🆕 添加認證信息供前端參考
            response.put("authType", authType);
            response.put("currentUser", Map.of(
                "id", currentUserId,
                "permissions", Map.of(
                    "userManage", permissionChecker.hasCurrentUserPermission("user.manage"),
                    "gradeManage", permissionChecker.hasCurrentUserPermission("grade.manage"),
                    "systemSettings", permissionChecker.hasCurrentUserPermission("system.settings.manage")
                )
            ));

            // 根據角色添加額外資料
            if ("student".equals(member.getRole())) {
                Student student = studentService.getStudentById(targetUserId);
                if (student != null) {
                    response.put("student", studentToMap(student));
                }
            } else if ("teacher".equals(member.getRole()) || "主任".equals(member.getRole())) {
                Teacher teacher = teacherService.getTeacherById(targetUserId);
                if (teacher != null) {
                    response.put("teacher", teacherToMap(teacher));
                }
            }

            log.debug("✅ 會員資料獲取成功 - 目標ID: {}, 可編輯: {}", targetUserId, canEdit);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ 獲得會員資料時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲得會員資料時發生錯誤"));
        }
    }

    // 更新會員資料
    @PostMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody Map<String, Object> profileData,
            HttpSession session) {

        try {
            Integer currentUserId = getCurrentUserId();
            String authType = userContextUtil.getCurrentAuthType();
            
            log.debug("🔍 更新會員資料 - 當前用戶ID: {}", currentUserId);

            if (currentUserId == null) {
                log.warn("❌ 用戶未登入或無法獲取用戶信息");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            // 獲得目標用戶ID
            Integer targetUserId = profileData.get("id") != null ? 
                Integer.parseInt(profileData.get("id").toString()) : currentUserId;

            log.debug("🎯 更新目標用戶ID: {}", targetUserId);

            // 獲得會員資料
            Member member = memberService.getMemberById(targetUserId);
            if (member == null) {
                log.warn("❌ 查無會員資料 - ID: {}", targetUserId);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "查無此會員"));
            }

            // 權限檢查
            boolean canEdit = canEdit(targetUserId, member);
            if (!canEdit) {
                log.warn("❌ 權限不足 - 當前用戶ID: {}, 目標用戶ID: {}", currentUserId, targetUserId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "無權限修改此資料"));
            }

            // 字串處理
            if (profileData.get("name") != null) {
                member.setName(profileData.get("name").toString());
            }
            if (profileData.get("email") != null) {
                member.setEmail(profileData.get("email").toString());
            }
            if (profileData.get("phone") != null) {
                String phone = profileData.get("phone").toString();
                member.setPhone(phone.isEmpty() ? null : phone);
            }
            
            member.setUpdateTime(java.time.LocalDateTime.now());

            // 狀態處理（只有有用戶管理權限的人可以修改他人狀態）
            if (canManageUserStatus(targetUserId)) {
                Object statusObj = profileData.get("status");
                if (statusObj != null) {
                    Boolean status = null;
                    
                    if (statusObj instanceof Boolean) {
                        status = (Boolean) statusObj;
                    } else if (statusObj instanceof String) {
                        String statusStr = statusObj.toString();
                        status = "true".equalsIgnoreCase(statusStr) || "1".equals(statusStr);
                    } else if (statusObj instanceof Number) {
                        status = ((Number) statusObj).intValue() != 0;
                    }
                    
                    if (status != null) {
                        member.setStatus(status);
                        log.info("✅ 更新狀態 - 目標用戶ID: {}, 新狀態: {}", targetUserId, status);
                    }
                }
            }

            // 保存基本資料
            memberService.updateProfile(member);

            // 根據角色更新詳細資料
            if ("student".equals(member.getRole())) {
                updateStudentData(targetUserId, profileData);
            } else if ("teacher".equals(member.getRole()) || "主任".equals(member.getRole())) {
                updateTeacherData(targetUserId, profileData);
            }

            log.info("✅ 會員資料更新成功 - 目標用戶ID: {}", targetUserId);
            return ResponseEntity.ok(Map.of("success", true, "message", "會員資料更新成功"));

        } catch (Exception e) {
            log.error("❌ 更新會員資料時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "會員資料更新失敗: " + e.getMessage()));
        }
    }

    // 專用的狀態切換 API
    @PostMapping("/status")
    public ResponseEntity<?> toggleStatus(
            @RequestBody Map<String, Object> requestData,
            HttpSession session) {

        try {
            Integer currentUserId = getCurrentUserId();
            
            log.debug("🔍 狀態切換請求 - 當前用戶ID: {}", currentUserId);

            if (currentUserId == null) {
                log.warn("❌ 用戶未登入或無法獲取用戶信息");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            // 獲得參數
            Integer targetUserId = Integer.parseInt(requestData.get("id").toString());
            String action = (String) requestData.get("action"); // "enable" 或 "disable"

            log.debug("🎯 狀態切換 - 目標用戶ID: {}, 動作: {}", targetUserId, action);

            // 權限檢查
            if (!canManageUserStatus(targetUserId)) {
                log.warn("❌ 權限不足或嘗試操作自己的帳號");
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "無權限執行此操作"));
            }

            // 執行狀態切換
            boolean enable = "enable".equals(action);
            boolean success = memberService.setStatus(targetUserId, enable);

            if (success) {
                String message = enable ? "帳號已啟用" : "帳號已停用";
                log.info("✅ 狀態切換成功 - 目標用戶ID: {}, 動作: {}, 結果: {}", 
                    targetUserId, action, message);
                return ResponseEntity.ok(Map.of(
                    "success", true, 
                    "message", message
                ));
            } else {
                log.error("❌ 狀態切換失敗 - 目標用戶ID: {}, 動作: {}", targetUserId, action);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "狀態切換失敗"));
            }

        } catch (Exception e) {
            log.error("❌ 狀態切換時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "狀態切換時發生錯誤"));
        }
    }

    // 🆕 新增：獲取當前用戶信息的 API（用於調試和驗證）
    @GetMapping("/current-user-info")
    public ResponseEntity<?> getCurrentUserInfo() {
        try {
            Integer currentUserId = getCurrentUserId();
            
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "用戶未登入"));
            }
            
            Map<String, Object> userInfo = Map.of(
                "userId", currentUserId,
                "authType", userContextUtil.getCurrentAuthType(),
                "permissions", Map.of(
                    "userManage", permissionChecker.hasCurrentUserPermission("user.manage"),
                    "gradeManage", permissionChecker.hasCurrentUserPermission("grade.manage"),
                    "systemSettings", permissionChecker.hasCurrentUserPermission("system.settings.manage")
                )
            );
            
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            log.error("❌ 獲取當前用戶信息失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取用戶信息失敗"));
        }
    }

    // 更新學生資料 - 保持不變
    private void updateStudentData(Integer studentId, Map<String, Object> data) {
        Student student = studentService.getStudentById(studentId);
        if (student != null) {
            student.setGender((String) data.get("gender"));
            
            String birthdayStr = (String) data.get("birthday");
            if (birthdayStr != null && !birthdayStr.isEmpty()) {
                student.setBirthday(java.time.LocalDate.parse(birthdayStr));
            }
            
            String gradeStr = (String) data.get("grade");
            if (gradeStr != null && !gradeStr.isEmpty()) {
                student.setGrade(Byte.parseByte(gradeStr));
            }
            
            student.setGuardianName((String) data.get("guardianName"));
            student.setGuardianPhone((String) data.get("guardianPhone"));
            student.setAddress((String) data.get("address"));
            
            String enrollDateStr = (String) data.get("enrollDate");
            if (enrollDateStr != null && !enrollDateStr.isEmpty()) {
                student.setEnrollDate(java.time.LocalDate.parse(enrollDateStr));
            }
            
            student.setRemark((String) data.get("remark"));
            studentService.updateStudent(student);
        }
    }

    // 更新教師資料 - 保持不變
    private void updateTeacherData(Integer teacherId, Map<String, Object> data) {
        Teacher teacher = teacherService.getTeacherById(teacherId);
        if (teacher != null) {
            teacher.setGender((String) data.get("gender"));
            
            String birthdayStr = (String) data.get("birthday");
            if (birthdayStr != null && !birthdayStr.isEmpty()) {
                teacher.setBirthday(java.time.LocalDate.parse(birthdayStr));
            }
            
            teacher.setSpecialty((String) data.get("specialty"));
            
            String hireDateStr = (String) data.get("hireDate");
            if (hireDateStr != null && !hireDateStr.isEmpty()) {
                teacher.setHireDate(java.time.LocalDate.parse(hireDateStr));
            }
            
            teacher.setAddress((String) data.get("address"));
            teacher.setRemark((String) data.get("remark"));
            teacherService.updateTeacher(teacher);
        }
    }
    
    // Member 轉 Map - 保持不變
    private Map<String, Object> memberToMap(Member member) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", member.getId());
        map.put("account", member.getAccount());
        map.put("name", member.getName());
        map.put("email", member.getEmail());
        map.put("phone", member.getPhone());
        map.put("role", member.getRole());
        map.put("status", member.getStatus());
        map.put("verified", member.getVerified());
        map.put("createTime", member.getCreateTime());
        map.put("updateTime", member.getUpdateTime());
        return map;
    }

    // Student 轉 Map - 保持不變
    private Map<String, Object> studentToMap(Student student) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", student.getId());
        map.put("gender", student.getGender());
        map.put("birthday", student.getBirthday());
        map.put("grade", student.getGrade());
        map.put("guardianName", student.getGuardianName());
        map.put("guardianPhone", student.getGuardianPhone());
        map.put("address", student.getAddress());
        map.put("enrollDate", student.getEnrollDate());
        map.put("remark", student.getRemark());
        return map;
    }

    // Teacher 轉 Map - 保持不變
    private Map<String, Object> teacherToMap(Teacher teacher) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", teacher.getId());
        map.put("gender", teacher.getGender());
        map.put("birthday", teacher.getBirthday());
        map.put("position", teacher.getPosition());
        map.put("specialty", teacher.getSpecialty());
        map.put("hireDate", teacher.getHireDate());
        map.put("address", teacher.getAddress());
        map.put("remark", teacher.getRemark());
        return map;
    }
}