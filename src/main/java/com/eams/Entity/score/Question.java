package com.eams.Entity.score;

import jakarta.persistence.*;
import java.sql.Timestamp;

import com.eams.Entity.course.Subject;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 科目 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @JsonBackReference("subject-questions")
    private Subject subject;

    /** 題型（中文：單選題／多選題…） → 對應資料表 q_type */
    @Column(name = "q_type", length = 50, nullable = false)
    private String qType;

    /** 科目名稱（對應資料表 type；你資料表有此欄且不可為 NULL） */
    @Column(name = "type", length = 50, nullable = false)
    private String type;

    /** 題幹 */
    @Column(name = "stem", columnDefinition = "NVARCHAR(MAX)")
    private String stem;

    /** 選項（JSON） */
    @Column(name = "options_json", columnDefinition = "NVARCHAR(MAX)")
    private String optionsJson;

    /** 預設配分（可選） */
    @Column(name = "points")
    private Integer points;

    // getter/setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getQType() { return qType; }
    public void setQType(String qType) { this.qType = qType; }

    public String getType() { return type; }           // ← 科目名稱
    public void setType(String type) { this.type = type; }

    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }

    public String getOptionsJson() { return optionsJson; }
    public void setOptionsJson(String optionsJson) { this.optionsJson = optionsJson; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    
}
