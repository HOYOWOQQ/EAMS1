package com.eams.Entity.notice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CommentUpdateDTO {
    @NotBlank(message = "留言內容不能為空")
    @Size(min = 1, max = 1000, message = "留言內容不能超過1000字")
    private String content;
    
    // 建構子
    public CommentUpdateDTO() {}
    
    public CommentUpdateDTO(String content) {
        this.content = content;
    }
    
    // Getter 和 Setter
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
