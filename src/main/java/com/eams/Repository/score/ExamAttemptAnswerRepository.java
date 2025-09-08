package com.eams.Repository.score;

import java.util.List;
import java.util.Optional;

import com.eams.Entity.score.ExamAttemptAnswer;
import com.eams.Entity.score.QuestionSheetItem;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ExamAttemptAnswerRepository extends JpaRepository<ExamAttemptAnswer, Integer> {

    List<ExamAttemptAnswer> findByAttempt_IdOrderBySeqNoAsc(Integer attemptId);

    // 途中作答：更新選項
    @Modifying
    @Query("""
        update ExamAttemptAnswer a
           set a.selectedOption = :opt
         where a.attempt.id = :attemptId and a.seqNo = :seqNo
    """)
    int updateSelected(@Param("attemptId") Integer attemptId,
                       @Param("seqNo") Integer seqNo,
                       @Param("opt") String option);
    
    Optional<ExamAttemptAnswer> findByAttempt_IdAndSeqNo(Integer attemptId, Integer seqNo);
}
