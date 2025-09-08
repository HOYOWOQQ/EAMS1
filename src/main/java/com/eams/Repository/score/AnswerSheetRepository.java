package com.eams.Repository.score;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eams.Entity.score.AnswerSheet;

public interface AnswerSheetRepository extends JpaRepository<AnswerSheet, Integer> {
	// 依考卷 ID 查詢該考卷的答案卷
	AnswerSheet findByExamPaper_Id(Integer examPaperId);
}
