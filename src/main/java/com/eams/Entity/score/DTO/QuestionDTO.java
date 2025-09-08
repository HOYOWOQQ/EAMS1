package com.eams.Entity.score.DTO;

import com.eams.Entity.course.Subject;
import com.eams.Entity.score.Question;

public class QuestionDTO {
    private Integer id;
    private Integer subjectId;
    private String  type;
    private String  stem;
    private String  optionsJson;
    private Integer points;

    // getter/setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }

    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    // mapping
    public static QuestionDTO fromEntity(Question q) {
        if (q == null) return null;
        QuestionDTO dto = new QuestionDTO();
        dto.id = q.getId();
        dto.subjectId = (q.getSubject() != null ? q.getSubject().getId() : null);
        dto.type = q.getType();
        dto.stem = q.getStem();
        dto.optionsJson = q.getOptionsJson();
        dto.points = q.getPoints();
        return dto;
    }

    public Question toEntity(Subject subject) {
        Question q = new Question();
        q.setId(this.id);
        q.setSubject(subject);
        q.setType(this.type);
        q.setStem(this.stem);
        q.setOptionsJson(this.optionsJson);
        q.setPoints(this.points);
        return q;
    }
}
