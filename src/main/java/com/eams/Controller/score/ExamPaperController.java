package com.eams.Controller.score;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import com.eams.Entity.score.DTO.QuestionSheetDTO;
import com.eams.Repository.course.CourseEnrollRepository;
import com.eams.Repository.score.ExamPaperRepository;
import com.eams.Repository.score.QuestionSheetRepository;
import com.eams.Entity.score.QuestionSheet;
import com.eams.Entity.score.DTO.ExamDTO;
import com.eams.Entity.score.DTO.ExamPaperDTO;
import com.eams.Service.score.ExamPaperService;
import com.eams.Service.score.ExamService;
import com.eams.Service.score.QuestionBankService;
import com.eams.utils.ScoreAccessGuard;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam-papers")
public class ExamPaperController {

    @Autowired private ExamPaperService examPaperService;
    @Autowired private ExamService examService;
    @Autowired private QuestionBankService questionBankService;
    @Autowired private ExamPaperRepository examPaperRepo;
    @Autowired private CourseEnrollRepository enrollRepo;
    @Autowired private QuestionSheetRepository questionSheetRepo;
    @Autowired private ScoreAccessGuard guard;

    /* ===== 小工具（加入臨時特例：UID=45 等同主任） ===== */
    private Integer me() { return guard.currentUserIdOr403(); }
    private boolean isTeacher()  { return guard.isTeacher(); }
    private boolean isDirector() {
        try {
            Integer uid = me();
            return guard.isDirectorOrAbove() || (uid != null && uid == 45);
        } catch (Throwable ignore) { return guard.isDirectorOrAbove(); }
    }
    /** 取代原本 guard.assertTeacherOrDirector()：允許 老師 / 主任 / UID=45 */
    private void assertTeacherDirectorOrId45() {
        if (!(isTeacher() || isDirector())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "需要老師或主任權限");
        }
    }

    /** 1. 新增或更新考卷 **/
    @PostMapping
    public ResponseEntity<ExamPaperDTO> save(@RequestBody ExamPaperDTO dto) {
        assertTeacherDirectorOrId45();
        if (isTeacher()) {
            guard.assertTeacherOwnsCourse(me(), dto.getCourseId());
        }
        return ResponseEntity.ok(examPaperService.save(dto));
    }

    /** 2. 根據 ID 查詢 **/
    @GetMapping("/{id}")
    public ResponseEntity<ExamPaperDTO> getById(@PathVariable int id) {
        assertTeacherDirectorOrId45();
        if (isTeacher()) {
            guard.assertTeacherOwnsExamPaper(me(), id);
        }
        ExamPaperDTO dto = examPaperService.findById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    /** 3. 查詢所有考卷 **/
    @GetMapping
    public ResponseEntity<List<ExamPaperDTO>> getAll() {
        assertTeacherDirectorOrId45();
        List<ExamPaperDTO> all = examPaperService.findAll();
        if (isDirector()) return ResponseEntity.ok(all);
        guard.filterExamPapersByTeacher(all, me());
        return ResponseEntity.ok(all);
    }

    /** 4. 根據課程 ID 查詢 **/
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<ExamPaperDTO>> getByCourseId(@PathVariable int courseId) {
        assertTeacherDirectorOrId45();
//        if (isTeacher()) guard.assertTeacherOwnsCourse(me(), courseId);
        return ResponseEntity.ok(examPaperService.findByCourseId(courseId));
    }

    /** 5. 根據考試類型查詢 **/
    @GetMapping("/by-type/{examType}")
    public ResponseEntity<List<ExamPaperDTO>> getByType(@PathVariable String examType) {
        assertTeacherDirectorOrId45();
        List<ExamPaperDTO> list = examPaperService.findByExamType(examType);
        if (isDirector()) return ResponseEntity.ok(list);
        guard.filterExamPapersByTeacher(list, me());
        return ResponseEntity.ok(list);
    }

    /** 6. 根據課程與考試類型查詢 **/
    @GetMapping("/by-course-and-type")
    public ResponseEntity<List<ExamPaperDTO>> getByCourseAndType(@RequestParam int courseId,
                                                                 @RequestParam String examType) {
        assertTeacherDirectorOrId45();
        if (isTeacher()) guard.assertTeacherOwnsCourse(me(), courseId);
        return ResponseEntity.ok(examPaperService.findByCourseIdAndType(courseId, examType));
    }

    /** 7. 根據日期區間查詢 **/
    @GetMapping("/by-date-range")
    public ResponseEntity<List<ExamPaperDTO>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        assertTeacherDirectorOrId45();
        List<ExamPaperDTO> list = examPaperService.findByDateRange(startDate, endDate);
        if (isDirector()) return ResponseEntity.ok(list);
        guard.filterExamPapersByTeacher(list, me());
        return ResponseEntity.ok(list);
    }

    /** 8. 刪除單筆考卷 **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable int id) {
        // 若要控管，這裡也可加：if (isTeacher()) guard.assertTeacherOwnsExamPaper(me(), id);
        boolean deleted = examPaperService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /** 9. 刪除指定課程考卷 **/
    @DeleteMapping("/by-course/{courseId}")
    public ResponseEntity<Integer> deleteByCourseId(@PathVariable int courseId) {
        // 若要控管，這裡也可加：if (isTeacher()) guard.assertTeacherOwnsCourse(me(), courseId);
        int count = examPaperService.deleteByCourseId(courseId);
        return ResponseEntity.ok(count);
    }

    /** 10. 刪除指定考試類型考卷 **/
    @DeleteMapping("/by-type/{examType}")
    public ResponseEntity<Integer> deleteByExamType(@PathVariable String examType) {
        int count = examPaperService.deleteByExamType(examType);
        return ResponseEntity.ok(count);
    }

    /** 11. 刪除日期區間考卷 **/
    @DeleteMapping("/by-date-range")
    public ResponseEntity<Integer> deleteByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        int count = examPaperService.deleteByDateRange(startDate, endDate);
        return ResponseEntity.ok(count);
    }

    /** 12. 取得所有考卷名稱 **/
    @GetMapping("/names")
    public ResponseEntity<List<String>> getAllPaperNames() {
        assertTeacherDirectorOrId45();
        if (isDirector()) return ResponseEntity.ok(examPaperService.getAllPaperNames());

        var list = examPaperService.findAll();
        guard.filterExamPapersByTeacher(list, me());
        var names = list.stream().map(ExamPaperDTO::getName).distinct().toList();
        return ResponseEntity.ok(names);
    }

    /** 13. 取得有成績的考卷名稱 **/
    @GetMapping("/names-with-results")
    public ResponseEntity<List<String>> getPaperNamesWithResults() {
        assertTeacherDirectorOrId45();
        if (isDirector()) return ResponseEntity.ok(examPaperService.getExamPapersWithResults());

        var canSee = examPaperService.findAll();
        guard.filterExamPapersByTeacher(canSee, me());

        var withResults = new java.util.HashSet<>(examPaperService.getExamPapersWithResults());
        var names = canSee.stream()
                          .map(ExamPaperDTO::getName)
                          .filter(withResults::contains)
                          .distinct()
                          .toList();
        return ResponseEntity.ok(names);
    }

    /** 14. 考卷成績筆數映射 **/
    @GetMapping("/result-count")
    public ResponseEntity<List<Object[]>> getExamPaperResultCounts() {
        assertTeacherDirectorOrId45();
        if (isDirector()) return ResponseEntity.ok(examPaperService.getExamPaperResultCounts());

        var canSee = examPaperService.findAll();
        guard.filterExamPapersByTeacher(canSee, me());
        var allowedNames = canSee.stream().map(ExamPaperDTO::getName).collect(java.util.stream.Collectors.toSet());

        var allCounts = examPaperService.getExamPaperResultCounts();
        var mine = allCounts.stream()
                            .filter(row -> row != null && row.length > 0 && allowedNames.contains((String) row[0]))
                            .toList();
        return ResponseEntity.ok(mine);
    }

    /** 建立考題卷 **/
    @PostMapping("/{examPaperId}/question-sheet")
    @Transactional
    public ResponseEntity<QuestionSheetDTO> buildQuestionSheet(
            @PathVariable Integer examPaperId,
            @RequestBody BuildSheetRequest req) {
        assertTeacherDirectorOrId45();
        if (isTeacher()) guard.assertTeacherOwnsExamPaper(me(), examPaperId);

        BigDecimal defPts = (req.getDefaultPoints() != null) ? req.getDefaultPoints() : BigDecimal.ZERO;
        var dto = questionBankService.buildQuestionSheetAndReturnDTO(examPaperId, req.getQuestionIds(), defPts);
        return ResponseEntity.ok(dto);
    }

    /** 題目卷產生答案卷 */
    @PostMapping("/{examPaperId}/answer-sheet/build")
    @Transactional
    public ResponseEntity<Void> buildAnswerSheet(@PathVariable Integer examPaperId) {
        assertTeacherDirectorOrId45();
        if (isTeacher()) guard.assertTeacherOwnsExamPaper(me(), examPaperId);
        questionBankService.buildAnswerSheetBySP(examPaperId);
        return ResponseEntity.ok().build();
    }

    /** 依據 examId 查詢所有考卷 */
    @GetMapping("/exams/{examId}/papers")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ExamPaperDTO>> listPapersOfExam(@PathVariable Integer examId) {
        assertTeacherDirectorOrId45();

        List<ExamPaperDTO> papers = examService.listPapersOfExam(examId);
        if (isDirector()) {
            return ResponseEntity.ok(papers);
        }

        Integer me = me();
        List<ExamPaperDTO> safe = guard.filterExamPapersByTeacherCopy(papers, me);
        return ResponseEntity.ok(safe);
    }

    @GetMapping("/exams/search-with-papers")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ExamWithPapersVO>> searchExamsWithPapers(
            @RequestParam(required = false) String q) {
        assertTeacherDirectorOrId45();
        var exams = examService.searchWithPapers(q);
        var list = exams.stream().map(ExamWithPapersVO::of).toList();

        if (isDirector()) return ResponseEntity.ok(list);

        Integer me = me();
        var filtered = list.stream().map(vo -> {
            if (vo.papers != null) {
                vo.papers = new ArrayList<>(guard.filterExamPapersByTeacherCopy(vo.papers, me));
            }
            return vo;
        }).toList();

        return ResponseEntity.ok(filtered);
    }

    /** 給前端用的簡單 VO（Exam + Papers） */
    public static class ExamWithPapersVO {
        public Integer id;
        public String  name;
        public String  type;
        public java.util.Date examDate;
        public List<ExamPaperDTO> papers;

        public static ExamWithPapersVO of(com.eams.Entity.score.Exam e) {
            ExamWithPapersVO vo = new ExamWithPapersVO();
            vo.id = e.getId();
            vo.name = e.getName();
            vo.type = e.getType();
            vo.examDate = e.getExamDate();
            vo.papers = (e.getExamPapers() == null)
                    ? new java.util.ArrayList<>()
                    : e.getExamPapers().stream()
                        .map(ExamPaperDTO::fromEntity)
                        .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
            return vo;
        }
    }

    // 取得考卷明細
    @GetMapping("/{examPaperId}/question-sheet")
    @Transactional(readOnly = true)
    public ResponseEntity<QuestionSheetDTO> getQuestionSheet(@PathVariable Integer examPaperId) {
        assertTeacherDirectorOrId45();
        if (isTeacher()) guard.assertTeacherOwnsExamPaper(me(), examPaperId);
        QuestionSheet sheet = questionBankService.getSheetByExamPaperId(examPaperId);
        return ResponseEntity.ok(QuestionSheetDTO.fromEntity(sheet));
    }

    /** 建/覆寫題目卷的請求體 */
    public static class BuildSheetRequest {
        private List<Integer> questionIds;
        private BigDecimal defaultPoints;
        public List<Integer> getQuestionIds() { return questionIds; }
        public void setQuestionIds(List<Integer> questionIds) { this.questionIds = questionIds; }
        public BigDecimal getDefaultPoints() { return defaultPoints; }
        public void setDefaultPoints(BigDecimal defaultPoints) { this.defaultPoints = defaultPoints; }
    }

    // 取得 Exam 清單（可模糊 q，limit 可選）
    @GetMapping("/exams")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ExamDTO>> listExams(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "200") Integer limit) {
        assertTeacherDirectorOrId45();
        List<ExamDTO> list = examService.listExamsDTO(q, limit == null ? 200 : limit);
        return ResponseEntity.ok(list);
    }

    // 取得單一 Exam
    @GetMapping("/exams/{examId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ExamDTO> getExam(@PathVariable Integer examId) {
        assertTeacherDirectorOrId45();
        ExamDTO dto = examService.findExamDTOById(examId);
        return (dto != null) ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // 新增／更新 Exam
    @PostMapping("/exams")
    @Transactional
    public ResponseEntity<ExamDTO> saveExam(@RequestBody ExamDTO dto) {
        assertTeacherDirectorOrId45();
        ExamDTO saved = examService.saveExamDTO(dto);
        return ResponseEntity.ok(saved);
    }

    // 刪除 Exam
    @DeleteMapping("/exams/{examId}")
    @Transactional
    public ResponseEntity<Void> deleteExam(@PathVariable Integer examId) {
        assertTeacherDirectorOrId45();
        boolean ok = examService.deleteExamById(examId);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /* =======================================================================
     * 1) 學生：列出自己可作答的考卷（可選關鍵字 q）
     * ======================================================================= */
    @GetMapping("/student/my-papers")
    @Transactional
    public ResponseEntity<List<ExamPaperDTO>> listMyPapersForStudent(
            @RequestParam(required = false) String q) {

        if (!guard.isStudentOrParent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Integer me = guard.currentUserIdOr403();

        List<Integer> myCourseIds = enrollRepo.findCourseIdsByStudentId(me);
        if (myCourseIds == null || myCourseIds.isEmpty()) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }

        List<ExamPaperDTO> list = examPaperRepo.findByCourse_IdIn(myCourseIds).stream()
                .map(ExamPaperDTO::fromEntity)
                .collect(java.util.stream.Collectors.toList());

        // ★ 這段：補題目卷資訊（題數 / 總配分 / 是否有題目卷）
        for (var dto : list) {
            var sheet = questionBankService.getSheetByExamPaperId(dto.getId());
            if (sheet != null && sheet.getItems() != null && !sheet.getItems().isEmpty()) {
                dto.setHasQuestionSheet(true);
                dto.setTotalQuestions(sheet.getItems().size());
                java.math.BigDecimal totalPts = sheet.getItems().stream()
                        .map(it -> it.getPoints() == null ? java.math.BigDecimal.ZERO : it.getPoints())
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                dto.setTotalPoints(totalPts);
            } else {
                dto.setHasQuestionSheet(false);
                dto.setTotalQuestions(0);
                dto.setTotalPoints(null);
            }
        }

        if (q != null && !q.isBlank()) {
            String kw = q.trim();
            list = list.stream().filter(p ->
                    (p.getName() != null && p.getName().contains(kw)) ||
                    (p.getCourseName() != null && p.getCourseName().contains(kw)) ||
                    (p.getExamName() != null && p.getExamName().contains(kw))
            ).toList();
        }

        return ResponseEntity.ok(list);
    }

    /* =======================================================================
     * 2) 學生：讀單張考卷（含修課驗證）
     * ======================================================================= */
    @GetMapping("/student/papers/{paperId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ExamPaperDTO> getPaperForStudent(@PathVariable Integer paperId) {
        if (!guard.isStudentOrParent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Integer me = guard.currentUserIdOr403();

        var paper = examPaperRepo.findById(paperId).orElse(null);
        if (paper == null) return ResponseEntity.notFound().build();

        boolean ok = enrollRepo.existsByCourseIdAndStudentId(paper.getCourse().getId(), me);
        if (!ok) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        return ResponseEntity.ok(ExamPaperDTO.fromEntity(paper));
    }

    /* =======================================================================
     * 3) 學生：讀題目卷（僅修課驗證）
     * ======================================================================= */
    @GetMapping("/{examPaperId}/question-sheet-for-student")
    @Transactional(readOnly = true)
    public ResponseEntity<QuestionSheetDTO> getQuestionSheetForStudent(@PathVariable Integer examPaperId) {
        if (!guard.isStudentOrParent()) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        Integer me = guard.currentUserIdOr403();

        var paper = examPaperRepo.findById(examPaperId).orElse(null);
        if (paper == null) return ResponseEntity.notFound().build();

        boolean ok = enrollRepo.existsByCourseIdAndStudentId(paper.getCourse().getId(), me);
        if (!ok) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        // 關鍵：一定要把 items + question 一起載入
        var sheetOpt = questionSheetRepo.findWithItemsAndQuestionByExamPaperId(examPaperId);
        var sheet = sheetOpt.orElse(null);
        return ResponseEntity.ok(QuestionSheetDTO.fromEntity(sheet));
    }
}
