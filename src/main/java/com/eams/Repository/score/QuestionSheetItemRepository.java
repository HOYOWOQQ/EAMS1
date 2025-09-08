package com.eams.Repository.score;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.score.QuestionSheetItem;

public interface QuestionSheetItemRepository extends JpaRepository<QuestionSheetItem, Integer> {
    List<QuestionSheetItem> findBySheet_IdOrderBySeqNoAsc(Integer sheetId);

    @Modifying
    @Query("delete from QuestionSheetItem i where i.sheet.id = :sheetId")
    void deleteAllBySheetId(@Param("sheetId") Integer sheetId);
    
    @Query("""
    		select i
    		from QuestionSheetItem i
    		join fetch i.sheet s
    		join fetch s.examPaper p
    		join fetch i.question q
    		where p.id = :paperId and i.seqNo =:seqNo
    		""")
    		    Optional<QuestionSheetItem> findItemByPaperAndSeqNo(@Param("paperId") Integer paperId,
    		                                                        @Param("seqNo") Integer seqNo);
}
