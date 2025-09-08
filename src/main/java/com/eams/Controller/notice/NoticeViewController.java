package com.eams.Controller.notice;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 在現有的 @RestController 類之外，創建一個新的 Controller 類
@Controller
@RequestMapping("/notice")
public class NoticeViewController {
    
    // 顯示通知列表頁面
    @GetMapping("/list")
    public String showNoticeList() {
        return "notice/notice-list"; // 返回 JSP 頁面路徑
    }
    
    // 顯示通知詳情頁面
    @GetMapping("/detail")
    public String showNoticeDetail() {
        return "notice/notice-detail"; // 返回通知詳情 JSP 頁面
    }
    
    // 顯示創建通知頁面
    @GetMapping("/create")
    public String showCreateNotice() {
        return "notice/create-notice"; // 返回創建通知 JSP 頁面
    }
    
    // 顯示編輯通知頁面
    @GetMapping("/edit")
    public String showEditNotice() {
        return "notice/edit-notice"; // 返回編輯通知 JSP 頁面
    }
}