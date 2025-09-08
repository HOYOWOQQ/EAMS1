package com.eams.Controller.score;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.eams.Entity.course.Subject;
import com.eams.Entity.score.Question;
import com.eams.Entity.score.QuestionAnswerKey;
import com.eams.Entity.score.QuestionSheet;
import com.eams.Entity.score.QuestionSheetItem;
import com.eams.Entity.score.QuestionUpsertReq;
import com.eams.Entity.score.DTO.AnswerSheetDTO;
import com.eams.Entity.score.DTO.QuestionDTO;
import com.eams.Entity.score.DTO.QuestionSheetDTO;
import com.eams.Entity.score.DTO.QuestionSheetItemDTO;
import com.eams.Entity.score.DTO.QuestionSheetItemView;
import com.eams.Repository.course.SubjectRepository;
import com.eams.Repository.score.AnswerSheetRepository;
import com.eams.Repository.score.QuestionAnswerKeyRepository;
import com.eams.Repository.score.QuestionRepository;
import com.eams.Repository.score.QuestionSheetItemRepository;
import com.eams.Repository.score.QuestionSheetRepository;
import com.eams.Service.score.QuestionBankService;
import com.eams.utils.ScoreAccessGuard;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/api/question-bank")
public class QuestionBankController {

    @Autowired private QuestionBankService questionBankService;

    // CRUD 會用到的 Repos
    @Autowired private QuestionRepository questionRepo;
    @Autowired private QuestionAnswerKeyRepository keyRepo;
    @Autowired private QuestionSheetRepository sheetRepo;
    @Autowired private QuestionSheetItemRepository sheetItemRepo;
    @Autowired private AnswerSheetRepository answerSheetRepo;
    @Autowired private SubjectRepository subjectRepo;
    @PersistenceContext
    private EntityManager em;
    @Autowired private ScoreAccessGuard guard;

    @Autowired(required = false)
    private ObjectMapper objectMapper;                 // 專案若有註冊會注入；否則 fallback
    private ObjectMapper mapper() { return (objectMapper != null) ? objectMapper : new ObjectMapper(); }

    /* ===================== 共用小工具 ===================== */

    /** 老師與主任以上可用 */
    private void assertTeacherOrDirector() { guard.assertTeacherOrDirector(); }
    private boolean isDirectorOrAbove() { return guard.isDirectorOrAbove(); }

    
    private Subject resolveSubject(QuestionUpsertReq req) {
        if (req.getSubjectId() != null) {
            return subjectRepo.findById(req.getSubjectId()).orElse(null);
        }
        if (req.getSubjectName() != null && !req.getSubjectName().isBlank()) {
            return subjectRepo.findSubjectByNameOrThrow(req.getSubjectName().trim());
        }
        return null;
    }

    /** 兼容 request 同時可能使用 qType 或 type 的情形（以 qType 為主） */
    private String readQType(QuestionUpsertReq req) {
        if (req.getType() != null && !req.getType().isBlank()) return req.getType().trim();
        if (req.getType()  != null && !req.getType().isBlank())  return req.getType().trim();
        return null;
    }

    /** 把 ["甲","乙","丙","丁"] 轉 {"A":"甲","B":"乙",...} */
    private Map<String, String> toLetteredMap(List<String> options) {
        String[] L = {"A","B","C","D","E","F","G","H","I","J"};
        Map<String, String> m = new LinkedHashMap<>();
        for (int i = 0; i < options.size(); i++) m.put(L[i], options.get(i));
        return m;
    }

    /* ===================== 題庫查詢 ===================== */

    /** 題庫分頁（依科目/關鍵字） */
    @GetMapping("/questions")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<QuestionDTO>> pageQuestions(
            @RequestParam(required = false) Integer subjectId,
            @RequestParam(required = false, name = "q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        assertTeacherOrDirector();
        return ResponseEntity.ok(
                questionBankService.pageQuestions(subjectId, keyword, page, size)
        );
    }

    /** 題目詳情（含答案鍵 JSON） */
    @GetMapping("/questions/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getQuestionDetail(@PathVariable Integer id) {
        assertTeacherOrDirector();
        return ResponseEntity.ok(questionBankService.getQuestionDetail(id));
    }

    /* ===================== 題庫題目：新增 / 修改 / 刪除 ===================== */

    /** 建立題目（四個選項必填；選項可追加；答案為單選） */
    @PostMapping("/questions")
    @Transactional
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionUpsertReq req) throws Exception {
        assertTeacherOrDirector();

        Subject subject = resolveSubject(req);
        if (subject == null) return ResponseEntity.badRequest().build();

        List<String> raw = (req.getOptions() == null) ? Collections.emptyList() : req.getOptions();
        List<String> opts = raw.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        if (opts.size() < 4) return ResponseEntity.badRequest().build();

        // 建立題目：type = 科目名稱、q_type = 題型（單選/多選）
        Question q = new Question();
        q.setSubject(subject);
        q.setType(subject.getName());                               // ← 寫入「科目名稱」到資料表 type
        q.setQType(req.getType() == null ? "" : req.getType().trim()); // ← 題型 → q_type
        q.setStem(req.getStem());
        q.setPoints(req.getPoints() == null ? 0 : req.getPoints());
        q.setOptionsJson(mapper().writeValueAsString(toLetteredMap(opts)));
        q = questionRepo.save(q);

        // 建立答案鍵：用 @MapsId 正確姿勢 → 設關聯，不手動 setQuestionId
        String label = (req.getAnswerLabel() == null ? "" : req.getAnswerLabel().trim().toUpperCase());
        QuestionAnswerKey key = new QuestionAnswerKey();
        key.setQuestion(q);                                         // ★ 關鍵：只設關聯，讓 @MapsId 自動帶 PK=FK
        key.setAnswerJson("{\"correct\":\"" + label + "\"}");
        keyRepo.save(key);

        return ResponseEntity.ok(QuestionDTO.fromEntity(q));
    }


    /** 修改題目（支援部分欄位；選項若要更新仍需 >=4） */
    @PutMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Integer id, @RequestBody QuestionUpsertReq req) throws Exception {
        assertTeacherOrDirector();

        Question q = questionRepo.findById(id).orElse(null);
        if (q == null) return ResponseEntity.notFound().build();

        Subject subject = resolveSubject(req);
        if (subject != null) {
            q.setSubject(subject);
            q.setType(subject.getName());                            // ← 同步資料表 type = 科目名稱
        }

        if (req.getStem() != null) q.setStem(req.getStem());
        if (req.getType() != null) q.setQType(req.getType().trim()); // ← 題型 → q_type
        if (req.getPoints() != null) q.setPoints(req.getPoints());

        if (req.getOptions() != null) {
            List<String> opts = req.getOptions().stream()
                    .map(s -> s == null ? "" : s.trim())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            if (opts.size() < 4) return ResponseEntity.badRequest().build();
            q.setOptionsJson(mapper().writeValueAsString(toLetteredMap(opts)));
        }
        questionRepo.save(q);

        // 是否要更新答案鍵？
        boolean hasAnswerField = (req.getAnswerLabel() != null);
        if (hasAnswerField) {
            String label = (req.getAnswerLabel() == null ? "" : req.getAnswerLabel().trim().toUpperCase());

            // 找既有答案鍵；若沒有就新建一筆（用 setQuestion，不手動 setQuestionId）
            QuestionAnswerKey key = keyRepo.findById(id).orElseGet(() -> {
                QuestionAnswerKey k = new QuestionAnswerKey();
                // 這裡用 reference 或直接用上面的 q 都可以
                k.setQuestion(em.getReference(Question.class, id));  // ★ 關鍵：設關聯，由 @MapsId 決定 PK
                return k;
            });
            key.setAnswerJson("{\"correct\":\"" + label + "\"}");
            keyRepo.save(key);
        }

        return ResponseEntity.ok(QuestionDTO.fromEntity(q));
    }

    /** 刪題（若被卷引用，交由 FK/DB 限制或先行檢查） */
    @DeleteMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer id) {
        assertTeacherOrDirector();
        keyRepo.deleteById(id);
        questionRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /* ===================== 題目卷 / 答案卷 ===================== */

    /** 以勾選題目建立/覆寫 題目卷（回傳 DTO） */
    @PostMapping("/exam-papers/{examPaperId}/question-sheet")
    @Transactional
    public ResponseEntity<QuestionSheetDTO> buildQuestionSheet(
            @PathVariable Integer examPaperId,
            @RequestBody BuildSheetRequest req) {

        assertTeacherOrDirector();

        if (!isDirectorOrAbove()) {
            int me = guard.currentUserIdOr403();
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
        }

        BigDecimal defPts = (req.getDefaultPoints() != null) ? req.getDefaultPoints() : BigDecimal.ZERO;
        QuestionSheetDTO dto = questionBankService.buildQuestionSheetAndReturnDTO(
                examPaperId, req.getQuestionIds(), defPts
        );
        return ResponseEntity.ok(dto);
    }

    /** 取某考卷的題目卷（含明細） */
    @GetMapping("/exam-papers/{examPaperId}/question-sheet")
    @Transactional(readOnly = true)
    public ResponseEntity<QuestionSheetDTO> getSheetOfPaper(@PathVariable Integer examPaperId) {
        assertTeacherOrDirector();
        if (!isDirectorOrAbove()) {
            int me = guard.currentUserIdOr403();
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
        }
        QuestionSheet sheet = sheetRepo.findByExamPaper_Id(examPaperId);
        return ResponseEntity.ok(QuestionSheetDTO.fromEntity(sheet));
    }

    /** 由題目卷生成答案卷（呼叫 SP） */
    @PostMapping("/exam-papers/{examPaperId}/answer-sheet/build")
    @Transactional
    public ResponseEntity<Void> buildAnswerSheet(@PathVariable Integer examPaperId) {
        assertTeacherOrDirector();
        if (!isDirectorOrAbove()) {
            int me = guard.currentUserIdOr403();
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
        }
        questionBankService.buildAnswerSheetBySP(examPaperId);
        return ResponseEntity.ok().build();
    }

    /** 查該考卷的答案卷（若尚未建立，回 200 + null） */
    @GetMapping("/exam-papers/{examPaperId}/answer-sheet")
    @Transactional(readOnly = true)
    public ResponseEntity<AnswerSheetDTO> getAnswerSheetOfPaper(@PathVariable Integer examPaperId) {
        assertTeacherOrDirector();
        if (!isDirectorOrAbove()) {
            int me = guard.currentUserIdOr403();
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
        }
        AnswerSheetDTO dto = questionBankService.getAnswerSheetOfPaper(examPaperId);
        return ResponseEntity.ok(dto);
    }

    /** 題目卷「實際上有的題目」列表（依 seqNo 排序） */
    @GetMapping("/exam-papers/{examPaperId}/question-sheet/items")
    @Transactional(readOnly = true)
    public ResponseEntity<List<QuestionSheetItemView>> listQuestionSheetItems(
            @PathVariable Integer examPaperId) {
        assertTeacherOrDirector();
        if (!isDirectorOrAbove()) {
            int me = guard.currentUserIdOr403();
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
        }
        return ResponseEntity.ok(questionBankService.listQuestionItemsOfPaper(examPaperId));
    }

    /** 答案卷明細（含正解與每題配分；由 Service join 出來） */
    @GetMapping("/exam-papers/{examPaperId}/answer-sheet/items")
    @Transactional(readOnly = true)
    public ResponseEntity<List<AnswerItemVO>> listAnswerSheetItems(@PathVariable Integer examPaperId) {
        assertTeacherOrDirector();
        if (!isDirectorOrAbove()) {
            int me = guard.currentUserIdOr403();
            guard.assertTeacherOwnsExamPaper(me, examPaperId);
        }
        return ResponseEntity.ok(questionBankService.listAnswerItemsOfPaper(examPaperId));
    }

    /* ===================== VO / Request 物件 ===================== */

    /** 建/覆寫題目卷請求體 */
    public static class BuildSheetRequest {
        private List<Integer> questionIds;
        private BigDecimal defaultPoints;
        public List<Integer> getQuestionIds() { return questionIds; }
        public void setQuestionIds(List<Integer> questionIds) { this.questionIds = questionIds; }
        public BigDecimal getDefaultPoints() { return defaultPoints; }
        public void setDefaultPoints(BigDecimal defaultPoints) { this.defaultPoints = defaultPoints; }
    }

    /** 提供前端看的答案卷明細（由 service 組合） */
    public static class AnswerItemVO {
        public Integer questionId;
        public String  stem;
        public String  optionsJson;
        public String  correct;   // A/B/C/D …
        public Integer seqNo;     // 題目順序
        public BigDecimal points; // 本題配分
    }
}
