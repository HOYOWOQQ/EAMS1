package com.eams.Entity.score;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "answer_sheet")
public class AnswerSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 對應考卷（唯一） */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", unique = true, nullable = false)
    @JsonBackReference("paper-asheet")
    private ExamPaper examPaper;

    @Column(name = "title", length = 255)
    private String title;
    
    @OneToMany(mappedBy = "sheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("asheet-items")      // ★ 加這行
    private List<AnswerSheetItem> items;

    // getter/setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ExamPaper getExamPaper() { return examPaper; }
    public void setExamPaper(ExamPaper examPaper) { this.examPaper = examPaper; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
