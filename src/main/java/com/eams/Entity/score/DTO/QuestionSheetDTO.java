package com.eams.Entity.score.DTO;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import com.eams.Entity.score.ExamPaper;
import com.eams.Entity.score.QuestionSheet;
import com.eams.Entity.score.QuestionSheetItem;

public class QuestionSheetDTO {
    private Integer id;
    private Integer examPaperId;
    private String  title;
    private Timestamp createdTime;
    private List<QuestionSheetItemDTO> items;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getExamPaperId() { return examPaperId; }
    public void setExamPaperId(Integer examPaperId) { this.examPaperId = examPaperId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Timestamp getCreatedTime() { return createdTime; }
    public void setCreatedTime(Timestamp createdTime) { this.createdTime = createdTime; }

    public List<QuestionSheetItemDTO> getItems() { return items; }
    public void setItems(List<QuestionSheetItemDTO> items) { this.items = items; }

    // ===== Mapping =====
    public static QuestionSheetDTO fromEntity(QuestionSheet e) {
        if (e == null) return null;
        QuestionSheetDTO dto = new QuestionSheetDTO();
        dto.id          = e.getId();
        dto.examPaperId = (e.getExamPaper() != null ? e.getExamPaper().getId() : null);
        dto.title       = e.getTitle();
        dto.createdTime = e.getCreatedTime();

        if (e.getItems() != null) {
            dto.items = e.getItems().stream()
                    .map(QuestionSheetItemDTO::fromEntity)
                    .collect(Collectors.toList());
        }
        return dto;
    }

    public QuestionSheet toEntity(ExamPaper paper, List<QuestionSheetItem> items) {
        QuestionSheet e = new QuestionSheet();
        e.setId(this.id);
        e.setExamPaper(paper);
        e.setTitle(this.title);
        e.setCreatedTime(this.createdTime);
        e.setItems(items);
        return e;
    }
}
