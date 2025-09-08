package com.eams.Repository.score;

import com.eams.Entity.score.Question;
import com.eams.Entity.score.QuestionAnswerKey;
import com.eams.Entity.score.QuestionSheet;
import com.eams.Entity.score.QuestionSheetItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    // 依科目 + 關鍵字 查詢
    @Query("""
        select q from Question q
        where (:subjectId is null or q.subject.id = :subjectId)
          and (:kw is null or lower(q.stem) like lower(concat('%', :kw, '%')))
        
    """)
    Page<Question> pageBySubjectAndKeyword(@Param("subjectId") Integer subjectId,
                                           @Param("kw") String keyword,
                                           Pageable pageable);
}


