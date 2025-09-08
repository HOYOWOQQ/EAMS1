package com.eams.Entity.score;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "answer_sheet_item")
public class AnswerSheetItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sheet_id", nullable = false)
	@JsonBackReference("asheet-items")
	private AnswerSheet sheet;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	@JsonIgnore
	private Question question;

	@Column(name = "seq_no", nullable = false)
	private Integer seqNo;

	/** 標準答案（JSON 快照） */
	@Column(name = "answer_json", columnDefinition = "NVARCHAR(MAX)")
	private String answerJson;

	@Column(name = "points", precision = 5, scale = 2)
	private BigDecimal points;

	// getter/setter
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public AnswerSheet getSheet() {
		return sheet;
	}

	public void setSheet(AnswerSheet sheet) {
		this.sheet = sheet;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public Integer getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public String getAnswerJson() {
		return answerJson;
	}

	public void setAnswerJson(String answerJson) {
		this.answerJson = answerJson;
	}

	public BigDecimal getPoints() {
		return points;
	}

	public void setPoints(BigDecimal points) {
		this.points = points;
	}
}
