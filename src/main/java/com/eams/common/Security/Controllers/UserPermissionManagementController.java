package com.eams.common.Security.Controllers;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.eams.Entity.member.Member;
import com.eams.Entity.member.DTO.StudentDTO;
import com.eams.Repository.member.MemberRepository;
import com.eams.Service.member.MemberService;
import com.eams.Service.member.StudentService;
import com.eams.common.Security.Services.MemberRoleService;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.Security.Services.PermissionService;
import com.eams.common.Security.Services.RolePermissionService;
import com.eams.common.Security.Services.RoleService;
import com.eams.common.Security.entity.MemberRole;
import com.eams.common.Security.entity.Permission;
import com.eams.common.Security.entity.Role;
import com.eams.common.Security.entity.RolePermission;
import com.eams.common.log.annotation.LogOperation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

//用戶權限管理控制器
@RestController
@RequestMapping("/api/admin/user-permissions")
@Slf4j
public class UserPermissionManagementController {
 
 @Autowired
 private MemberService memberService;
 
 @Autowired
 private RoleService roleService;
 
 @Autowired
 private PermissionService permissionService;
 
 @Autowired
 private MemberRoleService memberRoleService;
 
 @Autowired
 private RolePermissionService rolePermissionService;
 
 @Autowired
 private PermissionChecker permissionChecker;
 
 @Autowired
 private MemberRepository memberRepository;
 
 @Autowired
 private StudentService studentService;
 
 /**
  * 獲取所有用戶列表（用於權限分配頁面）
  */
 @GetMapping("/users")
 public ResponseEntity<Map<String, Object>> getUsers(
         @RequestParam(required = false) String search,
         @RequestParam(required = false) String role) {

     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }

     try {
         List<Member> members;

         if (search != null && !search.trim().isEmpty()) {
             // 使用你現有的搜尋方法
             members = memberService.search(search);
         } else if (role != null && !role.trim().isEmpty()) {
             // 按角色篩選
             members = memberService.findByRole(role);
         } else {
             // 獲取所有啟用用戶
             members = memberService.getActiveMembersList();
         }

         // 轉換為包含角色和權限信息的格式
         List<Map<String, Object>> users = members.stream()
             .filter(Member::getStatus) // 只取啟用用戶
             .map(member -> {
                 Map<String, Object> userInfo = new HashMap<>();
                 
                 // 基本用戶信息
                 userInfo.put("id", member.getId());
                 userInfo.put("account", member.getAccount());
                 userInfo.put("name", member.getName());
                 userInfo.put("email", member.getEmail());
                 userInfo.put("status", member.getStatus());
                 userInfo.put("verified", member.getVerified());
                 userInfo.put("role", member.getRole()); // 原始角色字段
                 
                 // 獲取用戶的角色信息
                 List<MemberRole> userRoles = memberRoleService.getUserActiveRoles(member.getId());
                 List<Map<String, Object>> rolesInfo = userRoles.stream().map(mr -> {
                     Map<String, Object> roleInfo = new HashMap<>();
                     roleInfo.put("roleId", mr.getRoleId());
                     roleInfo.put("isPrimary", mr.getIsPrimary());
                     roleInfo.put("memberRoleId", mr.getId());
                     
                     // 獲取角色名稱
                     roleService.getRoleById(mr.getRoleId()).ifPresent(r -> {
                         roleInfo.put("roleName", r.getRoleName());
                         roleInfo.put("roleCode", r.getRoleCode());
                     });
                     
                     return roleInfo;
                 }).collect(Collectors.toList());
                 
                 userInfo.put("roles", rolesInfo);
                 
                 // 獲取用戶權限數量
                 try {
                     List<String> permissions = permissionChecker.getUserPermissionsAsync(member.getId()).join();
                     userInfo.put("permissionCount", permissions.size());
                 } catch (Exception e) {
                     log.warn("獲取用戶 {} 權限失敗", member.getId(), e);
                     userInfo.put("permissionCount", 0);
                 }
                 
                 return userInfo;
             })
             .collect(Collectors.toList());

         Map<String, Object> result = new HashMap<>();
         result.put("users", users);
         result.put("total", users.size());
         result.put("timestamp", LocalDateTime.now());

         return ResponseEntity.ok(result);

     } catch (Exception e) {
         log.error("獲取用戶列表失敗", e);
         return ResponseEntity.status(500).build();
     }
 }

 
 /**
  * 獲取用戶當前的角色和權限
  */
 @GetMapping("/user/{userId}")
 public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable Integer userId) {
     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     try {
         // 獲取用戶基本信息
         Member member = memberService.getMemberById(userId);
         if (member == null) {
             return ResponseEntity.notFound().build();
         }
         
         // 獲取用戶角色
         List<MemberRole> userRoles = memberRoleService.getUserActiveRoles(userId);
         
         // 獲取用戶權限
         List<String> userPermissions = permissionChecker.getUserPermissionsAsync(userId).join();
         
         // 組裝用戶角色信息
         List<Map<String, Object>> roleList = userRoles.stream().map(mr -> {
             Map<String, Object> roleInfo = new HashMap<>();
             roleInfo.put("memberRoleId", mr.getId());
             roleInfo.put("roleId", mr.getRoleId());
             
             // 獲取角色詳細信息
             roleService.getRoleById(mr.getRoleId()).ifPresent(role -> {
                 roleInfo.put("roleName", role.getRoleName());
                 roleInfo.put("roleCode", role.getRoleCode());
                 roleInfo.put("description", role.getDescription());
             });
             
             roleInfo.put("isPrimary", mr.getIsPrimary());
             roleInfo.put("assignedAt", mr.getAssignedAt());
             roleInfo.put("expiresAt", mr.getExpiresAt());
             roleInfo.put("conditions", mr.getConditions());
             roleInfo.put("notes", mr.getNotes());
             
             return roleInfo;
         }).collect(Collectors.toList());
         
         Map<String, Object> result = new HashMap<>();
         result.put("userId", userId);
         result.put("userInfo", Map.of(
             "id", member.getId(),
             "account", member.getAccount(),
             "name", member.getName(),
             "email", member.getEmail(),
             "role", member.getRole(),
             "status", member.getStatus(),
             "verified", member.getVerified()
         ));
         result.put("roles", roleList);
         result.put("permissions", userPermissions);
         result.put("permissionCount", userPermissions.size());
         
         return ResponseEntity.ok(result);
         
     } catch (Exception e) {
         log.error("獲取用戶權限失敗: userId={}", userId, e);
         return ResponseEntity.status(500).build();
     }
 }
 
 /**
  * 獲取所有可用角色
  */
 @GetMapping("/roles")
 public ResponseEntity<List<Map<String, Object>>> getAllRoles() {
     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     try {
         List<Role> roles = roleService.getAllActiveRoles();
         
         List<Map<String, Object>> roleList = roles.stream().map(role -> {
             Map<String, Object> roleInfo = new HashMap<>();
             roleInfo.put("id", role.getId());
             roleInfo.put("roleName", role.getRoleName());
             roleInfo.put("roleCode", role.getRoleCode());
             roleInfo.put("description", role.getDescription());
             roleInfo.put("levelPriority", role.getLevelPriority());
             roleInfo.put("isSystemRole", role.getIsSystemRole());
             roleInfo.put("maxUsers", role.getMaxUsers());
             
             // 獲取角色統計信息
             Map<String, Object> stats = roleService.getRoleStatistics(role.getId());
             roleInfo.put("currentUserCount", stats.get("userCount"));
             roleInfo.put("permissionCount", stats.get("permissionCount"));
             
             return roleInfo;
         }).collect(Collectors.toList());
         
         return ResponseEntity.ok(roleList);
         
     } catch (Exception e) {
         log.error("獲取角色列表失敗", e);
         return ResponseEntity.status(500).build();
     }
 }
 
 /**
  * 獲取角色的權限列表
  */
 @GetMapping("/role/{roleId}/permissions")
 public ResponseEntity<List<Map<String, Object>>> getRolePermissions(@PathVariable Integer roleId) {
     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     try {
         List<RolePermission> rolePermissions = rolePermissionService.getRolePermissions(roleId);
         
         List<Map<String, Object>> permissionList = rolePermissions.stream()
             .map(rp -> {
                 Map<String, Object> permInfo = new HashMap<>();
                 Permission permission = rp.getPermission();
                 
                 permInfo.put("rolePermissionId", rp.getId());
                 permInfo.put("permissionId", permission.getId());
                 permInfo.put("permissionName", permission.getPermissionName());
                 permInfo.put("permissionCode", permission.getPermissionCode());
                 permInfo.put("category", permission.getCategory());
                 permInfo.put("description", permission.getDescription());
                 permInfo.put("resourceType", permission.getResourceType());
                 permInfo.put("actionType", permission.getActionType());
                 permInfo.put("isDangerous", permission.getIsDangerous());
                 permInfo.put("grantedAt", rp.getGrantedAt());
                 permInfo.put("expiresAt", rp.getExpiresAt());
                 permInfo.put("conditions", rp.getConditions());
                 
                 return permInfo;
             }).collect(Collectors.toList());
         
         return ResponseEntity.ok(permissionList);
         
     } catch (Exception e) {
         log.error("獲取角色權限失敗: roleId={}", roleId, e);
         return ResponseEntity.status(500).build();
     }
 }
 
 /**
  * 為用戶分配角色
  */
 @PostMapping("/user/{userId}/assign-role")
 @LogOperation(
     type = "USER_ROLE_ASSIGN", 
     name = "分配用戶角色",
     targetType = "USER"
 )
 public ResponseEntity<Map<String, Object>> assignRoleToUser(
         @PathVariable Integer userId,
         @RequestBody AssignRoleRequest request) {
     
     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     Map<String, Object> result = new HashMap<>();
     
     try {
         // 檢查用戶是否存在且可分配角色
         if (!memberService.canAssignRole(userId)) {
             result.put("success", false);
             result.put("message", "用戶不存在或狀態不允許分配角色");
             return ResponseEntity.badRequest().body(result);
         }
         
         // 檢查角色是否存在
         if (!roleService.getRoleById(request.getRoleId()).isPresent()) {
             result.put("success", false);
             result.put("message", "角色不存在");
             return ResponseEntity.badRequest().body(result);
         }
         
         // 檢查用戶是否已有該角色
         if (memberRoleService.hasRole(userId, request.getRoleId())) {
             result.put("success", false);
             result.put("message", "用戶已擁有該角色");
             return ResponseEntity.badRequest().body(result);
         }
         
         // 分配角色
         MemberRole memberRole = memberRoleService.assignRole(
             userId,
             request.getRoleId(),
             getCurrentUserId(),
             request.getExpiresAt(),
             request.getConditions(),
             request.getIsPrimary(),
             request.getNotes()
         );
         StudentDTO studentdto = studentService.findStudentById(userId);
         
         result.put("success", true);
         result.put("message", "角色分配成功");
         result.put("memberRoleId", memberRole.getId());
         result.put("userId", userId);
         result.put("userName", studentdto.getName());
         
         return ResponseEntity.ok(result);
         
     } catch (Exception e) {
         log.error("分配用戶角色失敗: userId={}, roleId={}", userId, request.getRoleId(), e);
         result.put("success", false);
         result.put("message", "分配角色失敗: " + e.getMessage());
         return ResponseEntity.status(500).body(result);
     }
 }
 
 /**
  * 移除用戶角色
  */
 @DeleteMapping("/user/{userId}/role/{memberRoleId}")
 @LogOperation(
     type = "USER_ROLE_REMOVE", 
     name = "移除用戶角色",
     targetType = "USER"
 )
 public ResponseEntity<Map<String, Object>> removeRoleFromUser(
         @PathVariable Integer userId,
         @PathVariable Integer memberRoleId) {
     
     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     Map<String, Object> result = new HashMap<>();
     
     try {
         boolean success = memberRoleService.removeRole(memberRoleId);
         
         if (success) {
             result.put("success", true);
             result.put("message", "角色移除成功");
         } else {
             result.put("success", false);
             result.put("message", "角色移除失敗，記錄不存在");
             return ResponseEntity.badRequest().body(result);
         }
         
         return ResponseEntity.ok(result);
         
     } catch (Exception e) {
         log.error("移除用戶角色失敗: userId={}, memberRoleId={}", userId, memberRoleId, e);
         result.put("success", false);
         result.put("message", "移除角色失敗: " + e.getMessage());
         return ResponseEntity.status(500).body(result);
     }
 }
 
 /**
  * 更新角色分配設定
  */
 @PutMapping("/user/{userId}/role/{memberRoleId}")
 @LogOperation(
     type = "USER_ROLE_UPDATE", 
     name = "更新用戶角色設定",
     targetType = "USER"
 )
 public ResponseEntity<Map<String, Object>> updateUserRole(
         @PathVariable Integer userId,
         @PathVariable Integer memberRoleId,
         @RequestBody UpdateUserRoleRequest request) {
     
     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     Map<String, Object> result = new HashMap<>();
     
     try {
         MemberRole updatedRole = memberRoleService.updateMemberRole(
             memberRoleId,
             request.getExpiresAt(),
             request.getConditions(),
             request.getIsPrimary(),
             request.getNotes()
         );
         
         if (updatedRole != null) {
             result.put("success", true);
             result.put("message", "角色設定更新成功");
             result.put("memberRole", Map.of(
                 "id", updatedRole.getId(),
                 "expiresAt", updatedRole.getExpiresAt(),
                 "conditions", updatedRole.getConditions(),
                 "isPrimary", updatedRole.getIsPrimary(),
                 "notes", updatedRole.getNotes()
             ));
         } else {
             result.put("success", false);
             result.put("message", "角色設定更新失敗，記錄不存在");
             return ResponseEntity.badRequest().body(result);
         }
         
         return ResponseEntity.ok(result);
         
     } catch (Exception e) {
         log.error("更新用戶角色設定失敗: userId={}, memberRoleId={}", userId, memberRoleId, e);
         result.put("success", false);
         result.put("message", "更新失敗: " + e.getMessage());
         return ResponseEntity.status(500).body(result);
     }
 }
 
 /**
  * 批量分配角色
  */
 @PostMapping("/batch-assign-role")
 @LogOperation(
     type = "BATCH_USER_ROLE_ASSIGN", 
     name = "批量分配用戶角色",
     targetType = "USER"
 )
 public ResponseEntity<Map<String, Object>> batchAssignRole(@RequestBody BatchAssignRoleRequest request) {
     if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     Map<String, Object> result = new HashMap<>();
     int successCount = 0;
     List<String> errors = new ArrayList<>();
     
     try {
         for (Integer userId : request.getUserIds()) {
             try {
                 // 檢查用戶是否可分配角色
                 if (!memberService.canAssignRole(userId)) {
                     errors.add("用戶 " + userId + " 狀態不允許分配角色");
                     continue;
                 }
                 
                 // 檢查是否已有該角色
                 if (!memberRoleService.hasRole(userId, request.getRoleId())) {
                     memberRoleService.assignRole(
                         userId,
                         request.getRoleId(),
                         getCurrentUserId(),
                         request.getExpiresAt(),
                         request.getConditions(),
                         false, // 批量分配不設為主要角色
                         request.getNotes()
                     );
                     successCount++;
                 } else {
                     errors.add("用戶 " + userId + " 已擁有該角色");
                 }
             } catch (Exception e) {
                 errors.add("用戶 " + userId + " 分配失敗: " + e.getMessage());
             }
         }
         
         result.put("success", true);
         result.put("message", "批量分配完成");
         result.put("totalUsers", request.getUserIds().size());
         result.put("successCount", successCount);
         result.put("errorCount", errors.size());
         result.put("errors", errors);
         
         return ResponseEntity.ok(result);
         
     } catch (Exception e) {
         log.error("批量分配角色失敗", e);
         result.put("success", false);
         result.put("message", "批量分配失敗: " + e.getMessage());
         return ResponseEntity.status(500).body(result);
     }
 }
 
 /**
  * 獲取系統統計信息
  */
 @GetMapping("/statistics")
 public ResponseEntity<Map<String, Object>> getSystemStatistics() {
     if (!permissionChecker.hasCurrentUserPermission("system.settings.manage")) {
         return ResponseEntity.status(403).build();
     }
     
     try {
         Map<String, Object> stats = new HashMap<>();
         
         // 用戶統計
         Map<String, Object> memberStats = memberService.getMemberStatistics();
         stats.put("memberStatistics", memberStats);
         
         // 角色統計
         List<Role> roles = roleService.getAllActiveRoles();
         Map<String, Object> roleStats = new HashMap<>();
         roleStats.put("totalRoles", roles.size());
         roleStats.put("systemRoles", roleService.getSystemRoles().size());
         roleStats.put("customRoles", roleService.getNonSystemRoles().size());
         stats.put("roleStatistics", roleStats);
         
         // 權限統計
         List<Permission> permissions = permissionService.getAllActivePermissions();
         Map<String, Object> permissionStats = new HashMap<>();
         permissionStats.put("totalPermissions", permissions.size());
         permissionStats.put("dangerousPermissions", permissionService.getDangerousPermissions().size());
         permissionStats.put("categoryStats", permissionService.getPermissionCategoryStatistics());
         stats.put("permissionStatistics", permissionStats);
         
         // 即將過期的角色分配
         List<MemberRole> expiringRoles = memberRoleService.getExpiringRoles(7);
         stats.put("expiringRoles", expiringRoles.size());
         
         stats.put("generatedAt", LocalDateTime.now());
         
         return ResponseEntity.ok(stats);
         
     } catch (Exception e) {
         log.error("獲取系統統計失敗", e);
         return ResponseEntity.status(500).build();
     }
 }
 
// // 輔助方法
// private boolean checkPermission(String permissionCode) {
//     try {
//         Integer userId = getCurrentUserId();
//         return permissionChecker.hasPermissionAsync(userId, permissionCode).join();
//     } catch (Exception e) {
//         log.error("權限檢查失敗", e);
//         return false;
//     }
// }
 
 private Integer getCurrentUserId() {
     // 從你的 OperationLogAspect 複製邏輯，轉為 Integer
     try {
         HttpServletRequest request = getCurrentRequest();
         if (request != null && request.getSession(false) != null) {
             HttpSession session = request.getSession(false);
             Object userId = session.getAttribute("id");
             if (userId instanceof Number) {
                 return ((Number) userId).intValue();
             }
             if (userId instanceof String) {
                 try {
                     return Integer.parseInt((String) userId);
                 } catch (NumberFormatException ignored) {}
             }
         }
     } catch (Exception e) {
         log.warn("獲取當前用戶ID失敗", e);
     }
     return null;
 }
 
 private HttpServletRequest getCurrentRequest() {
     ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
     return attributes != null ? attributes.getRequest() : null;
 }
}

//請求 DTO 類
@Data
class AssignRoleRequest {
 private Integer roleId;
 private LocalDateTime expiresAt;
 private String conditions;
 private Boolean isPrimary = false;
 private String notes;
}

@Data
class UpdateUserRoleRequest {
 private LocalDateTime expiresAt;
 private String conditions;
 private Boolean isPrimary;
 private String notes;
}

@Data
class BatchAssignRoleRequest {
 private List<Integer> userIds;
 private Integer roleId;
 private LocalDateTime expiresAt;
 private String conditions;
 private String notes;
}