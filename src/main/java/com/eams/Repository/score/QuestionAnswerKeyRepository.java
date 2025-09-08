package com.eams.Repository.score;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eams.Entity.score.QuestionAnswerKey;

public interface QuestionAnswerKeyRepository extends JpaRepository<QuestionAnswerKey, Integer> {
    // PK = questionId
}
