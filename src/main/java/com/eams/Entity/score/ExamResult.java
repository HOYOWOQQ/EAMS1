package com.eams.Entity.score;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.eams.Entity.scholarship.ScholarshipGrant;
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
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="exam_result")
public class ExamResult {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name="score")
    private Integer score;

    @Column(name="rank_in_class")
    private Integer rankInClass;

    @Column(name="comment")
    private String comment;

    @Column(name="reviewed_time")
    private Timestamp reviewedTime;

    @Column(name="create_time")
    private Timestamp createTime;

    @ManyToOne
    @JoinColumn(name="student_id")
    private Student student;
    
    @Column(name = "notified")
    private Boolean notified;
    
    @Column(name = "notify_time")
    private Date notifyTime;
    
    @ManyToOne
    @JoinColumn(name="exam_paper_id")
    @JsonBackReference("paper-results")
    private ExamPaper examPaper;

    @ManyToOne
    @JoinColumn(name="reviewed_by")
    @JsonBackReference
    private Teacher teacher;
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "examResult", cascade = CascadeType.ALL)
    @JsonManagedReference
  	private List<ScholarshipGrant> scholarshipGrant=new LinkedList<ScholarshipGrant>();

    public ExamResult() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getRankInClass() {
        return rankInClass;
    }
    public void setRankInClass(Integer rankInClass) {
        this.rankInClass = rankInClass;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getReviewedTime() {
        return reviewedTime;
    }
    public void setReviewedTime(Timestamp reviewedTime) {
        this.reviewedTime = reviewedTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Student getStudent() {
        return student;
    }
    public void setStudent(Student student) {
        this.student = student;
    }

    public ExamPaper getExamPaper() {
        return examPaper;
    }
    public void setExamPaper(ExamPaper examPaper) {
        this.examPaper = examPaper;
    }

    public Teacher getTeacher() {
        return teacher;
    }
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    
    
    public Boolean getNotified() {
		return notified;
	}

	public void setNotified(Boolean notified) {
		this.notified = notified;
	}

	public Date getNotifyTime() {
		return notifyTime;
	}

	public void setNotifyTime(Date notifyTime) {
		this.notifyTime = notifyTime;
	}

	@Transient
    public String getStudentName() {
        return student != null && student.getMember() != null ? student.getMember().getName() : null;
    }

    @Transient
    public String getExamPaperName() {
        return examPaper != null ? examPaper.getName() : null;
    }

    @Transient
    public String getReviewerName() {
        return teacher != null && teacher.getMember() != null ? teacher.getMember().getName() : null;
    }

    @Override
    public String toString() {
        return "ExamResult [id=" + id + ", student=" + getStudentName() + ", examPaper=" + getExamPaperName() +
               ", score=" + score + ", rankInClass=" + rankInClass + "]";
    }
}
