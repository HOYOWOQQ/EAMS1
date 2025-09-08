package com.eams.Entity.score;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "exam")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 考試名稱（例：113 上學期段考） */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /** 考試類型（例：段考、模擬考） */
    @Column(name = "type", length = 100)
    private String type;

    /** 說明（選填） */
    @Column(name = "description", length = 255)
    private String description;

    /** 考試日期（整場考試） */
    @Column(name = "exam_date")
    private Date examDate;

    /** 建立時間 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private Timestamp createTime;

    /** 一場考試 -> 多張考卷 */
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "exam-examPapers")
    private List<ExamPaper> examPapers;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getExamDate() { return examDate; }
    public void setExamDate(Date examDate) { this.examDate = examDate; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public List<ExamPaper> getExamPapers() { return examPapers; }
    public void setExamPapers(List<ExamPaper> examPapers) { this.examPapers = examPapers; }
}
