package com.eams.Entity.score.DTO;

import com.eams.Entity.score.AnswerSheet;
import com.eams.Entity.score.ExamPaper;

public class AnswerSheetDTO {
    private Integer id;
    private Integer examPaperId;
    private String  title;

    // getter/setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getExamPaperId() { return examPaperId; }
    public void setExamPaperId(Integer examPaperId) { this.examPaperId = examPaperId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public static AnswerSheetDTO fromEntity(AnswerSheet e) {
        if (e == null) return null;
        AnswerSheetDTO dto = new AnswerSheetDTO();
        dto.id = e.getId();
        dto.examPaperId = (e.getExamPaper() != null ? e.getExamPaper().getId() : null);
        dto.title = e.getTitle();
        return dto;
    }

    public AnswerSheet toEntity(ExamPaper paper) {
        AnswerSheet e = new AnswerSheet();
        e.setId(this.id);
        e.setExamPaper(paper);
        e.setTitle(this.title);
        return e;
    }
}
