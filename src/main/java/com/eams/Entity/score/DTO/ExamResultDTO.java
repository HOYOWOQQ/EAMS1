package com.eams.Entity.score.DTO;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

import com.eams.Entity.course.Subject;
import com.eams.Entity.fee.PaymentItem;
import com.eams.Entity.fee.PaymentNotice;
import com.eams.Entity.member.Member;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.eams.Entity.score.ExamPaper;
import com.eams.Entity.score.ExamResult;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class ExamResultDTO {

    private int id;//

    private Integer score;//

    private Integer rankInClass;//

    private String comment;//

    private Timestamp reviewedTime;//

    private Timestamp createTime;
    private Boolean notified;
    private Date    notifyTime;
    
    private Integer student_id;//

    private Integer exam_paper_id;//

    private Integer reviewed_by;//
    private String examPaperName;
    private String reviewName;
    private String studentName;
   
    public static ExamResultDTO fromEntity(ExamResult entity) {
        if (entity == null) return null;

        ExamResultDTO dto = new ExamResultDTO();
        dto.setId(entity.getId());
        dto.setScore(entity.getScore());
        dto.setRankInClass(entity.getRankInClass());
        dto.setExamPaperName(entity.getExamPaper().getName());
        dto.setComment(entity.getComment());
        dto.setReviewedTime(entity.getReviewedTime());
        dto.setCreateTime(entity.getCreateTime());
        dto.setStudent_id(entity.getStudent().getId());
        dto.setReviewed_by(entity.getTeacher().getId());
        dto.setExam_paper_id(entity.getExamPaper().getId()); 
        dto.setReviewName(entity.getReviewerName());
        dto.setStudentName(entity.getStudentName());
        dto.setNotified(entity.getNotified());
        dto.setNotifyTime(entity.getNotifyTime());
        return dto;
    }
    
    public ExamResult toEntity(ExamResultDTO dto) {
        ExamResult entity = new ExamResult();
        entity.setId(dto.getId());
        entity.setScore(dto.getScore());
        entity.setRankInClass(dto.getRankInClass());
        entity.setComment(dto.getComment());
        entity.setReviewedTime(dto.getReviewedTime());
        entity.setCreateTime(dto.getCreateTime());
        entity.setNotified(dto.getNotified());
        entity.setNotifyTime(dto.getNotifyTime());
        
        // 設定 ExamPaper（只帶 id）
        if (dto.getExam_paper_id() != null) {
            ExamPaper paper = new ExamPaper();
            paper.setId(dto.getExam_paper_id());
            entity.setExamPaper(paper);
        }

        // 設定 Student（只帶 id）
        if (dto.getStudent_id() != null) {
            Student student = new Student(); // 你的學生 Entity
            student.setId(dto.getStudent_id());
            entity.setStudent(student);
        }

        // 設定 Teacher（只帶 id）
        if (this.getReviewed_by() != null) {
            Teacher teacher = new Teacher(); // 你的老師 Entity
            teacher.setId(this.getReviewed_by());
            
            entity.setTeacher(teacher);
        }

        return entity;
    }

   
   
}
