package com.eams.Entity.notice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoticeCreateDTO {
    @NotNull(message = "課程ID不能為空")
    private Integer courseId;
    
    @NotBlank(message = "標題不能為空")
    @Size(min = 5, max = 200, message = "標題長度必須在5-200字之間")
    private String title;
    
    @NotBlank(message = "內容不能為空")
    @Size(min = 10, message = "內容至少需要10個字符")
    private String content;
    
    // 建構子
    public NoticeCreateDTO() {}
    
    public NoticeCreateDTO(Integer courseId, String title, String content) {
        this.courseId = courseId;
        this.title = title;
        this.content = content;
    }
    
    // Getter 和 Setter
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
