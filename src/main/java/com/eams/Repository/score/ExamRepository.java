package com.eams.Repository.score;

import com.eams.Entity.score.Exam;
import com.eams.Entity.score.ExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Integer> {

    // 依名稱模糊查（給卡片查詢用）
    Page<Exam> findByNameContainingIgnoreCase(String name, Pageable pageable);

    
    @Query(""" 
        select ep from ExamPaper ep
        join fetch ep.subject s
        left join fetch ep.course c
        where ep.exam.id = :examId
        order by ep.examDate desc, ep.id desc
    """)
    List<ExamPaper> findPapersByExamId(@Param("examId") Integer examId);

    // 依名稱模糊查並同時載入考卷（點卡片即展開下方 datatable）
    @Query("""
        select distinct e from Exam e
        left join fetch e.examPapers ep
        where lower(e.name) like lower(concat('%', :q, '%'))
        order by e.examDate desc, e.id desc
    """)
    List<Exam> searchWithPapers(@Param("q") String q);
}
