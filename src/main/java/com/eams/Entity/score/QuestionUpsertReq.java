// com.eams.Entity.score.QuestionUpsertReq
package com.eams.Entity.score;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 建/改題目用的簡單請求體 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionUpsertReq {
    private Integer subjectId;     // 可空；若空則用 subjectName 反查
    private String  subjectName;   // 科目中文名（可空）
    private String  type;          // 題型中文（單選題…）-- 對應 Question.type
    private String  stem;          // 題幹
    private List<String> options;  // 選項（A/B/C/D…，自由長度≥4）
    private String  answerLabel;   // 正確選項（A/B/C/D…）
    private Integer points;        // 可空
    
    private String  explanation;   // ← 加上這個（可為 null）
    // ---- getters / setters ----
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStem() { return stem; }
    public void setStem(String stem) { this.stem = stem; }
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    public String getAnswerLabel() { return answerLabel; }
    public void setAnswerLabel(String answerLabel) { this.answerLabel = answerLabel; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
