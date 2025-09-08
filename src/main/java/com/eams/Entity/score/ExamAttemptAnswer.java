package com.eams.Entity.score;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_attempt_answer")
public class ExamAttemptAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	/** 所屬作答 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attempt_id", nullable = false)
	@JsonIgnore
	private ExamAttempt attempt;

	/** 題庫題目（schema 有 question_id） */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	@JsonIgnore
	private Question question;

	/** 題號 */
	@Column(name = "seq_no", nullable = false)
	private Integer seqNo;

	/** 學生選項（單選：A/B/C/D…） */
	@Column(name = "selected_option", length = 1)
	private String selectedOption;

	/** 是否正確 */
	@Column(name = "is_correct")
	private Boolean isCorrect;

	/** 該題得分 */
	@Column(name = "points", precision = 5, scale = 2)
	private BigDecimal points;

	// ===== Getter / Setter =====
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ExamAttempt getAttempt() {
		return attempt;
	}

	public void setAttempt(ExamAttempt attempt) {
		this.attempt = attempt;
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

	public String getSelectedOption() {
		return selectedOption;
	}

	public void setSelectedOption(String selectedOption) {
		this.selectedOption = selectedOption;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public BigDecimal getPoints() {
		return points;
	}

	public void setPoints(BigDecimal points) {
		this.points = points;
	}
}
