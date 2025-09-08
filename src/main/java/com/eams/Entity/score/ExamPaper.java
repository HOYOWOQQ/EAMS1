package com.eams.Entity.score;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.eams.Entity.course.Course;
import com.eams.Entity.course.Subject;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "exam_paper")
public class ExamPaper {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                    // 考卷ID
    
    @Column(name = "name")
    private String name;               // 考卷名稱
    
    @Column(name = "exam_type")
    private String examType;           // 考試類型（期中、段考等）
    
    @Column(name = "exam_date")
    private Date examDate;             // 考試日期
    
    @Column(name = "total_score")
    private BigDecimal totalScore;     // 滿分
    
    @Column(name = "description")
    private String description;        // 補充說明
    
    @CreationTimestamp
    @Column(name="create_time", updatable = false)
    private Timestamp createTime;      // 建立時間
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @JsonBackReference("course-examPapers")
    private Course course;             // 對應 Course
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    @JsonBackReference("exam-examPapers") // 對應Exam
    private Exam exam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;		//對應subject
    
     @OneToOne(mappedBy = "examPaper", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
     @JsonManagedReference("paper-qsheet")
     private QuestionSheet questionSheet;

     @OneToOne(mappedBy = "examPaper", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
     @JsonManagedReference("paper-asheet")
     private AnswerSheet answerSheet;
    
    @OneToMany(
        mappedBy = "examPaper",
        cascade = { CascadeType.PERSIST, CascadeType.MERGE },
        fetch = FetchType.EAGER
    )
    @JsonManagedReference("paper-results")
    private List<ExamResult> examResults = new LinkedList<>();
    
    
    @OneToMany(mappedBy = "examPaper", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonManagedReference("examPaper-attempts")
    private List<ExamAttempt> attempts = new ArrayList<>();
    public ExamPaper() { }

    // ===== Getter / Setter =====
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public Date getExamDate() { return examDate; }
    public void setExamDate(Date examDate) { this.examDate = examDate; }

    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public QuestionSheet getQuestionSheet() { return questionSheet; }
    public void setQuestionSheet(QuestionSheet questionSheet) { this.questionSheet = questionSheet; }

    public AnswerSheet getAnswerSheet() { return answerSheet; }
    public void setAnswerSheet(AnswerSheet answerSheet) { this.answerSheet = answerSheet; }

    public List<ExamResult> getExamResults() { return examResults; }
    public void setExamResults(List<ExamResult> examResults) { this.examResults = examResults; }

    @Override
    public String toString() {
       
        return "ExamPaper{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", examType='" + examType + '\'' +
                ", examDate=" + examDate +
                ", totalScore=" + totalScore +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                '}';
    }
    
}
