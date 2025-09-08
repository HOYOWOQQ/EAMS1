package com.eams.Controller.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eams.Service.member.MemberService;
import com.eams.Service.member.StudentService;
import com.eams.Service.member.TeacherService;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.log.annotation.LogOperation;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@Slf4j
public class AddMemberApiController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 新增會員
    @PostMapping("/add")
    @LogOperation(
        type = "MEMBER_CREATE",
        name = "新增會員",
        description = "新增系統會員",
        targetType = "MEMBER"
    )
    public ResponseEntity<?> addMember(@RequestBody Map<String, Object> memberData) {
    	if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
    	    return ResponseEntity.status(403).body(Map.of(
    	        "success", false, 
    	        "message", "您沒有新增會員的權限"
    	    ));
    	}

        try {
            // 基本驗證
            String account = (String) memberData.get("account");
            String password = (String) memberData.get("password");
            String name = (String) memberData.get("name");
            String role = (String) memberData.get("role");

            if (account == null || account.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
                
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "請填寫所有必填欄位"));
            }

            // 檢查帳號是否已存在
//            if (memberService.isAccountExists(account.trim())) {
//                return ResponseEntity.badRequest()
//                    .body(Map.of("success", false, "message", "此帳號已存在，請使用其他帳號"));
//            }

            // 檢查Email是否已存在
            String email = (String) memberData.get("email");
            if (email != null && !email.trim().isEmpty() && memberService.isEmailExists(email.trim())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "此Email已被使用，請使用其他Email"));
            }

            // 建立 Member 物件
            Member member = new Member();
            member.setAccount(account.trim());
            
            member.setPassword(password.trim());
            String encodedPassword = passwordEncoder.encode(password.trim());
            member.setPassword(encodedPassword);
            
            member.setName(name.trim());
            member.setRole(role.trim());
            member.setEmail(email != null ? email.trim() : null);
            member.setPhone((String) memberData.get("phone"));
            member.setVerified(false);
            member.setStatus(true);
            member.setCreateTime(LocalDateTime.now());
            member.setUpdateTime(LocalDateTime.now());

            // 儲存基本會員資料
            memberService.saveMember(member);

            // 根據角色插入額外資料
            if ("student".equals(role)) {
                insertStudentData(memberData, member.getId());
            } else if ("teacher".equals(role)) {
                insertTeacherData(memberData, member.getId());
            }

            Map<String, Object> response = Map.of(
                "success", true, 
                "message", "會員新增成功",
                "data", Map.of(
                    "memberId", member.getId(),
                    "memberName", member.getName(),
                    "memberAccount", member.getAccount(),
                    "memberRole", member.getRole(),
                    "createdAt", member.getCreateTime()
                )
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("新增會員時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "新增失敗: " + e.getMessage()));
        }
    }

    // 檢查帳號是否可用的 API
    @GetMapping("/check-account")
    public ResponseEntity<?> checkAccountAvailability(@RequestParam String account) {
        // 🔥 使用權限字串檢查
        if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false, 
                "message", "您沒有檢查帳號的權限"
            ));
        }

        try {
            if (account == null || account.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "請提供帳號"));
            }

            boolean exists = memberService.isAccountExists(account.trim());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "account", account.trim(),
                "available", !exists,
                "message", exists ? "此帳號已被使用" : "此帳號可以使用"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("檢查帳號可用性時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "檢查帳號時發生錯誤"));
        }
    }

    // 檢查Email是否可用的 API
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmailAvailability(@RequestParam String email) {
        // 🔥 使用權限字串檢查
    	if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false, 
                "message", "您沒有檢查Email的權限"
            ));
        }

        try {
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "請提供Email"));
            }

            boolean exists = memberService.isEmailExists(email.trim());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "email", email.trim(),
                "available", !exists,
                "message", exists ? "此Email已被使用" : "此Email可以使用"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("檢查Email可用性時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "檢查Email時發生錯誤"));
        }
    }

    // 🔥 新增：取得新增會員權限狀態的 API
    @GetMapping("/add-permission")
    public ResponseEntity<?> getAddPermission() {
        boolean hasPermission = permissionChecker.hasCurrentUserPermission("user.manage");
        Integer currentUserId = permissionChecker.getCurrentUserId();

        Map<String, Object> response = Map.of(
            "hasPermission", hasPermission,
            "currentUserId", currentUserId != null ? currentUserId : 0,
            "message", hasPermission ? "有新增會員權限" : "無新增會員權限"
        );

        return ResponseEntity.ok(response);
    }

    // ===== 私有方法 =====

    // 插入學生資料
    private void insertStudentData(Map<String, Object> data, Integer memberId) {
        try {
            Student student = new Student();
            student.setId(memberId);
            
            // 處理年級
            String gradeStr = (String) data.get("grade");
            if (gradeStr != null && !gradeStr.trim().isEmpty()) {
                try {
                    student.setGrade(Byte.parseByte(gradeStr.trim()));
                } catch (NumberFormatException e) {
                    log.warn("年級格式錯誤: {}, 設為預設值 0", gradeStr);
                    student.setGrade((byte) 0);
                }
            } else {
                student.setGrade((byte) 0);
            }
            
            student.setGender((String) data.get("gender"));
            student.setGuardianName((String) data.get("guardianName"));
            student.setGuardianPhone((String) data.get("guardianPhone"));
            student.setAddress((String) data.get("address"));

            // 處理生日
            String birthdayStr = (String) data.get("birthday");
            if (birthdayStr != null && !birthdayStr.trim().isEmpty()) {
                try {
                    student.setBirthday(java.time.LocalDate.parse(birthdayStr.trim()));
                } catch (Exception e) {
                    log.warn("生日格式錯誤: {}", birthdayStr);
                }
            }

            // 處理入學日
            String enrollDateStr = (String) data.get("enrollDate");
            if (enrollDateStr != null && !enrollDateStr.trim().isEmpty()) {
                try {
                    student.setEnrollDate(java.time.LocalDate.parse(enrollDateStr.trim()));
                } catch (Exception e) {
                    log.warn("入學日格式錯誤: {}", enrollDateStr);
                }
            }
            
            // 儲存學生資料
            studentService.saveStudent(student);
            log.info("學生資料儲存完成 - 會員ID: {}, 年級: {}", memberId, student.getGrade());
            
        } catch (Exception e) {
            log.error("插入學生資料時發生錯誤 - 會員ID: {}", memberId, e);
            throw new RuntimeException("插入學生資料失敗", e);
        }
    }
    
    // 插入教師資料
    private void insertTeacherData(Map<String, Object> data, Integer memberId) {
        try {
            Teacher teacher = new Teacher();
            teacher.setId(memberId);
            teacher.setGender((String) data.get("gender"));
            teacher.setSpecialty((String) data.get("specialty"));
            teacher.setAddress((String) data.get("address"));
            teacher.setPosition("老師"); // 預設職位
            
            // 處理生日
            String birthdayStr = (String) data.get("birthday");
            if (birthdayStr != null && !birthdayStr.trim().isEmpty()) {
                try {
                    teacher.setBirthday(java.time.LocalDate.parse(birthdayStr.trim()));
                } catch (Exception e) {
                    log.warn("生日格式錯誤: {}", birthdayStr);
                }
            }

            // 處理到職日
            String hireDateStr = (String) data.get("hireDate");
            if (hireDateStr != null && !hireDateStr.trim().isEmpty()) {
                try {
                    teacher.setHireDate(java.time.LocalDate.parse(hireDateStr.trim()));
                } catch (Exception e) {
                    log.warn("到職日格式錯誤: {}", hireDateStr);
                }
            }

            // 儲存教師資料
            teacherService.saveTeacher(teacher);
            log.info("教師資料儲存完成 - 會員ID: {}, 專長: {}", memberId, teacher.getSpecialty());
            
        } catch (Exception e) {
            log.error("插入教師資料時發生錯誤 - 會員ID: {}", memberId, e);
            throw new RuntimeException("插入教師資料失敗", e);
        }
    }
}