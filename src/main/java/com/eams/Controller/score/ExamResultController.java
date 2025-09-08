package com.eams.Controller.score;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.eams.Entity.score.ExamResult;
import com.eams.Entity.score.DTO.ExamResultDTO;
import com.eams.Service.score.ExamResultService;
import com.eams.utils.ScoreAccessGuard;

@RestController
@RequestMapping("/api/exam-results")
public class ExamResultController {

    @Autowired private ExamResultService examResultService;
    @Autowired private ScoreAccessGuard guard;

    /* ==============================
     *            查詢類
     * ============================== */

    /** 查詢所有考試成績
     *  - 主任以上：全部
     *  - 老師：僅自己負責的考卷之成績
     *  - 學生/家長：僅自己的 */
    @GetMapping
    public ResponseEntity<List<ExamResultDTO>> getAllResults() {
        Integer me = guard.currentUserIdOr403();

        if (guard.isDirectorOrAbove()) {
            return ResponseEntity.ok(examResultService.findAllDTO());
        }
        if (guard.isTeacher()) {
            var all = examResultService.findAllDTO();
            guard.filterExamResultsByTeacher(all, me);
            return ResponseEntity.ok(all);
        }
        // 學生或家長
        if (guard.isStudentOrParent()) {
            return ResponseEntity.ok(examResultService.findByStudentIdDTO(me));
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "未授權的角色");
    }

    /** 查某位學生所有成績
     *  - 學生/家長：忽略路徑參數改回傳自己的
     *  - 老師：可看，但只會拿到自己負責之考卷的成績
     *  - 主任以上：可看全部 */
    @GetMapping("/student/{name}")
    public ResponseEntity<List<ExamResultDTO>> getByStudent(@PathVariable String name) {
        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent()) {
            return ResponseEntity.ok(examResultService.findByStudentIdDTO(me));
        }

        var list = examResultService.findByStudentNameDTO(name);
        if (guard.isDirectorOrAbove()) return ResponseEntity.ok(list);

        if (guard.isTeacher()) {
            guard.filterExamResultsByTeacher(list, me);
            return ResponseEntity.ok(list);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "未授權的角色");
    }

    /** 查某張考卷所有成績
     *  - 學生/家長：禁止
     *  - 老師：需為該考卷任課老師
     *  - 主任以上：可看 */
    @GetMapping("/papers/{paperId}")
    public ResponseEntity<List<ExamResultDTO>> listResultsOfPaper(@PathVariable Integer paperId) {
        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長不得檢視整張考卷的成績");

        if (guard.isTeacher()) {
            guard.assertTeacherOwnsExamPaper(me, paperId);
        }
        var list = examResultService.listByPaperId(paperId);
        return ResponseEntity.ok(list);
    }

    /** 查詢所有教師姓名（下拉）
     *  - 限老師/主任以上（學生/家長不需要） */
    @GetMapping("/teachers")
    public ResponseEntity<List<String>> getTeacherNames() {
        guard.currentUserIdOr403();
        guard.assertTeacherOrDirector();
        return ResponseEntity.ok(examResultService.getAllTeacherNames());
    }

    /** 查詢所有考卷名稱（下拉）
     *  - 老師：只回傳自己負責的考卷名稱
     *  - 主任以上：全部
     *  - 學生/家長：不開放 */
    @GetMapping("/papers")
    public ResponseEntity<List<String>> getExamPaperNames() {
        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長無須操作此資源");

        if (guard.isDirectorOrAbove()) {
            return ResponseEntity.ok(examResultService.getAllExamPaperNames());
        }

        // 老師：先拿 DTO（含 paperId/courseId），用 guard 過濾，再取名字
        var all = examResultService.findAllPapersAsDTO(); // 需由 service 回傳含 paperId/courseId 的簡單 DTO
        guard.filterExamPapersByTeacher(all, me);
        var names = all.stream().map(ExamResultService::extractPaperName).toList();
        return ResponseEntity.ok(names);
    }

    /* ==============================
     *            新增/更新/刪除
     * ============================== */

    /** 新增成績（單筆）
     *  - 學生/家長：禁止
     *  - 老師：僅能對自己負責的考卷新增（teacherId 以 JWT 覆寫）
     *  - 主任以上：允許 */
    @PostMapping("/grade")
    public ResponseEntity<ExamResult> addResult(
            @RequestParam String studentName,
            @RequestParam int examPaperId,
            @RequestParam int score,
            @RequestParam int teacherId) {

        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長不可新增成績");

//        if (guard.isTeacher()) {
//            guard.assertTeacherOwnsExamPaper(me, examPaperId);
//            teacherId = me; // 強制以 JWT 身份作為 teacherId
//        }
        ExamResult result = examResultService.addExamResult(studentName, examPaperId, score, teacherId);
        if (result == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(result);
    }

    /** 更新成績（依學生+考卷名）
     *  - 風險高、歸屬難驗證：僅「主任以上」使用
     *  - 老師請改用 PATCH /{resultId} */
    @PutMapping("/update")
    public ResponseEntity<String> updateResult(
            @RequestParam String studentName,
            @RequestParam String examPaperName,
            @RequestParam int score,
            @RequestParam int rankInClass) {

        guard.currentUserIdOr403();
        guard.assertDirectorOrAbove();

        Timestamp now = new Timestamp(new Date().getTime());
        boolean updated = examResultService.updateExamResult(studentName, examPaperName, score, rankInClass, now);
        return updated ? ResponseEntity.ok("更新成功")
                       : ResponseEntity.badRequest().body("查無對應資料");
    }

    /** 局部更新（老師/主任以上用）
     *  - 學生/家長：禁止
     *  - 老師：需為這筆成績所屬考卷之任課老師
     *  - 主任以上：允許 */
    @PatchMapping("/{resultId}")
    public ResponseEntity<Void> updateScore(
            @PathVariable Integer resultId,
            @RequestBody UpdateScoreReq req) {

        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長不可修改成績");

        if (guard.isTeacher()) {
            Integer paperId = examResultService.findPaperIdByResultId(resultId);
            if (paperId == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "成績不存在");
            guard.assertTeacherOwnsExamPaper(me, paperId);
        }

        // 老師：reviewedBy 一律記自己；主任以上：可用請求中的 reviewedBy
        Integer reviewedBy = guard.isTeacher() ? me : req.getReviewedBy();
        examResultService.updateScoreAndRecalcRank(resultId, req.getScore(), req.getComment(), reviewedBy);
        return ResponseEntity.ok().build();
    }

    /** 刪除單筆成績
     *  - 學生/家長：禁止
     *  - 老師：需為該考卷任課老師
     *  - 主任以上：允許 */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteByStudentIdAndPaperId(
            @RequestParam int studentId,
            @RequestParam int examPaperId) {

        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長不可刪除成績");

        if (guard.isTeacher()) {
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
        }

        boolean deleted = examResultService.deleteByStudentIdAndPaperId(studentId, examPaperId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /** 刪除某位學生的全部成績
     *  - 風險高 → 僅主任以上可用 */
    @DeleteMapping("/student/{name}")
    public ResponseEntity<String> deleteAllByStudent(@PathVariable String name) {
        guard.currentUserIdOr403();
        guard.assertDirectorOrAbove();
        int count = examResultService.deleteAllByStudentName(name);
        return ResponseEntity.ok("共刪除 " + count + " 筆成績");
    }

    /* ==============================
     *           批次新增（新版）
     * ============================== */

    /** 批次新增（QueryString 版，以 studentIds 批次建立；若 UI 沒打分數給 0） */
    @PostMapping("/grade/batch")
    @Transactional
    public ResponseEntity<BatchGradeResp> addResultsBatchByIds(
            @RequestParam int examPaperId,
            @RequestParam(required = false) Integer teacherId,
            @RequestParam List<Integer> studentIds,
            @RequestParam(defaultValue = "0") int defaultScore
    ) {
        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長不可新增成績");
//
//        // 老師身份強制覆寫
//        if (guard.isTeacher()) {
//            guard.assertTeacherOwnsExamPaper(me, examPaperId);
//            teacherId = me;
//        }
//        if (teacherId == null)
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teacherId 缺失");

        var resp = examResultService.addExamResultsBatchByIds(examPaperId, teacherId, studentIds, defaultScore);
        return ResponseEntity.ok(resp);
    }

    /** 批次新增（JSON 版：可各自帶分數/評語，studentId 與 studentName 擇一） */
    @PostMapping("/grade/batch-json")
    @Transactional
    public ResponseEntity<BatchGradeResp> addResultsBatchJson(@RequestBody BatchGradeReq req) {
        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長不可新增成績");

        if (guard.isTeacher()) {
            guard.assertTeacherOwnsExamPaper(me, req.getExamPaperId());
            req.setTeacherId(me); // 覆寫
        }
        if (req.getTeacherId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teacherId 缺失");

        var resp = examResultService.addExamResultsBatchJson(req);
        return ResponseEntity.ok(resp);
    }

    /* ==============================
     *      相容舊版（避免 405）
     * ============================== */

    /** 舊版相容：/api/exam-results/batchInsert?teacherId=&examPaperId=&studentIds=1&studentIds=2... */
    @PostMapping("/batchInsert")
    @Transactional
    public ResponseEntity<BatchGradeResp> legacyBatchInsert(
            @RequestParam int teacherId,
            @RequestParam int examPaperId,
            @RequestParam List<Integer> studentIds
    ) {
        Integer me = guard.currentUserIdOr403();

        if (guard.isStudentOrParent())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "學生/家長不可新增成績");

        // 老師身份強制覆寫
        if (guard.isTeacher()) {
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
            teacherId = me;
        }
        var resp = examResultService.addExamResultsBatchByIds(examPaperId, teacherId, studentIds, 0);
        return ResponseEntity.ok(resp);
    }

    /* ==============================
     *           VO / DTO 區
     * ============================== */

    /** PATCH body */
    public static class UpdateScoreReq {
        private Integer score;
        private String comment;
        private Integer reviewedBy;
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public Integer getReviewedBy() { return reviewedBy; }
        public void setReviewedBy(Integer reviewedBy) { this.reviewedBy = reviewedBy; }
    }

    /** 批次新增（JSON）請求體 */
    public static class BatchGradeReq {
        private Integer examPaperId;
        private Integer teacherId;     // 老師登入會被覆寫為自己
        private Integer defaultScore;  // items 沒填 score 時的預設
        private List<Item> items;

        public static class Item {
            private Integer studentId;   // 與 studentName 擇一
            private String  studentName;
            private Integer score;
            private String  comment;

            public Integer getStudentId() { return studentId; }
            public void setStudentId(Integer studentId) { this.studentId = studentId; }
            public String getStudentName() { return studentName; }
            public void setStudentName(String studentName) { this.studentName = studentName; }
            public Integer getScore() { return score; }
            public void setScore(Integer score) { this.score = score; }
            public String getComment() { return comment; }
            public void setComment(String comment) { this.comment = comment; }
        }

        public Integer getExamPaperId() { return examPaperId; }
        public void setExamPaperId(Integer examPaperId) { this.examPaperId = examPaperId; }
        public Integer getTeacherId() { return teacherId; }
        public void setTeacherId(Integer teacherId) { this.teacherId = teacherId; }
        public Integer getDefaultScore() { return defaultScore; }
        public void setDefaultScore(Integer defaultScore) { this.defaultScore = defaultScore; }
        public List<Item> getItems() { return items; }
        public void setItems(List<Item> items) { this.items = items; }
    }

    /** 批次新增回應 */
    public static class BatchGradeResp {
        public int requested;            // 前端送來的數量
        public int inserted;             // 成功筆數
        public int skipped;              // 跳過筆數（重覆或找不到學生）
        public List<Integer> failedIds  = new ArrayList<>();
        public List<String>  failedNames = new ArrayList<>();
    }
}
