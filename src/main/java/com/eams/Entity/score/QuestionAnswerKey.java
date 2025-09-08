package com.eams.Entity.score;

import jakarta.persistence.*;

@Entity
@Table(name = "question_answer_key")
public class QuestionAnswerKey {

    /** PK = FK question_id */
    @Id
    @Column(name = "question_id")
    private Integer questionId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "question_id", referencedColumnName = "id")
    private Question question;

    /** 標準答案（JSON） */
    @Column(name = "answer_json", columnDefinition = "NVARCHAR(MAX)")
    private String answerJson;

    @Column(name = "extra_notes", length = 500)
    private String extraNotes;

    // getter/setter
    public Integer getQuestionId() { return questionId; }
    public void setQuestionId(Integer questionId) { this.questionId = questionId; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getAnswerJson() { return answerJson; }
    public void setAnswerJson(String answerJson) { this.answerJson = answerJson; }

    public String getExtraNotes() { return extraNotes; }
    public void setExtraNotes(String extraNotes) { this.extraNotes = extraNotes; }
}
