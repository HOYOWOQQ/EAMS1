package com.eams.Entity.score.DTO;

import com.eams.Entity.score.Question;
import com.eams.Entity.score.QuestionAnswerKey;

public class QuestionAnswerKeyDTO {
    private Integer questionId;
    private String  answerJson;
    private String  extraNotes;

    // getter/setter
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }
    public String getAnswerJson() { return answerJson; }
    public void setAnswerJson(String answerJson) { this.answerJson = answerJson; }
    public String getExtraNotes() { return extraNotes; }
    public void setExtraNotes(String extraNotes) { this.extraNotes = extraNotes; }

    public static QuestionAnswerKeyDTO fromEntity(QuestionAnswerKey e) {
        if (e == null) return null;
        QuestionAnswerKeyDTO dto = new QuestionAnswerKeyDTO();
        dto.questionId = e.getQuestionId();
        dto.answerJson = e.getAnswerJson();
        dto.extraNotes = e.getExtraNotes();
        return dto;
    }

    public QuestionAnswerKey toEntity(Question question) {
        QuestionAnswerKey e = new QuestionAnswerKey();
        e.setQuestion(question);
        e.setAnswerJson(this.answerJson);
        e.setExtraNotes(this.extraNotes);
        return e;
    }
}
