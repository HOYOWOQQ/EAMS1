package com.eams.Entity.score;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "question_sheet")
public class QuestionSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 對應考卷（唯一） */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", unique = true, nullable = false)
    @JsonBackReference("paper-qsheet")   // ★ 與 ExamPaper.questionSheet 成對
    private ExamPaper examPaper;

    @Column(name = "title", length = 255)
    private String title;

    /** 建立時間（DB 自動帶） */
    @CreationTimestamp
    @Column(name = "created_time", nullable = false, updatable = false)
    private Timestamp createdTime;

    /** 題目明細 */
    @OneToMany(mappedBy = "sheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("qsheet-items") // ★ 與 QuestionSheetItem.sheet 成對
    private List<QuestionSheetItem> items;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ExamPaper getExamPaper() { return examPaper; }
    public void setExamPaper(ExamPaper examPaper) { this.examPaper = examPaper; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Timestamp getCreatedTime() { return createdTime; }
    public void setCreatedTime(Timestamp createdTime) { this.createdTime = createdTime; }

    public List<QuestionSheetItem> getItems() { return items; }
    public void setItems(List<QuestionSheetItem> items) { this.items = items; }
}
