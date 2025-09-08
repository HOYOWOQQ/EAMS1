package com.eams.Controller.member;

import com.eams.Entity.member.Member;
import com.eams.Entity.member.Teacher;
import com.eams.Service.member.MemberService;
import com.eams.Service.member.TeacherService;
import com.eams.common.Security.Services.MemberRoleService;
import com.eams.common.Security.Services.PermissionChecker;
import com.eams.common.Security.entity.MemberRole;
import com.eams.Service.member.EmailService;
import com.eams.utils.TokenUtil;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TeacherService teacherService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private MemberRoleService memberRoleService;
    
    @Autowired
    private PermissionChecker permissionChecker;
    
    @GetMapping
    public String loginPage() {
        // 顯示登入頁
        return "auth/login"; // 對應 /views/auth/login.jsp
    }

    @PostMapping
    @ResponseBody
    public String login(@RequestParam String account,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
    	
        Member m = memberService.login(account, password);

        if (m == null) {
            model.addAttribute("inputAccount", account);
            return "帳號或密碼錯誤";
        }

        if (!m.isStatus()) {
            model.addAttribute("inputAccount", account);
            return "帳號已停用，請聯絡管理員";
        }
        
        // 檢查帳號是否已驗證信箱
        if (!m.isVerified()) {
            System.out.println("[LoginController] 登入失敗 - 帳號未驗證: " + account);
            model.addAttribute("inputAccount", account);
            return "帳號尚未驗證信箱，請完成信箱驗證後再登入";
        }

        session.setAttribute("id", m.getId());
        session.setAttribute("name", m.getName());
        session.setAttribute("firstLogin", m.getLastPwdChange() == null);

        // 判斷是否為主任
        if ("teacher".equals(m.getRole())) {
            Teacher teacher = teacherService.getTeacherById(m.getId());
            if (teacher != null && "主任".equals(teacher.getPosition())) {
                session.setAttribute("position", "主任");
            } else {
                session.setAttribute("position", "老師");
            }
        }

        session.setAttribute("UserAccount", account);
        session.setAttribute("member", m);
        session.setAttribute("role", m.getRole());

        if ("student".equals(m.getRole())) {
            session.setAttribute("isStudent", true);
            session.setAttribute("studentId", m.getId());
        } else if ("teacher".equals(m.getRole())) {
            session.setAttribute("isTeacher", true);
            session.setAttribute("teacherId", m.getId());
        } else {
            session.setAttribute("unknow", true);
        }
        
        try {
            List<MemberRole> userRoles = memberRoleService.getUserActiveRoles(m.getId());
            List<String> permissions = permissionChecker.getUserPermissionsAsync(m.getId()).join();
            
            // 存儲動態權限資訊
            session.setAttribute("userRoles", userRoles);
            session.setAttribute("userPermissions", permissions);
            
            System.out.println("[LoginController] 用戶 " + account + " 登入成功，擁有 " + 
                              userRoles.size() + " 個角色，" + permissions.size() + " 個權限");
        } catch (Exception e) {
            System.err.println("[LoginController] 載入用戶權限失敗: " + e.getMessage());
            // 權限載入失敗不影響基本登入功能
        }

        return "登入成功";
    }
}