package com.eams.Repository.score;

import com.eams.Entity.score.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Integer> {
 
    
 // 某學生在某考卷的最新一次作答
    Optional<ExamAttempt> findTopByExamPaper_IdAndStudent_IdOrderByStartTimeDesc(Integer examPaperId, Integer studentId);

    // 取該考卷所有作答
    List<ExamAttempt> findByExamPaper_IdOrderByStartTimeDesc(Integer examPaperId);

    // 取某學生所有作答
    List<ExamAttempt> findByStudent_IdOrderByStartTimeDesc(Integer studentId);
    
 // 只給學生查自己的 attempt 清單
    List<ExamAttempt> findByExamPaper_IdAndStudent_IdOrderByStartTimeDesc(Integer paperId, Integer studentId);
    
    
    boolean existsByIdAndStudent_Id(Integer id, Integer studentId);
    boolean existsByExamPaper_IdAndStudent_Id(Integer paperId, Integer studentId);
    boolean existsByExamPaper_IdAndStudent_IdAndStatus(Integer paperId, Integer studentId, String status);
    @Query(value = """
        SELECT ep.id
        FROM exam_attempt a
        JOIN exam_paper ep ON ep.id = a.exam_paper_id
        WHERE a.id = :attemptId
    """, nativeQuery = true)
    Optional<Integer> findPaperIdByAttemptId(@Param("attemptId") Integer attemptId);
}

