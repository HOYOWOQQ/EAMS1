package com.eams.Entity.notice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentCreateDTO {
    @NotNull(message = "通知ID不能為空")
    private Integer noticeId;
    
    @NotBlank(message = "留言內容不能為空")
    @Size(min = 1, max = 1000, message = "留言內容不能超過1000字")
    private String content;
    
    private Integer parentCommentId;
    
    // 建構子
    public CommentCreateDTO() {}
    
    public CommentCreateDTO(Integer noticeId, String content, Integer parentCommentId) {
        this.noticeId = noticeId;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }
    
    // Getter 和 Setter
    public Integer getNoticeId() { return noticeId; }
    public void setNoticeId(Integer noticeId) { this.noticeId = noticeId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Integer getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Integer parentCommentId) { this.parentCommentId = parentCommentId; }
}
