package com.eams.Repository.score;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.eams.Entity.score.QuestionSheet;

public interface QuestionSheetRepository extends JpaRepository<QuestionSheet, Integer> {

	
	QuestionSheet findByExamPaper_Id(Integer examPaperId);
	
	@Query("""
			  select s from QuestionSheet s
			  left join fetch s.items i
			  left join fetch i.question q
			  where s.examPaper.id = :paperId
			""")
			Optional<QuestionSheet> findWithItemsAndQuestionByExamPaperId(@Param("paperId") Integer paperId);
}
