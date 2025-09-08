package com.eams.Service.score;

import com.eams.Entity.member.Teacher;
import com.eams.Entity.score.*;

import com.eams.Repository.score.ExamResultRepository;
import com.eams.Repository.score.QuestionSheetItemRepository;
import com.eams.Repository.score.AnswerSheetItemRepository;
import com.eams.Repository.score.ExamAttemptAnswerRepository;
import com.eams.Repository.score.ExamAttemptRepository;
import com.eams.Repository.score.ExamPaperRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Service
public class ExamAttemptService {

    @Autowired private ExamAttemptRepository attemptRepo;
    @Autowired private ExamAttemptAnswerRepository answerRepo;
    @Autowired private AnswerSheetItemRepository stdRepo;
    @Autowired private ExamResultRepository resultRepo;
    @Autowired private ExamPaperRepository paperRepo;
    @Autowired private QuestionSheetItemRepository sheetItemRepo;

    @PersistenceContext private EntityManager em;

    /** 學生按下「開始作答」：建一筆 attempt */
    @Transactional
    public Integer start(Integer studentId, Integer examPaperId) {
        // 先檢查是否已經有作答紀錄
        boolean exists = attemptRepo.existsByExamPaper_IdAndStudent_Id(examPaperId, studentId);
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "此考卷僅能作答一次");
        }

        ExamAttempt a = new ExamAttempt();
        a.setStatus("未提交"); // 預設狀態
        a.setStartTime(Timestamp.from(Instant.now()));
        a.setStudent(em.getReference(com.eams.Entity.member.Student.class, studentId));
        a.setExamPaper(em.getReference(ExamPaper.class, examPaperId));

        attemptRepo.save(a);
        return a.getId();
    }

    /** 途中答題（可重複呼叫做 upsert） */
    @Transactional
    public void saveAnswer(Integer attemptId, Integer seqNo, String selectedOption) {
        var attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到作答"));

        // 1) 找出這張卷的該題（帶 question 與 points）
        Integer paperId = attempt.getExamPaper().getId();
        var item = sheetItemRepo.findItemByPaperAndSeqNo(paperId, seqNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "題目不存在或題號錯誤"));

        // 2) 先找是否已有同一題的答案（upsert）
        var answer = answerRepo.findByAttempt_IdAndSeqNo(attemptId, seqNo)
                .orElseGet(() -> {
                    var a = new ExamAttemptAnswer();
                    a.setAttempt(attempt);
                    a.setSeqNo(seqNo);
                    a.setQuestion(item.getQuestion());        // ★ 這行確保 question_id 不會是 NULL
                    a.setPoints(BigDecimal.ZERO);             // 先置 0，提交時再算或這裡先算
                    a.setIsCorrect(false);                    // 先給 false，若有答案卷再更新
                    return a;
                });

        // 3) 更新選項
        answer.setSelectedOption(selectedOption);

        // 4) （可選）立即判對
        // String correct = answerSheetItemRepo.findCorrectOption(paperId, seqNo);
        // boolean right = Objects.equals(selectedOption, correct);
        // answer.setIsCorrect(right);
        // answer.setPoints(right ? item.getPoints() : BigDecimal.ZERO);

        answerRepo.save(answer);
    }

    /** 交卷：自動評分（客觀題）→ upsert exam_result → 重算排名 */
    @Transactional
    public int submit(Integer attemptId) {
        ExamAttempt a = attemptRepo.findById(attemptId).orElseThrow();
        a.setStatus("已提交");
        a.setSubmitTime(Timestamp.from(Instant.now()));

        // 標準答案（以題號對應）
        Integer paperId = a.getExamPaper().getId();
        List<AnswerSheetItem> std = stdRepo.findStdByPaperId(paperId);

        Map<Integer, String> stdMap = new HashMap<>();
        Map<Integer, BigDecimal> ptsMap = new HashMap<>();               
        for (AnswerSheetItem i : std) {
            stdMap.put(i.getSeqNo(), i.getAnswerJson());
            ptsMap.put(i.getSeqNo(),                                    
                    Optional.ofNullable(i.getPoints()).orElse(BigDecimal.ZERO));
        }

        // 作答
        List<ExamAttemptAnswer> answers =
                answerRepo.findByAttempt_IdOrderBySeqNoAsc(attemptId);

        BigDecimal total = BigDecimal.ZERO;                             
        for (ExamAttemptAnswer ans : answers) {
            String right = stdMap.get(ans.getSeqNo());
            boolean correct = compareChoice(ans.getSelectedOption(), right);
            ans.setIsCorrect(correct);

            BigDecimal gain = correct
                    ? ptsMap.getOrDefault(ans.getSeqNo(), BigDecimal.ZERO)
                    : BigDecimal.ZERO;                                    

            ans.setPoints(gain);                                          
            total = total.add(gain);                                      
        }

        a.setAutoScore(total);                                             
        attemptRepo.save(a);                                               // 更新 status/submitTime/autoScore
        answerRepo.saveAll(answers);

        // upsert exam_result
        ExamResult r = resultRepo
                .findByStudent_IdAndExamPaper_Id(a.getStudent().getId(), paperId)
                .orElseGet(ExamResult::new);

        r.setStudent(a.getStudent());
        r.setExamPaper(a.getExamPaper());
        r.setScore(total.intValue());                                      // exam_result.score 若是 INT
        r.setComment("系統自動評分");
        Teacher teacher = new Teacher();
        teacher.setId(45);
        r.setTeacher(teacher);
        if (r.getCreateTime() == null) {                                   
            r.setCreateTime(new Timestamp(System.currentTimeMillis()));
        }
        resultRepo.save(r);

        // 排名
        resultRepo.recalcRank(paperId);

        return total.intValue();                                           // 方法仍回傳 int
    }

    /** 單選比對：從標準答案 JSON 取 correct 值後比對 */
    private boolean compareChoice(String selected, String stdAnswerJson) {
        if (selected == null || stdAnswerJson == null) return false;
        // 最簡做法：只存 A/B/C/D 時，標準答案 JSON 可直接等於字母；若是 {"correct":"A"} 可簡單解析：
        String correct = stdAnswerJson;
        int pos = stdAnswerJson.indexOf("\"correct\"");
        if (pos >= 0) {
            int q = stdAnswerJson.indexOf('"', stdAnswerJson.indexOf(':', pos) + 1);
            int q2 = stdAnswerJson.indexOf('"', q + 1);
            if (q >= 0 && q2 > q) correct = stdAnswerJson.substring(q + 1, q2);
        }
        return selected.trim().equalsIgnoreCase(correct.trim());
    }
}
