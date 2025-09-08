package com.eams.Entity.score;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "question_sheet_item")
public class QuestionSheetItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 所屬題目卷 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sheet_id", nullable = false)
    @JsonBackReference("qsheet-items")   // ★ 與 QuestionSheet.items 成對
    private QuestionSheet sheet;

    /** 題庫題目（避免序列化展開） */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    /** 題號 */
    @Column(name = "seq_no", nullable = false)
    private Integer seqNo;

    /** 配分 */
    @Column(name = "points", precision = 5, scale = 2)
    private BigDecimal points;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public QuestionSheet getSheet() { return sheet; }
    public void setSheet(QuestionSheet sheet) { this.sheet = sheet; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public Integer getSeqNo() { return seqNo; }
    public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
	public BigDecimal getPoints() {
		return points;
	}
	public void setPoints(BigDecimal points) {
		this.points = points;
	}

    
}
