package com.eams.Entity.score.DTO;

import java.math.BigDecimal;

import com.eams.Entity.score.AnswerSheet;
import com.eams.Entity.score.AnswerSheetItem;
import com.eams.Entity.score.Question;

public class AnswerSheetItemDTO {
    private Integer id;
    private Integer sheetId;
    private Integer questionId;
    private Integer seqNo;
    private String  answerJson;
    private BigDecimal points;

    // getter/setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSheetId() { return sheetId; }
    public void setSheetId(Integer sheetId) { this.sheetId = sheetId; }
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
    public String getAnswerJson() { return answerJson; }
    public void setAnswerJson(String answerJson) { this.answerJson = answerJson; }
    

    public BigDecimal getPoints() {
		return points;
	}
	public void setPoints(BigDecimal points) {
		this.points = points;
	}
	public static AnswerSheetItemDTO fromEntity(AnswerSheetItem e) {
        if (e == null) return null;
        AnswerSheetItemDTO dto = new AnswerSheetItemDTO();
        dto.id = e.getId();
        dto.sheetId = (e.getSheet() != null ? e.getSheet().getId() : null);
        dto.questionId = (e.getQuestion() != null ? e.getQuestion().getId() : null);
        dto.seqNo = e.getSeqNo();
        dto.answerJson = e.getAnswerJson();
        dto.points = e.getPoints();
        return dto;
    }

    public AnswerSheetItem toEntity(AnswerSheet sheet, Question question) {
        AnswerSheetItem e = new AnswerSheetItem();
        e.setId(this.id);
        e.setSheet(sheet);
        e.setQuestion(question);
        e.setSeqNo(this.seqNo);
        e.setAnswerJson(this.answerJson);
        e.setPoints(this.points);
        return e;
    }
}
