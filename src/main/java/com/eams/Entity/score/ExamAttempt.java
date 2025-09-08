package com.eams.Entity.score;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.eams.Entity.member.Student;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@Table(name = "exam_attempt")
public class ExamAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 對應考卷 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false)
    @JsonIgnore
    private ExamPaper examPaper;

    /** 學生 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    /** 狀態 STARTED / SUBMITTED… */
    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "submit_time")
    private Timestamp submitTime;

    /** 系統自動評分 */
    @Column(name = "auto_score", precision = 6, scale = 2)
    private BigDecimal autoScore;
    
    
    @OneToMany(
    	    mappedBy = "attempt",
    	    cascade = { CascadeType.PERSIST, CascadeType.MERGE },
    	    orphanRemoval = true,
    	    fetch = FetchType.LAZY
    	)
    	@JsonManagedReference("attempt-answers")   
    	private List<ExamAttemptAnswer> answers = new ArrayList<>();
    
    @PrePersist
    public void prePersist() {
        if (this.status == null || this.status.isBlank()) {
            this.status = "未提交";
        }
    }
    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ExamPaper getExamPaper() { return examPaper; }
    public void setExamPaper(ExamPaper examPaper) { this.examPaper = examPaper; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getSubmitTime() { return submitTime; }
    public void setSubmitTime(Timestamp submitTime) { this.submitTime = submitTime; }

    public BigDecimal getAutoScore() { return autoScore; }
    public void setAutoScore(BigDecimal autoScore) { this.autoScore = autoScore; }

    public List<ExamAttemptAnswer> getAnswers() { return answers; }
    public void setAnswers(List<ExamAttemptAnswer> answers) { this.answers = answers; }
}
