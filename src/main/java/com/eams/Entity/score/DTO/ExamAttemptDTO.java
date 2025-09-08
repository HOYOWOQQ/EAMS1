package com.eams.Entity.score.DTO;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.eams.Entity.member.Student;
import com.eams.Entity.score.ExamAttempt;
import com.eams.Entity.score.ExamPaper;

public class ExamAttemptDTO {

    private Integer id;
    private Integer examPaperId;
    private Integer studentId;
    private String  status;
    private Timestamp startTime;
    private Timestamp submitTime;
    private BigDecimal autoScore;

    // ===== Getter / Setter =====
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getExamPaperId() { return examPaperId; }
    public void setExamPaperId(Integer examPaperId) { this.examPaperId = examPaperId; }
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    public Timestamp getSubmitTime() { return submitTime; }
    public void setSubmitTime(Timestamp submitTime) { this.submitTime = submitTime; }
    public BigDecimal getAutoScore() { return autoScore; }
    public void setAutoScore(BigDecimal autoScore) { this.autoScore = autoScore; }

    // ===== Mapping =====
    public static ExamAttemptDTO fromEntity(ExamAttempt e) {
        if (e == null) return null;
        ExamAttemptDTO dto = new ExamAttemptDTO();
        dto.id          = e.getId();
        dto.examPaperId = (e.getExamPaper() != null ? e.getExamPaper().getId() : null);
        dto.studentId   = (e.getStudent() != null ? e.getStudent().getId() : null);
        dto.status      = e.getStatus();
        dto.startTime   = e.getStartTime();
        dto.submitTime  = e.getSubmitTime();
        dto.autoScore   = e.getAutoScore();
        return dto;
    }

    /** 不查 DB 的輕量 toEntity（只帶 id 的代理） */
    public ExamAttempt toEntity() {
        ExamAttempt e = new ExamAttempt();
        e.setId(this.id);
        if (this.examPaperId != null) {
            ExamPaper p = new ExamPaper();
            p.setId(this.examPaperId);
            e.setExamPaper(p);
        }
        if (this.studentId != null) {
            Student s = new Student();
            s.setId(this.studentId);
            e.setStudent(s);
        }
        e.setStatus(this.status);
        e.setStartTime(this.startTime);
        e.setSubmitTime(this.submitTime);
        e.setAutoScore(this.autoScore);
        return e;
    }
}
