package com.eams.common;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.eams.Entity.member.Member;
import com.eams.common.Security.Services.MemberRoleService;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.Security.entity.MemberRole;
import jakarta.servlet.http.HttpSession;

@Controller
public class ApiSessionController {
    
    @Autowired
    private MemberRoleService memberRoleService;
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    @GetMapping("/api/getsession")
    public ResponseEntity<ApiResponse<Map<String,Object>>> getSessionData(HttpSession session){
        
        Map<String,Object> data = new HashMap<String, Object>();
        
        Object member = session.getAttribute("member");
        
        if (member == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("未登入"));
        }
        
        Member memberObj = (Member) member;
        
        // 🎯 原有的基本資訊
        data.put("id", memberObj.getId());
        data.put("name", memberObj.getName());
        data.put("role", memberObj.getRole());
        data.put("account", memberObj.getAccount());
        
        // 🆕 從 session 中獲取動態權限資訊（如果存在）
        Object userRoles = session.getAttribute("userRoles");
        Object userPermissions = session.getAttribute("userPermissions");
        Object permissionsLoadTime = session.getAttribute("permissionsLoadTime");
        
        if (userRoles != null && userPermissions != null) {
            // Session 中已有權限資訊，直接使用
            data.put("userRoles", userRoles);
            data.put("userPermissions", userPermissions);
            data.put("permissionsLoadTime", permissionsLoadTime);
        } else {
            // Session 中沒有權限資訊，實時查詢並存入 session
            try {
                List<MemberRole> roles = memberRoleService.getUserActiveRoles(memberObj.getId());
                List<String> permissions = permissionChecker.getUserPermissionsAsync(memberObj.getId()).join();
                
                // 存入 session 以供下次使用
                session.setAttribute("userRoles", roles);
                session.setAttribute("userPermissions", permissions);
                session.setAttribute("permissionsLoadTime", LocalDateTime.now());
                
                data.put("userRoles", roles);
                data.put("userPermissions", permissions);
                data.put("permissionsLoadTime", LocalDateTime.now());
            } catch (Exception e) {
                // 權限查詢失敗，設為空陣列
                data.put("userRoles", List.of());
                data.put("userPermissions", List.of());
                data.put("permissionsLoadTime", LocalDateTime.now());
            }
        }
        
        // 🆕 其他 session 中的屬性
        data.put("position", session.getAttribute("position"));
        data.put("isStudent", session.getAttribute("isStudent"));
        data.put("isTeacher", session.getAttribute("isTeacher"));
        data.put("firstLogin", session.getAttribute("firstLogin"));
        
        // 🆕 便利的權限判斷屬性
        @SuppressWarnings("unchecked")
        List<String> permissions = (List<String>) data.get("userPermissions");
        data.put("hasManagePermission", permissions.contains("user.manage"));
        data.put("hasMonitorPermission", permissions.contains("system.monitor"));
        
        // 🆕 主要角色資訊
        @SuppressWarnings("unchecked")
        List<MemberRole> roles = (List<MemberRole>) data.get("userRoles");
        String primaryRole = roles.stream()
            .filter(role -> role.getIsPrimary())
            .map(role -> role.getRole() != null ? role.getRole().getRoleName() : "")
            .findFirst()
            .orElse(null);
        data.put("primaryRole", primaryRole);
        
        return ResponseEntity.ok(ApiResponse.success("查詢成功", data));
    }
}