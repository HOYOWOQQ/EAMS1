package com.eams.Entity.score.DTO;

import java.sql.Timestamp;
import java.util.Date;

import com.eams.Entity.score.Exam;

public class ExamDTO {

    private Integer id;
    private String name;
    private String type;
    private String description;
    private Date examDate;
    private Timestamp createTime;

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

    // ===== Mapping Helpers =====
    public static ExamDTO fromEntity(Exam entity) {
        if (entity == null) return null;
        ExamDTO dto = new ExamDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDescription(entity.getDescription());
        dto.setExamDate(entity.getExamDate());
        dto.setCreateTime(entity.getCreateTime());
        return dto;
    }

    public Exam toEntity() {
        Exam e = new Exam();
        e.setId(this.id);
        e.setName(this.name);
        e.setType(this.type);
        e.setDescription(this.description);
        e.setExamDate(this.examDate);
        // createTime 交給 DB 自動填
        return e;
    }
}
