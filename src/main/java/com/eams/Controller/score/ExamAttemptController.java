package com.eams.Controller.score;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.eams.Entity.score.ExamAttempt;
import com.eams.Entity.score.ExamAttemptAnswer;
import com.eams.Entity.score.ExamPaper;
import com.eams.Entity.score.DTO.ExamAttemptAnswerDTO;
import com.eams.Entity.score.DTO.ExamAttemptDTO;
import com.eams.Repository.course.CourseEnrollRepository;
import com.eams.Repository.score.ExamAttemptAnswerRepository;
import com.eams.Repository.score.ExamAttemptRepository;
import com.eams.Repository.score.ExamPaperRepository;
import com.eams.Service.score.ExamAttemptService;
import com.eams.utils.ScoreAccessGuard;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/exam-attempt")
public class ExamAttemptController {

    @Autowired private ExamAttemptService attemptService;
    @Autowired private ExamAttemptRepository attemptRepo;
    @Autowired private ExamAttemptAnswerRepository answerRepo;
    @Autowired private CourseEnrollRepository enrollRepo;
    @Autowired private ExamPaperRepository examPaperRepo;
    @Autowired private com.eams.Service.score.QuestionBankService questionBankService;
    
    @Autowired private ScoreAccessGuard guard;

    /* ===== 小工具（統一從 Guard 來）===== */
    private Integer me() { return guard.currentUserIdOr403(); }
    private boolean isStudent() { return guard.isStudentOrParent(); }
    private boolean isTeacher() { return guard.isTeacher(); }
    private boolean isDirector() { return guard.isDirectorOrAbove(); }

    
    private boolean paperHasQuestions(Integer examPaperId) {
        var sheet = questionBankService.getSheetByExamPaperId(examPaperId);
        return sheet != null && sheet.getItems() != null && !sheet.getItems().isEmpty();
    }
    /** 驗證：學生是否有修這張考卷所屬的課程（否則 403） */
    private void assertStudentEnrolledThisPaper(Integer studentId, Integer examPaperId) {
        ExamPaper paper = examPaperRepo.findById(examPaperId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到考卷"));
        if (paper.getCourse() == null || paper.getCourse().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "考卷未綁定課程");
        }
        Integer courseId = paper.getCourse().getId();

        boolean ok = enrollRepo.existsByCourse_IdAndStudent_Id(courseId, studentId);
        if (!ok) {
            // 便於快速定位
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "無權存取此課程的考試 (courseId=" + courseId + ", studentId=" + studentId + ")");
        }
    }

    /** 驗證：目前學生是否為此 attempt 的擁有者（否則 403） */
    private void assertOwnAttemptIfStudent(Integer attemptId) {
        if (!isStudent()) return; // 老師/主任以上放行（會在其他地方做歸屬檢查）
        Integer myId = me();
        ExamAttempt a = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到作答紀錄"));
        if (!a.getStudent().getId().equals(myId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "不可操作他人的作答紀錄");
        }
    }

    /** 開始作答：建立 attempt */
    @PostMapping("/start")
    @Transactional
    public ResponseEntity<StartAttemptResp> start(@RequestBody StartAttemptReq req) {
        Integer myId = me();
        Integer paperId = req.getExamPaperId();

        if (isStudent()) {
            assertStudentEnrolledThisPaper(myId, paperId);
        } else {
            if (req.getStudentId() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentId 必填");
            if (isTeacher()) guard.assertTeacherOwnsExamPaper(me(), paperId);
            assertStudentEnrolledThisPaper(req.getStudentId(), paperId);
        }

        // ★ 沒有題目卷 or 題目數=0 一律擋下
        if (!paperHasQuestions(paperId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "此考卷尚未建立題目卷，無法開始作答");
        }

        Integer sid = isStudent() ? myId : req.getStudentId();
        Integer id  = attemptService.start(sid, paperId);

        StartAttemptResp resp = new StartAttemptResp();
        resp.setAttemptId(id);
        resp.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
        return ResponseEntity.ok(resp);
    }

    /** 途中儲存答案（可重複呼叫） */
    @PostMapping("/{attemptId}/answer")
    @Transactional
    public ResponseEntity<Void> saveAnswer(@PathVariable Integer attemptId,
                                           @RequestBody SaveAnswerReq req) {
        // 學生只能改自己；老師/主任以上需要能觸及這次作答的考卷
        assertOwnAttemptIfStudent(attemptId);
        if (!isStudent()) {
            Integer myId = me();
            Integer paperId = attemptRepo.findPaperIdByAttemptId(attemptId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到作答紀錄"));
            if (isTeacher()) guard.assertTeacherOwnsExamPaper(myId, paperId);
            // 主任以上放行
        }
        attemptService.saveAnswer(attemptId, req.getSeqNo(), req.getSelectedOption());
        return ResponseEntity.ok().build();
    }

    /** 交卷：自動評分 + upsert 成績 + 排名 */
    @PostMapping("/{attemptId}/submit")
    @Transactional
    public ResponseEntity<SubmitResp> submit(@PathVariable Integer attemptId) {
        // 學生只能交自己的；老師/主任以上同 saveAnswer 的邏輯
        assertOwnAttemptIfStudent(attemptId);
        if (!isStudent()) {
            Integer myId = me();
            Integer paperId = attemptRepo.findPaperIdByAttemptId(attemptId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到作答紀錄"));
            if (isTeacher()) guard.assertTeacherOwnsExamPaper(myId, paperId);
        }
        BigDecimal total = new BigDecimal(attemptService.submit(attemptId));
        SubmitResp resp = new SubmitResp();
        resp.setAttemptId(attemptId);
        resp.setTotalScore(total);
        return ResponseEntity.ok(resp);
    }

    /** 單次作答資訊 */
    @GetMapping("/{attemptId}")
    @Transactional
    public ResponseEntity<ExamAttemptDTO> getAttempt(@PathVariable Integer attemptId) {
        assertOwnAttemptIfStudent(attemptId);
        if (!isStudent()) {
            Integer myId = me();
            Integer paperId = attemptRepo.findPaperIdByAttemptId(attemptId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到作答紀錄"));
            if (isTeacher()) guard.assertTeacherOwnsExamPaper(myId, paperId);
        }
        ExamAttempt a = attemptRepo.findById(attemptId).orElseThrow();
        return ResponseEntity.ok(ExamAttemptDTO.fromEntity(a));
    }

    /** 單次作答的答案列表（依題號） */
    @GetMapping("/{attemptId}/answers")
    @Transactional
    public ResponseEntity<List<ExamAttemptAnswerDTO>> listAnswers(@PathVariable Integer attemptId) {
        assertOwnAttemptIfStudent(attemptId);
        if (!isStudent()) {
            Integer myId = me();
            Integer paperId = attemptRepo.findPaperIdByAttemptId(attemptId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到作答紀錄"));
            if (isTeacher()) guard.assertTeacherOwnsExamPaper(myId, paperId);
        }
        List<ExamAttemptAnswer> list = answerRepo.findByAttempt_IdOrderBySeqNoAsc(attemptId);
        return ResponseEntity.ok(list.stream().map(ExamAttemptAnswerDTO::fromEntity).toList());
    }

    /** 查某卷所有作答 */
    @GetMapping("/papers/{paperId}/attempts")
    public ResponseEntity<List<ExamAttemptDTO>> listAttemptsOfPaper(@PathVariable Integer paperId) {
        if (isStudent()) {
            Integer myId = me();
            assertStudentEnrolledThisPaper(myId, paperId);
            var list = attemptRepo
                    .findByExamPaper_IdAndStudent_IdOrderByStartTimeDesc(paperId, myId)
                    .stream().map(ExamAttemptDTO::fromEntity).toList();
            return ResponseEntity.ok(list);
        } else {
            Integer myId = me();
            if (isTeacher()) guard.assertTeacherOwnsExamPaper(myId, paperId);
            // 主任以上：全部
            var list = attemptRepo
                    .findByExamPaper_IdOrderByStartTimeDesc(paperId)
                    .stream().map(ExamAttemptDTO::fromEntity).toList();
            return ResponseEntity.ok(list);
        }
    }

    /** 查某學生「在某張考卷」最新一次作答（學生/老師/主任用） */
    @GetMapping("/students/{studentId}/latest")
    public ResponseEntity<ExamAttemptDTO> latestOfStudentOnPaper(
            @PathVariable Integer studentId,
            @RequestParam Integer paperId) {

        Integer myId = me();

        if (isStudent()) {
            // 忽略路徑 studentId，改用本人
            assertStudentEnrolledThisPaper(myId, paperId);
            var opt = attemptRepo.findTopByExamPaper_IdAndStudent_IdOrderByStartTimeDesc(paperId, myId);
            return ResponseEntity.ok(opt.map(ExamAttemptDTO::fromEntity).orElse(null));
        } else {
            // 老師：需為該考卷任課老師；主任以上放行
            if (isTeacher()) guard.assertTeacherOwnsExamPaper(myId, paperId);
            var opt = attemptRepo.findTopByExamPaper_IdAndStudent_IdOrderByStartTimeDesc(paperId, studentId);
            return ResponseEntity.ok(opt.map(ExamAttemptDTO::fromEntity).orElse(null));
        }
    }

    /** 學生查看自己在某張考卷的最新一次作答（簡化版） */
    @GetMapping("/me/latest")
    public ResponseEntity<ExamAttemptDTO> myLatestOnPaper(@RequestParam Integer paperId) {
        Integer myId = me();
        assertStudentEnrolledThisPaper(myId, paperId);
        var opt = attemptRepo.findTopByExamPaper_IdAndStudent_IdOrderByStartTimeDesc(paperId, myId);
        return ResponseEntity.ok(opt.map(ExamAttemptDTO::fromEntity).orElse(null));
    }

    /* ======= Request/Response VO ======= */

    public static class StartAttemptReq {
        private Integer studentId;
        private Integer examPaperId;
        public Integer getStudentId() { return studentId; }
        public void setStudentId(Integer studentId) { this.studentId = studentId; }
        public Integer getExamPaperId() { return examPaperId; }
        public void setExamPaperId(Integer examPaperId) { this.examPaperId = examPaperId; }
    }

    public static class SaveAnswerReq {
        private Integer seqNo;
        private String selectedOption; // A/B/C/D
        public Integer getSeqNo() { return seqNo; }
        public void setSeqNo(Integer seqNo) { this.seqNo = seqNo; }
        public String getSelectedOption() { return selectedOption; }
        public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    }

    public static class StartAttemptResp {
        private Integer attemptId;
        private Timestamp startTime;
        public Integer getAttemptId() { return attemptId; }
        public void setAttemptId(Integer attemptId) { this.attemptId = attemptId; }
        public Timestamp getStartTime() { return startTime; }
        public void setStartTime(Timestamp startTime) { this.startTime = startTime; }
    }

    public static class SubmitResp {
        private Integer attemptId;
        private BigDecimal totalScore;
        public Integer getAttemptId() { return attemptId; }
        public void setAttemptId(Integer attemptId) { this.attemptId = attemptId; }
        public BigDecimal getTotalScore() { return totalScore; }
        public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    }
}
