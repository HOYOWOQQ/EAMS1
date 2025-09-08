package com.eams.Entity.score.DTO;

import java.math.BigDecimal;

import com.eams.Entity.score.ExamAttempt;
import com.eams.Entity.score.ExamAttemptAnswer;
import com.eams.Entity.score.Question;

public class ExamAttemptAnswerDTO {

    private Integer id;
    private Integer attemptId;
    private Integer questionId;
    private Integer seqNo;
    private String  selectedOption;
    private Boolean isCorrect;
    private BigDecimal points;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getAttemptId() { return attemptId; }
    public void setAttemptId(Integer attemptId) { this.attemptId = attemptId; }
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    public BigDecimal getPoints() { return points; }
    public void setPoints(BigDecimal points) { this.points = points; }

    // ===== Mapping =====
    public static ExamAttemptAnswerDTO fromEntity(ExamAttemptAnswer e) {
        if (e == null) return null;
        ExamAttemptAnswerDTO dto = new ExamAttemptAnswerDTO();
        dto.id             = e.getId();
        dto.attemptId      = (e.getAttempt() != null ? e.getAttempt().getId() : null);
        dto.questionId     = (e.getQuestion() != null ? e.getQuestion().getId() : null);
        dto.seqNo          = e.getSeqNo();
        dto.selectedOption = e.getSelectedOption();
        dto.isCorrect      = e.getIsCorrect();
        dto.points         = e.getPoints();
        return dto;
    }

    public ExamAttemptAnswer toEntity() {
        ExamAttemptAnswer e = new ExamAttemptAnswer();
        e.setId(this.id);
        if (this.attemptId != null) {
            ExamAttempt a = new ExamAttempt();
            a.setId(this.attemptId);
            e.setAttempt(a);
        }
        if (this.questionId != null) {
            Question q = new Question();
            q.setId(this.questionId);
            e.setQuestion(q);
        }
        e.setSeqNo(this.seqNo);
        e.setSelectedOption(this.selectedOption);
        e.setIsCorrect(this.isCorrect);
        e.setPoints(this.points);
        return e;
    }
}
