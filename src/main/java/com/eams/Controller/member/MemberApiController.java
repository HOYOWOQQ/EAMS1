package com.eams.Controller.member;

import com.eams.Service.member.MemberService;
import com.eams.Service.member.StudentService;
import com.eams.Service.member.TeacherService;
import com.eams.common.ApiResponse;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.eams.Entity.member.DTO.StudentDTO;
import com.eams.Entity.member.DTO.TeacherDTO;
import com.eams.common.log.util.UserContextUtil;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.Security.Services.CustomUserDetails;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/member")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Slf4j
public class MemberApiController {

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
    
    // 頭像存儲目錄
    private static final String AVATAR_DIR = System.getProperty("user.dir") + "/uploads/avatars/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
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
     * 檢查是否有查看會員列表的權限
     */
    private boolean hasViewPermission() {
        // 🔥 修改：user.manage 權限應該包含查看權限
        return permissionChecker.hasCurrentUserPermission("user.manage") ||
               permissionChecker.hasCurrentUserPermission("system.settings.manage");
    }
    
    /**
     * 檢查是否可以操作指定會員的頭像
     */
    private boolean canManageAvatar(Integer targetMemberId) {
        if (permissionChecker.hasCurrentUserPermission("user.manage")) {
            return true;
        }
        
        Integer currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        // 可以管理自己的頭像
        return currentUserId.equals(targetMemberId);
    }
    
    // ===== API 端點 =====
    
    @GetMapping("/student/all")
    public ResponseEntity<ApiResponse<List<StudentDTO>>> getStudentList(){
    	List<Student> all = studentService.getAllStudent();
    	
    	List<StudentDTO> list = all.stream().map(StudentDTO::fromEntity).collect(Collectors.toList());
    	
    	return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
    }
    
    @GetMapping("/teacher/all")
    public ResponseEntity<ApiResponse<List<TeacherDTO>>> getTeacherList(){
    	List<Teacher> all = teacherService.getAllTeacher();
    	
    	List<TeacherDTO> list = all.stream().map(TeacherDTO::fromEntity).collect(Collectors.toList());
    	
    	return ResponseEntity.ok(ApiResponse.success("查詢成功", list));
    }

    @GetMapping("/list")
    public ResponseEntity<?> getMemberList(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String grade, 
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(required = false) String name,
            HttpSession session) {
    	
//   	 if (!permissionChecker.hasCurrentUserPermission("user.manage")) {
//         return ResponseEntity.status(403).build();
//     }
        
    	try {
            log.debug("會員列表查詢開始 - 搜尋條件: role={}, grade={}, status={}, verified={}, name={}", 
                     role, grade, status, verified, name);
            // 查詢會員列表
            List<Member> allMember = memberService.findByCondition(role, grade, status, verified, name);
            List<Map<String, Object>> result = new ArrayList<>();

            for (Member m : allMember) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("account", m.getAccount());
                map.put("role", m.getRole());
                map.put("name", m.getName());
                
                if (m.getStudent() != null) {
                    map.put("grade", m.getStudent().getGrade());
                } else {
                    map.put("grade", null);
                }
                
                map.put("status", m.getStatus());
                map.put("verified", m.getVerified());
                result.add(map);
            }

            log.info("✅ 查詢成功，共 {} 筆資料", result.size());
            
            return ResponseEntity.ok(ApiResponse.success("查詢成功",result));

        } catch (Exception e) {
            System.err.println("[MemberApiController] 查詢會員列表時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "查詢會員列表時發生錯誤"));
        }
    }
    
    // ===== 頭像相關功能 =====
    
    @PostMapping("/avatar/upload")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("avatar") MultipartFile file,
            @RequestParam(value = "id", required = false) Integer memberId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer currentUserId = getCurrentUserId();
            String authType = userContextUtil.getCurrentAuthType();
            
            if (currentUserId == null) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer targetMemberId = memberId != null ? memberId : currentUserId;
            
            if (!canManageAvatar(targetMemberId)) {
                response.put("success", false);
                response.put("message", "您沒有權限管理此頭像");
                return ResponseEntity.status(403).body(response);
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
            File uploadDir = new File(AVATAR_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // 生成檔案名稱
            String fileExtension = getFileExtension(file.getOriginalFilename());
            String fileName = targetMemberId + "_" + System.currentTimeMillis() + fileExtension;
            String filePath = AVATAR_DIR + fileName;
            
            // 刪除舊頭像
            deleteOldAvatar(targetMemberId);
            
            // 儲存新頭像
            Path targetPath = Paths.get(filePath);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            response.put("success", true);
            response.put("message", "頭像上傳成功");
            response.put("data", Map.of(
                "avatarUrl", "/api/member/avatar?id=" + targetMemberId,
                "timestamp", System.currentTimeMillis(),
                "authType", authType
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 頭像上傳失敗", e);
            response.put("success", false);
            response.put("message", "上傳過程中發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/avatar")
    public ResponseEntity<Resource> getAvatar(
            @RequestParam(value = "id", required = false) Integer memberId,
            HttpSession session) {
        
        try {
            Integer currentUserId = getCurrentUserId();
            Integer targetMemberId = memberId != null ? memberId : currentUserId;
            
            if (targetMemberId == null) {
                return ResponseEntity.status(400).build();
            }
            
            // 查找頭像檔案
            File avatarDir = new File(AVATAR_DIR);
            if (!avatarDir.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            final Integer finalTargetMemberId = targetMemberId;
            File[] avatarFiles = avatarDir.listFiles((dir, name) -> 
                name.startsWith(finalTargetMemberId + "_"));
            
            if (avatarFiles == null || avatarFiles.length == 0) {
                return ResponseEntity.notFound().build();
            }
            
            File avatarFile = avatarFiles[avatarFiles.length - 1];
            Path filePath = avatarFile.toPath();
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
            log.error("❌ 取得頭像失敗", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @DeleteMapping("/avatar/delete")
    public ResponseEntity<Map<String, Object>> deleteAvatar(
            @RequestParam(value = "id", required = false) Integer memberId,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer currentUserId = getCurrentUserId();
            String authType = userContextUtil.getCurrentAuthType();
            
            if (currentUserId == null) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(401).body(response);
            }
            
            Integer targetMemberId = memberId != null ? memberId : currentUserId;
            
            if (!canManageAvatar(targetMemberId)) {
                response.put("success", false);
                response.put("message", "您沒有權限刪除此頭像");
                return ResponseEntity.status(403).body(response);
            }
            
            boolean deleted = deleteOldAvatar(targetMemberId);
            
            response.put("success", true);
            response.put("message", deleted ? "頭像刪除成功" : "沒有找到頭像檔案");
            response.put("authType", authType);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 刪除頭像失敗", e);
            response.put("success", false);
            response.put("message", "刪除過程中發生錯誤: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/avatar/status")
    public ResponseEntity<Map<String, Object>> getAvatarStatus(
            @RequestParam(value = "id", required = false) Integer memberId) {
        
        try {
            Integer currentUserId = getCurrentUserId();
            String authType = userContextUtil.getCurrentAuthType();
            
            if (currentUserId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }
            
            Integer targetMemberId = memberId != null ? memberId : currentUserId;
            
            // 檢查是否有頭像
            File avatarDir = new File(AVATAR_DIR);
            boolean hasAvatar = false;
            String avatarUrl = null;
            
            if (avatarDir.exists()) {
                final Integer finalTargetMemberId = targetMemberId;
                File[] avatarFiles = avatarDir.listFiles((dir, name) -> 
                    name.startsWith(finalTargetMemberId + "_"));
                
                hasAvatar = avatarFiles != null && avatarFiles.length > 0;
                if (hasAvatar) {
                    avatarUrl = "/api/member/avatar?id=" + targetMemberId;
                }
            }
            
            Map<String, Object> response = Map.of(
                "hasAvatar", hasAvatar,
                "avatarUrl", avatarUrl != null ? avatarUrl : "",
                "canManage", canManageAvatar(targetMemberId),
                "authType", authType,
                "currentUserId", currentUserId,
                "targetMemberId", targetMemberId
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 檢查頭像狀態失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "檢查頭像狀態失敗"));
        }
    }
    
    // ===== 輔助方法 =====
    
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
    
    private boolean deleteOldAvatar(Integer memberId) {
        try {
            File avatarDir = new File(AVATAR_DIR);
            if (!avatarDir.exists()) {
                return false;
            }
            
            final Integer finalMemberId = memberId;
            File[] oldFiles = avatarDir.listFiles((dir, name) -> 
                name.startsWith(finalMemberId + "_"));
            
            if (oldFiles != null) {
                for (File oldFile : oldFiles) {
                    oldFile.delete();
                }
                return oldFiles.length > 0;
            }
            
            return false;
        } catch (Exception e) {
            log.error("❌ 刪除舊頭像失敗", e);
            return false;
        }
    }
}