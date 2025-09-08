package com.eams.Entity.score.DTO;

import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Objects;

import com.eams.Entity.score.ExamPaper;

// 如果專案有 lombok，保留 @Data / @NoArgsConstructor / @AllArgsConstructor
@lombok.Data
@lombok.NoArgsConstructor
public class ExamPaperDTO {

    private int id;                       // 考卷ID
    private String name;                  // 考卷名稱
    private String examType;              // 考試類型
    @com.fasterxml.jackson.annotation.JsonFormat(pattern = "yyyy-MM-dd")
    private Date examDate;                // 考試日期
    private BigDecimal totalScore;        // 滿分
    private String description;           // 補充說明

    private Integer examId;               // 考試ID
    private String  examName;             // ✅ 考試名稱（前端要顯示）
    private Integer subjectId;            // 科目ID
    private String  subjectName;          // ✅ 科目名稱（如需顯示）

    private Integer courseId;             // 課程ID
    private String  courseName;           // 課程名稱
    private Timestamp createTime;         // 建立時間

    // 題目卷摘要（給學生端判斷是否可作答）
    private Integer    totalQuestions;    // ✅ 題數
    private BigDecimal totalPoints;       // ✅ 總配分
    private Boolean    hasQuestionSheet;  // ✅ 是否已有題目卷

    public static ExamPaperDTO fromEntity(ExamPaper e) {
        if (e == null) return null;

        ExamPaperDTO dto = new ExamPaperDTO();
        dto.id          = e.getId();
        dto.name        = e.getName();
        dto.examType    = e.getExamType();
        dto.examDate    = e.getExamDate();
        dto.totalScore  = e.getTotalScore();
        dto.description = e.getDescription();
        dto.createTime  = e.getCreateTime();

        // 課程
        if (e.getCourse() != null) {
            dto.courseId   = e.getCourse().getId();
            dto.courseName = e.getCourse().getName();
        }

        // 考試
        if (e.getExam() != null) {
            dto.examId   = e.getExam().getId();
            dto.examName = e.getExam().getName();
        }

        // 科目
        if (e.getSubject() != null) {
            dto.subjectId   = e.getSubject().getId();
            dto.subjectName = e.getSubject().getName();
        }

        // 題目卷摘要
        // 假設 ExamPaper 有關聯：e.getQuestionSheet()，且內含 items，每個 item 有 getPoints()
        var sheet = (e.getQuestionSheet() != null) ? e.getQuestionSheet() : null;
        if (sheet != null && sheet.getItems() != null && !sheet.getItems().isEmpty()) {
            dto.hasQuestionSheet = true;
            dto.totalQuestions   = sheet.getItems().size();

            BigDecimal sum = BigDecimal.ZERO;
            for (var it : sheet.getItems()) {
                BigDecimal pts = (it.getPoints() == null) ? BigDecimal.ZERO : it.getPoints();
                sum = sum.add(pts);
            }
            dto.totalPoints = sum;
        } else {
            dto.hasQuestionSheet = false;
            dto.totalQuestions   = 0;
            dto.totalPoints      = BigDecimal.ZERO;
        }

        return dto;
    }
}
