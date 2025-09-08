package com.eams.Repository.score;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.score.AnswerSheetItem;

public interface AnswerSheetItemRepository extends JpaRepository<AnswerSheetItem, Integer> {

    // 取該考卷的標準答案（用題號對應）
    @Query("""
        select i from AnswerSheetItem i
        where i.sheet.examPaper.id = :paperId
        order by i.seqNo
    """)
    List<AnswerSheetItem> findStdByPaperId(@Param("paperId") Integer paperId);
}
