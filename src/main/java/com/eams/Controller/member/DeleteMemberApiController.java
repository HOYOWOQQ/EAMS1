package com.eams.Controller.member;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/member")
//@Slf4j
public class DeleteMemberApiController {

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private PermissionChecker permissionChecker;

    // 刪除會員
    @DeleteMapping("/delete/{id}")
//    @LogOperation(
//        type = "MEMBER_DELETE",
//        name = "刪除會員",
//        description = "刪除系統會員",
//        targetType = "MEMBER"
//    )
    public ResponseEntity<?> deleteMember(@PathVariable int id) {
        // 🔥 使用權限字串檢查，而非角色字串比對
    	if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
    	    return ResponseEntity.status(403).body(Map.of(
    	        "success", false, 
    	        "message", "您沒有刪除會員的權限"
    	    ));
    	}

        try {
            // 檢查會員是否存在
            Member member = memberService.getMemberById(id);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "找不到指定的會員"));
            }
            
            // 防止刪除自己的帳號
            Integer currentUserId = permissionChecker.getCurrentUserId();
            if (currentUserId != null && currentUserId.equals(id)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "無法刪除自己的帳號"));
            }
            
            // 執行刪除操作
            // 1. 先刪除相關的學生或教師資料
            if ("student".equals(member.getRole())) {
                Student student = studentService.getStudentById(id);
                if (student != null) {
                    studentService.deleteStudent(student);
                }
            } else if ("teacher".equals(member.getRole())) {
                Teacher teacher = teacherService.getTeacherById(id);
                if (teacher != null) {
                    teacherService.deleteTeacher(teacher);
                }
            }
            
            // 2. 刪除會員資料
            memberService.deleteMember(member);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "會員「" + member.getName() + "」已成功刪除",
                "data", Map.of(
                    "deletedMember", Map.of(
                        "id", member.getId(),
                        "name", member.getName(),
                        "account", member.getAccount(),
                        "role", member.getRole()
                    ),
                    "deletedAt", LocalDateTime.now()
                )
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            //log.error("刪除會員時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "刪除失敗: " + e.getMessage()));
        }
    }
    
    // 獲取會員資訊（刪除前確認用）
    @GetMapping("/delete-info/{id}")
    public ResponseEntity<?> getMemberDeleteInfo(@PathVariable int id) {
        // 🔥 使用權限字串檢查
        if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false, 
                "message", "您沒有查看刪除資訊的權限"
            ));
        }

        try {
            Member member = memberService.getMemberById(id);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "找不到指定的會員"));
            }
            
            // 防止刪除自己的帳號
            Integer currentUserId = permissionChecker.getCurrentUserId();
            if (currentUserId != null && currentUserId.equals(id)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "無法刪除自己的帳號"));
            }
            
            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("id", member.getId());
            memberInfo.put("name", member.getName());
            memberInfo.put("account", member.getAccount());
            memberInfo.put("role", member.getRole());
            memberInfo.put("email", member.getEmail());
            memberInfo.put("phone", member.getPhone());
            memberInfo.put("verified", member.isVerified());
            memberInfo.put("status", member.getStatus());
            memberInfo.put("createTime", member.getCreateTime());
            memberInfo.put("updateTime", member.getUpdateTime());
            
            Map<String, Object> response = Map.of(
                "success", true,
                "member", memberInfo,
                "canDelete", true,
                "requestedAt", LocalDateTime.now()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            //log.error("獲取刪除資訊時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "獲取資訊失敗: " + e.getMessage()));
        }
    }

    // 🔥 新增：檢查刪除權限的 API
    @GetMapping("/delete-permission")
    public ResponseEntity<?> getDeletePermission() {
        boolean hasPermission = permissionChecker.hasCurrentUserPermission("user.manage");
        Integer currentUserId = permissionChecker.getCurrentUserId();

        Map<String, Object> response = Map.of(
            "hasPermission", hasPermission,
            "canDelete", hasPermission, // 為了相容前端，同時提供兩個欄位
            "currentUserId", currentUserId != null ? currentUserId : 0,
            "message", hasPermission ? "有刪除會員權限" : "無刪除會員權限"
        );

        return ResponseEntity.ok(response);
    }

    // 🔥 新增：獲取可刪除會員列表的 API
    @GetMapping("/deletable-members")
    public ResponseEntity<?> getDeletableMembers() {
        // 檢查權限
        if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
            return ResponseEntity.status(403).body(Map.of(
                "success", false, 
                "message", "您沒有查看可刪除會員列表的權限"
            ));
        }

        try {
            Integer currentUserId = permissionChecker.getCurrentUserId();
            
            // 獲取所有會員，但排除自己
            List<Member> allMembers = memberService.getAllMembers();
            List<Map<String, Object>> deletableMembers = new ArrayList<>();
            
            for (Member member : allMembers) {
                // 排除自己
                if (currentUserId == null || !member.getId().equals(currentUserId)) {
                    Map<String, Object> memberInfo = Map.of(
                        "id", member.getId(),
                        "name", member.getName(),
                        "account", member.getAccount(),
                        "role", member.getRole(),
                        "status", member.getStatus()
                    );
                    deletableMembers.add(memberInfo);
                }
            }

            Map<String, Object> response = Map.of(
                "success", true,
                "members", deletableMembers,
                "total", deletableMembers.size()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            //log.error("獲取可刪除會員列表時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "獲取會員列表失敗: " + e.getMessage()));
        }
    }
}