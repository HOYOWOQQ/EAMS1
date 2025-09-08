package com.eams.Service.score;

import com.eams.Entity.course.Subject;
import com.eams.Entity.score.*;
import com.eams.Entity.score.DTO.AnswerSheetDTO;
import com.eams.Entity.score.DTO.QuestionDTO;
import com.eams.Entity.score.DTO.QuestionSheetDTO;
import com.eams.Entity.score.DTO.QuestionSheetItemDTO;
import com.eams.Entity.score.DTO.QuestionSheetItemView;
import com.eams.Repository.score.AnswerSheetRepository;
import com.eams.Repository.score.ExamPaperRepository;
import com.eams.Repository.score.QuestionAnswerKeyRepository;
import com.eams.Repository.score.QuestionRepository;
import com.eams.Repository.score.QuestionSheetItemRepository;
import com.eams.Repository.score.QuestionSheetRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class QuestionBankService {

    @Autowired private QuestionRepository questionRepo;
    @Autowired private QuestionAnswerKeyRepository keyRepo;
    @Autowired private QuestionSheetRepository sheetRepo;
    @Autowired private QuestionSheetItemRepository sheetItemRepo;
    @Autowired private ExamPaperRepository examPaperRepo;
    @Autowired private AnswerSheetRepository answerSheetRepo;   
    @PersistenceContext private EntityManager em;

    /** 依科目 + 關鍵字 分頁查題庫 */
    @Transactional(readOnly = true)
    public Page<QuestionDTO> pageQuestions(Integer subjectId, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepo.pageBySubjectAndKeyword(subjectId, keyword, pageable)
                .map(QuestionDTO::fromEntity);
    }

    /** 題目詳情（含答案鍵 JSON） */
    @Transactional(readOnly = true)
    public Map<String, Object> getQuestionDetail(Integer questionId) {
        Question q = questionRepo.findById(questionId).orElseThrow();
        QuestionAnswerKey key = keyRepo.findById(questionId).orElse(null);
        Map<String, Object> vo = new LinkedHashMap<>();
        vo.put("question", QuestionDTO.fromEntity(q));
        vo.put("answerJson", key != null ? key.getAnswerJson() : null);
        return vo;
    }

    /** 老師選題 → 覆寫/建立該考卷的題目卷（並依順序與配分建立 item） */
    @Transactional
    public Integer buildQuestionSheet(Integer examPaperId, List<Integer> questionIds, BigDecimal defaultPoints) {
        ExamPaper paper = examPaperRepo.findById(examPaperId).orElseThrow();

        // 取或建 question_sheet
        QuestionSheet sheet = sheetRepo.findByExamPaper_Id(examPaperId);
        if (sheet == null) {
            sheet = new QuestionSheet();
            sheet.setExamPaper(paper);
            sheet.setTitle("題目卷 - " + Optional.ofNullable(paper.getName()).orElse(String.valueOf(paper.getId())));
            sheet = sheetRepo.save(sheet);
        }

        // 清舊 items
        sheetItemRepo.deleteAllBySheetId(sheet.getId());

        // 新增 items
        int seq = 1;
        for (Integer qid : questionIds) {
            QuestionSheetItem item = new QuestionSheetItem();
            item.setSheet(sheet);
            Question qref = em.getReference(Question.class, qid);
            item.setQuestion(qref);
            item.setSeqNo(seq++);
            item.setPoints(defaultPoints); // 直接用 BigDecimal
            sheetItemRepo.save(item);
        }
        return sheet.getId();
    }

    /** 4) 由題目卷生成答案卷（呼叫 SP） */
    @Transactional
    public void buildAnswerSheetBySP(Integer examPaperId) {
        em.createNativeQuery("EXEC sp_build_answer_sheet_from_question_sheet @ExamPaperId = :id")
          .setParameter("id", examPaperId)
          .executeUpdate();
    }
    
    @Transactional
    public QuestionSheetDTO buildQuestionSheetAndReturnDTO(Integer examPaperId, List<Integer> questionIds, BigDecimal defaultPoints) {
        // 先沿用你原本的建立流程
        Integer sheetId = buildQuestionSheet(examPaperId, questionIds, defaultPoints);

        // 回頭查出最新的 QuestionSheet 並轉 DTO
        QuestionSheet sheet = sheetRepo.findById(sheetId).orElseThrow();

        QuestionSheetDTO dto = QuestionSheetDTO.fromEntity(sheet);

        

        return dto;
    }
    //取得考卷明細
    @Transactional(readOnly = true)
    public QuestionSheet getSheetByExamPaperId(Integer PaperId) {
    	return sheetRepo.findWithItemsAndQuestionByExamPaperId(PaperId).orElse(null);
    }
    
    // 取得某考卷的答案卷
    @Transactional(readOnly = true)
    public AnswerSheetDTO getAnswerSheetOfPaper(Integer examPaperId) {
        AnswerSheet ans = answerSheetRepo.findByExamPaper_Id(examPaperId);
        return AnswerSheetDTO.fromEntity(ans); 
    }
    //取得明細
    @Transactional(readOnly = true)
    public List<QuestionSheetItemView> listQuestionItemsOfPaper(Integer examPaperId) {
        QuestionSheet sheet = sheetRepo.findByExamPaper_Id(examPaperId);
        if (sheet == null || sheet.getItems() == null) return List.of();

        return sheet.getItems().stream()
            .sorted(Comparator.comparingInt(QuestionSheetItem::getSeqNo))
            .map(it -> {
                Question q = it.getQuestion();           // LAZY，因為在 Tx 內可取
                Subject  s = (q != null ? q.getSubject() : null);

                QuestionSheetItemView v = new QuestionSheetItemView();
                v.itemId      = it.getId();
                v.seqNo       = it.getSeqNo();
                v.points      = it.getPoints();

                if (q != null) {
                    v.questionId  = q.getId();
                    v.qType       = q.getType();          // 對應資料表欄位 q_type（你 Entity 已 @Column(name="q_type")）
                    v.stem        = q.getStem();
                    v.optionsJson = q.getOptionsJson();
                    if (s != null) {
                        v.subjectId   = s.getId();
                        v.subjectName = s.getName();      // 若 Subject 有 name 欄位
                    }
                }
                return v;
            })
            .toList();
    }
    
 // ================== 查詢答案卷明細 ==================
    @Transactional(readOnly = true)
    public List<com.eams.Controller.score.QuestionBankController.AnswerItemVO> listAnswerItemsOfPaper(Integer examPaperId) {
        // 先找到題目卷
        QuestionSheet sheet = sheetRepo.findByExamPaper_Id(examPaperId);
        if (sheet == null || sheet.getItems() == null) {
            return List.of();
        }

        // 依序號排序
        return sheet.getItems().stream()
                .sorted(Comparator.comparingInt(QuestionSheetItem::getSeqNo))
                .map(item -> {
                    Question q = item.getQuestion();
                    QuestionAnswerKey key = keyRepo.findById(q.getId()).orElse(null);

                    com.eams.Controller.score.QuestionBankController.AnswerItemVO vo =
                            new com.eams.Controller.score.QuestionBankController.AnswerItemVO();
                    vo.questionId = q.getId();
                    vo.stem = q.getStem();
                    vo.optionsJson = q.getOptionsJson();
                    vo.correct = (key != null ? key.getAnswerJson() : null);  // 直接丟 JSON，或你可以 parse 後只取 "A"
                    vo.seqNo = item.getSeqNo();
                    vo.points = (item.getPoints() != null ? item.getPoints() : BigDecimal.ZERO);
                    return vo;
                })
                .toList();
    }
}
