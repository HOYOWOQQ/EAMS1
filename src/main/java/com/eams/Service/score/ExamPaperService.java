package com.eams.Service.score;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eams.Entity.course.Course;
import com.eams.Entity.course.Subject;
import com.eams.Entity.score.Exam;
import com.eams.Entity.score.ExamPaper;
import com.eams.Entity.score.DTO.ExamPaperDTO;
import com.eams.Repository.score.ExamPaperRepository;


@Service
@Transactional
public class ExamPaperService {

	@Autowired
    private ExamPaperRepository repository;

    /** 新增或更新考卷 **/
    public ExamPaperDTO save(ExamPaperDTO dto) {
        ExamPaper entity = toEntity(dto);
        ExamPaper saved = repository.save(entity);
        return ExamPaperDTO.fromEntity(saved);
    }

    /** 根據 ID 查詢 **/
    public ExamPaperDTO findById(int id) {
        return repository.findById(id)
                .map(ExamPaperDTO::fromEntity)
                .orElse(null);
    }

    /** 查詢全部 **/
    public List<ExamPaperDTO> findAll() {
        return repository.findAllWithCourse().stream()
                .map(ExamPaperDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /** 查詢考卷名子 **/
    public List<String> getAllPaperNames2() {
        return repository.findAllDistinctExamPaperNames2();
    }
    /** 根據 courseId 查詢 **/
    public List<ExamPaperDTO> findByCourseId(int courseId) {
        return repository.findByCourse_IdOrderByExamDateDesc(courseId).stream()
                .map(ExamPaperDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 根據 examType 查詢 **/
    public List<ExamPaperDTO> findByExamType(String examType) {
        return repository.findByExamTypeOrderByExamDateDesc(examType).stream()
                .map(ExamPaperDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    
    
    /** 根據 courseId + examType 查詢 **/
    public List<ExamPaperDTO> findByCourseIdAndType(int courseId, String examType) {
        return repository.findByCourse_IdAndExamTypeOrderByExamDateDesc(courseId, examType).stream()
                .map(ExamPaperDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 根據日期區間查詢 **/
    public List<ExamPaperDTO> findByDateRange(Date start, Date end) {
        return repository.findByExamDateBetweenOrderByExamDateAsc(start, end).stream()
                .map(ExamPaperDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /** 刪除 by id **/
    public boolean deleteById(int id) {
        if (repository.findById(id).isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    /** 刪除 by courseId **/
    public int deleteByCourseId(int courseId) {
        return repository.deleteByCourse_Id(courseId);
    }

    /** 刪除 by examType **/
    public int deleteByExamType(String examType) {
        return repository.deleteByExamType(examType);
    }

    /** 刪除 by date range **/
    public int deleteByDateRange(Date start, Date end) {
        return repository.deleteByExamDateBetween(start, end);
    }

    /** 取得所有考卷名稱 **/
    public List<String> getAllPaperNames() {
        return repository.findAllDistinctExamPaperNames();
    }

    /** 取得有成績的考卷名稱 **/
    public List<String> getExamPapersWithResults() {
        return repository.findExamPaperNamesWithResults();
    }

    /** 取得考卷與成績筆數映射 **/
    public List<Object[]> getExamPaperResultCounts() {
        return repository.findExamPaperNameWithResultCount();
    }
    
    
    private ExamPaper toEntity(ExamPaperDTO dto) {
        if (dto == null) return null;

        ExamPaper entity = new ExamPaper();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setExamType(dto.getExamType());
        entity.setExamDate(dto.getExamDate());
        entity.setTotalScore(dto.getTotalScore());
        entity.setDescription(dto.getDescription());
        entity.setCreateTime(dto.getCreateTime());
        
        if(dto.getExamId()!=null) {
        	Exam exam = new Exam();
        	exam.setId(dto.getExamId());
        	entity.setExam(exam);
        }
        
        if(dto.getSubjectId()!=null) {
        	Subject subject = new Subject();
            subject.setId(dto.getSubjectId());
            entity.setSubject(subject);
        }
        
        
        if (dto.getCourseId() != null) {
            Course course = new Course();
            course.setId(dto.getCourseId());
            course.setName(dto.getName());
            entity.setCourse(course);  // <== 必須設定這個關聯物件
        }

        return entity;
    }
}
