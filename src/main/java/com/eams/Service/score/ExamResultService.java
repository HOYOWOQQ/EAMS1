package com.eams.Service.score;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Controller.score.ExamResultController;
import com.eams.Entity.member.Student;
import com.eams.Entity.member.Teacher;
import com.eams.Entity.score.ExamPaper;
import com.eams.Entity.score.ExamResult;
import com.eams.Entity.score.DTO.ExamResultDTO;
import com.eams.Repository.member.StudentRepository;
import com.eams.Repository.member.TeacherRepository;
import com.eams.Repository.score.ExamPaperRepository;
import com.eams.Repository.score.ExamResultRepository;

import jakarta.persistence.EntityManager;

@Service
@Transactional
public class ExamResultService {

    @Autowired
    private ExamResultRepository examResultRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeacherRepository teacherRepo;

    @Autowired
    private ExamPaperRepository paperRepo;
    
    @Autowired private EntityManager em;

    //  新增考試成績
    public ExamResult addExamResult(String studentName, int examPaperId, int score, int teacherId) {
        Student student = studentRepo.findByMemberName(studentName);
        Teacher teacher = teacherRepo.findById(teacherId).orElse(null);
        ExamPaper paper = paperRepo.findById(examPaperId).orElse(null);

        if (student == null || teacher == null || paper == null) {
            return null;
        }

        ExamResult result = new ExamResult();
        result.setStudent(student);
        result.setTeacher(teacher);
        result.setExamPaper(paper);
        result.setScore(score);
        result.setReviewedTime(new Timestamp(new Date().getTime()));
        result = examResultRepo.save(result);

        // ⬇️ 重算這張考卷排名
        recalcRankByPaperSafe(examPaperId);
        return result;
    }

    //  查詢所有成績（不含 DTO 過濾，給 EasyDataTable 自動搜尋）
    public List<ExamResult> findAll() {
        return examResultRepo.findAll();
    }

    //  查某位學生的全部成績
    public List<ExamResult> findByStudentName(String studentName) {
        return examResultRepo.findByStudent_Member_Name(studentName);
    }

    //  查某張考卷的成績
    public List<ExamResult> findByPaperName(String paperName) {
        return examResultRepo.findByExamPaper_Name(paperName);
    }

    //  更新成績資料
    public boolean updateExamResult(String studentName, String paperName, int score, int rank, Timestamp reviewedTime) {
        int updated = examResultRepo.updateScoreByStudentAndPaper(studentName, paperName, score, rank, reviewedTime);
        return updated > 0;
    }
    //刪除成績
    public boolean deleteByStudentIdAndPaperId(int studentId, int examPaperId) {
        int deleted = examResultRepo.deleteByStudentIdAndPaperId(studentId, examPaperId);
        if (deleted > 0) {
            recalcRankByPaperSafe(examPaperId); // ⬅️ 補上
            return true;
        }
        return false;
    }

    //  刪除某位學生的所有成績
    public int deleteAllByStudentName(String studentName) {
        List<ExamResult> list = examResultRepo.findByStudent_Member_Name(studentName);
        examResultRepo.deleteAll(list);
        return list.size();
    }

    //  取得所有老師名稱
    public List<String> getAllTeacherNames() {
        return examResultRepo.findAllTeacherNames();
    }

    //  取得所有考卷名稱
    public List<String> getAllExamPaperNames() {
        return examResultRepo.findAllExamPaperNames();
    }

    //  插入空白成績紀錄（無分數）
    public ExamResult insertBlankResult(int studentId, int paperId, int teacherId) {
        ExamResult result = new ExamResult();
        result.setStudent(studentRepo.findById(studentId).orElse(null));
        result.setExamPaper(paperRepo.findById(paperId).orElse(null));
        result.setTeacher(teacherRepo.findById(teacherId).orElse(null));
        return examResultRepo.save(result);
    }

    //  依老師姓名找出對應 ID（轉換流程輔助）
    public Integer getTeacherIdByName(String name) {
        return examResultRepo.findMemberIdByName(name);
    }

    //  依考卷名稱找出對應 ID（轉換流程輔助）
    public Integer getExamPaperIdByName(String name) {
        return examResultRepo.findExamPaperIdByName(name);
    }
    
    public int batchInsertBlankResults(int teacherId, int examPaperId, List<Integer> studentIds) {
        Teacher teacher = teacherRepo.findById(teacherId).orElse(null);
        ExamPaper paper = paperRepo.findById(examPaperId).orElse(null);

        if (teacher == null || paper == null || studentIds == null || studentIds.isEmpty()) {
            return 0;
        }

        int count = 0;

        for (Integer studentId : studentIds) {
            Student student = studentRepo.findById(studentId).orElse(null);
            if (student != null) {
                ExamResult result = new ExamResult();
                result.setStudent(student);
                result.setTeacher(teacher);
                result.setExamPaper(paper);
                result.setReviewedTime(null); // 尚未評閱
                result.setScore(0);        // 尚未給分
                examResultRepo.save(result);
                count++;
            }
        }

        return count; // 回傳成功新增幾筆
    }
    
 
    @Transactional(readOnly = true)
    public List<ExamResultDTO> listByPaperId(Integer paperId) {
        
        var list = examResultRepo.findByExamPaper_IdOrderByScoreDesc(paperId);
        return list.stream().map(ExamResultDTO::fromEntity).toList();
    }

    // ===== 2) 更新分數/批閱資訊 + 重算排名（圖一下面的 PATCH） =====
    @Transactional
    public void updateScoreAndRecalcRank(Integer resultId,
                                         Integer score,
                                         String comment,
                                         Integer reviewedBy) {
        ExamResult er = examResultRepo.findById(resultId).orElseThrow();

        if (score != null)   er.setScore(score);
        if (comment != null) er.setComment(comment);

        

        
        if (reviewedBy != null) {
             Teacher t = new Teacher(); t.setId(reviewedBy);
            er.setTeacher(t);
         }

        er.setReviewedTime(new java.sql.Timestamp(System.currentTimeMillis()));

        examResultRepo.save(er); // 先存

        // 以該張考卷為範圍重算排名
        Integer paperId = er.getExamPaper().getId();
        recalcRankByPaperSafe(paperId);
    }
    
    @Transactional(readOnly = true)
    public List<ExamResultDTO> findAllDTO() {
        return findAll().stream().map(ExamResultDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<ExamResultDTO> findByStudentIdDTO(Integer studentId) {
        return examResultRepo.findByStudent_IdOrderByCreateTimeDesc(studentId)
                         .stream().map(ExamResultDTO::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<ExamResultDTO> findByStudentNameDTO(String name) {
    	return examResultRepo.findByStudent_Member_NameOrderByCreateTimeDesc(name)
                .stream().map(ExamResultDTO::fromEntity).toList();
    }

    /** 找出某筆成績對應的 paperId，讓 controller 能做權限判斷 */
    @Transactional(readOnly = true)
    public Integer findPaperIdByResultId(Integer resultId) {
        return examResultRepo.findPaperIdByResultId(resultId).orElse(null);
    }

    /** 給 /papers 下拉：回傳簡單 DTO（含 paperId/courseId/name） */
    @Transactional(readOnly = true)
    public List<com.eams.Entity.score.DTO.ExamPaperDTO> findAllPapersAsDTO() {
        return paperRepo.findAllWithCourse()
                .stream().map(com.eams.Entity.score.DTO.ExamPaperDTO::fromEntity).toList();
    }

    // 也方便把 DTO -> name 抓出來（controller 直接用 method reference）
    public static String extractPaperName(com.eams.Entity.score.DTO.ExamPaperDTO dto) {
        return dto.getName();
    }
    
    @Transactional
    public void recalcRankByPaperSafe(Integer paperId) {
        try {
            examResultRepo.recalcRank(paperId); 
        } catch (Exception e) {
            // Fallback：JVM 端 Dense Rank
            var list = examResultRepo.findByExamPaper_IdOrderByScoreDesc(paperId);
            int rank = 0;
            Integer prev = null;
            for (ExamResult er : list) {
                Integer s = er.getScore();
                if (s == null) s = 0;
                if (!s.equals(prev)) {
                    rank += 1;      
                    prev = s;
                }
                er.setRankInClass(rank);
                examResultRepo.save(er);
            }
        }
    }
    
    // ---- 批次（IDs 版本） ----
    @Transactional
    public ExamResultController.BatchGradeResp addExamResultsBatchByIds(
            int examPaperId, int teacherId, java.util.List<Integer> studentIds, int defaultScore) {

        ExamResultController.BatchGradeResp out = new ExamResultController.BatchGradeResp();
        out.requested = (studentIds == null) ? 0 : studentIds.size();
        if (out.requested == 0) return out;

        // 基本資料
        var teacher = teacherRepo.findById(teacherId).orElse(null);
        var paper   = paperRepo.findById(examPaperId).orElse(null);
        if (teacher == null || paper == null) return out;

        // 去重、排序（降低鎖衝突、避免同一學生被重複 insert）
        var ids = studentIds.stream().filter(Objects::nonNull).distinct().sorted().toList();

        // 批次大小（避免一次塞太多、也降低死鎖概率）
        final int BATCH = 50;
        java.util.List<ExamResult> buffer = new java.util.ArrayList<>(BATCH);

        for (int i = 0; i < ids.size(); i++) {
            Integer sid = ids.get(i);
            var student = studentRepo.findById(sid).orElse(null);
            if (student == null) { out.skipped++; out.failedIds.add(sid); continue; }

            // 檢查是否已存在（同一學生 + 同一考卷），避免 PK/UK 衝突
            boolean exists = examResultRepo.existsByStudentIdAndExamPaperId(sid, examPaperId);
            if (exists) { out.skipped++; continue; }

            var r = new ExamResult();
            r.setStudent(student);
            r.setTeacher(teacher);
            r.setExamPaper(paper);
            r.setScore(defaultScore);
            r.setReviewedTime(new java.sql.Timestamp(System.currentTimeMillis()));

            buffer.add(r);

            // 滿一批就吐
            if (buffer.size() >= BATCH) {
                examResultRepo.saveAll(buffer);
                em.flush(); em.clear();
                out.inserted += buffer.size();
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            examResultRepo.saveAll(buffer);
            em.flush(); em.clear();
            out.inserted += buffer.size();
        }

        // 整批只重算一次排名
        recalcRankByPaperSafe(examPaperId);

        return out;
    }

    // ---- 批次（JSON 版本：可各自分數/評語，name 或 id 擇一）----
    @Transactional
    public ExamResultController.BatchGradeResp addExamResultsBatchJson(ExamResultController.BatchGradeReq req) {
        ExamResultController.BatchGradeResp out = new ExamResultController.BatchGradeResp();
        var items = (req.getItems() == null) ? java.util.List.<ExamResultController.BatchGradeReq.Item>of() : req.getItems();
        out.requested = items.size();
        if (out.requested == 0) return out;

        var paper   = paperRepo.findById(req.getExamPaperId()).orElse(null);
        var teacher = teacherRepo.findById(req.getTeacherId()).orElse(null);
        if (paper == null || teacher == null) return out;

        // 先把要查的 key 拆出來
        var idSet   = items.stream().map(ExamResultController.BatchGradeReq.Item::getStudentId).filter(Objects::nonNull).collect(java.util.stream.Collectors.toSet());
        var nameSet = items.stream().map(ExamResultController.BatchGradeReq.Item::getStudentName).filter(s -> s != null && !s.isBlank()).collect(java.util.stream.Collectors.toSet());

        // 一次撈齊（避免 N+1）
        var byId   = new java.util.HashMap<Integer, Student>();
        studentRepo.findAllById(idSet).forEach(s -> byId.put(s.getId(), s));

        var byName = new java.util.HashMap<String, Student>();
        if (!nameSet.isEmpty()) {
            // 你如果有 findByMemberNameIn 可以用；沒有就 loop findByMemberName
            for (String name : nameSet) {
                var s = studentRepo.findByMemberName(name);
                if (s != null) byName.put(name, s);
            }
        }

        final int BATCH = 50;
        java.util.List<ExamResult> buffer = new java.util.ArrayList<>(BATCH);
        Integer defaultScore = (req.getDefaultScore() == null) ? 0 : req.getDefaultScore();

        for (var it : items) {
            Student student = null;
            if (it.getStudentId() != null)       student = byId.get(it.getStudentId());
            else if (it.getStudentName() != null) student = byName.get(it.getStudentName());

            if (student == null) {
                if (it.getStudentId() != null) out.failedIds.add(it.getStudentId());
                if (it.getStudentName() != null) out.failedNames.add(it.getStudentName());
                out.skipped++;
                continue;
            }

            boolean exists = examResultRepo.existsByStudentIdAndExamPaperId(student.getId(), req.getExamPaperId());
            if (exists) { out.skipped++; continue; }

            var r = new ExamResult();
            r.setStudent(student);
            r.setTeacher(teacher);
            r.setExamPaper(paper);
            r.setScore( (it.getScore() != null) ? it.getScore() : defaultScore );
            r.setComment(it.getComment());
            r.setReviewedTime(new java.sql.Timestamp(System.currentTimeMillis()));

            buffer.add(r);

            if (buffer.size() >= BATCH) {
                examResultRepo.saveAll(buffer);
                em.flush(); em.clear();
                out.inserted += buffer.size();
                buffer.clear();
            }
        }

        if (!buffer.isEmpty()) {
            examResultRepo.saveAll(buffer);
            em.flush(); em.clear();
            out.inserted += buffer.size();
        }

        recalcRankByPaperSafe(req.getExamPaperId());
        return out;
    }
    
    public ExamResultDTO toDTO(ExamResult entity) {
    	ExamResultDTO dto = new ExamResultDTO();
    	  dto.setId(entity.getId());
          dto.setScore(entity.getScore());
          dto.setRankInClass(entity.getRankInClass());
          dto.setExamPaperName(entity.getExamPaper().getName());
          dto.setComment(entity.getComment());
          dto.setReviewedTime(entity.getReviewedTime());
          dto.setCreateTime(entity.getCreateTime());
          dto.setStudent_id(entity.getStudent().getId());
          dto.setReviewed_by(entity.getTeacher().getId());
          dto.setExam_paper_id(entity.getExamPaper().getId());
          dto.setReviewName(entity.getReviewerName());
          dto.setStudentName(entity.getStudentName());
          
    	return dto;
    }
}
