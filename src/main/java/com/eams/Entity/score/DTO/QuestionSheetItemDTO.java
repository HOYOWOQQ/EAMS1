package com.eams.Entity.score.DTO;

import java.math.BigDecimal;

import com.eams.Entity.score.Question;
import com.eams.Entity.score.QuestionSheetItem;

public class QuestionSheetItemDTO {
    private Integer id;
    private Integer seqNo;
    private BigDecimal points;
    private QuestionDTO question;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }

    public BigDecimal getPoints() { return points; }
    public void setPoints(BigDecimal points) { this.points = points; }

    public QuestionDTO getQuestion() { return question; }
    public void setQuestion(QuestionDTO question) { this.question = question; }

    // ===== Mapping =====
    public static QuestionSheetItemDTO fromEntity(QuestionSheetItem i) {
        if (i == null) return null;
        QuestionSheetItemDTO dto = new QuestionSheetItemDTO();
        dto.id = i.getId();
        dto.seqNo = i.getSeqNo();
        dto.points = i.getPoints();
        dto.question = QuestionDTO.fromEntity(i.getQuestion());
        return dto;
    }

    public QuestionSheetItem toEntity(Question q) {
        QuestionSheetItem i = new QuestionSheetItem();
        i.setId(this.id);
        i.setSeqNo(this.seqNo);
        i.setPoints(this.points);
        i.setQuestion(q);
        return i;
    }
}
